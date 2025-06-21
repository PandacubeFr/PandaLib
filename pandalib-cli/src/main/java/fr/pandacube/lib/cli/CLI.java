package fr.pandacube.lib.cli;

import fr.pandacube.lib.cli.commands.CLIBrigadierDispatcher;
import fr.pandacube.lib.cli.log.CLILogger;
import fr.pandacube.lib.util.log.Log;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Class to handle general standard IO operation for a CLI application. It uses Jline’s {@link LineReader} for the
 * console rendering, a JUL {@link Logger} for logging, and Brigadier to handle commands.
 */
public class CLI extends Thread {
	
	private final LineReader reader;
	private final Logger logger;


	/**
	 * Create a new instance of {@link CLI}.
	 * @throws IOException if an IO error occurs.
	 */
	public CLI() throws IOException {
		super("Console Thread");
		setDaemon(true);

		Terminal terminal = TerminalBuilder.builder().build();
		reader = LineReaderBuilder.builder().terminal(terminal)
				.completer(CLIBrigadierDispatcher.instance)
				.build()
		;

		// configure logger's formatter
		System.setProperty("net.md_5.bungee.log-date-format", "yyyy-MM-dd HH:mm:ss");
		logger = CLILogger.getLogger(this);
	}


	/**
	 * Gets the Jline {@link LineReader} of this CLI instance.
	 * @return the Jline {@link LineReader} of this CLI instance.
	 */
	public LineReader getConsoleReader() {
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
			while((line = reader.readLine(">")) != null) {
				if (line.trim().isEmpty())
					continue;
				String cmdLine = line;
				Thread.ofVirtual().name("CLICmdThread #"+(i++))
						.start(() -> CLIBrigadierDispatcher.instance.execute(cmdLine));
			}
		} catch (UserInterruptException | EndOfFileException ignore) { }
		
	}
	
	
	
}
