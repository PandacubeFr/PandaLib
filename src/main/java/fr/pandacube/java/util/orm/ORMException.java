package fr.pandacube.java.util.orm;

public class ORMException extends Exception {
	private static final long serialVersionUID = 1L;

	public ORMException(Throwable initCause) {
		super(initCause);
	}

	public ORMException(String message, Throwable initCause) {
		super(message, initCause);
	}

	public ORMException(String message) {
		super(message);
	}

}
