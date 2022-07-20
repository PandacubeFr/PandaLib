package fr.pandacube.lib.cli;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

class DailyLogRotateFileHandler extends Handler {
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private BufferedWriter currentFile = null;
	private String currentFileDate = getCurrentDay();
	private boolean closed = false;


	@Override
	public synchronized void close() throws SecurityException {
		closed = true;
		if (currentFile != null) try {
			currentFile.close();
		} catch (IOException ignored) {}
	}

	@Override
	public synchronized void flush() {
		if (closed) return;
		if (currentFile == null) return;
		try {
			currentFile.flush();
		} catch (IOException ignored) {}
	}

	@Override
	public synchronized void publish(LogRecord record) {
		if (closed) return;
		if (!isLoggable(record)) return;

		if (currentFile == null || !currentFileDate.equals(getCurrentDay())) changeFile();

		if (currentFile == null) return;

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
			} catch (IOException ignored) {}
			new File("logs/latest.log").renameTo(new File("logs/" + currentFileDate + ".log"));
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
}