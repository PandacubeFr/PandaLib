package fr.pandacube.lib.db;

/**
 * Exception thrown when something bad happends when using the {@link DB} API.
 */
public class DBException extends Exception {

    /* package */ DBException(Throwable initCause) {
        super(initCause);
    }

    /* package */ DBException(String message, Throwable initCause) {
        super(message, initCause);
    }

    /* package */ DBException(String message) {
        super(message);
    }

}
