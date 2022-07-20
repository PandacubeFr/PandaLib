package fr.pandacube.lib.core.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.pandacube.lib.chat.ChatColorUtil;
import fr.pandacube.lib.util.Log;

/**
 * Class tht loads a specific config file or directory
 *
 */
public abstract class AbstractConfig {
	
	/**
	 * Correspond au dossier ou au fichier de configuration traité par la sous-classe
	 * courante de {@link AbstractConfig}
	 */
	protected final File configFile;
	
	/**
	 * @param configDir the parent directory
	 * @param fileOrDirName The name of the config file or folder
	 * @param type if the provided name is a file or a directory
	 * @throws IOException if we cannot create the file
	 */
	public AbstractConfig(File configDir, String fileOrDirName, FileType type) throws IOException {
		configFile = new File(configDir, fileOrDirName);
		if (type == FileType.DIR)
			configFile.mkdir();
		else
			configFile.createNewFile();
	}
	
	/**
	 * Gets the lines from the config file
	 * @param ignoreEmpty <code>true</code> if we ignore the empty lines
	 * @param ignoreHashtagComment <code>true</code> if we ignore the comment lines (starting with {@code #})
	 * @param trimOutput <code>true</code> if we want to trim all lines using {@link String#trim()}
	 * @param f the file to read
	 * @return the list of lines, filtered according to the parameters
	 */
	protected List<String> getFileLines(boolean ignoreEmpty, boolean ignoreHashtagComment, boolean trimOutput, File f) throws IOException {
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
	 * Retourne toutes les lignes du fichier de configuration
	 * @param ignoreEmpty <code>true</code> si on doit ignorer les lignes vides
	 * @param ignoreHashtagComment <code>true</code> si on doit ignorer les lignes commentés (commençant par un #)
	 * @param trimOutput <code>true</code> si on doit appeller la méthode String.trim() sur chaque ligne retournée
	 * @return la liste des lignes utiles
	 */
	protected List<String> getFileLines(boolean ignoreEmpty, boolean ignoreHashtagComment, boolean trimOutput) throws IOException {
		return getFileLines(ignoreEmpty, ignoreHashtagComment, trimOutput, configFile);
	}
	
	
	
	protected List<File> getFileList() {
		if (!configFile.isDirectory())
			return null;
		
		return Arrays.asList(configFile.listFiles());
	}
	
	
	

	
	/**
	 * Découpe une chaine de caractère contenant une série de noeuds
	 * de permissions séparés par des point-virgules et la retourne sous forme d'une liste.
	 * @param perms la chaine de permissions à traiter
	 * @return <code>null</code> si le paramètre est nulle ou si <code>perms.equals("*")</code>, ou alors la chaine splittée.
	 */
	public static List<String> splitPermissionsString(String perms) {
		if (perms == null || perms.equals("*"))
			return null;
		return getSplittedString(perms, ";");
	}
	
	
	
	
	
	public static List<String> getSplittedString(String value, String split) {
		return List.of(value.split(split));
	}
	


	public static String getTranslatedColorCode(String string) {
		return ChatColorUtil.translateAlternateColorCodes('&', string);
	}
	
	
	
	
	protected void warning(String message) {
		Log.warning("Erreur dans la configuration de '"+configFile.getName()+"' : "+message);
	}
	
	
	
	
	protected enum FileType {
		FILE, DIR
	}
	
}
