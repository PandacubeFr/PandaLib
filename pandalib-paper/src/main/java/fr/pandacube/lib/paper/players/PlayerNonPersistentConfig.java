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

/**
 * Handles the player related configuration that is not persisted to disk.
 */
public class PlayerNonPersistentConfig {
    private static final Map<UUID, Map<String, ConfigEntry>> data = new HashMap<>();

    private static long tick = 0;

    static {
        new ConfigListeners();
    }


    /**
     * Sets the value of the provided configuration key for the player.
     * @param player the player.
     * @param key the configuration key to set.
     * @param value the new value.
     * @param expirationPolicy the expiration policy for this config. If the config key already exists for this player. the expiration will be overridden.
     */
    public static void setData(UUID player, String key, String value, ExpirationPolicy expirationPolicy) {
        data.computeIfAbsent(Objects.requireNonNull(player, "playerId"), pp -> new HashMap<>())
                .put(Objects.requireNonNull(key, "key"),
                        new ConfigEntry(Objects.requireNonNull(value, "value"),
                                Objects.requireNonNull(expirationPolicy, "expiration")
                        )
                );
    }

    /**
     * Unsets the value of the provided configuration key for the player.
     * @param player the player.
     * @param key the configuration key to update.
     */
    public static void unsetData(UUID player, String key) {
        data.getOrDefault(Objects.requireNonNull(player, "playerId"), new HashMap<>())
                .remove(Objects.requireNonNull(key, "key"));
    }

    /**
     * Gets the value of the provided configuration key of the player.
     * @param player the player.
     * @param key the configuration key.
     * @return the value of the configuration, or {@code deflt} if the configuration is not set.
     */
    public static String getData(UUID player, String key) {
        Map<String, ConfigEntry> playerData = data.getOrDefault(Objects.requireNonNull(player, "playerId"), new HashMap<>());
        ConfigEntry ce = playerData.get(Objects.requireNonNull(key, "key"));
        if (ce == null)
            return null;
        if (!ce.expirationPolicy.valid(player, key)) {
            playerData.remove(key);
            return null;
        }
        return ce.value;
    }

    /**
     * Tells if the provided config key is set for the player.
     * @param player the player.
     * @param key the configuration key.
     * @return true if the value is set, false otherwise.
     */
    public static boolean isDataSet(UUID player, String key) {
        return getData(player, key) != null;
    }












    private record ConfigEntry(String value, ExpirationPolicy expirationPolicy) { }


    /**
     * Super class for all expiration policies.
     */
    public static abstract class ExpirationPolicy {
        /**
         * Creates an expiration policy.
         */
        public ExpirationPolicy() {}

        /**
         * Tests if the associated configuration is still valid (not expired).
         * @param player the player.
         * @param key the configuration key.
         * @return true if the associated configuration is still valid, false otherwise.
         */
        abstract boolean valid(UUID player, String key);
    }

    /**
     * Expiration policy for a config that expires when the player logs out.
     */
    public static class ExpiresLogout extends ExpirationPolicy {
        /**
         * Creates a logout expiration policy.
         */
        public ExpiresLogout() {
            super();
        }

        @Override
        protected boolean valid(UUID player, String key) {
            return Bukkit.getPlayer(player) != null; // should not be call if player reconnects because it is removed on player quit
        }
    }

    /**
     * Expiration policy for a config that expires after a certain amount of game tick.
     */
    public static class ExpiresTick extends ExpirationPolicy {
        final long expirationTick;

        /**
         * Creates a delay expiration policy.
         * @param expirationDelayTick the number of tick after which the config will expire. If 0, will expire immediately ; 1 to expire on the next tick.
         */
        public ExpiresTick(long expirationDelayTick) {
            expirationTick = tick + expirationDelayTick;
        }

        @Override
        protected boolean valid(UUID player, String key) {
            return tick < expirationTick;
        }
    }

    /**
     * Expiration policy for a config that expires when the server stops.
     */
    public static class ExpiresServerStop extends ExpirationPolicy {
        /**
         * Creates a server stop expiration policy.
         */
        public ExpiresServerStop() {
            super();
        }

        @Override
        protected boolean valid(UUID player, String key) {
            return true;
        }
    }







    private static class ConfigListeners implements Listener {
        private ConfigListeners() {
            Bukkit.getPluginManager().registerEvents(this, PandaLibPaper.getPlugin());
        }

        @EventHandler
        public void onPlayerQuit(PlayerQuitEvent event) {
            data.getOrDefault(event.getPlayer().getUniqueId(), new HashMap<>())
                    .entrySet()
                    .removeIf(e -> e.getValue().expirationPolicy instanceof ExpiresLogout);
        }

        @EventHandler
        public void onTickStart(ServerTickStartEvent event) {
            tick++;
        }
    }




    private PlayerNonPersistentConfig() {}
}
