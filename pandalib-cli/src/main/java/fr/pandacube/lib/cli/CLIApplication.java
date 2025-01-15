package fr.pandacube.lib.cli;

import fr.pandacube.lib.cli.commands.CommandAdmin;
import fr.pandacube.lib.cli.commands.CommandStop;
import fr.pandacube.lib.cli.log.CLILogger;
import fr.pandacube.lib.util.log.Log;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * Main class of a CLI application.
 */
public abstract class CLIApplication {

    private static CLIApplication instance;

    /**
     * Returns the current application instance.
     * @return the current application instance.
     */
    public static CLIApplication getInstance() {
        return instance;
    }


    /**
     * The instance of {@link CLI} for this application.
     */
    public final CLI cli;

    /**
     * Creates a new application instance.
     */
    @SuppressWarnings("CallToPrintStackTrace")
    protected CLIApplication() {
        instance = this;
        CLI tmpCLI = null;
        try {
            tmpCLI = new CLI();
            Log.setLogger(tmpCLI.getLogger());
        } catch (Throwable t) {
            System.err.println("Unable to start application " + getName() + " version " + getClass().getPackage().getImplementationVersion());
            t.printStackTrace();
            System.exit(1);
        }
        cli = tmpCLI;

        try {
            Log.info("Starting " + getName() + " version " + getClass().getPackage().getImplementationVersion());

            start();

            new CommandAdmin();
            new CommandStop();

            Runtime.getRuntime().addShutdownHook(shutdownThread);

            cli.start(); // actually starts the CLI thread

            Log.info("Application started.");
        } catch (Throwable t) {
            Log.severe("Unable to start application " + getName() + " version " + getClass().getPackage().getImplementationVersion(), t);
        }
    }

    /**
     * Returns the application's {@link Logger}.
     * @return the application's {@link Logger}.
     */
    public Logger getLogger() {
        return cli.getLogger();
    }


    private final Thread shutdownThread = new Thread(this::stop);

    private final AtomicBoolean stopping = new AtomicBoolean(false);

    /**
     * Stops this application.
     */
    public final void stop() {
        synchronized (stopping) {
            if (stopping.get())
                return;
            stopping.set(true);
        }
        Log.info("Stopping " + getName() + " version " + getClass().getPackage().getImplementationVersion());
        try {
            end();
        } catch (Throwable t) {
            Log.severe("Error stopping application " + getName() + " version " + getClass().getPackage().getImplementationVersion(), t);
        } finally {
            Log.info("Bye bye.");

            CLILogger.actuallyResetLogManager();
            if (!Thread.currentThread().equals(shutdownThread))
                System.exit(0);
        }
    }

    /**
     * Tells if this application is currently stopping, that is the {@link #stop()} method has been called.
     * @return true if the application is stopping, false otherwise.
     */
    public boolean isStopping() {
        return stopping.get();
    }


    /**
     * Gets the name of this application.
     * @return the name of this application.
     */
    public abstract String getName();

    /**
     * Method to override to initialize stuff in this application.
     * This method is called on instanciation of this Application.
     * @throws Exception If an exception is thrown, the application will not start.
     */
    protected abstract void start() throws Exception;

    /**
     * Method to override to reload specific stuff in this application.
     * This method is called by using the command {@code admin reload}.
     */
    public abstract void reload();

    /**
     * Method to override to execute stuff when this application stops.
     * This method is called once before this application terminates, possibly from a shutdown hook Thread.
     */
    protected abstract void end();


}
