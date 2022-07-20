package fr.pandacube.lib.core.config;

import java.io.File;
import java.io.IOException;

public abstract class AbstractConfigManager {
	
	protected final File configDir;
	
	public AbstractConfigManager(File configD) throws IOException {
		configDir = configD;
		
		configDir.mkdirs();
		
		init();
	}
	
	/**
	 * Implementation must close all closeable configuration (saving for example)
	 */
	public abstract void close() throws IOException;
	
	/**
	 * Implementation must init all config data
	 */
	public abstract void init() throws IOException;
	
	
	
	
	public synchronized void reloadConfig() throws IOException {
		close();
		init();
	}
	

}
