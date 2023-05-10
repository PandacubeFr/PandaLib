package fr.pandacube.lib.bungee.util;

import fr.pandacube.lib.util.logs.DailyLogRotateFileHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.log.ConciseFormatter;

import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * A log rotate that extends the functionnalities of {@link DailyLogRotateFileHandler}
 * to adapt with bungee specificities.
 */
public class BungeeDailyLogRotateFileHandler extends DailyLogRotateFileHandler {

    /**
     * Initialize this file handler.
     * @param hideInitialHandlerLogEntries true if we want to hide some InitialHandler log entries
     */
    public static void init(boolean hideInitialHandlerLogEntries) {
        ProxyServer.getInstance().getLogger().addHandler(new BungeeDailyLogRotateFileHandler(hideInitialHandlerLogEntries));
    }

    private BungeeDailyLogRotateFileHandler(boolean hideInitialHandlerLogEntries) {
        if (hideInitialHandlerLogEntries)
            setFilter(new InitialHandlerLogRemover());
        setFormatter(new ConciseFormatter(false));
        setLevel(Level.parse(System.getProperty("net.md_5.bungee.file-log-level", "INFO")));
    }


    private class InitialHandlerLogRemover implements Filter {

        @Override
        public boolean isLoggable(LogRecord record) {
            String formattedRecord = getFormatter().format(record);
            if (formattedRecord.contains("<-> InitialHandler has connected")) return false;
            if (formattedRecord.contains("<-> InitialHandler has pinged")) return false;
            return true;
        }

    }
}
