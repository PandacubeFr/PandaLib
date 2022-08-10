package fr.pandacube.lib.cli.log;

import java.io.PrintStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.pandacube.lib.cli.CLI;
import fr.pandacube.lib.util.Log;
import net.md_5.bungee.log.ColouredWriter;
import net.md_5.bungee.log.ConciseFormatter;
import net.md_5.bungee.log.LoggingOutputStream;

/**
 * Initializer for the logging system of a CLI application.
 */
public class CLILogger {

	private static Logger logger = null;

	/**
	 * Initialize and return the logger for this application.
	 * @param cli the CLI instance to use
	 * @return the logger of this application.
	 */
	public static synchronized Logger getLogger(CLI cli) {
		if (logger == null) {
			logger = Logger.getGlobal();
			logger.setLevel(Level.ALL);
			logger.setUseParentHandlers(false);
			
			Handler cliLogHandler = new ColouredWriter(cli.getConsoleReader());
			cliLogHandler.setFormatter(new ConciseFormatter(true));
			logger.addHandler(cliLogHandler);
			
			Handler fileHandler = new DailyLogRotateFileHandler();
			fileHandler.setLevel(Level.INFO);
			fileHandler.setFormatter(new ConciseFormatter(false));
			logger.addHandler(fileHandler);
			
	        System.setErr(new PrintStream(new LoggingOutputStream(logger, Level.SEVERE), true));
	        System.setOut(new PrintStream(new LoggingOutputStream(logger, Level.INFO), true));

			Log.setLogger(logger);
		}
		return logger;
	}
	
}
