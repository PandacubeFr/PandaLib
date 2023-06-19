package fr.pandacube.lib.db;

/**
 * Exception thrown when something bad happens when initializing a new table using {@link DB#initTable(Class)}.
 */
public class DBInitTableException extends DBException {

    /* package */ <E extends SQLElement<E>> DBInitTableException(Class<E> tableElem, Throwable t) {
        super("Error while initializing table " + ((tableElem != null) ? tableElem.getName() : "null"), t);
    }

    /* package */ <E extends SQLElement<E>> DBInitTableException(Class<E> tableElem, String message) {
        super("Error while initializing table " + ((tableElem != null) ? tableElem.getName() : "null") + ": " + message);
    }

}
