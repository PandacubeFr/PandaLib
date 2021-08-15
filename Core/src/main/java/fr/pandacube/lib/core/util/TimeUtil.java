package fr.pandacube.lib.core.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import fr.pandacube.lib.core.commands.SuggestionsSupplier;

public class TimeUtil {
	

	private static DateTimeFormatter cmpDayOfWeekFormatter = DateTimeFormatter.ofPattern("EEE", Locale.getDefault());
	private static DateTimeFormatter dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.getDefault());
	private static DateTimeFormatter dayOfMonthFormatter = DateTimeFormatter.ofPattern("d", Locale.getDefault());
	private static DateTimeFormatter cmpMonthFormatter = DateTimeFormatter.ofPattern("MMM", Locale.getDefault());
	private static DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM", Locale.getDefault());
	private static DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("uuuu", Locale.getDefault());

	private static DateTimeFormatter HMSFormatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.getDefault());
	private static DateTimeFormatter HMFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());
	
	
	
	
	public static String relativeDateFr(long displayTime, boolean showSeconds, boolean compactWords) {
		long currentTime = System.currentTimeMillis();
		
		
		LocalDateTime displayDateTime = toLocalDateTime(displayTime);
		LocalDateTime currentDateTime = toLocalDateTime(currentTime);
		
		
		long timeDiff = currentTime - displayTime;
		long timeDiffSec = timeDiff / 1000;
		
		if (timeDiffSec < -1) {
			// in the future
			if (timeDiffSec > -60) {
				if (showSeconds)
					return "dans " + (-timeDiffSec) + " secondes";
				else
					return "dans moins d’une minute";
			}
			if (timeDiffSec > -60*2) // dans 2 min
				return "dans ̈" + (int)Math.floor((-timeDiffSec)/60) + " minute";
			if (timeDiffSec > -3600) // dans 1h
				return "dans " + (int)Math.floor((-timeDiffSec)/60) + " minutes";
			if (timeDiffSec > -3600*2) // dans 2h
				return "dans " + (int)Math.floor((-timeDiffSec)/(3600)) + " heure";
			if (timeDiffSec > -3600*12) // dans 12h
				return "dans " + (int)Math.floor((-timeDiffSec)/(3600)) + " heures";
			
			LocalDateTime nextMidnight = LocalDateTime.of(currentDateTime.getYear(), currentDateTime.getMonth(), currentDateTime.getDayOfMonth(), 0, 0).plusDays(1);
			if (displayDateTime.isBefore(nextMidnight)) // aujourd'hui
				return "aujourd’hui à " + (showSeconds ? HMSFormatter : HMFormatter).format(displayDateTime);
			if (displayDateTime.isBefore(nextMidnight.plusDays(1))) // demain
				return "demain à " + (showSeconds ? HMSFormatter : HMFormatter).format(displayDateTime);
			if (displayDateTime.isBefore(nextMidnight.plusDays(5))) // dans moins d'1 semaine
				return (compactWords ? cmpDayOfWeekFormatter : dayOfWeekFormatter).format(displayDateTime) + " "
						+ dayOfMonthFormatter.format(displayDateTime) + " à "
						+ (showSeconds ? HMSFormatter : HMFormatter).format(displayDateTime);
			
			return fullDateFr(displayTime, showSeconds, true, compactWords);
			
		}
		else {
			// present and past
			if (timeDiffSec <= 1)
				return "maintenant";
			if (timeDiffSec < 60) { // ya moins d'1 min
				if (showSeconds)
					return "il y a " + timeDiffSec + " secondes";
				else
					return "il y a moins d’une minute";
			}
			if (timeDiffSec < 60*2) // ya moins de 2 min
				return "il y a " + (int)Math.floor((timeDiffSec)/60) + " minute";
			if (timeDiffSec < 3600) // ya moins d'1h
				return "il y a " + (int)Math.floor((timeDiffSec)/60) + " minutes";
			if (timeDiffSec < 3600*2) // ya moins de 2h
				return "il y a " + (int)Math.floor((timeDiffSec)/(3600)) + " heure";
			if (timeDiffSec < 3600*12) // ya moins de 12h
				return "il y a " + (int)Math.floor((timeDiffSec)/(3600)) + " heures";
			
			LocalDateTime lastMidnight = LocalDateTime.of(currentDateTime.getYear(), currentDateTime.getMonth(), currentDateTime.getDayOfMonth(), 0, 0);
			
			if (!displayDateTime.isBefore(lastMidnight)) // aujourd'hui
				return "aujourd’hui à " + (showSeconds ? HMSFormatter : HMFormatter).format(displayDateTime);
			if (!displayDateTime.isBefore(lastMidnight.minusDays(1))) // hier
				return "hier à " + (showSeconds ? HMSFormatter : HMFormatter).format(displayDateTime);
			if (!displayDateTime.isBefore(lastMidnight.minusDays(6))) // ya moins d'1 semaine
				return (compactWords ? cmpDayOfWeekFormatter : dayOfWeekFormatter).format(displayDateTime) + " dernier à "
						+ (showSeconds ? HMSFormatter : HMFormatter).format(displayDateTime);
			
			return fullDateFr(displayTime, showSeconds, true, compactWords);
			
		}
		
	}
	
	public static String fullDateFr(long displayTime, boolean showSeconds, boolean showWeekday, boolean compactWords) {
		LocalDateTime displayDateTime = toLocalDateTime(displayTime);
		return (showWeekday ? ((compactWords ? cmpDayOfWeekFormatter : dayOfWeekFormatter).format(displayDateTime) + " ") : "")
				+ dayOfMonthFormatter.format(displayDateTime) + " "
				+ (compactWords ? cmpMonthFormatter : monthFormatter).format(displayDateTime) + " "
				+ yearFormatter.format(displayDateTime) + " à "
				+ (showSeconds ? HMSFormatter : HMFormatter).format(displayDateTime);
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
				.sorted((u1, u2) -> u2.compareTo(u1))
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
		switch (u) {
		case DAYS:
			return fr ? "j" : "d";
		case HOURS:
			return "h";
		case MINUTES:
			return "m";
		case SECONDS:
			return "s";
		case MILLISECONDS:
			return "ms";
		case MICROSECONDS:
			return "μs";
		case NANOSECONDS:
			return "ns";
		default:
			throw new IllegalArgumentException("Invalid TimeUnit: " + Objects.toString(u));
		}
	}
	
	public static int timeUnitToLeftPadLength(TimeUnit u) {
		switch (u) {
		case NANOSECONDS:
		case MICROSECONDS:
		case MILLISECONDS:
			return 3;
		case SECONDS:
		case MINUTES:
		case HOURS:
			return 2;
		case DAYS:
		default:
			return 1;
		}
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
	 * @return
	 */
	public static String durationToString(long msDuration, boolean milliseconds) {
		return durationToLongString(msDuration, TimeUnit.DAYS, milliseconds ? TimeUnit.MILLISECONDS : TimeUnit.SECONDS, true, true, false);
	}

	/**
	 * Equivalent to {@link #durationToLongString(long, TimeUnit, TimeUnit, boolean, boolean, boolean) TimeUnit.durationToLongString(msDuration, TimeUnit.DAYS, TimeUnit.SECONDS, true, true, false)}
	 * @param msDuration
	 * @return
	 */
	public static String durationToString(long msDuration) {
		return durationToLongString(msDuration, TimeUnit.DAYS, TimeUnit.SECONDS, true, true, false);
	}
	
	/**
	 * Equivalent to {@link #durationToLongString(long, TimeUnit, TimeUnit, boolean, boolean, boolean) TimeUnit.durationToLongString(msDuration, TimeUnit.DAYS, TimeUnit.SECONDS, false, false, false)}
	 * @param msDuration
	 * @return
	 */
	public static String durationToParsableString(long msDuration) {
		return durationToLongString(msDuration, TimeUnit.DAYS, TimeUnit.SECONDS, false, false, false);
	}
	

	
	/**
	 * @see {@link com.earth2me.essentials.utils.DateUtil#parseDuration(String, boolean)}
	 */
	public static long parseDuration(String time, boolean future) throws Exception {
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
			for (int i = 0; i < tokenChars.length; i++) {
				char c = tokenChars[i];
				if (Character.isDigit(c)) {
					scanAndRemovePastSuffixes(remainingSuffixes, accSuffix);
					accSuffix = "";
					continue;
				}
				else if (Character.isLetter(c)) {
					accSuffix += c;
				}
				else
					return Collections.emptyList();
			}
			String prefixToken = token.substring(0, token.length() - accSuffix.length());
			return SuggestionsSupplier.collectFilteredStream(remainingSuffixes.stream(), accSuffix)
					.stream()
					.map(str -> prefixToken + str)
					.collect(Collectors.toList());
		};
	}
	
	private static List<String> allSuffixes = Arrays.asList("y", "mo", "w", "d", "h", "m", "s");
	private static List<String> emptyTokenSuggestions = allSuffixes.stream().map(p -> "1" + p).collect(Collectors.toList());
	private static void scanAndRemovePastSuffixes(List<String> suffixes, String foundSuffix) {
		for (int i = 0; i < suffixes.size(); i++) {
			if (foundSuffix.startsWith(suffixes.get(i))) {
				for (int j = i; j >= 0; j--) {
					suffixes.remove(j);
				}
				return;
			}
		}
	}
	

}
