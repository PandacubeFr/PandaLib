package fr.pandacube.lib.commands;

import java.util.logging.Logger;

/**
 * Throw an instance of this exception to indicate to the plugin command handler that the user has missused the command.
 * The message, if provided, must indicate the reason of the mussusage of the command. It will be displayed on the
 * screen with eventual indications of how to use the command (help command for example).
 * If a {@link Throwable} cause is provided, it will be relayed to the plugin {@link Logger}.
 * 
 */
public class BadCommandUsage extends RuntimeException {

	/** Constructs a new runtime exception with no message or cause.
	 */
	public BadCommandUsage() {
		super();
	}

	/** Constructs a new runtime exception with the specified cause.
	 * @param cause the cause.
	 */
	public BadCommandUsage(Throwable cause) {
		super(cause);
	}

	/** Constructs a new runtime exception with the specified message.
	 * @param message the message.
	 */
	public BadCommandUsage(String message) {
		super(message);
	}

	/** Constructs a new runtime exception with the specified message and cause.
	 * @param message the message.
	 * @param cause the cause.
	 */
	public BadCommandUsage(String message, Throwable cause) {
		super(message, cause);
	}
}