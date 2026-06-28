package fr.pandacube.lib.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Provides utility methods to manipulate files and directories
 */
public class FileUtil {


	/**
	 * Recursively delete the provided file and all of its content if it is a directory.
	 * @param target the target file or directory.
	 */
	public static void delete(File target) {
		delete(target, null);
	}



	/**
	 * Recursively delete the provided file and all of its content if it is a directory.
	 * @param target the target file or directory.
	 * @param excludedChildren the list of path to exclude from the deletion, all relative to the target. Wildcards are
	 *                         not supported, but a full subdirectory can be excluded from deletion.
	 * @return true if everything has been deleted, false if either a deletion failed or an existing file was excluded.
	 */
	public static boolean delete(File target, Collection<String> excludedChildren) {
		return delete0(target, prepareExclusionSet(target, excludedChildren));
	}




	private static boolean delete0(File target, Set<File> excluded) {
		if (excluded.contains(target))
			return false;

		boolean completeDelete = true;
		if (target.isDirectory()) {
			for (File child : Objects.requireNonNull(target.listFiles())) {
				if (!delete0(child, excluded))
					completeDelete = false;
			}
		}

		if (completeDelete)
			completeDelete = target.delete();

		return completeDelete;
	}




	/**
	 * Recursively copy the provided source file or directory to the provided target.
	 * @param source the source file or directory.
	 * @param target the copy destination.
	 * @throws IOException if an IO error occurs.
	 * @throws IllegalStateException if target file already exists and one of source or target is not a directory.
	 * @throws IllegalArgumentException if at least one of the parameter is null, or if the source doesn't exist.
	 */
	public static void copy(File source, File target) throws IOException {
		copy(source, target, null);
	}



	/**
	 * Recursively copy the provided source file or directory to the provided target.
	 * @param source the source file or directory.
	 * @param target the copy destination.
	 * @param excludedChildren the list of path to exclude from the copying, all relative to the source. Wildcards are
	 *                         not supported, but a full subdirectory can be excluded from the copy.
	 * @throws IOException if an IO error occurs.
	 * @throws IllegalStateException if target file already exists and one of source or target is not a directory.
	 * @throws IllegalArgumentException if at least one of the parameter is null, or if the source doesn't exist.
	 */
	public static void copy(File source, File target, Collection<String> excludedChildren) throws IOException {
		if (source == null || !source.exists()) {
			throw new IllegalArgumentException("source is null or doesn't exist: " + source);
		}
		if (target == null) {
			throw new IllegalArgumentException("target cannot be null");
		}
		if (target.exists() && !(target.isDirectory() && source.isDirectory())) {
			throw new IllegalStateException("target file already exists and one of source or target is not a directory: " + target);
		}

		copy0(source, target, prepareExclusionSet(source, excludedChildren));
	}



	private static void copy0(File source, File target, Set<File> excluded) throws IOException {

		if (excluded.contains(source))
			return;

		BasicFileAttributes sourceAttr = Files.readAttributes(source.toPath(), BasicFileAttributes.class);
		if (sourceAttr.isDirectory()) {
			if (!target.exists())
				target.mkdirs();
			for (String child : Objects.requireNonNull(source.list())) {
				copy0(new File(source, child), new File(target, child), excluded);
			}
		}
		else if (sourceAttr.isRegularFile() || sourceAttr.isSymbolicLink()) {
			Files.copy(source.toPath(), target.toPath());
		}
	}





	private static Set<File> prepareExclusionSet(File exclusionRoot, Collection<String> excludedChildren) {
		return excludedChildren == null ? Set.of()
				: excludedChildren.stream()
				.map(path -> new File(exclusionRoot, path))
				.collect(Collectors.toSet());
	}




	private FileUtil() {}
}
