package fr.pandacube.lib.paper.reflect;

import org.bukkit.Bukkit;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;

/**
 * Provides reflection tools related to {@code org.bukkit.craftbukkit}.
 */
public class OBCReflect {
	
	private static final String CRAFTBUKKIT_PACKAGE = Bukkit.getServer().getClass().getPackage().getName();

	/**
	 * Returns the OBC class that has the provided name, wrapped into a {@link ReflectClass}.
	 * @param obcClass the name of the class, including the subpackage in which the requested class is. This parameter
	 *                 will be prefixed with the {@code OBC} package and the current package version.
	 * @return the OBC class that has the provided name, wrapped into a {@link ReflectClass}.
	 * @throws ClassNotFoundException if the provided class was not found in {@code OBC} package.
	 */
	public static ReflectClass<?> ofClass(String obcClass) throws ClassNotFoundException {
		return Reflect.ofClass(CRAFTBUKKIT_PACKAGE + "." + obcClass);
	}

}
