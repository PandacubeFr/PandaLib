package fr.pandacube.java;

import org.bukkit.plugin.java.JavaPlugin;

public class SpigotMain extends JavaPlugin {

	@Override
	public void onLoad() {
		PandacubeUtil.setMasterLogger(getServer().getLogger());
		PandacubeUtil.setPluginLogger(getLogger());
	}
	
}
