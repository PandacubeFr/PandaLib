package fr.pandacube.lib.db;

public class DBInitTableException extends DBException {

	/* package */ <E extends SQLElement<E>> DBInitTableException(Class<E> tableElem, Throwable t) {
		super("Error while initializing table " + ((tableElem != null) ? tableElem.getName() : "null"), t);
	}

	/* package */ <E extends SQLElement<E>> DBInitTableException(Class<E> tableElem, String message) {
		super("Error while initializing table " + ((tableElem != null) ? tableElem.getName() : "null") + ": " + message);
	}

}
