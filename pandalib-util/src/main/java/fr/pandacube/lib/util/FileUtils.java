package fr.pandacube.lib.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Provides utility methods to manipulate files and directories
 */
public class FileUtils {


	/**
	 * Recursively delete the provided file and all of its content if it is a directory.
	 * @param target the target file or directory.
	 */
	public static void delete(File target) {
		if (target.isDirectory())
			for (File child : target.listFiles())
				delete(child);
		target.delete();
	}

	/**
	 * Recursively copy the provided source file or directory to the provided target.
	 * @param source the source file or directory.
	 * @param target the copy destination.
	 * @throws IOException if an IO error occurs.
	 * @throws IllegalStateException if the target destination already exists and is not a directory.
	 * @throws IllegalArgumentException if at least one of the parameter is null, or if the source doesn't exist.
	 */
	public static void copy(File source, File target) throws IOException {
		if (source == null || !source.exists() || !source.isDirectory()) {
			throw new IllegalArgumentException("source is null or doesn't exist: " + source);
		}
		if (target == null) {
			throw new IllegalArgumentException("target cannot be null");
		}
		if (target.exists() && !target.isDirectory()) {
			throw new IllegalStateException("target file already exists but is not a directory: " + target);
		}
		BasicFileAttributes sourceAttr = Files.readAttributes(source.toPath(), BasicFileAttributes.class);
		if (sourceAttr.isDirectory()) {
			if (target.mkdir()) {
				for (String child : source.list())
					copy(new File(source, child), new File(target, child));
			}
			else {
				throw new IOException("Cannot create directory " + target);
			}
		}
		else if (sourceAttr.isRegularFile()) {
			Files.copy(source.toPath(), target.toPath());
		}
	}
}
