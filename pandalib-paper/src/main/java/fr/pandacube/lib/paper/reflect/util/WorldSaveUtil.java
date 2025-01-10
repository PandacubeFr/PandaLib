package fr.pandacube.lib.paper.reflect.util;

import fr.pandacube.lib.chat.Chat;
import fr.pandacube.lib.chat.ChatConfig.PandaTheme;
import fr.pandacube.lib.paper.modules.PerformanceAnalysisManager;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftWorld;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.ServerLevel;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.util.ProgressListener;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 * Provides static methods to save worlds presumably in a better way than Bukkit provides (better flushing, more released RAM).
 */
public class WorldSaveUtil {

	/**
	 * Save the provided world using the NMS {@link ServerLevel#save(ProgressListener, boolean, boolean)} method.
	 * @param w the world to save.
	 */
	public static void nmsSaveFlush(World w) {
		PerformanceAnalysisManager.getInstance().setAlteredTPSTitle(
				Chat.text("Sauvegarde map ").color(PandaTheme.CHAT_BROWN_2_SAT).thenData(w.getName()).thenText(" ...")
				);
		
		try {
			ReflectWrapper.wrapTyped(w, CraftWorld.class).getHandle().save(null, true, false);
		} finally {
			PerformanceAnalysisManager.getInstance().setAlteredTPSTitle(null);
		}
	}

	/**
	 * Save all the loaded worlds, using {@link #nmsSaveFlush(World)}.
	 */
	public static void nmsSaveAllFlush() {
		Bukkit.getWorlds().forEach(WorldSaveUtil::nmsSaveFlush);
	}

	private WorldSaveUtil() {}
	
}
