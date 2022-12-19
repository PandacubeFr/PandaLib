package fr.pandacube.lib.core.backup;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import fr.pandacube.lib.chat.Chat;
import fr.pandacube.lib.util.MemoryUtil;
import fr.pandacube.lib.util.TimeUtil;

/**
 * Handles the creation of a zip file that will have the content of a provided folder.
 */
public class ZipCompressor {
	private static final int BUFFER_SIZE = 16 * 1024;



	private final File srcDir, destFile;
	private final int compressionLevel;
	private final BiPredicate<File, String> filter;
	
	private final List<Entry> entriesToCompress;
	private ZipOutputStream zipOutStream;

	private final Object stateLock = new Object();
	private final long inputByteSize;
	private long startTime;
	private long elapsedByte = 0;
	private Exception exception = null;
	private boolean started = false;
	private boolean finished = false;

	/**
	 * Creates a new zip compressor.
	 * @param s the source directory.
	 * @param d the destination file.
	 * @param c the compression level, used in {@link ZipOutputStream#setLevel(int)} .
	 * @param f a filter that returns true for the files to include in the zip file, false to exclude.
	 */
	public ZipCompressor(File s, File d, int c, BiPredicate<File, String> f) {
		srcDir = s;
		destFile = d;
		compressionLevel = c;
		filter = f;
		
		entriesToCompress = new ArrayList<>();
		inputByteSize = addEntry("");
		
	}

	/**
	 * Returns a displayable representation of the running compression.
	 * @return a displayable representation of the running compression.
	 */
	public Chat getState() {
		synchronized (stateLock) {
			if (!started) {
				return Chat.text("Démarrage...");
			}
			else if (!finished && exception == null) {
				float progress = getProgress();
				long elapsedTime = System.nanoTime() - startTime;
				long remainingTime = (long)(elapsedTime / progress) - elapsedTime;
				return Chat.chat()
						.infoColor()
						.thenData(Math.round(progress*100*10)/10 + "% ")
						.thenText("(")
						.thenData(MemoryUtil.humanReadableSize(elapsedByte) + "/" + MemoryUtil.humanReadableSize(inputByteSize))
						.thenText(") - Temps restant estimé : ")
						.thenData(TimeUtil.durationToString(remainingTime / 1000000));
			}
			else if (exception != null) {
				return Chat.failureText("Erreur lors de l'archivage (voir console pour les détails)");
			}
			else { // finished
				return Chat.successText("Terminé !");
			}
		}
	}

	/**
	 * Gets the progress of the running compression.
	 * @return the progress of the running compression 0 when it starts and 1 when it finishes.
	 */
	public float getProgress() {
		if (!started)
			return 0;
		if (finished)
			return 1;
		return elapsedByte / (float) inputByteSize;
	}
	
	/**
	 * Run the compression on the current thread, and returns after the end of the compression.
	 * Should be run asynchronously (not on Server Thread).
	 * @throws Exception if an error occurs during compression.
	 */
	public void compress() throws Exception {
		destFile.getParentFile().mkdirs();
		
		try(ZipOutputStream zipStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(destFile), BUFFER_SIZE))) {
			zipOutStream = zipStream;
			zipOutStream.setLevel(compressionLevel);
			
			synchronized (stateLock) {
				startTime = System.nanoTime();
				started = true;
			}
			
			for (Entry entry : entriesToCompress) {
				entry.zip();
			}
			
			synchronized (stateLock) {
				finished = true;
			}
		} catch (Exception e) {
			synchronized (stateLock) {
				exception = e;
			}
			throw e;
		}
	}

	
	private long addEntry(String currentEntry) {
		final File currentFile = new File(srcDir, currentEntry);
		if (!currentFile.exists())
			return 0;
		if (currentFile.isDirectory()) {
			if (!currentEntry.isEmpty()) { // it's not the zip root directory
				currentEntry += "/";
				entriesToCompress.add(new Entry(currentFile, currentEntry));
			}
			
			long sum = 0;
			for (String child : currentFile.list()) {
				String childEntry = currentEntry + child;
				if (filter.test(new File(currentFile, child), childEntry))
					sum += addEntry(childEntry);
			}
			
			return sum;
		}
		else { // is a file
			entriesToCompress.add(new Entry(currentFile, currentEntry));
			return currentFile.length();
		}
	}
	
	private class Entry {
		File file;
		String entry;
		Entry(File f, String e) {
			file = f;
			entry = e;
		}
		
		void zip() throws IOException {
			ZipEntry zipEntry = new ZipEntry(entry);
			BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
			if (attributes.isDirectory()) {
				zipOutStream.putNextEntry(zipEntry);
				zipOutStream.closeEntry();
			}
			else {

				zipEntry.setTime(attributes.lastModifiedTime().toMillis());
				zipOutStream.putNextEntry(zipEntry);
				
				try {
					Files.copy(file.toPath(), zipOutStream);
				}
				finally {
					zipOutStream.closeEntry();
				}
				
				synchronized (stateLock) {
					elapsedByte += attributes.size();
				}
			}
		}
		
	}
}
