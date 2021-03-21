package fr.pandacube.lib.core.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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
		if (source.isDirectory()) {
			target.mkdir();
			for (String child : source.list())
				copy(new File(source, child), new File(target, child));
		}
		else if (source.isFile()) {
			Files.copy(source.toPath(), target.toPath());
		}
	}
}
