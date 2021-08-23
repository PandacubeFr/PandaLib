package fr.pandacube.lib.cli;

import java.io.PrintStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import net.md_5.bungee.log.ColouredWriter;
import net.md_5.bungee.log.ConciseFormatter;
import net.md_5.bungee.log.LoggingOutputStream;

public class CLILogger extends Logger {

	
	/* package */ CLILogger(CLI cli) {
		super("CoreLogger", null);
		setLevel(Level.ALL);
		setUseParentHandlers(false);
		
		Handler cliLogHandler = new ColouredWriter(cli.getConsoleReader());
		cliLogHandler.setFormatter(new ConciseFormatter(true));
		addHandler(cliLogHandler);
		
		Handler fileHandler = new DailyLogRotateFileHandler();
		fileHandler.setLevel(Level.INFO);
		fileHandler.setFormatter(new ConciseFormatter(false));
		addHandler(fileHandler);
		
        System.setErr(new PrintStream(new LoggingOutputStream(this, Level.SEVERE), true));
        System.setOut(new PrintStream(new LoggingOutputStream(this, Level.INFO), true));
	}
	
	@Override
	public void log(LogRecord record) {
		record.setLongThreadID(Thread.currentThread().getId());
		
		super.log(record);
	}
	

}
