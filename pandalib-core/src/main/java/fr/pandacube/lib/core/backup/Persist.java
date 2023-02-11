package fr.pandacube.lib.core.backup;

import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import fr.pandacube.lib.core.json.Json;
import fr.pandacube.lib.util.Log;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles the data stored used for backup manager, like dirty status of data to be backed up.
 * The data is stored using JSON format, in a file in the root backup directory.
 * The file is updated on disk on every call to a {@code set*(...)} method.
 */
public class Persist {

	private Map<String, Long> dirtySince = new HashMap<>();

	private final File file;
	
	// private final Set<String> dirtyWorldsSave = new HashSet<>();

	/**
	 * Creates a new instance, immediatly loading the data from the file if it exists, or creating an empty one if not.
	 * @param bm the associated backup manager.
	 */
	public Persist(BackupManager bm) {
		file = new File(bm.getBackupDirectory(), "source-dirty-since.json");
		load();
	}


	private void load() {
		boolean loaded = false;
		try (FileReader reader = new FileReader(file)) {
			dirtySince = Json.gson.fromJson(reader, new TypeToken<Map<String, Long>>(){}.getType());
			loaded = true;
		}
		catch (final IOException ignored) { }
		catch (final JsonParseException e) {
			Log.severe("cannot load " + file, e);
		}
		finally {
			if (dirtySince == null)
				dirtySince = new HashMap<>();
		}

		if (!loaded) {
			save();
		}
	}

	private void save() {
		try (FileWriter writer = new FileWriter(file, false)) {
			Json.gsonPrettyPrinting.toJson(dirtySince, writer);
		}
		catch (final JsonParseException | IOException e) {
			Log.severe("could not save " + file, e);
		}
	}


	/**
	 * Sets the backup process with the provided id as dirty.
	 * @param id the id of the backup process.
	 */
	public synchronized void setDirtySinceNow(String id) {
		dirtySince.put(id, System.currentTimeMillis());
		save();
	}

	/**
	 * Sets the backup process with the provided id as not dirty.
	 * @param id the id of the backup process.
	 */
	public synchronized void setNotDirty(String id) {
		dirtySince.put(id, -1L);
		save();
	}


	public synchronized boolean isDirty(String id) {
		return isDirtySince(id) != -1;
	}
	
	public synchronized long isDirtySince(String id) {
		if (!dirtySince.containsKey(id))
			setDirtySinceNow(id);
		return dirtySince.get(id);
	}
	
	
	
	
}