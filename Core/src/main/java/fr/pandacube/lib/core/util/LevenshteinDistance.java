package fr.pandacube.lib.core.util;

import java.util.Objects;
import java.util.function.ToIntBiFunction;

public class LevenshteinDistance {
	
	private final String initialList;

	private final int elementAdditionScore;
	private final int elementDeletionScore;
	
	private final ToIntBiFunction<Character, Character> elementDistanceFunction;
	
	private int[] prev, curr; 
	
	private int progress = 0;
	
	public LevenshteinDistance(String initList, String finList, int addScore, int delScore, ToIntBiFunction<Character, Character> elemDistFn) {
		initialList = initList == null ? "" : initList;
		elementAdditionScore = addScore;
		elementDeletionScore = delScore;
		elementDistanceFunction = elemDistFn == null ? ((e1, e2) -> Objects.equals(e1, e2) ? 0 : 1) : elemDistFn;

		prev = new int[initialList.length() + 1];
		
		curr = new int[initialList.length() + 1];
		for (int i = 0; i < curr.length; i++)
			curr[i] = i * elementDeletionScore;
		
		add(finList);
	}
	
	public LevenshteinDistance() {
		this(null, null, 1, 1, null);
	}
	
	public LevenshteinDistance(String initList) {
		this(initList, null, 1, 1, null);
	}
	
	public int getCurrentDistance() {
		return curr[curr.length - 1];
	}
	
	public void add(String els) {
		for (char el : els.toCharArray())
			add(el);
	}
	
	public void add(char el) {
		progress++;
		// swap score arrays
		int[] tmp = prev; prev = curr; curr = tmp;
		
		curr[0] = progress * elementAdditionScore;
		
		for (int i = 1; i < curr.length; i++) {
			int S = prev[i - 1] + elementDistanceFunction.applyAsInt(initialList.charAt(i - 1), el);
			int A = prev[i] + elementAdditionScore;
			int D = curr[i - 1] + elementDeletionScore;
			curr[i] = Math.min(S, Math.min(A, D));
		}
	}
	
	
	
}
