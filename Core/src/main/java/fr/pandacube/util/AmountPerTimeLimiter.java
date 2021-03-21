package fr.pandacube.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AmountPerTimeLimiter {
	private final double maxAmount;
	private final long duration;
	
	private List<Entry> entries = new ArrayList<>();
	
	public AmountPerTimeLimiter(double a, long d) {
		maxAmount = a;
		duration = d;
	}
	
	
	
	
	
	private class Entry {
		private final long time;
		private double amount;
		public Entry(long t, double a) {
			time = t; amount = a;
		}
	}
	
	
	public synchronized double getAmountSinceDuration() {
		long currT = System.currentTimeMillis();
		entries = entries.stream()
				.filter(e -> e.time > currT - duration)
				.collect(Collectors.toList());
				
		return entries.stream()
				.mapToDouble(e -> e.amount)
				.sum();
	}
	
	public synchronized void add(double amount) {
		long currT = System.currentTimeMillis();
		if (!entries.isEmpty() && entries.get(entries.size()-1).time > currT - 1000)
			entries.get(entries.size()-1).amount += amount;
		else
			entries.add(new Entry(System.currentTimeMillis(), amount));
	}
	
	public boolean canAdd(double amount) {
		return getAmountSinceDuration() + amount < maxAmount;
	}
	
	public double getRemainingForNow() {
		return Math.max(0, maxAmount - getAmountSinceDuration());
	}
}
