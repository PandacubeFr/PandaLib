package fr.pandacube.java.util.db2.sql_tools;

public class ORMInitTableException extends ORMException {
	private static final long serialVersionUID = 1L;

	/* package */ <T extends SQLElement> ORMInitTableException(Class<T> tableElem) {
		super("Error while initializing table " + tableElem.getName());
	}

	/* package */ <T extends SQLElement> ORMInitTableException(Class<T> tableElem, Throwable t) {
		super("Error while initializing table " + tableElem.getName(), t);
	}

}
