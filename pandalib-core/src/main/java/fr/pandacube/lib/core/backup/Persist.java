package fr.pandacube.lib.core.backup;

import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import fr.pandacube.lib.core.json.Json;
import fr.pandacube.lib.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Persist {
	protected final BackupManager backupManager;

	private Map<String, Long> dirtySince = new HashMap<>();

	private final File file;
	
	// private final Set<String> dirtyWorldsSave = new HashSet<>();
	
	public Persist(BackupManager bm) {
		backupManager = bm;
		file = new File(bm.getBackupDirectory(), "source-dirty-since.yml");
		load();
	}

	public void reload() {
		load();
	}

	protected void load() {
		boolean loaded = false;
		try {
			dirtySince = Json.gson.fromJson(new FileReader(file), new TypeToken<Map<String, Long>>(){}.getType());
			loaded = true;
		}
		catch (final FileNotFoundException ignored) { }
		catch (final JsonParseException e) {
			Log.severe("cannot load " + file, e);
		}

		if (!loaded) {
			save();
		}
	}
	
	public void save() {
		try {
			Json.gsonPrettyPrinting.toJson(dirtySince, new FileWriter(file, false));
		}
		catch (final FileNotFoundException ignored) { }
		catch (final JsonParseException | IOException e) {
			Log.severe("could not save " + file, e);
		}
	}
	
	

	
	
	public void setDirtySinceNow(String id) {
		dirtySince.put(id, System.currentTimeMillis());
		save();
	}

	public void setNotDirty(String id) {
		dirtySince.put(id, -1L);
		save();
	}
	
	
	public boolean isDirty(String id) {
		return isDirtySince(id) != -1;
	}
	
	public long isDirtySince(String id) {
		if (!dirtySince.containsKey(id))
			setDirtySinceNow(id);
		return dirtySince.get(id);
	}
	
	
	
	
}