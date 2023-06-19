package fr.pandacube.lib.paper.players;

import fr.pandacube.lib.paper.PandaLibPaper;
import fr.pandacube.lib.util.Log;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.UUID;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class PaperPlayerConfigStorage {

    static final File storageFile = new File(PandaLibPaper.getPlugin().getDataFolder(), "playerdata.yml");
    static boolean initialized = false;

    static final LinkedHashMap<ConfigKey, ConfigEntry> data = new LinkedHashMap<>();
    static final LinkedHashMap<UUID, LinkedHashSet<ConfigEntry>> playerSortedData = new LinkedHashMap<>();
    static final LinkedHashMap<String, LinkedHashSet<ConfigEntry>> keySortedData = new LinkedHashMap<>();
    static boolean changed = false;



    private static synchronized void initIfNeeded() {
        if (initialized)
            return;

        try {
            load();
        } catch (InvalidConfigurationException|IOException e) {
            throw new RuntimeException("Unable to load the player data file.", e);
        }

        // auto-save every 30 seconds
        Bukkit.getScheduler().runTaskTimerAsynchronously(PandaLibPaper.getPlugin(), PaperPlayerConfigStorage::save, 600, 600);

        initialized = true;
    }






    private static synchronized void load() throws IOException, InvalidConfigurationException {
        YamlConfiguration config = new YamlConfiguration();
        data.clear();
        playerSortedData.clear();
        keySortedData.clear();
        if (!storageFile.exists())
            return;
        config.load(storageFile);
        for (String pIdStr : config.getKeys(false)) {
            UUID pId;
            try {
                pId = UUID.fromString(pIdStr);
            } catch (IllegalArgumentException e) {
                Log.severe("Invalid player UUID: '" + pIdStr + "'", e);
                continue;
            }
            ConfigurationSection sec = config.getConfigurationSection(pIdStr);
            for (String key : sec.getKeys(false)) {
                String value = sec.getString(key);
                create(pId, key, value);
            }
        }
        changed = false;
    }



    private static synchronized void save() {
        YamlConfiguration config = new YamlConfiguration();
        for (UUID pId : playerSortedData.keySet()) {
            String pIdStr = pId.toString();
            ConfigurationSection sec = new YamlConfiguration();
            for (ConfigEntry e : playerSortedData.get(pId)) {
                sec.set(e.key, e.value);
            }
            config.set(pIdStr, sec);
        }
        try {
            config.save(storageFile);
        } catch (IOException e) {
            throw new RuntimeException("Unable to save the player data file.", e);
        }
        changed = false;
    }







    private static synchronized void create(UUID player, String key, String newValue) {
        ConfigKey cKey = new ConfigKey(player, key);
        ConfigEntry e = new ConfigEntry(player, key, newValue);
        data.put(cKey, e);
        playerSortedData.computeIfAbsent(player, p -> new LinkedHashSet<>()).add(e);
        keySortedData.computeIfAbsent(key, p -> new LinkedHashSet<>()).add(e);
    }


    public static synchronized void set(UUID player, String key, String newValue) {
        initIfNeeded();
        ConfigKey cKey = new ConfigKey(player, key);
        ConfigEntry e = data.get(cKey);
        if (e != null && newValue == null) { // delete
            data.remove(cKey);
            if (playerSortedData.containsKey(player))
                playerSortedData.get(player).remove(e);
            if (keySortedData.containsKey(key))
                keySortedData.get(key).remove(e);
            changed = true;
        }
        else if (e == null && newValue != null) { // create
            create(player, key, newValue);
            changed = true;
        }
        else if (e != null && !newValue.equals(e.value)) { // update
            e.value = newValue;
            changed = true;
        }
    }

    public static synchronized String get(UUID player, String key) {
        initIfNeeded();
        ConfigEntry e = data.get(new ConfigKey(player, key));
        return e != null ? e.value : null;
    }

    public static String get(UUID p, String k, String deflt) {
        String value = get(p, k);
        return value == null ? deflt : value;
    }

    public static synchronized void update(UUID p, String k, String deflt, UnaryOperator<String> updater) {
        String oldValue = get(p, k, deflt);
        set(p, k, updater.apply(oldValue));
    }

    public static void unset(UUID p, String k) {
        set(p, k, null);
    }


    public static LinkedHashSet<ConfigEntry> getAllFromPlayer(UUID p) {
        initIfNeeded();
        return new LinkedHashSet<>(playerSortedData.getOrDefault(p, new LinkedHashSet<>()));
    }

    public static LinkedHashSet<ConfigEntry> getAllWithKeys(String key) {
        initIfNeeded();
        return new LinkedHashSet<>(keySortedData.getOrDefault(key, new LinkedHashSet<>()));
    }

    public static LinkedHashSet<ConfigEntry> getAllWithKeyValue(String k, String v) {
        initIfNeeded();
        return getAllWithKeys(k).stream()
                .filter(c -> c.value.equals(v))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }



    private record ConfigKey(UUID playerId, String key) { }

    public static class ConfigEntry {
        private final UUID playerId;
        private final String key;
        private String value;

        private ConfigEntry(UUID playerId, String key, String value) {
            this.playerId = playerId;
            this.key = key;
            this.value = value;
        }

        public UUID getPlayerId() {
            return playerId;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(playerId, key);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ConfigEntry o
                    && Objects.equals(playerId, o.playerId)
                    && Objects.equals(key, o.key);
        }
    }
}
