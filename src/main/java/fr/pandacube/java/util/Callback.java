package fr.pandacube.java.util;

@FunctionalInterface
public interface Callback<T> {
	public void done(T arg);
}
