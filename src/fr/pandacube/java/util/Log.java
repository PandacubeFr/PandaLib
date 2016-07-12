package fr.pandacube.java.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {
	
	private static Logger logger;

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

}
