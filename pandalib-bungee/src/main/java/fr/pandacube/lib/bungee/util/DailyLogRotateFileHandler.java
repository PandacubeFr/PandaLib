package fr.pandacube.lib.bungee.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadFactory;
import java.util.logging.ErrorManager;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.zip.GZIPOutputStream;

import com.google.common.io.Files;
import fr.pandacube.lib.bungee.PandaLibBungee;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.log.ConciseFormatter;

public class DailyLogRotateFileHandler extends Handler {

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


    public static void init(boolean hideInitialHandlerLogEntries) {
        ProxyServer.getInstance().getLogger().addHandler(new DailyLogRotateFileHandler(hideInitialHandlerLogEntries));
    }


    private BufferedWriter currentFile = null;
    private String currentFileDate = getCurrentDay();
    private boolean closed = false;

    private DailyLogRotateFileHandler(boolean hideInitialHandlerLogEntries) {
        if (hideInitialHandlerLogEntries)
            setFilter(new InitialHandlerLogRemover());
        setFormatter(new ConciseFormatter(false));
        setLevel(Level.parse(System.getProperty("net.md_5.bungee.file-log-level", "INFO")));
    }

    @Override
    public synchronized void close() throws SecurityException {
        closed = true;
        if (currentFile != null) try {
            currentFile.close();
        } catch (IOException ignored) {
        }
    }

    @Override
    public synchronized void flush() {
        if (closed) return;
        if (currentFile == null) return;
        try {
            currentFile.flush();
        } catch (IOException ignored) {
        }
    }

    @Override
    public synchronized void publish(LogRecord record) {
        if (closed || !isLoggable(record))
            return;

        if (currentFile == null || !currentFileDate.equals(getCurrentDay()))
            changeFile();

        if (currentFile == null)
            return;

        String formattedMessage;

        try {
            formattedMessage = getFormatter().format(record);
        } catch (Exception ex) {
            reportError(null, ex, ErrorManager.FORMAT_FAILURE);
            return;
        }

        try {
            currentFile.write(formattedMessage);
            currentFile.flush();
        } catch (Exception ex) {
            reportError(null, ex, ErrorManager.WRITE_FAILURE);
        }
    }

    private void changeFile() {
        if (currentFile != null) {
            try {
                currentFile.flush();
                currentFile.close();
            } catch (IOException ignored) {
            }
            File fileNewName = new File("logs/" + currentFileDate + ".log");
            new File("logs/latest.log").renameTo(fileNewName);
            ProxyServer.getInstance().getScheduler().runAsync(PandaLibBungee.getPlugin(), () -> compress(fileNewName));
        }

        currentFileDate = getCurrentDay();
        try {
            File logDir = new File("logs");
            logDir.mkdir();
            currentFile = new BufferedWriter(new FileWriter("logs/latest.log", true));
        } catch (SecurityException | IOException e) {
            reportError("Erreur lors de l'initialisation d'un fichier log", e, ErrorManager.OPEN_FAILURE);
            currentFile = null;
        }
    }

    private String getCurrentDay() {
        return dateFormat.format(new Date());
    }


    private void compress(File sourceFile) {
        File destFile = new File(sourceFile.getParentFile(), sourceFile.getName() + ".gz");
        if (destFile.exists())
            destFile.delete();
        try (GZIPOutputStream os = new GZIPOutputStream(new FileOutputStream(destFile))) {
            Files.copy(sourceFile, os);
        } catch (IOException e) {
            if (destFile.exists())
                destFile.delete();
            throw new RuntimeException(e);
        }
        sourceFile.delete();

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
