package fr.pandacube.java.util;

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
	
}
