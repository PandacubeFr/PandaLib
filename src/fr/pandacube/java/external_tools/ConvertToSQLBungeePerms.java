package fr.pandacube.java.external_tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConvertToSQLBungeePerms {
	public static void main(String[] ç) throws Exception {
		
		List<String> content = getFileLines(true, false, true, new File("convertToBungeePerms.txt"));
		FileOutputStream output = new FileOutputStream(new File("output.sql"));
		
		
		String currentSQLFormat = null;
		
		
		for (String line : content) {
			
			if (line.startsWith("#sql:"))
				currentSQLFormat = line.substring("#sql:".length());
			else
				output.write(currentSQLFormat.replace("%%%perm%%%", line).concat("\n").getBytes());
		}
		
		output.flush();
		output.close();
		
		
		
		
	}
	
	
	
	
	
	
	

	/**
	 * Retourne toutes les lignes d'un fichier donné
	 * @param ignoreEmpty <code>true</code> si on doit ignorer les lignes vides
	 * @param ignoreHashtagComment <code>true</code> si on doit ignorer les lignes commentés (commençant par un #)
	 * @param trimOutput <code>true</code> si on doit appeller la méthode String.trim() sur chaque ligne retournée
	 * @param f le fichier à lire
	 * @return la liste des lignes utiles
	 * @throws IOException
	 */
	protected static List<String> getFileLines(boolean ignoreEmpty, boolean ignoreHashtagComment, boolean trimOutput, File f) throws IOException {
		if (!f.isFile())
			return null;
		
		BufferedReader reader = new BufferedReader(new FileReader(f));
		
		List<String> lines = new ArrayList<String>();
		
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
	
	
}
