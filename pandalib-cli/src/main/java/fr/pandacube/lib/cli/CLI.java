package fr.pandacube.lib.cli;

import java.io.IOException;
import java.util.logging.Logger;

import fr.pandacube.lib.cli.commands.CLIBrigadierDispatcher;
import fr.pandacube.lib.cli.log.CLILogger;
import jline.console.ConsoleReader;
import org.fusesource.jansi.AnsiConsole;

import fr.pandacube.lib.util.log.Log;

/**
 * Class to handle general standard IO operation for a CLI application. It uses Jline’s {@link ConsoleReader} for the
 * console rendering, a JUL {@link Logger} for logging, and Brigadier to handle commands.
 */
public class CLI extends Thread {
	
	private final ConsoleReader reader;
	private final Logger logger;


	/**
	 * Create a new instance of {@link CLI}.
	 * @throws IOException if an IO error occurs.
	 */
	public CLI() throws IOException {
		super("Console Thread");
		setDaemon(true);

        AnsiConsole.systemInstall();
		reader = new ConsoleReader();
		reader.setPrompt(">");
		reader.addCompleter(CLIBrigadierDispatcher.instance);

		// configure logger's formatter
		System.setProperty("net.md_5.bungee.log-date-format", "yyyy-MM-dd HH:mm:ss");
		logger = CLILogger.getLogger(this);
	}


	/**
	 * Gets the Jline {@link ConsoleReader} of this CLI instance.
	 * @return the Jline {@link ConsoleReader} of this CLI instance.
	 */
	public ConsoleReader getConsoleReader() {
		return reader;
	}

	/**
	 * Gets the {@link Logger} of this CLI instance.
	 * @return the {@link Logger} of this CLI instance.
	 */
	public Logger getLogger() {
		return logger;
	}


	/**
	 * Runs the main loop of the console interface. This method will not return until the input stream is closed.
	 */
	@Override
	public void run() {
		
		int i = 0;
		String line;
		try {
			while((line = reader.readLine()) != null) {
				if (line.trim().isEmpty())
					continue;
				String cmdLine = line;
				new Thread(() -> CLIBrigadierDispatcher.instance.execute(cmdLine), "CLICmdThread #"+(i++)).start();
			}
		} catch (IOException e) {
			Log.severe(e);
		}
		
	}
	
	
	
}
