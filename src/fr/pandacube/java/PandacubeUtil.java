package fr.pandacube.java;

import java.nio.charset.Charset;
import java.util.logging.Logger;

public class PandacubeUtil {
	

	
	public static final Charset NETWORK_CHARSET = Charset.forName("UTF-8");
	
	public static final int NETWORK_TCP_BUFFER_SIZE = 1024*1024;
	
	public static final int NETWORK_TIMEOUT = 30*1000; // 30 secondes
	
	
	
	
	/**
	 * Représente le logger du serveur Spigot ou de Bungee,selon l'environnement
	 */
	private static Logger masterLogger;
	
	/**
	 * Représente le logger de PandacubeUtil, mais défini selon l'environnement Spigot ou Bungee.
	 */
	private static Logger pluginLogger;

	public static Logger getMasterLogger() {
		return masterLogger;
	}

	public static void setMasterLogger(Logger masterLogger) {
		PandacubeUtil.masterLogger = masterLogger;
	}

	public static Logger getPluginLogger() {
		return pluginLogger;
	}

	public static void setPluginLogger(Logger pluginLogger) {
		PandacubeUtil.pluginLogger = pluginLogger;
	}

}
