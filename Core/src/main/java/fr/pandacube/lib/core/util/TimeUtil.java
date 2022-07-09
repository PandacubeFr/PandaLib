package fr.pandacube.lib.core.util;

import fr.pandacube.lib.core.commands.SuggestionsSupplier;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TimeUtil {
	

	private static final DateTimeFormatter cmpDayOfWeekFormatter = DateTimeFormatter.ofPattern("EEE", Locale.getDefault());
	private static final DateTimeFormatter dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.getDefault());
	private static final DateTimeFormatter dayOfMonthFormatter = DateTimeFormatter.ofPattern("d", Locale.getDefault());
	private static final DateTimeFormatter cmpMonthFormatter = DateTimeFormatter.ofPattern("MMM", Locale.getDefault());
	private static final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM", Locale.getDefault());
	private static final DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("uuuu", Locale.getDefault());

	private static final DateTimeFormatter HMSFormatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.getDefault());
	private static final DateTimeFormatter HMFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());
	private static final DateTimeFormatter HFormatter = DateTimeFormatter.ofPattern("H'h'", Locale.getDefault());
	

	public static String relativeDateFr(long displayTime, boolean showSeconds, boolean compactWords) {
		return relativeDateFr(displayTime,
				showSeconds ? RelativePrecision.SECONDS : RelativePrecision.MINUTES,
				showSeconds ? DisplayPrecision.SECONDS : DisplayPrecision.MINUTES,
				compactWords);
	}
	
	
	public static String relativeDateFr(long displayTime, RelativePrecision relPrecision, DisplayPrecision dispPrecision, boolean compactWords) {
		long currentTime = System.currentTimeMillis();
		
		
		LocalDateTime displayDateTime = toLocalDateTime(displayTime);
		LocalDateTime currentDateTime = toLocalDateTime(currentTime);
		
		
		long timeDiff = currentTime - displayTime;
		long timeDiffSec = timeDiff / 1000;
		
		if (timeDiffSec < -1) {
			// in the future
			if (relPrecision == RelativePrecision.SECONDS) {
				if (timeDiffSec > -60)
					return "dans " + (-timeDiffSec) + (compactWords ? "s" : " secondes"); 
			}
			if (relPrecision.morePreciseOrEqTo(RelativePrecision.MINUTES)) {
				if (timeDiffSec > -60)
					return compactWords ? "dans moins d’1min" : "dans moins d’une minute";
				if (timeDiffSec > -60*2) // dans 2 min
					return compactWords ? "dans 1min" : "dans une minute";
				if (timeDiffSec > -3600) // dans moins d’1h
					return "dans " + (-timeDiffSec/60) + (compactWords ? "min" : " minutes");
			}
			if (relPrecision.morePreciseOrEqTo(RelativePrecision.HOURS)) {
				if (timeDiffSec > -3600) // dans moins d’1h
					return compactWords ? "dans moins d’1h" : "dans moins d’une heure";
				if (timeDiffSec > -3600*2) // dans moins de 2h
					return compactWords ? "dans 1h" : "dans une heure";
				if (timeDiffSec > -3600*12) // dans moins de 12h
					return "dans " + (-timeDiffSec/3600) + (compactWords ? "h" : " heures");
			}
			if (relPrecision.morePreciseOrEqTo(RelativePrecision.DAYS)) {
				LocalDateTime nextMidnight = LocalDateTime.of(currentDateTime.getYear(), currentDateTime.getMonth(), currentDateTime.getDayOfMonth(), 0, 0).plusDays(1);
				if (displayDateTime.isBefore(nextMidnight)) // aujourd'hui
					return "aujourd’hui à " + dayTimeFr(displayTime, dispPrecision);
				if (displayDateTime.isBefore(nextMidnight.plusDays(1))) // demain
					return "demain à " + dayTimeFr(displayTime, dispPrecision);
				if (displayDateTime.isBefore(nextMidnight.plusDays(5))) // dans moins d'1 semaine
					return (compactWords ? cmpDayOfWeekFormatter : dayOfWeekFormatter).format(displayDateTime) + " "
							+ dayOfMonthFormatter.format(displayDateTime) + " à "
							+ dayTimeFr(displayTime, dispPrecision);
			}

		}
		else {
			// present and past
			if (timeDiffSec <= 1)
				return "maintenant";

			if (relPrecision == RelativePrecision.SECONDS) {
				if (timeDiffSec < 60) // ya moins d'1 min
					return "il y a " + timeDiffSec + (compactWords ? "s" : " secondes");
			}
			if (relPrecision.morePreciseOrEqTo(RelativePrecision.MINUTES)) {
				if (timeDiffSec < 60) // ya moins d'1 min
					return compactWords ? "il y a moins d’1min" : "il y a moins d’une minute";
				if (timeDiffSec < 60*2) // ya moins de 2 min
					return compactWords ? "il y a 1min" : "il y a une minute";
				if (timeDiffSec < 3600) // ya moins d'1h
					return "il y a " + (timeDiffSec/60) + (compactWords ? "min" : " minutes");
			}
			if (relPrecision.morePreciseOrEqTo(RelativePrecision.HOURS)) {
				if (timeDiffSec < 3600) // ya moins d'1h
					return "il y a moins d’une heure";
				if (timeDiffSec < 3600*2) // ya moins de 2h
					return "il y a une heure";
				if (timeDiffSec < 3600*12) // ya moins de 12h
					return "il y a " + (timeDiffSec/3600) + " heures";
			}
			if (relPrecision.morePreciseOrEqTo(RelativePrecision.DAYS)) {
				LocalDateTime lastMidnight = LocalDateTime.of(currentDateTime.getYear(), currentDateTime.getMonth(), currentDateTime.getDayOfMonth(), 0, 0);
				if (!displayDateTime.isBefore(lastMidnight)) // aujourd'hui
					return "aujourd’hui à " + dayTimeFr(displayTime, dispPrecision);
				if (!displayDateTime.isBefore(lastMidnight.minusDays(1))) // hier
					return "hier à " + dayTimeFr(displayTime, dispPrecision);
				if (!displayDateTime.isBefore(lastMidnight.minusDays(6))) // ya moins d'1 semaine
					return (compactWords ? cmpDayOfWeekFormatter : dayOfWeekFormatter).format(displayDateTime) + " dernier à "
							+ dayTimeFr(displayTime, dispPrecision);
			}

		}
		return fullDateFr(displayTime, dispPrecision, true, compactWords);

	}
	
	
	public enum RelativePrecision {
		NONE, DAYS, HOURS, MINUTES, SECONDS;

		public boolean morePreciseThan(RelativePrecision o) { return ordinal() > o.ordinal(); }
		public boolean lessPreciseThan(RelativePrecision o) { return ordinal() < o.ordinal(); }
		public boolean morePreciseOrEqTo(RelativePrecision o) { return ordinal() >= o.ordinal(); }
		public boolean lessPreciseOrEqTo(RelativePrecision o) { return ordinal() <= o.ordinal(); }
	}
	
	public enum DisplayPrecision {
		DAYS, HOURS, MINUTES, SECONDS
	}
	

	public static String fullDateFr(long displayTime, boolean showSeconds, boolean showWeekday, boolean compactWords) {
		return fullDateFr(displayTime, showSeconds ? DisplayPrecision.SECONDS : DisplayPrecision.MINUTES, showWeekday, compactWords);
	}
	
	public static String fullDateFr(long displayTime, DisplayPrecision precision, boolean showWeekday, boolean compactWords) {
		LocalDateTime displayDateTime = toLocalDateTime(displayTime);
		String ret = (showWeekday ? ((compactWords ? cmpDayOfWeekFormatter : dayOfWeekFormatter).format(displayDateTime) + " ") : "")
				+ dayOfMonthFormatter.format(displayDateTime) + " "
				+ (compactWords ? cmpMonthFormatter : monthFormatter).format(displayDateTime) + " "
				+ yearFormatter.format(displayDateTime);
		if (precision == DisplayPrecision.DAYS)
			return ret;
		return ret + " à " + dayTimeFr(displayTime, precision);
	}
	
	public static String dayTimeFr(long displayTime, DisplayPrecision precision) {
		DateTimeFormatter tFormatter = switch(precision) {
		case HOURS -> HFormatter;
		case MINUTES -> HMFormatter;
		case SECONDS -> HMSFormatter;
		default -> throw new IllegalArgumentException("precision");
		};
		return tFormatter.format(toLocalDateTime(displayTime));
	}
	
	
	private static LocalDateTime toLocalDateTime(long msTime) {
		return Instant.ofEpochMilli(msTime).atZone(TimeZone.getDefault().toZoneId()).toLocalDateTime();
	}
	
	
	
	
	
	
	
	
	
	
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
					return toString(v, leadingZeros ? timeUnitToLeftPadLength(u) : 1) + timeUnitToSuffix(u, fr);
				})
				.collect(Collectors.joining(spaces ? " " : ""));
		// ensure there is at least something to display (for instance : "0s")
		return oneDisplayed.get() ? ret : (toString(0, leadingZeros ? timeUnitToLeftPadLength(lUnit) : 1) + timeUnitToSuffix(lUnit, fr));
	}
	
	
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
	
	public static int timeUnitToLeftPadLength(TimeUnit u) {
		return switch (u) {
			case NANOSECONDS, MICROSECONDS, MILLISECONDS -> 3;
			case SECONDS, MINUTES, HOURS -> 2;
			case DAYS -> 1;
		};
	}
	
	public static String toString(long value, int leftPad) {
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
	 */
	public static String durationToString(long msDuration, boolean milliseconds) {
		return durationToLongString(msDuration, TimeUnit.DAYS, milliseconds ? TimeUnit.MILLISECONDS : TimeUnit.SECONDS, true, true, false);
	}

	/**
	 * Equivalent to {@link #durationToLongString(long, TimeUnit, TimeUnit, boolean, boolean, boolean) TimeUnit.durationToLongString(msDuration, TimeUnit.DAYS, TimeUnit.SECONDS, true, true, false)}
	 * @param msDuration the duration in ms
	 */
	public static String durationToString(long msDuration) {
		return durationToLongString(msDuration, TimeUnit.DAYS, TimeUnit.SECONDS, true, true, false);
	}
	
	/**
	 * Equivalent to {@link #durationToLongString(long, TimeUnit, TimeUnit, boolean, boolean, boolean) TimeUnit.durationToLongString(msDuration, TimeUnit.DAYS, TimeUnit.SECONDS, false, false, false)}
	 * @param msDuration the duration in ms
	 */
	public static String durationToParsableString(long msDuration) {
		return durationToLongString(msDuration, TimeUnit.DAYS, TimeUnit.SECONDS, false, false, false);
	}
	

	
	/**
	 * @see <a href="https://github.com/EssentialsX/Essentials/blob/2.x/Essentials/src/main/java/com/earth2me/essentials/utils/DateUtil.java">Essentials DateUtil#parseDuration(String, boolean)</a>
	 */
	public static long parseDuration(String time, boolean future) throws Exception {
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
	
	
	
	
	
	public static <S> SuggestionsSupplier<S> suggestDuration() {
		return (s, ti, token, args) -> {
			if (token.isEmpty()) {
				return emptyTokenSuggestions;
			}
			List<String> remainingSuffixes = new ArrayList<>(allSuffixes);
			char[] tokenChars = token.toCharArray();
			String accSuffix = "";
			for (char c : tokenChars) {
				if (Character.isDigit(c)) {
					scanAndRemovePastSuffixes(remainingSuffixes, accSuffix);
					accSuffix = "";
				} else if (Character.isLetter(c)) {
					accSuffix += c;
				} else
					return Collections.emptyList();
			}
			String prefixToken = token.substring(0, token.length() - accSuffix.length());
			return SuggestionsSupplier.collectFilteredStream(remainingSuffixes.stream(), accSuffix)
					.stream()
					.map(str -> prefixToken + str)
					.collect(Collectors.toList());
		};
	}
	
	private static final List<String> allSuffixes = Arrays.asList("y", "mo", "w", "d", "h", "m", "s");
	private static final List<String> emptyTokenSuggestions = allSuffixes.stream().map(p -> "1" + p).collect(Collectors.toList());
	private static void scanAndRemovePastSuffixes(List<String> suffixes, String foundSuffix) {
		for (int i = 0; i < suffixes.size(); i++) {
			if (foundSuffix.startsWith(suffixes.get(i))) {
				suffixes.subList(0, i + 1).clear();
				return;
			}
		}
	}
	

}
