package fr.pandacube.util.measurement;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtil {
	public static String durationToString(long msec_time, boolean dec_seconde) {
		boolean neg = msec_time < 0;
		msec_time = Math.abs(msec_time);
		int j = 0, h = 0, m = 0, s = 0;
		long msec = msec_time;

		j = (int) (msec / (1000 * 60 * 60 * 24));
		msec -= (long) (1000 * 60 * 60 * 24) * j;
		h = (int) (msec / (1000 * 60 * 60));
		msec -= (long) (1000 * 60 * 60) * h;
		m = (int) (msec / (1000 * 60));
		msec -= (long) (1000 * 60) * m;
		s = (int) (msec / 1000);
		msec -= (long) 1000 * s;

		String result = "";
		if (j > 0) result = result.concat(j + "j ");
		if (h > 0) result = result.concat(h + "h ");
		if (m > 0) result = result.concat(m + "m ");
		if (s > 0 && !dec_seconde) result = result.concat(s + "s");
		else if (dec_seconde && (s > 0 || msec > 0)) {
			msec += s * 1000;
			result = result.concat((msec / 1000D) + "s");
		}

		if (result.equals("")) result = "0";
		result = result.trim();
		if (neg)
			result = "-" + result;
			
		return result;
	}

	public static String durationToString(long msec_time) {
		return durationToString(msec_time, false);
	}
	

	
	/**
	 * @see {@link com.earth2me.essentials.utils.DateUtil#parseDateDiff(String, boolean)}
	 */
	public static long parseDateDiff(String time, boolean future) throws Exception {
		Pattern timePattern = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?"
				+ "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?"
				+ "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?"
				+ "(?:([0-9]+)\\s*(?:s[a-z]*)?)?", Pattern.CASE_INSENSITIVE);
		Matcher m = timePattern.matcher(time);
		int years = 0;
		int months = 0;
		int weeks = 0;
		int days = 0;
		int hours = 0;
		int minutes = 0;
		int seconds = 0;
		boolean found = false;
		while (m.find()) {
			if (m.group() == null || m.group().isEmpty()) continue;
			for (int i = 0; i < m.groupCount(); i++)
				if (m.group(i) != null && !m.group(i).isEmpty()) {
					found = true;
					break;
				}
			if (found) {
				if (m.group(1) != null && !m.group(1).isEmpty()) years = Integer.parseInt(m.group(1));
				if (m.group(2) != null && !m.group(2).isEmpty()) months = Integer.parseInt(m.group(2));
				if (m.group(3) != null && !m.group(3).isEmpty()) weeks = Integer.parseInt(m.group(3));
				if (m.group(4) != null && !m.group(4).isEmpty()) days = Integer.parseInt(m.group(4));
				if (m.group(5) != null && !m.group(5).isEmpty()) hours = Integer.parseInt(m.group(5));
				if (m.group(6) != null && !m.group(6).isEmpty()) minutes = Integer.parseInt(m.group(6));
				if (m.group(7) != null && !m.group(7).isEmpty()) seconds = Integer.parseInt(m.group(7));
				break;
			}
		}
		if (!found) throw new Exception("Format de durée invalide");
		Calendar c = new GregorianCalendar();
		if (years > 0) c.add(Calendar.YEAR, years * (future ? 1 : -1));
		if (months > 0) c.add(Calendar.MONTH, months * (future ? 1 : -1));
		if (weeks > 0) c.add(Calendar.WEEK_OF_YEAR, weeks * (future ? 1 : -1));
		if (days > 0) c.add(Calendar.DAY_OF_MONTH, days * (future ? 1 : -1));
		if (hours > 0) c.add(Calendar.HOUR_OF_DAY, hours * (future ? 1 : -1));
		if (minutes > 0) c.add(Calendar.MINUTE, minutes * (future ? 1 : -1));
		if (seconds > 0) c.add(Calendar.SECOND, seconds * (future ? 1 : -1));
		Calendar max = new GregorianCalendar();
		max.add(Calendar.YEAR, 10);
		if (c.after(max)) return max.getTimeInMillis();
		return c.getTimeInMillis();
	}

}
