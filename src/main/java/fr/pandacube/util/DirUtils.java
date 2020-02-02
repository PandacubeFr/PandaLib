package fr.pandacube.util;

import java.io.File;

public class DirUtils {
	public static void delete(final File target) {
		if (target.isDirectory())
			for (final File child : target.listFiles())
				delete(child);
		
		target.delete();
	}
}
