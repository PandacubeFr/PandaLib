package fr.pandacube.lib.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility class providing methods to display human-readable time and duration, and parse duration strings.
 * <p>
 * The methods that return date and daytime are hardcoded in French.
 */
public class TimeUtil {
	

	private static final DateTimeFormatter cmpDayOfWeekFormatter = DateTimeFormatter.ofPattern("EEE", Locale.FRENCH);
	private static final DateTimeFormatter dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH);
	private static final DateTimeFormatter dayOfMonthFormatter = DateTimeFormatter.ofPattern("d", Locale.FRENCH);
	private static final DateTimeFormatter cmpMonthFormatter = DateTimeFormatter.ofPattern("MMM", Locale.FRENCH);
	private static final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM", Locale.FRENCH);
	private static final DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("uuuu", Locale.FRENCH);
	private static final DateTimeFormatter compactDateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(Locale.FRENCH);

	private static final DateTimeFormatter HMSFormatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.FRENCH);
	private static final DateTimeFormatter HMFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.FRENCH);
	private static final DateTimeFormatter HFormatter = DateTimeFormatter.ofPattern("H'h'", Locale.FRENCH);


	/**
	 * Provides a human-readable date of the provided time, with ability to adapt the text relatively to the current
	 * time (for instance "il y a 13 minutes" (french for "13 minutes ago"))
	 * <p>
	 * <b>This method renders the text in French.</b>
	 *
	 * @param time the timestamp in milliseconds of the time to display.
	 * @param showSeconds if the returned string should include seconds (true) or not (false). To have more control
	 *                    over the precision, call {@link #relativeDateFr(long, RelativePrecision, DisplayPrecision,
	 *                    boolean)}.
	 * @param compactWords true to use compact words, false to use full words.
	 * @return a human-readable {@link String} representation of the provided time.
	 */
	public static String relativeDateFr(long time, boolean showSeconds, boolean compactWords) {
		return relativeDateFr(time,
				showSeconds ? RelativePrecision.SECONDS : RelativePrecision.MINUTES,
				showSeconds ? DisplayPrecision.SECONDS : DisplayPrecision.MINUTES,
				compactWords);
	}


	/**
	 * Provides a human-readable date of the provided time, with ability to adapt the text relatively to the current
	 * time (for instance "il y a 13 minutes" (french for "13 minutes ago"))
	 * <p>
	 * <b>This method renders the text in French.</b>
	 *
	 * @param time the timestamp in milliseconds of the time to display.
	 * @param relPrecision the precision of the relative text.
	 * @param displayPrecision the precision of the full date and time.
	 * @param compactWords true to use compact words, false to use full words.
	 * @return a human-readable {@link String} representation of the provided time.
	 */
	public static String relativeDateFr(long time, RelativePrecision relPrecision, DisplayPrecision displayPrecision, boolean compactWords) {
		long currentTime = System.currentTimeMillis();
		
		
		LocalDateTime displayDateTime = toLocalDateTime(time);
		LocalDateTime currentDateTime = toLocalDateTime(currentTime);
		
		
		long timeDiff = currentTime - time;
		long timeDiffSec = timeDiff / 1000;
		
		if (timeDiffSec < -1) {
			// in the future
			if (relPrecision == RelativePrecision.SECONDS) {
				if (timeDiffSec > -60)
					return "dans " + (-timeDiffSec) + (compactWords ? "s" : " secondes"); 
			}
			if (relPrecision.ordinal() >= RelativePrecision.MINUTES.ordinal()) {
				if (timeDiffSec > -60)
					return compactWords ? "dans moins d’1min" : "dans moins d’une minute";
				if (timeDiffSec > -60*2) // dans 2 min
					return compactWords ? "dans 1min" : "dans une minute";
				if (timeDiffSec > -3600) // dans moins d'1 h
					return "dans " + (-timeDiffSec/60) + (compactWords ? "min" : " minutes");
			}
			if (relPrecision.ordinal() >= RelativePrecision.HOURS.ordinal()) {
				if (timeDiffSec > -3600) // dans moins d'1 h
					return compactWords ? "dans moins d’1h" : "dans moins d’une heure";
				if (timeDiffSec > -3600*2) // dans moins de 2 h
					return compactWords ? "dans 1h" : "dans une heure";
				if (timeDiffSec > -3600*12) // dans moins de 12 h
					return "dans " + (-timeDiffSec/3600) + (compactWords ? "h" : " heures");
			}
			if (relPrecision.ordinal() >= RelativePrecision.DAYS.ordinal()) {
				LocalDateTime nextMidnight = LocalDateTime.of(currentDateTime.getYear(), currentDateTime.getMonth(), currentDateTime.getDayOfMonth(), 0, 0).plusDays(1);
				if (displayDateTime.isBefore(nextMidnight)) // aujourd'hui
					return "aujourd’hui à " + dayTimeFr(time, displayPrecision);
				if (displayDateTime.isBefore(nextMidnight.plusDays(1))) // demain
					return "demain à " + dayTimeFr(time, displayPrecision);
				if (displayDateTime.isBefore(nextMidnight.plusDays(5))) // dans moins d'1 semaine
					return (compactWords ? cmpDayOfWeekFormatter : dayOfWeekFormatter).format(displayDateTime) + " "
							+ dayOfMonthFormatter.format(displayDateTime) + " à "
							+ dayTimeFr(time, displayPrecision);
			}

		}
		else {
			// present and past
			if (timeDiffSec <= 1)
				return "maintenant";

			if (relPrecision == RelativePrecision.SECONDS) {
				if (timeDiffSec < 60) // il y a moins d'1 min
					return "il y a " + timeDiffSec + (compactWords ? "s" : " secondes");
			}
			if (relPrecision.ordinal() >= RelativePrecision.MINUTES.ordinal()) {
				if (timeDiffSec < 60) // il y a moins d'1 min
					return compactWords ? "il y a moins d’1min" : "il y a moins d’une minute";
				if (timeDiffSec < 60*2) // il y a moins de 2 min
					return compactWords ? "il y a 1min" : "il y a une minute";
				if (timeDiffSec < 3600) // il y a moins d'1 h
					return "il y a " + (timeDiffSec/60) + (compactWords ? "min" : " minutes");
			}
			if (relPrecision.ordinal() >= RelativePrecision.HOURS.ordinal()) {
				if (timeDiffSec < 3600) // il y a moins d'1 h
					return "il y a moins d’une heure";
				if (timeDiffSec < 3600*2) // il y a moins de 2 h
					return "il y a une heure";
				if (timeDiffSec < 3600*12) // il y a moins de 12 h
					return "il y a " + (timeDiffSec/3600) + " heures";
			}
			if (relPrecision.ordinal() >= RelativePrecision.DAYS.ordinal()) {
				LocalDateTime lastMidnight = LocalDateTime.of(currentDateTime.getYear(), currentDateTime.getMonth(), currentDateTime.getDayOfMonth(), 0, 0);
				if (!displayDateTime.isBefore(lastMidnight)) // aujourd'hui
					return "aujourd’hui à " + dayTimeFr(time, displayPrecision);
				if (!displayDateTime.isBefore(lastMidnight.minusDays(1))) // hier
					return "hier à " + dayTimeFr(time, displayPrecision);
				if (!displayDateTime.isBefore(lastMidnight.minusDays(6))) // il y a moins d'1 semaine
					return (compactWords ? cmpDayOfWeekFormatter : dayOfWeekFormatter).format(displayDateTime) + " dernier à "
							+ dayTimeFr(time, displayPrecision);
			}

		}
		return fullDateFr(time, displayPrecision, true, compactWords, false);

	}

	/**
	 * Enumeration of different level of precision to display a relative time.
	 */
	public enum RelativePrecision {
		/**
		 * No relative display.
		 */
		NONE,
		/**
		 * Days precision for relative display.
		 */
		DAYS,
		/**
		 * Hours precision for relative display.
		 */
		HOURS,
		/**
		 * Minutes precision for relative display.
		 */
		MINUTES,
		/**
		 * Seconds precision for relative display.
		 */
		SECONDS
	}

	/**
	 * Enumeration of different level of precision to display a date and daytime.
	 */
	public enum DisplayPrecision {
		/**
		 * Display only the date.
		 */
		DAYS,
		/**
		 * Display the date and the hour of the day.
		 */
		HOURS,
		/**
		 * Display the date and the time of the day with minute precision.
		 */
		MINUTES,
		/**
		 * Display the date and the time of the day with second precision.
		 */
		SECONDS
	}


	/**
	 * Returns a string representation of the date (and eventually day time) of the provided timestamp.
	 * <p>
	 * <b>This method renders the text in French.</b>
	 *
	 * @param timestamp the time to represent in the returned string.
	 * @param showSeconds if the returned string should include seconds (true) or not (false). To have more control
	 *                    over the precision, call {@link #fullDateFr(long, DisplayPrecision, boolean, boolean, boolean)}.
	 * @param showWeekday true to show the week day, false otherwise.
	 * @param compactWords true to use compact words, false to use full words.
	 * @return a string representation of the date (and eventually day time) of the provided timestamp.
	 */
	public static String fullDateFr(long timestamp, boolean showSeconds, boolean showWeekday, boolean compactWords) {
		return fullDateFr(timestamp, showSeconds, showWeekday, compactWords, false);
	}


	/**
	 * Returns a string representation of the date (and eventually day time) of the provided timestamp.
	 * <p>
	 * <b>This method renders the text in French.</b>
	 *
	 * @param timestamp the time to represent in the returned string.
	 * @param showSeconds if the returned string should include seconds (true) or not (false). To have more control
	 *                    over the precision, call {@link #fullDateFr(long, DisplayPrecision, boolean, boolean, boolean)}.
	 * @param showWeekday true to show the week day, false otherwise.
	 * @param compactWords true to use compact words, false to use full words.
	 * @param compactDate true to use compact date (DD/MM/YYYY).
	 * @return a string representation of the date (and eventually day time) of the provided timestamp.
	 */
	public static String fullDateFr(long timestamp, boolean showSeconds, boolean showWeekday, boolean compactWords, boolean compactDate) {
		return fullDateFr(timestamp, showSeconds ? DisplayPrecision.SECONDS : DisplayPrecision.MINUTES, showWeekday, compactWords, compactDate);
	}

	/**
	 * Returns a string representation of the date (and eventually day time) of the provided timestamp.
	 * <p>
	 * <b>This method renders the text in French.</b>
	 *
	 * @param timestamp the time to represent in the returned string.
	 * @param precision the {@link DisplayPrecision} fo the returned string.
	 * @param showWeekday true to show the week day, false otherwise.
	 * @param compactWords true to use compact words, false to use full words.
	 * @param compactDate true to use compact date (DD/MM/YYYY).
	 * @return a string representation of the date (and eventually day time) of the provided timestamp.
	 */
	public static String fullDateFr(long timestamp, DisplayPrecision precision, boolean showWeekday, boolean compactWords, boolean compactDate) {
		LocalDateTime displayDateTime = toLocalDateTime(timestamp);
		String date = showWeekday ? ((compactWords ? cmpDayOfWeekFormatter : dayOfWeekFormatter).format(displayDateTime) + " ") : "";
		if (compactDate) {
			date += compactDateFormatter.format(displayDateTime);
		}
		else {
			date += dayOfMonthFormatter.format(displayDateTime) + " "
					+ (compactWords ? cmpMonthFormatter : monthFormatter).format(displayDateTime) + " "
					+ yearFormatter.format(displayDateTime);
		}
		if (precision == DisplayPrecision.DAYS)
			return date;
		return date + (compactDate ? " " : " à ") + dayTimeFr(timestamp, precision);
	}

	/**
	 * Returns a string representation of the time of the day of the provided timestamp.
	 * <p>
	 * <b>This method renders the text in French.</b>
	 *
	 * @param timestamp the time to represent in the returned string.
	 * @param precision the {@link DisplayPrecision} fo the returned string.
	 * @return a string representation of the time of the day of the provided timestamp.
	 */
	public static String dayTimeFr(long timestamp, DisplayPrecision precision) {
		DateTimeFormatter tFormatter = switch(precision) {
		case HOURS -> HFormatter;
		case MINUTES -> HMFormatter;
		case SECONDS -> HMSFormatter;
		default -> throw new IllegalArgumentException("precision");
		};
		return tFormatter.format(toLocalDateTime(timestamp));
	}
	
	
	private static LocalDateTime toLocalDateTime(long msTime) {
		return Instant.ofEpochMilli(msTime).atZone(TimeZone.getDefault().toZoneId()).toLocalDateTime();
	}


	/**
	 * Converts the provided duration into a human-readable {@link String}.
	 * @param msDuration the duration in millisecond.
	 * @param hUnit the biggest unit of time to display.
	 * @param lUnit the smallest unit of time to display.
	 * @param spaces true to put spaces between time units (e.g.: {@code "1s 500ms"}) or false otherwise (e.g.: {@code "1s500ms"})).
	 * @param fr true to use French unit symbols (it only changes the day symbol from "d" to "j").
	 * @param leadingZeros to use leading zeros when necessary in front of some durations.
	 * @return a {@link String} representation of the duration.
	 */
	public static String durationToLongString(long msDuration, TimeUnit hUnit, TimeUnit lUnit, boolean spaces, boolean fr, boolean leadingZeros) {
		if (lUnit.compareTo(hUnit) > 0) {
			TimeUnit tmp = lUnit;
			lUnit = hUnit;
			hUnit = tmp;
		}
		if (lUnit.compareTo(TimeUnit.MILLISECONDS) < 0)
			lUnit = TimeUnit.MILLISECONDS;
		if (hUnit.compareTo(TimeUnit.MILLISECONDS) < 0)
			hUnit = TimeUnit.MILLISECONDS;
		
		
		AtomicLong remainingTime = new AtomicLong(msDuration);
		AtomicBoolean oneDisplayed = new AtomicBoolean(false);
		final TimeUnit fLUnit = lUnit, fHUnit = hUnit;
		
		String ret = Arrays.stream(TimeUnit.values())
				.sequential()
				.filter(u -> u.compareTo(fLUnit) >= 0 && u.compareTo(fHUnit) <= 0)
				.sorted(Comparator.reverseOrder())
				.filter(u -> {
					if (u.convert(remainingTime.get(), TimeUnit.MILLISECONDS) == 0 && !oneDisplayed.get())
						return false;
					oneDisplayed.set(true);
					return true;
				})
				.map(u -> {
					long v = u.convert(remainingTime.get(), TimeUnit.MILLISECONDS);
					remainingTime.addAndGet(TimeUnit.MILLISECONDS.convert(-v, u));
					return toStringWithPaddingZeros(v, leadingZeros ? timeUnitToLeftPadLength(u) : 1) + timeUnitToSuffix(u, fr);
				})
				.collect(Collectors.joining(spaces ? " " : ""));
		// ensure there is at least something to display (for instance : "0s")
		return oneDisplayed.get() ? ret : (toStringWithPaddingZeros(0, leadingZeros ? timeUnitToLeftPadLength(lUnit) : 1) + timeUnitToSuffix(lUnit, fr));
	}

	/**
	 * Provides a unit symbol for the provided {@link TimeUnit}.
	 * @param u the {@link TimeUnit}.
	 * @param fr true to use French unit symbols (it only changes the {@link TimeUnit#DAYS} symbol from "d" to "j").
	 * @return a unit symbol for the provided {@link TimeUnit}.
	 */
	public static String timeUnitToSuffix(TimeUnit u, boolean fr) {
		return switch (u) {
			case DAYS -> fr ? "j" : "d";
			case HOURS -> "h";
			case MINUTES -> "m";
			case SECONDS -> "s";
			case MILLISECONDS -> "ms";
			case MICROSECONDS -> "μs";
			case NANOSECONDS -> "ns";
		};
	}

	/**
	 * Indicate the 0-padded length of a number for the provided {@link TimeUnit}.
	 * Will returns 3 for below-second time units, 2 for seconds, minutes and hours and 1 otherwise.
	 * @param u the {@link TimeUnit}
	 * @return the 0-padded length of a number for the provided {@link TimeUnit}.
	 */
	public static int timeUnitToLeftPadLength(TimeUnit u) {
		return switch (u) {
			case NANOSECONDS, MICROSECONDS, MILLISECONDS -> 3;
			case SECONDS, MINUTES, HOURS -> 2;
			case DAYS -> 1;
		};
	}

	/**
	 * Converts the provided long to a {@link String} and eventually prepend any {@code "0"} necessary to make the
	 * returned string’s length at least {@code leftPad}.
	 * @param value the value to convert to {@link String}.
	 * @param leftPad the minimal length of the returned String.
	 * @return the string representation of the provided value, with eventual zeros prepended.
	 */
	public static String toStringWithPaddingZeros(long value, int leftPad) {
		String valueStr = Long.toString(value);
		int padding = leftPad - valueStr.length();
		if (padding <= 0)
			return valueStr;
		return "0".repeat(padding) + valueStr;
	}
	
	
	
	
	
	
	/**
	 * Equivalent to {@link #durationToLongString(long, TimeUnit, TimeUnit, boolean, boolean, boolean) TimeUnit.durationToLongString(msDuration, TimeUnit.DAYS, milliseconds ? TimeUnit.MILLISECONDS : TimeUnit.SECONDS, true, true, false)}
	 * @param msDuration the duration in ms
	 * @param milliseconds if the milliseconds are displayed or not
	 * @return a {@link String} representation of the duration.
	 */
	public static String durationToString(long msDuration, boolean milliseconds) {
		return durationToLongString(msDuration, TimeUnit.DAYS, milliseconds ? TimeUnit.MILLISECONDS : TimeUnit.SECONDS, true, true, false);
	}

	/**
	 * Equivalent to {@link #durationToLongString(long, TimeUnit, TimeUnit, boolean, boolean, boolean) TimeUnit.durationToLongString(msDuration, TimeUnit.DAYS, TimeUnit.SECONDS, true, true, false)}
	 * @param msDuration the duration in ms
	 * @return a {@link String} representation of the duration.
	 */
	public static String durationToString(long msDuration) {
		return durationToLongString(msDuration, TimeUnit.DAYS, TimeUnit.SECONDS, true, true, false);
	}
	
	/**
	 * Equivalent to {@link #durationToLongString(long, TimeUnit, TimeUnit, boolean, boolean, boolean) TimeUnit.durationToLongString(msDuration, TimeUnit.DAYS, TimeUnit.SECONDS, false, false, false)}
	 * @param msDuration the duration in ms
	 * @return a {@link String} representation of the duration.
	 */
	public static String durationToParsableString(long msDuration) {
		return durationToLongString(msDuration, TimeUnit.DAYS, TimeUnit.SECONDS, false, false, false);
	}
	

	
	/**
	 * Parse a duration string into a time in the past of future, relative to now.
	 * Source: <a href="https://github.com/EssentialsX/Essentials/blob/2.x/Essentials/src/main/java/com/earth2me/essentials/utils/DateUtil.java">Essentials DateUtil#parseDuration(String, boolean)</a>
	 * @param time the duration to parse.
	 * @param future thur to return the time in the future, false for the time in the past.
	 * @return the computed timestamp in millisecond.
	 * @throws IllegalArgumentException if the format is not valid.
	 */
	public static long parseDuration(String time, boolean future) {
		@SuppressWarnings("RegExpSimplifiable")
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
			if (m.group() == null || m.group().isEmpty())
				continue;
			for (int i = 0; i < m.groupCount(); i++) {
				if (m.group(i) != null && !m.group(i).isEmpty()) {
					found = true;
					break;
				}
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
		if (!found)
			throw new IllegalArgumentException("Invalid duration format");
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
		return c.after(max) ? max.getTimeInMillis() : c.getTimeInMillis();
	}


	private TimeUtil() {}


}
