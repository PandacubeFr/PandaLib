package fr.pandacube.lib.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class to track and limit the amount of a specific value for a specified amount of duration.
 *
 * An exemple of application is for rolling expense limit of a debit card: you cannot expense more that {@code $X}
 * during a rolling period of {@code $Y} time.
 *
 * Here is an example usage of this class:
 * <pre>
 *     AmountPerTimeLimiter instance = new AmountPerTimeLimiter(X, Y);
 *     void tryExpense(double amount) {
 *         if (instance.canAdd(amount)) {
 *             // do the action (here, it’s the expense)
 *             instance.add(amount);
 *         }
 *     }
 * </pre>
 */
public class AmountPerTimeLimiter {
	private final double maxAmount;
	private final long duration;
	
	private List<Entry> entries = new ArrayList<>();

	/**
	 * Create a new instance of {@link AmountPerTimeLimiter}
	 * @param maximumAmount the maximum amount possible in the specified interval
	 * @param timeInterval the interval in milliseconds
	 */
	public AmountPerTimeLimiter(double maximumAmount, long timeInterval) {
		maxAmount = maximumAmount;
		duration = timeInterval;
	}


	/**
	 * Compute and returns the amount considered for the current time interval.
	 * @return the amount considered.
	 */
	public synchronized double getAmountSinceDuration() {
		long currT = System.currentTimeMillis();
		entries = entries.stream()
				.filter(e -> e.time > currT - duration)
				.collect(Collectors.toList());
				
		return entries.stream()
				.mapToDouble(e -> e.amount)
				.sum();
	}

	/**
	 * Register the provided amount into this limiter, at the current system time.
	 * @param amount the amount added.
	 */
	public synchronized void add(double amount) {
		long currT = System.currentTimeMillis();
		if (!entries.isEmpty() && entries.get(entries.size()-1).time == currT)
			entries.get(entries.size()-1).amount += amount;
		else
			entries.add(new Entry(currT, amount));
	}

	/**
	 * Determine if the provided amount can be added into the limiter without exceeding the limit.
	 * @param amount the amount to test.
	 * @return if it’s possible to add that amount now.
	 */
	public boolean canAdd(double amount) {
		return getAmountSinceDuration() + amount < maxAmount;
	}

	/**
	 * Gets the amount that can be added right now into the limiter.
	 * @return the maximum amount that can be added.
	 */
	public double getRemainingForNow() {
		return Math.max(0, maxAmount - getAmountSinceDuration());
	}





	private static class Entry {
		private final long time;
		private double amount;
		public Entry(long t, double a) {
			time = t; amount = a;
		}
	}
}
