package fr.pandacube.lib.cli;

import fr.pandacube.lib.cli.commands.CommandAdmin;
import fr.pandacube.lib.cli.commands.CommandStop;
import fr.pandacube.lib.util.log.Log;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * Main class of a CLI application.
 */
public abstract class CLIApplication {

    private static CLIApplication instance;

    public static CLIApplication getInstance() {
        return instance;
    }






    public final CLI cli;

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

            Runtime.getRuntime().addShutdownHook(new Thread(this::stop));

            cli.start(); // actually starts the CLI thread

            Log.info("Application started.");
        } catch (Throwable t) {
            Log.severe("Unable to start application " + getName() + " version " + getClass().getPackage().getImplementationVersion(), t);
        }
    }

    public Logger getLogger() {
        return cli.getLogger();
    }


    private final AtomicBoolean stopping = new AtomicBoolean(false);

    @SuppressWarnings("finally")
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
            System.exit(0);
        }
    }

    public boolean isStopping() {
        return stopping.get();
    }




    public abstract String getName();

    protected abstract void start() throws Exception;

    public abstract void reload();

    protected abstract void end();


}
