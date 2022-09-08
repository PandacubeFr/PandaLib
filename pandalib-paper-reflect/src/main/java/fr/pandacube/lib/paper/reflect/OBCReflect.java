package fr.pandacube.lib.paper.reflect;

import org.bukkit.Bukkit;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;

/**
 * Provides reflection tools related to {@code org.bukkit.craftbukkit}.
 */
public class OBCReflect {
	
	private static final String OBC_PACKAGE_PREFIX = "org.bukkit.craftbukkit.";
	
	private static final String OBC_PACKAGE_VERSION;

	static {
		String name = Bukkit.getServer().getClass().getName()
				.substring(OBC_PACKAGE_PREFIX.length());
		name = name.substring(0, name.indexOf("."));

		OBC_PACKAGE_VERSION = name;
	}


	/**
	 * Returns the OBC class that has the provided name, wrapped into a {@link ReflectClass}.
	 * @param obcClass the name of the class, including the subpackage in whitch the requested class is. This parameter
	 *                 will be prefixed with the {@code OBC} package and the current package version.
	 * @return the OBC class that has the provided name, wrapped into a {@link ReflectClass}.
	 * @throws ClassNotFoundException if the provided class was not found in {@code OBC} package.
	 */
	public static ReflectClass<?> ofClass(String obcClass) throws ClassNotFoundException {
		return Reflect.ofClass(OBC_PACKAGE_PREFIX + OBC_PACKAGE_VERSION + "." + obcClass);
	}
	
	
	

}
