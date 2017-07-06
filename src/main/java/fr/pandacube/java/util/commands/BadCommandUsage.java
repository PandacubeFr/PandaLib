package fr.pandacube.java.util.commands;

import java.util.logging.Logger;

/**
 * Throw an instance of this exception to indicate to the plugin command handler
 * that the user has missused the command. The message, if provided, must indicate
 * the reason of the mussusage of the command. It will be displayed on the screen
 * with eventually indication of how to use the command (help command for example).
 * If a {@link Throwable} cause is provided, it will be relayed to the plugin {@link Logger}.
 * 
 */
public class BadCommandUsage extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public BadCommandUsage() {
		super();
	}
	
	public BadCommandUsage(Throwable cause) {
		super(cause);
	}

	public BadCommandUsage(String message) {
		super(message);
	}
	
	public BadCommandUsage(String message, Throwable cause) {
		super(message, cause);
	}
}