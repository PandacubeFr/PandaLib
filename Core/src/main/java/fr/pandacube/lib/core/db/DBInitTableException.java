package fr.pandacube.lib.core.db;

public class DBInitTableException extends DBException {
	private static final long serialVersionUID = 1L;

	/* package */ <E extends SQLElement<E>> DBInitTableException(Class<E> tableElem) {
		super("Error while initializing table " + ((tableElem != null) ? tableElem.getName() : "null"));
	}

	/* package */ <E extends SQLElement<E>> DBInitTableException(Class<E> tableElem, Throwable t) {
		super("Error while initializing table " + ((tableElem != null) ? tableElem.getName() : "null"), t);
	}

	/* package */ <E extends SQLElement<E>> DBInitTableException(Class<E> tableElem, String message) {
		super("Error while initializing table " + ((tableElem != null) ? tableElem.getName() : "null") + ": " + message);
	}

}
