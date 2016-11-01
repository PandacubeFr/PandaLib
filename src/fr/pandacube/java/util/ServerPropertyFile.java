package fr.pandacube.java.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;

public class ServerPropertyFile {

	private transient File file;
	
	private String name = "default_name";
	private String memory = "512M";
	private String javaArgs = "";
	private String MinecraftArgs = "";
	private String jarFile = "";

	public ServerPropertyFile(File f) {
		if (f == null) throw new IllegalArgumentException("f ne doit pas être null");
		file = f;

	}

	/**
	 * Charge le fichier de configuration dans cette instance de la classe
	 *
	 * @return true si le chargement a réussi, false sinon
	 */
	public boolean loadFromFile() {
		try (BufferedReader in = new BufferedReader(new FileReader(file))) {

			ServerPropertyFile dataFile = new Gson().fromJson(in, getClass());
			
			name = dataFile.name;
			memory = dataFile.memory;
			javaArgs = dataFile.javaArgs;
			MinecraftArgs = dataFile.MinecraftArgs;
			jarFile = dataFile.jarFile;
			
			return true;
		} catch (IOException e) {
			Log.severe(e);
			return false;
		}
	}

	public boolean save() {
		try (BufferedWriter out = new BufferedWriter(new FileWriter(file, false))) {

			new Gson().toJson(this, out);
			out.flush();

			return true;
		} catch (IOException e) {
			Log.severe(e);
		}

		return false;
	}

	public String getName() {
		return name;
	}

	public String getMemory() {
		return memory;
	}

	public String getJavaArgs() {
		return javaArgs;
	}

	public String getMinecraftArgs() {
		return MinecraftArgs;
	}

	public String getJarFile() {
		return jarFile;
	}

	public void setName(String n) {
		if (n == null || !n.matches("^[a-zA-Z]$")) throw new IllegalArgumentException();
		name = n;
	}

	public void setMemory(String m) {
		if (m == null || !m.matches("^[0-9]+[mgMG]$")) throw new IllegalArgumentException();
		memory = m;
	}

	public void setJavaArgs(String ja) {
		if (ja == null) throw new IllegalArgumentException();
		javaArgs = ja;
	}

	public void setMinecraftArgs(String ma) {
		if (ma == null) throw new IllegalArgumentException();
		MinecraftArgs = ma;
	}

	public void setJarFile(String j) {
		if (j == null) throw new IllegalArgumentException();
		jarFile = j;
	}

}
