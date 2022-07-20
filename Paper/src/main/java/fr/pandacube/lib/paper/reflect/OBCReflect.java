package fr.pandacube.lib.paper.reflect;

import org.bukkit.Bukkit;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.Reflect.ReflectClass;

public class OBCReflect {
	
	private static final String OBC_PACKAGE_PREFIX = "org.bukkit.craftbukkit.";
	
	private static final String OBC_PACKAGE_VERSION;

	static {
		String name = Bukkit.getServer().getClass().getName()
				.substring(OBC_PACKAGE_PREFIX.length());
		name = name.substring(0, name.indexOf("."));

		OBC_PACKAGE_VERSION = name;
	}
	
	
	
	public static ReflectClass<?> ofClass(String obcClass) throws ClassNotFoundException {
		return Reflect.ofClass(OBC_PACKAGE_PREFIX + OBC_PACKAGE_VERSION + "." + obcClass);
	}
	
	
	

}
