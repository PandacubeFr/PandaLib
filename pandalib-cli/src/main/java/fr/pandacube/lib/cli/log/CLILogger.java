package fr.pandacube.lib.cli.log;

import fr.pandacube.lib.cli.CLI;
import fr.pandacube.lib.util.Log;
import fr.pandacube.lib.util.ThrowableUtil;
import fr.pandacube.lib.util.logs.DailyLogRotateFileHandler;
import net.md_5.bungee.log.ColouredWriter;
import net.md_5.bungee.log.ConciseFormatter;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Initializer for the logging system of a CLI application.
 */
public class CLILogger {

	private static Logger logger = null;

	/**
	 * Initialize and return the logger for this application.
	 * @param cli the CLI instance to use
	 * @return the logger for this application.
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
			
	        System.setErr(newRedirector(logger, Level.SEVERE));
	        System.setOut(newRedirector(logger, Level.INFO));

			Log.setLogger(logger);
		}
		return logger;
	}




	private static PrintStream newRedirector(Logger logger, Level level) {
		PipedOutputStream pos = new PipedOutputStream();
		PrintStream ps = new PrintStream(pos);
		PipedInputStream pis = new PipedInputStream();
		ThrowableUtil.wrapEx(() -> pos.connect(pis));
		Scanner s = new Scanner(pis);

		Thread t = new Thread(() -> {
			while(s.hasNextLine()) {
				logger.logp(level, "", "", s.nextLine());
			}
			s.close();
		}, "Logging Redirector Thread (" + level + ")");
		t.setDaemon(true);
		t.start();
		return ps;
	}
	
}
