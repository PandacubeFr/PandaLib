package fr.pandacube.lib.paper.players;

import fr.pandacube.lib.paper.PandaLibPaper;
import fr.pandacube.lib.util.log.Log;
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

/**
 * Provides rudimentary player data storage using a file in the plugin configuration.
 * The file is loaded on the first access, and is auto-saved if needed every 30 seconds.
 */
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
        if (!changed)
            return;
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


    /**
     * Sets the value of the provided configuration key for the player.
     * @param player the player.
     * @param key the configuration key to set.
     * @param value the new value.
     */
    public static synchronized void set(UUID player, String key, String value) {
        initIfNeeded();
        ConfigKey cKey = new ConfigKey(player, key);
        ConfigEntry e = data.get(cKey);
        if (e != null && value == null) { // delete
            data.remove(cKey);
            if (playerSortedData.containsKey(player))
                playerSortedData.get(player).remove(e);
            if (keySortedData.containsKey(key))
                keySortedData.get(key).remove(e);
            changed = true;
        }
        else if (e == null && value != null) { // create
            create(player, key, value);
            changed = true;
        }
        else if (e != null && !value.equals(e.value)) { // update
            e.value = value;
            changed = true;
        }
    }

    /**
     * Gets the value of the provided configuration key of the player.
     * @param player the player.
     * @param key the configuration key.
     * @return the value of the configuration, or null if the configuration is not set.
     */
    public static synchronized String get(UUID player, String key) {
        initIfNeeded();
        ConfigEntry e = data.get(new ConfigKey(player, key));
        return e != null ? e.value : null;
    }

    /**
     * Gets the value of the provided configuration key of the player.
     * @param player the player.
     * @param key the configuration key.
     * @param deflt the default value if the configuration is not set.
     * @return the value of the configuration, or {@code deflt} if the configuration is not set.
     */
    public static String get(UUID player, String key, String deflt) {
        String value = get(player, key);
        return value == null ? deflt : value;
    }

    /**
     * Updates the value of the provided configuration key for the player, using the provided updater.
     * @param player the player.
     * @param key the configuration key to update.
     * @param deflt the default value to use if the configuration is not already set.
     * @param updater the unary operator to use to update th value. The old value is used as the parameter of the updater,
     *                and it returns the new value of the configuration.
     */
    public static synchronized void update(UUID player, String key, String deflt, UnaryOperator<String> updater) {
        String oldValue = get(player, key, deflt);
        set(player, key, updater.apply(oldValue));
    }

    /**
     * Unsets the value of the provided configuration key for the player.
     * @param player the player.
     * @param key the configuration key to update.
     */
    public static void unset(UUID player, String key) {
        set(player, key, null);
    }

    /**
     * Gets all the config key-value pairs of the provided player.
     * @param player the player.
     * @return all the config key-value pairs of the provided player.
     */
    public static LinkedHashSet<ConfigEntry> getAllFromPlayer(UUID player) {
        initIfNeeded();
        return new LinkedHashSet<>(playerSortedData.getOrDefault(player, new LinkedHashSet<>()));
    }

    /**
     * Gets all the config key-value pairs of all players that have the provided key.
     * @param key the key.
     * @return all the config key-value pairs of all players that have the provided key.
     */
    public static LinkedHashSet<ConfigEntry> getAllWithKeys(String key) {
        initIfNeeded();
        return new LinkedHashSet<>(keySortedData.getOrDefault(key, new LinkedHashSet<>()));
    }

    /**
     * Gets all the config key-value pairs of all players that have the provided key AND value.
     * @param key the key.
     * @param v the value.
     * @return all the config key-value pairs of all players that have the provided key AND value.
     */
    public static LinkedHashSet<ConfigEntry> getAllWithKeyValue(String key, String v) {
        initIfNeeded();
        return getAllWithKeys(key).stream()
                .filter(c -> c.value.equals(v))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }



    private record ConfigKey(UUID playerId, String key) { }

    /**
     * Class holding the playerId-key-value triplet.
     */
    public static class ConfigEntry {
        private final UUID playerId;
        private final String key;
        private String value;

        /**
         * Creates a new {@link ConfigEntry}.
         * @param playerId the player id.
         * @param key the key.
         * @param value the value.
         */
        private ConfigEntry(UUID playerId, String key, String value) {
            this.playerId = playerId;
            this.key = key;
            this.value = value;
        }

        /**
         * Gets the player id.
         * @return the player id.
         */
        public UUID getPlayerId() {
            return playerId;
        }

        /**
         * Gets the config key.
         * @return the config key.
         */
        public String getKey() {
            return key;
        }

        /**
         * Gets the config value.
         * @return the config value.
         */
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


    private PaperPlayerConfigStorage() {}
}
