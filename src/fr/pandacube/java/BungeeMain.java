package fr.pandacube.java;

import net.md_5.bungee.api.plugin.Plugin;

public class BungeeMain extends Plugin {

	@Override
	public void onLoad() {
		PandacubeUtil.setMasterLogger(getProxy().getLogger());
		PandacubeUtil.setPluginLogger(getLogger());
	}
	
}
