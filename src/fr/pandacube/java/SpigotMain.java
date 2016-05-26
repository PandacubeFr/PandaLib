package fr.pandacube.java;

import org.bukkit.plugin.java.JavaPlugin;

public class SpigotMain extends JavaPlugin {

	@Override
	public void onLoad() {
		PandacubeUtil.setServerLogger(getServer().getLogger());
		PandacubeUtil.setPluginLogger(getLogger());
	}
	
}
