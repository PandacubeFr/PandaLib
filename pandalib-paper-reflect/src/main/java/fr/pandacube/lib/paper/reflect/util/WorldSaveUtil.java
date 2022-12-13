package fr.pandacube.lib.paper.reflect.util;

import org.bukkit.Bukkit;
import org.bukkit.World;

import fr.pandacube.lib.chat.Chat;
import fr.pandacube.lib.chat.ChatConfig.PandaTheme;
import fr.pandacube.lib.paper.modules.PerformanceAnalysisManager;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftWorld;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.ChunkMap;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

public class WorldSaveUtil {
	
	private static ChunkMap getChunkMap(World w) {
		return ReflectWrapper.wrapTyped(w, CraftWorld.class).getHandle().getChunkSource().chunkMap;
	}
	
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
	
	public static void nmsSaveAllFlush() {
		Bukkit.getWorlds().forEach(WorldSaveUtil::nmsSaveFlush);
	}
	
}
