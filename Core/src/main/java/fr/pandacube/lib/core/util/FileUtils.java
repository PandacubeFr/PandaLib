package fr.pandacube.lib.core.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

public class FileUtils {
	
	
	
	public static void delete(File target) {
		if (target.isDirectory())
			for (File child : target.listFiles())
				delete(child);
		target.delete();
	}
	
	
	public static void copy(File source, File target) throws IOException {
		if (target.exists() && !target.isDirectory()) {
			throw new IllegalStateException("target file already exists: " + target);
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
