package fr.pandacube.util;

@FunctionalInterface
public interface Callback<T> {
	public void done(T arg);
}
