package fr.pandacube.lib.core.config;

import fr.pandacube.lib.chat.ChatColorUtil;
import fr.pandacube.lib.util.log.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that loads a specific config file or directory.
 */
public abstract class AbstractConfig {
	
	/**
	 * The {@link File} corresponding to this config file or directory.
	 */
	protected final File configFile;
	
	/**
	 * Creates a new {@link AbstractConfig}.
	 * @param configDir the parent directory
	 * @param fileOrDirName The name of the config file or folder
	 * @param type if the provided name is a file or a directory
	 * @throws IOException if we cannot create the file or directory.
	 */
	protected AbstractConfig(File configDir, String fileOrDirName, FileType type) throws IOException {
		configFile = new File(configDir, fileOrDirName);
		if (type == FileType.DIR)
			configFile.mkdir();
		else
			configFile.createNewFile();
	}
	
	/**
	 * Gets the lines from the provided file.
	 * @param ignoreEmpty {@code true} if we ignore the empty lines.
	 * @param ignoreHashtagComment {@code true} if we ignore the comment lines (starting with {@code #}).
	 * @param trimOutput {@code true} if we want to trim all lines using {@link String#trim()}.
	 * @param f the file to read.
	 * @return the list of lines, filtered according to the parameters, or null if it’s not a regular file.
	 * @throws IOException if an IO error occurs.
	 */
	protected static List<String> getFileLines(boolean ignoreEmpty, boolean ignoreHashtagComment, boolean trimOutput, File f) throws IOException {
		if (!f.isFile())
			return null;
		
		BufferedReader reader = new BufferedReader(new FileReader(f));
		
		List<String> lines = new ArrayList<>();
		
		String line;
		while ((line = reader.readLine()) != null) {
			String trimmedLine = line.trim();
			
			if (ignoreEmpty && trimmedLine.equals(""))
				continue;
			
			if (ignoreHashtagComment && trimmedLine.startsWith("#"))
				continue;
			
			if (trimOutput)
				lines.add(trimmedLine);
			else
				lines.add(line);
		}
		
		
		reader.close();
		
		return lines;
	}


	/**
	 * Gets the lines from the config file.
	 * @param ignoreEmpty {@code true} if we ignore the empty lines.
	 * @param ignoreHashtagComment {@code true} if we ignore the comment lines (starting with {@code #}).
	 * @param trimOutput {@code true} if we want to trim all lines using {@link String#trim()}.
	 * @return the list of lines, filtered according to the parameters, or null if it’s not a regular file.
	 * @throws IOException if an IO error occurs.
	 */
	protected List<String> getFileLines(boolean ignoreEmpty, boolean ignoreHashtagComment, boolean trimOutput) throws IOException {
		return getFileLines(ignoreEmpty, ignoreHashtagComment, trimOutput, configFile);
	}


	/**
	 * Gets the list of files in the config directory.
	 * @return the list of files in the config directory, or null if this config is not a directory.
	 */
	protected List<File> getFileList() {
		File[] arr = configFile.listFiles();
		return arr != null ? List.of(arr) : null;
	}
	
	
	

	
	/**
	 * Splits the provided string into a list of permission nodes.
	 * The permission nodes must be separated by {@code ";"}.
	 * @param perms one or more permissions nodes, separated by {@code ";"}.
	 * @return {@code null} if the parameter is null or is equal to {@code "*"}, or the string split using {@code ";"}.
	 */
	public static List<String> splitPermissionsString(String perms) {
		if (perms == null || perms.equals("*"))
			return null;
		return List.of(perms.split(";"));
	}


	/**
	 * Utility method to that translate the {@code '&'} formatted string to the legacy format.
	 * @param string the string to convert.
	 * @return a legacy formatted string (using {@code '§'}).
	 */
	public static String getTranslatedColorCode(String string) {
		return ChatColorUtil.translateAlternateColorCodes('&', string);
	}


	/**
	 * Logs the message as a warning into console, prefixed with the context of this config.
	 * @param message the message to log.
	 */
	protected void warning(String message) {
		Log.warning("Error in configuration '"+configFile.getName()+"': " + message);
	}


	/**
	 * The type of config.
	 */
	protected enum FileType {
		/** A config file. */
		FILE,
		/** A config directory. */
		DIR
	}
	
}
