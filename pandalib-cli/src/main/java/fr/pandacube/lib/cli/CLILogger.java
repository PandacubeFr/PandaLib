package fr.pandacube.lib.cli;

import java.io.PrintStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.md_5.bungee.log.ColouredWriter;
import net.md_5.bungee.log.ConciseFormatter;
import net.md_5.bungee.log.LoggingOutputStream;

public class CLILogger {

	private static Logger logger = null;
	
	/* package */ static synchronized Logger getLogger(CLI cli) {
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
			
		}
		return logger;
	}
	
}
