package fr.pandacube.java.util.db.sql_tools;

public class ORMInitTableException extends ORMException {
	private static final long serialVersionUID = 1L;

	/* package */ <E extends SQLElement<E>> ORMInitTableException(Class<E> tableElem) {
		super("Error while initializing table " + ((tableElem != null) ? tableElem.getName() : "null"));
	}

	/* package */ <E extends SQLElement<E>> ORMInitTableException(Class<E> tableElem, Throwable t) {
		super("Error while initializing table " + ((tableElem != null) ? tableElem.getName() : "null"), t);
	}

}
