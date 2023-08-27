package fr.pandacube.lib.util.log;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class to easily log info into a provided logger. This class avoid the needs to fetch the logger everytime it
 * is needed.
 *
 * For instance, this piece of code:
 * <pre>
 * getTheLoggerFromSomewhere().info(message);
 * </pre>
 *
 * Can be simplified by one line to put in the startup code of the application:
 * <pre>
 * Log.setLogger(getTheLoggerFromSomewhere());
 * </pre>
 * And this code everywhere the application needs to log something:
 * <pre>
 * Log.info(message);
 * </pre>
 *
 * This the {@link #setLogger(Logger)} method is not called, thi class will use the logger returned by
 * {@link Logger#getGlobal()}.
 */
public final class Log {

	private static Logger logger = Logger.getGlobal();
	private static final AtomicBoolean logDebug = new AtomicBoolean(false);

	/**
	 * Determine if {@link #debug(Throwable)}, {@link #debug(String)} and {@link #debug(String, Throwable)} will actually
	 * log a message or not.
	 * @param debug true to enable debug, false otherwise
	 */
	public static void setDebug(boolean debug) {
		logDebug.set(debug);
	}

	/**
	 * Tells if the debug mode is enabled or not.
	 * @return true if debug is enabled, false otherwise.
	 */
	public static boolean isDebugEnabled() {
		return logDebug.get();
	}

	/**
	 * Get the backend logger of this class.
	 * @return the logger.
	 */
	public static Logger getLogger() {
		return logger;
	}

	/**
	 * Set the backend logger of this class.
	 * @param logger the logger to use.
	 */
	public static void setLogger(Logger logger) {
		Log.logger = logger;
	}

	/**
	 * Log the provided message using logger level {@link Level#INFO}.
	 * @param message the message to log
	 * @see Logger#info(String)
	 */
	public static void info(String message) {
		logger.info(message);
	}

	/**
	 * Log the provided message and throwable using logger level {@link Level#WARNING}.
	 * @param message the message to log
	 * @param throwable the throwable to log
	 */
	public static void warning(String message, Throwable throwable) {
		logger.log(Level.WARNING, message, throwable);
	}

	/**
	 * Log the provided throwable using logger level {@link Level#WARNING}.
	 * @param throwable the throwable to log
	 */
	public static void warning(Throwable throwable) {
		logger.log(Level.WARNING, "", throwable);
	}

	/**
	 * Log the provided message using logger level {@link Level#WARNING}.
	 * @param message the message to log
	 * @see Logger#warning(String)
	 */
	public static void warning(String message) {
		logger.warning(message);
	}

	/**
	 * Log the provided message and throwable using logger level {@link Level#SEVERE}.
	 * @param message the message to log
	 * @param throwable the throwable to log
	 */
	public static void severe(String message, Throwable throwable) {
		logger.log(Level.SEVERE, message, throwable);
	}

	/**
	 * Log the provided throwable using logger level {@link Level#SEVERE}.
	 * @param throwable the throwable to log
	 */
	public static void severe(Throwable throwable) {
		logger.log(Level.SEVERE, "", throwable);
	}

	/**
	 * Log the provided message using logger level {@link Level#SEVERE}.
	 * @param message the message to log
	 * @see Logger#severe(String)
	 */
	public static void severe(String message) {
		logger.severe(message);
	}

	/**
	 * Log the provided message and throwable using logger level {@link Level#INFO}, if the debug mode is enabled.
	 * @param message the message to log
	 * @param throwable the throwable to log
	 * @see #isDebugEnabled()
	 * @see #setDebug(boolean)
	 */
	public static void debug(String message, Throwable throwable) {
		if (!logDebug.get()) return;
		logger.log(Level.INFO, message, throwable);
	}

	/**
	 * Log the provided throwable using logger level {@link Level#INFO}, if the debug mode is enabled.
	 * @param throwable the throwable to log
	 * @see #isDebugEnabled()
	 * @see #setDebug(boolean)
	 */
	public static void debug(Throwable throwable) {
		if (!logDebug.get()) return;
		logger.log(Level.INFO, "", throwable);
	}

	/**
	 * Log the provided message using logger level {@link Level#INFO}, if the debug mode is enabled.
	 * @param message the message to log
	 * @see #isDebugEnabled()
	 * @see #setDebug(boolean)
	 * @see Logger#info(String)
	 */
	public static void debug(String message) {
		if (!logDebug.get()) return;
		logger.info(message);
	}

}
