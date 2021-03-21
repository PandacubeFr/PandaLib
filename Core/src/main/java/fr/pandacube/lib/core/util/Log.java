package fr.pandacube.lib.core.util;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {

	private static Logger logger;
	private static AtomicBoolean logDebug = new AtomicBoolean(false);

	public static void setDebugState(boolean newVal) {
		logDebug.set(newVal);
	}

	public static boolean getDebugState() {
		return logDebug.get();
	}

	public static Logger getLogger() {
		return logger;
	}

	public static void setLogger(Logger l) {
		logger = l;
	}

	public static void info(String message) {
		logger.info(message);
	}

	public static void warning(String message, Throwable t) {
		logger.log(Level.WARNING, message, t);
	}

	public static void warning(Throwable t) {
		logger.log(Level.WARNING, "", t);
	}

	public static void warning(String message) {
		logger.warning(message);
	}

	public static void severe(String message, Throwable t) {
		logger.log(Level.SEVERE, message, t);
	}

	public static void severe(Throwable t) {
		logger.log(Level.SEVERE, "", t);
	}

	public static void severe(String message) {
		logger.severe(message);
	}

	public static void debug(String message, Throwable t) {
		if (!logDebug.get()) return;
		logger.log(Level.INFO, message, t);
	}

	public static void debug(Throwable t) {
		if (!logDebug.get()) return;
		logger.log(Level.INFO, "", t);
	}

	public static void debug(String message) {
		if (!logDebug.get()) return;
		logger.info(message);
	}

}
