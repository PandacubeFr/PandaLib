package fr.pandacube.lib.paper.world;

import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftServer;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;

/**
 * The currently loaded {@link LevelDir}.
 * There is only one loaded by a vanilla/Paper server.
 */
public class ServerLevelDir extends LevelDir {

    /**
     * The unique instance of {@link ServerLevelDir}.
     */
    public static final ServerLevelDir INSTANCE = new ServerLevelDir();



    private ServerLevelDir() {
        super(Bukkit.getServer().getLevelDirectory().toFile());
    }


    @Override
    public ServerDimensionDir getDimension(NamespacedKey dim) {
        return new ServerDimensionDir(dim);
    }


    /**
     * Get the name of the level as defined in the server configuration.
     * @return the name of the level
     */
    public static String getServerLevelName() {
        return ReflectWrapper.wrapTyped(Bukkit.getServer(), CraftServer.class).getServer().getLevelIdName();
    }


    @Override
    public String toString() {
        return "ServerLevelDir(" + getDirectory().getAbsolutePath() + ")";
    }
}
