package fr.pandacube.lib.cli;

import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import net.md_5.bungee.log.LoggingOutputStream;

public class CoreLogger extends Logger {

	
	public CoreLogger(ConsoleInterface cli) {
		super("CoreLogger", null);
		setLevel(Level.ALL);
		setUseParentHandlers(false);
		addHandler(cli);
        System.setErr(new PrintStream(new LoggingOutputStream(this, Level.SEVERE), true));
        System.setOut(new PrintStream(new LoggingOutputStream(this, Level.INFO), true));
	}
	
	
	@Override
	public void log(LogRecord record) {
		record.setLongThreadID(Thread.currentThread().getId());
		
		super.log(record);
	}
	

}
