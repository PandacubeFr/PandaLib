package fr.pandacube.lib.paper.players;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import fr.pandacube.lib.paper.PandaLibPaper;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PlayerNonPersistentConfig {
    private static final Map<UUID, Map<String, ConfigEntry>> data = new HashMap<>();

    private static long tick = 0;

    static {
        new ConfigListeners();
    }


    public static void setData(UUID playerId, String key, String value, Expiration expiration) {
        data.computeIfAbsent(Objects.requireNonNull(playerId, "playerId"), pp -> new HashMap<>())
                .put(Objects.requireNonNull(key, "key"),
                        new ConfigEntry(Objects.requireNonNull(value, "value"),
                                Objects.requireNonNull(expiration, "expiration")
                        )
                );
    }

    public static void unsetData(UUID playerId, String key) {
        data.getOrDefault(Objects.requireNonNull(playerId, "playerId"), new HashMap<>())
                .remove(Objects.requireNonNull(key, "key"));
    }

    public static String getData(UUID playerId, String key) {
        Map<String, ConfigEntry> playerData = data.getOrDefault(Objects.requireNonNull(playerId, "playerId"), new HashMap<>());
        ConfigEntry ce = playerData.get(Objects.requireNonNull(key, "key"));
        if (ce == null)
            return null;
        if (!ce.expiration.valid(playerId, key)) {
            playerData.remove(key);
            return null;
        }
        return ce.value;
    }

    public static boolean isDataSet(UUID playerId, String key) {
        return getData(playerId, key) != null;
    }












    private record ConfigEntry(String value, Expiration expiration) { }




    public static abstract class Expiration {
        abstract boolean valid(UUID player, String key);
    }

    public static class ExpiresLogout extends Expiration {
        protected boolean valid(UUID player, String key) {
            return Bukkit.getPlayer(player) != null; // should not be call if player reconnects because it is removed on player quit
        }
    }

    public static class ExpiresTick extends Expiration {
        long expirationTick;

        public ExpiresTick(long expirationDelayTick) {
            expirationTick = tick + expirationDelayTick;
        }

        protected boolean valid(UUID player, String key) {
            return tick < expirationTick;
        }
    }

    public static class ExpiresServerStop extends Expiration {
        protected boolean valid(UUID player, String key) {
            return true;
        }
    }







    private static class ConfigListeners implements Listener {
        public ConfigListeners() {
            Bukkit.getPluginManager().registerEvents(this, PandaLibPaper.getPlugin());
        }

        @EventHandler
        public void onPlayerQuit(PlayerQuitEvent event) {
            data.getOrDefault(event.getPlayer().getUniqueId(), new HashMap<>())
                    .entrySet()
                    .removeIf(e -> e.getValue().expiration instanceof ExpiresLogout);
        }

        @EventHandler
        public void onTickStart(ServerTickStartEvent event) {
            tick++;
        }
    }
}
