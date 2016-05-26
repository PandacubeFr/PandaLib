package fr.pandacube.java;

import java.util.logging.Logger;

public class PandacubeUtil {
	
	/**
	 * Représente le logger du serveur Spigot ou de Bungee,selon l'environnement
	 */
	private static Logger serverLogger;
	
	/**
	 * Représente le logger de PandacubeUtil, mais défini selon l'environnement Spigot ou Bungee.
	 */
	private static Logger pluginLogger;

	public static Logger getServerLogger() {
		return serverLogger;
	}

	public static void setServerLogger(Logger serverLogger) {
		PandacubeUtil.serverLogger = serverLogger;
	}

	public static Logger getPluginLogger() {
		return pluginLogger;
	}

	public static void setPluginLogger(Logger pluginLogger) {
		PandacubeUtil.pluginLogger = pluginLogger;
	}

}
