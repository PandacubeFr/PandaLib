package fr.pandacube.lib.core.config;

import java.io.File;
import java.io.IOException;

/**
 * An abstract manager for a set of configuration files and folders.
 * Its uses is to manage the loading/reloading of the configuration of a plugin.
 */
public abstract class AbstractConfigManager {

	/**
	 * The global configuration directory.
	 * May be the one provided by the environmenet API (like Plugin.getPluginFolder() in Bukkit).
	 */
	protected final File configDir;

	/**
	 * Create a new instance of config manager.
	 * @param configD the config directory.
	 * @throws IOException if an IO error occurs.
	 */
	public AbstractConfigManager(File configD) throws IOException {
		configDir = configD;
		
		configDir.mkdirs();
		
		init();
	}
	
	/**
	 * Closes the configuration. May handle saving of any non-saved data.
	 * @throws IOException if an IO error occurs.
	 */
	public abstract void close() throws IOException;
	
	/**
	 * Loads (or reloads) the configuration data.
	 * @throws IOException if an IO error occurs.
	 */
	public abstract void init() throws IOException;


	/**
	 * Utility method to close then reload the config.
	 * @throws IOException if an IO error occurs.
	 * @see #close()
	 * @see #init()
	 */
	public synchronized void reloadConfig() throws IOException {
		close();
		init();
	}
	

}
