package fr.pandacube.lib.cli;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import fr.pandacube.lib.core.util.Log;
import jline.console.ConsoleReader;
import net.md_5.bungee.log.ConciseFormatter;

public class ConsoleInterface extends Handler {
	
	
	public static final String ANSI_RESET = "\u001B[0m";
	
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_DARK_RED = "\u001B[31m";
	public static final String ANSI_DARK_GREEN = "\u001B[32m";
	public static final String ANSI_GOLD = "\u001B[33m";
	public static final String ANSI_DARK_BLUE = "\u001B[34m";
	public static final String ANSI_DARK_PURPLE = "\u001B[35m";
	public static final String ANSI_DARK_AQUA = "\u001B[36m";
	public static final String ANSI_GRAY = "\u001B[37m";
	
	public static final String ANSI_DARK_GRAY = "\u001B[30;1m";
	public static final String ANSI_RED = "\u001B[31;1m";
	public static final String ANSI_GREEN = "\u001B[32;1m";
	public static final String ANSI_YELLOW = "\u001B[33;1m";
	public static final String ANSI_BLUE = "\u001B[34;1m";
	public static final String ANSI_LIGHT_PURPLE = "\u001B[35;1m";
	public static final String ANSI_AQUA = "\u001B[36;1m";
	public static final String ANSI_WHITE = "\u001B[37;1m";
	
	public static final String ANSI_BOLD = "\u001B[1m";
	
	public static final String ANSI_CLEAR_SCREEN = "\u001B[2J\u001B[1;1H";
	
	
	
	
	private ConsoleReader reader;
	private PrintWriter out;
	
	
	
	public ConsoleInterface() throws IOException {
		reader = new ConsoleReader();
		reader.setBellEnabled(false);
		reader.setPrompt("\r"+ANSI_LIGHT_PURPLE+">");
		out = new PrintWriter(reader.getOutput());
		reader.addCompleter(BrigadierDispatcher.instance);

		// configuration du formatteur pour le logger
		System.setProperty("net.md_5.bungee.log-date-format", "yyyy-MM-dd HH:mm:ss");
		setFormatter(new ConciseFormatter(true));
	}
	
	
	
	
	public ConsoleReader getConsoleReader() {
		return reader;
	}
	
	
	
	
	public void loop() {
		
		int i = 0;
		String line;
		try {
			while((line = reader.readLine()) != null) {
				if (line.trim().equals(""))
					continue;
				String cmdLine = line;
				new Thread(() -> {
					BrigadierDispatcher.instance.execute(cmdLine);
				}, "CLICmdThread #"+(i++)).start();
				
			}
		} catch (IOException e) {
			Log.severe(e);
		}
		
		
		
		
		
		
		
	}
	
	
	
	
	private synchronized void println(String str) {
		try {
			out.println('\r'+ANSI_RESET+str);
			out.flush();
			reader.drawLine();
			reader.flush();
		} catch (IOException e) {
			Log.severe(e);
		}
	}





	@Override
	public void close() throws SecurityException { }

	@Override
	public void flush() { }

	@Override
	public void publish(LogRecord record) {
		if (!isLoggable(record))
			return;
		
		String formattedMessage;
		
		try {
			formattedMessage = getFormatter().format(record);
		} catch (Exception ex) {
			reportError(null, ex, ErrorManager.FORMAT_FAILURE);
			return;
		}

		try {
			println(formattedMessage.trim());
		} catch (Exception ex) {
			reportError(null, ex, ErrorManager.WRITE_FAILURE);
			return;
		}
	}

	
}
