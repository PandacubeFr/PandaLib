package fr.pandacube.lib.paper.modules.backup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.error.YAMLException;

import fr.pandacube.lib.paper.PandaLibPaper;
import fr.pandacube.lib.util.Log;

public class Persist extends YamlConfiguration {
	protected final BackupManager backupManager;

	private final File file;
	
	// private final Set<String> dirtyWorldsSave = new HashSet<>();
	
	public Persist(BackupManager bm) {
		file = new File(PandaLibPaper.getPlugin().getDataFolder(), "backup_persist.yml");
		backupManager = bm;
		load();
	}

	public void reload() {
		load();
	}

	protected void load() {
		boolean loaded = false;
		try {
			load(file);
			loaded = true;
		}
		catch (final FileNotFoundException ignored) { }
		catch (final IOException e) {
			Log.severe("cannot load " + file, e);
		}
		catch (final InvalidConfigurationException e) {
			if (e.getCause() instanceof YAMLException) Log.severe("Config file " + file + " isn't valid!", e);
			else if (e.getCause() == null || e.getCause() instanceof ClassCastException) Log.severe("Config file " + file + " isn't valid!");
			else Log.severe("cannot load " + file, e);
		}
		
		if (!loaded) {
			options().copyDefaults(true);
			save();
		}
	}
	
	public void save() {
		try {
			save(file);
		}
		catch (final IOException e) {
			Log.severe("could not save " + file, e);
		}
	}
	
	
	
	
	
	/**
	 * Make the specified world dirty for compress. Also makes the specified world clean for saving if nobody is connected there.
	 */
	public void updateDirtyStatusAfterSave(final World world) {
		if (world == null)
			return;
		if (!isDirty(Type.WORLDS, world.getName())) { // don't set dirty if it is already
			setDirtySinceNow(Type.WORLDS, world.getName());
			Log.info("[Backup] " + Type.WORLDS + "\\" + world.getName() + " was saved and is now dirty. Next backup on "
					+ DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG)
							.format(new Date(backupManager.getNextCompress(System.currentTimeMillis())))
					);
		}
	}
	
	/**
	 * Update the dirty status after the specified compress process is done.
	 * @param t the type of process
	 * @param n the name of the process (for instance, the name of the world)
	 */
	public void updateDirtyStatusAfterCompress(Type t, String n) {
		if (t == Type.WORLDS)
			setNotDirty(t, n);
		else
			setDirtySinceNow(t, n);
	}
	
	
	private void setDirtySinceNow(Type t, String n) {
		set(t + "." + n + ".dirty_since", System.currentTimeMillis());
		save();
	}
	
	private void setNotDirty(Type t, String n) {
		if (t == Type.WORKDIR)
			return; // WORKDIR are always considered dirty
		set(t + "." + n + ".dirty_since", -1);
		save();
	}
	
	
	public boolean isDirty(Type t, String n) {
		if (t == Type.WORKDIR)
			return true;
		return isDirtySince(t, n) != -1;
	}
	
	public long isDirtySince(Type t, String n) {
		if (!contains(t + "." + n + ".dirty_since"))
			setDirtySinceNow(t, n);
		return getLong(t + "." + n + ".dirty_since");
	}
	
	/*
	 * 
	 * 	type: // (worlds|others)
	 * 		name: // (root|plugin|<worldName>)
	 * 			dirty_since: (true|false)
	 * 
	 */
	
	
	
	
}