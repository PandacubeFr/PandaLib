package fr.pandacube.lib.core.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

public class ThrowableUtil {
	
	
	public static String stacktraceToString(Throwable t) {
		if (t == null) return null;
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			try (PrintStream ps = new PrintStream(os, false, StandardCharsets.UTF_8)) {
				t.printStackTrace(ps);
				ps.flush();
			}
			return os.toString(StandardCharsets.UTF_8);
		} catch (IOException e) {
			return null;
		}
	}
	
	
	
	
	


	/**
	 * Wraps a {@link SupplierException} into a try catch.
	 * @param supp the {@link SupplierException} to run and get the value from.
	 * @return the value returned by the provided supplier.
	 * @throws RuntimeException if the provided {@link SupplierException} throws a checked exception.
	 */
	public static <T> T wrapEx(SupplierException<T> supp) {
		try {
			return supp.get();
		} catch (Exception e) {
			throw uncheck(e, false);
		}
	}
	
	/**
	 * Wraps a {@link RunnableException} into a try catch.
	 * @param run the {@link RunnableException} to run.
	 * @throws RuntimeException if the provided {@link RunnableException} throws a checked exception.
	 */
	public static void wrapEx(RunnableException run) {
		try {
			run.run();
		} catch (Exception e) {
			throw uncheck(e, false);
		}
	}



	/**
	 * Wraps a {@link SupplierException} into a try catch.
	 * @param supp the {@link SupplierException} to run and get the value from.
	 * @return the value returned by the provided supplier.
	 * @throws RuntimeException if the provided {@link SupplierException} throws a checked exception.
	 */
	public static <T> T wrapReflectEx(SupplierException<T> supp) {
		try {
			return supp.get();
		} catch (Exception e) {
			throw uncheck(e, true);
		}
	}

	/**
	 * Wraps a {@link RunnableException} into a try catch.
	 * @param run the {@link RunnableException} to run.
	 * @throws RuntimeException if the provided {@link RunnableException} throws a checked exception.
	 */
	public static void wrapReflectEx(RunnableException run) {
		try {
			run.run();
		} catch (Exception e) {
			throw uncheck(e, true);
		}
	}



	/**
	 * A supplier that can possibly throw a checked exception
	 */
	@FunctionalInterface
	public interface SupplierException<T> {
		T get() throws Exception;
	}



	/**
	 * A runnable that can possibly throw a checked exception
	 */
	@FunctionalInterface
	public interface RunnableException {
		void run() throws Exception;
	}




	private static RuntimeException uncheck(Throwable t, boolean convertReflectionExceptionToError) {
		if (t instanceof Error er) {
			throw er;
		}
		if (t instanceof RuntimeException re)
			return re;

		if (convertReflectionExceptionToError) {
			Error er = null;
			if (t instanceof ClassNotFoundException ce) {
				er = new NoClassDefFoundError();
				er.initCause(ce);
			}
			else if (t instanceof IllegalAccessException ce) {
				er = new IllegalAccessError();
				er.initCause(ce);
			}
			else if (t instanceof NoSuchFieldException ce) {
				er = new NoSuchFieldError();
				er.initCause(ce);
			}
			else if (t instanceof NoSuchMethodException ce) {
				er = new NoSuchMethodError();
				er.initCause(ce);
			}
			else if (t instanceof InstantiationException ce) {
				er = new InstantiationError();
				er.initCause(ce);
			}
			if (er != null)
				throw er;

			if (t instanceof InvocationTargetException ce) {
				Throwable cause = ce.getCause();
				return uncheck(cause, false);
			}
		}

		return new RuntimeException(t);
	}
	
}
