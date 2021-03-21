package fr.pandacube.lib.core.db;

public class DBException extends Exception {
	private static final long serialVersionUID = 1L;

	public DBException(Throwable initCause) {
		super(initCause);
	}

	public DBException(String message, Throwable initCause) {
		super(message, initCause);
	}

	public DBException(String message) {
		super(message);
	}

}
