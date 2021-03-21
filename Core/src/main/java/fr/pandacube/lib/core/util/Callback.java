package fr.pandacube.lib.core.util;

@FunctionalInterface
public interface Callback<T> {
	public void done(T arg);
}
