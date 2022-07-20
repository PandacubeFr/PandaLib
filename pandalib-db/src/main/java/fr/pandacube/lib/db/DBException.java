package fr.pandacube.lib.db;

public class DBException extends Exception {

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
