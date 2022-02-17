package fr.pandacube.lib.core.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class ThrowableUtil {
	
	
	public static String stacktraceToString(Throwable t) {
		if (t == null) return null;
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			try (PrintStream ps = new PrintStream(os, false, "UTF-8")) {
				t.printStackTrace(ps);
				ps.flush();
			}
			return os.toString("UTF-8");
		} catch (IOException e) {
			return null;
		}
	}
	
	
	
	
	
	
	

	
	/**
	 * A supplier that can possibly throw a checked exception
	 */
	@FunctionalInterface
	public interface SupplierException<T> {
		public T get() throws Exception;
	}

	/**
	 * Wraps a {@link SupplierException} into a try catch.
	 * @param supp the {@link SupplierException} to run and get the value from.
	 * @return the value returned by the provided supplier.
	 * @throws RuntimeException if the provided {@link SupplierException} throws an exception.
	 */
	public static <T> T wrapEx(SupplierException<T> supp) {
		try {
			return supp.get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
	/**
	 * A runnable that can possibly throw a checked exception
	 */
	@FunctionalInterface
	public interface RunnableException {
		public void run() throws Exception;
	}
	
	/**
	 * Wraps a {@link RunnableException} into a try catch.
	 * @param run the {@link RunnableException} to run.
	 * @throws RuntimeException if the provided {@link RunnableException} throws an exception.
	 */
	public static void wrapEx(RunnableException run) {
		try {
			run.run();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
