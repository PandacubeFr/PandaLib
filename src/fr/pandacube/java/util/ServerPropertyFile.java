package fr.pandacube.java.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class ServerPropertyFile {
	
	private File file;
	
	private Map<String, Object> data;
	
	
	public ServerPropertyFile(File f) {
		if (f == null) throw new IllegalArgumentException("f ne doit pas être null");
		file = f;
		
		data = new HashMap<String, Object>();
		data.put("name", "default_name");
		data.put("memory", "512M");
		data.put("javaArgs", "");
		data.put("MinecraftArgs", "");
		data.put("jarFile", "");
		data.put("isLobby", false);
	}
	
	
	/**
	 * Charge le fichier de configuration dans cette instance de la classe
	 * @return true si le chargement a réussi, false sinon
	 */
	public boolean loadFromFile() {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(file));
			
			
			@SuppressWarnings("unchecked")
			Map<String, Object> dataFile = new Gson().fromJson(in, Map.class);

			if (!dataFile.containsKey("name") || !(dataFile.get("name") instanceof String))
				return false;

			if (!dataFile.containsKey("memory") || !(dataFile.get("memory") instanceof String))
				return false;

			if (!dataFile.containsKey("javaArgs") || !(dataFile.get("javaArgs") instanceof String))
				return false;

			if (!dataFile.containsKey("MinecraftArgs") || !(dataFile.get("MinecraftArgs") instanceof String))
				return false;

			if (!dataFile.containsKey("jarFile") || !(dataFile.get("jarFile") instanceof String))
				return false;

			if (!dataFile.containsKey("isLobby") || !(dataFile.get("isLobby") instanceof Boolean))
				return false;
			
			data = dataFile;
			
			return true;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				in.close();
			} catch (Exception e) { }
		}
		return false;
	}
	
	
	
	public boolean save() {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(file,false));
			
			String jsonStr = new Gson().toJson(data);
			
			out.append(jsonStr);
			
			out.flush();
			
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} 
		finally {
			try {
				out.close();
			} catch (Exception e) { }
		}
		
		return false;
	}
	
	
	
	

	public String getName() {
		return (String) data.get("name");
	}
	public String getMemory() {
		return (String) data.get("memory");
	}
	public String getJavaArgs() {
		return (String) data.get("javaArgs");
	}
	public String getMinecraftArgs() {
		return (String) data.get("MinecraftArgs");
	}
	public String getJarFile() {
		return (String) data.get("jarFile");
	}
	public boolean getIsLobby() {
		return (boolean) data.get("isLobby");
	}
	
	
	
	
	

	public void setName(String n) {
		if (n == null || !n.matches("^[a-zA-Z]$"))
			throw new IllegalArgumentException();
		data.put("name", n);
	}
	public void setMemory(String m) {
		if (m == null || !m.matches("^[0-9]+[mgMG]$"))
			throw new IllegalArgumentException();
		data.put("memory", m);
	}
	public void setJavaArgs(String ja) {
		if (ja == null)
			throw new IllegalArgumentException();
		data.put("javaArgs", ja);
	}
	public void setMinecraftArgs(String ma) {
		if (ma == null)
			throw new IllegalArgumentException();
		data.put("MinecraftArgs", ma);
	}
	
	public void setJarFile(String j) {
		if (j == null)
			throw new IllegalArgumentException();
		data.put("jarFile", j);
	}
	
	public void setIsLobby(boolean l) {
		data.put("isLobby", l);
	}
	
	
}
