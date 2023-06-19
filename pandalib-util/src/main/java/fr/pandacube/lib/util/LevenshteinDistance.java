package fr.pandacube.lib.util;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToIntBiFunction;

/**
 * Implementation of the <a href="https://en.wikipedia.org/wiki/Levenshtein_distance">Levenshtein distance algorithm</a>
 * that operate on characters. Its purpose is to compute a "distance" between two strings of characters, that represents
 * how many edition operations must be performed on the first string ({@code initialString}) to obtain the second
 * one ({@code finalString}).
 * <p>
 * All the parameters of the algorithm are configurable:
 * <ul>
 *     <li>The score of adding a character</li>
 *     <li>The score of removing a character</li>
 *     <li>The score of replacing any pair of character</li>
 * </ul>
 *
 * A simple usage of this class is to call the constructor {@link #LevenshteinDistance(String, String, int, int,
 * ToIntBiFunction)} (for a full control of the parameters) or {@link #LevenshteinDistance(String, String)} (to keep the
 * default parameters value); then to call the method {@link #getCurrentDistance()} to compute the Levenshtein distance
 * between the two strings.
 * <p>
 * A more advanced usage offer the possibility to progressively compute a distance from a predefined
 * {@code initialString} to a {@code finalString} that is fed progressively using {@link #add(char)} or
 * {@link #add(String)}. This is useful if the {@code finalString} is an input that is currently being typed by the
 * user, so the application can progressively update a list of suggested words based on the distance.
 * For this usage, you can use those constructors to avoid initializing the {@code finalString}:
 * {@link #LevenshteinDistance(String, int, int, ToIntBiFunction)} or {@link #LevenshteinDistance(String)}.
 */
public class LevenshteinDistance {
	
	private final String initialString;

	private final int elementAdditionScore;
	private final int elementDeletionScore;
	
	private final ToIntBiFunction<Character, Character> elementDistanceFunction;
	
	private int[] prev, curr; // dynamic programming interval arrays
	
	private int progress = 0;

	/**
	 * Create a new instance of {@link LevenshteinDistance} that compute the edit-distance between {@code initialString}
	 * and {@code finalString}.
	 * <p>
	 * The score of each edition action is provided as parameters.
	 *
	 * @param initialString the initial string. Cannot be null.
	 * @param finalString the final string. Can be null, and may be provided later using {@link #add(String)} or
	 *                    character after character using {@link #add(char)}.
	 * @param elementAdditionScore the score for adding a character.
	 * @param elementDeletionScore the score for removing a character.
	 * @param elementDistanceFunction a {@link Function} that computes the score for replacing the character provided as
	 *                                first argument, to the character provided as second argument. If it is null, it
	 *                                will use a default function that will return 0 if the two characters are equals, 1
	 *                                otherwise.
	 */
	public LevenshteinDistance(String initialString, String finalString, int elementAdditionScore, int elementDeletionScore, ToIntBiFunction<Character, Character> elementDistanceFunction) {
		this.initialString = Objects.requireNonNull(initialString, "initialList");
		this.elementAdditionScore = elementAdditionScore;
		this.elementDeletionScore = elementDeletionScore;
		this.elementDistanceFunction = elementDistanceFunction == null ? ((e1, e2) -> Objects.equals(e1, e2) ? 0 : 1) : elementDistanceFunction;

		prev = new int[this.initialString.length() + 1];

		curr = new int[this.initialString.length() + 1];
		for (int i = 0; i < curr.length; i++)
			curr[i] = i * this.elementDeletionScore;

		if (finalString != null)
			add(finalString);
	}

	/**
	 * Create a new instance of {@link LevenshteinDistance} that will compute the edit-distance between
	 * {@code initialString} and a final string  provided later using {@link #add(String)} or character after character
	 * using {@link #add(char)}.
	 * <p>
	 * The score of each edition action is provided as parameters.
	 *
	 * @param initialString the initial string. Cannot be null.
	 * @param elementAdditionScore the score for adding a character.
	 * @param elementDeletionScore the score for removing a character.
	 * @param elementDistanceFunction a {@link Function} that computes the score for replacing the character provided as
	 *                                first argument, to the character provided as second argument. If it is null, it
	 *                                will use a default function that will return 0 if the two characters are equals, 1
	 *                                otherwise.
	 */
	public LevenshteinDistance(String initialString, int elementAdditionScore, int elementDeletionScore, ToIntBiFunction<Character, Character> elementDistanceFunction) {
		this(initialString, null, elementAdditionScore, elementDeletionScore, elementDistanceFunction);
	}

	/**
	 * Create a new instance of {@link LevenshteinDistance} that compute the edit-distance between {@code initialString}
	 * and {@code finalString}.
	 * <p>
	 * All the edition action are valued to 1. To customize the edition action score, use
	 * {@link #LevenshteinDistance(String, String, int, int, ToIntBiFunction)}.
	 *
	 * @param initialString the initial string. Cannot be null.
	 * @param finalString the final string. Can be null, and may be provided later using {@link #add(String)} or
	 *                    character after character using {@link #add(char)}.
	 */
	public LevenshteinDistance(String initialString, String finalString) {
		this(initialString, finalString, 1, 1, null);
	}

	/**
	 * Create a new instance of {@link LevenshteinDistance} that will compute the edit-distance between
	 * {@code initialString} and a final string  provided later using {@link #add(String)} or character after character
	 * using {@link #add(char)}.
	 * <p>
	 * All the edition action are valued to 1. To customize the edition action score, use
	 * {@link #LevenshteinDistance(String, int, int, ToIntBiFunction)}.
	 *
	 * @param initialString the initial string. Cannot be null.
	 */
	public LevenshteinDistance(String initialString) {
		this(initialString, null);
	}

	/**
	 * Add the provided string of characters to the {@code finalString}, and update the Levenshtein distance returned by
	 * {@link #getCurrentDistance()}.
	 * @param els the string of character.
	 */
	public void add(String els) {
		for (char el : els.toCharArray())
			add(el);
	}

	/**
	 * Add a single character to the {@code finalString}, and update the Levenshtein distance returned by
	 * 	 * {@link #getCurrentDistance()}.
	 * @param el the character.
	 */
	public void add(char el) {
		progress++;
		// swap score arrays
		int[] tmp = prev; prev = curr; curr = tmp;
		
		curr[0] = progress * elementAdditionScore;
		
		for (int i = 1; i < curr.length; i++) {
			int S = prev[i - 1] + elementDistanceFunction.applyAsInt(initialString.charAt(i - 1), el);
			int A = prev[i] + elementAdditionScore;
			int D = curr[i - 1] + elementDeletionScore;
			curr[i] = Math.min(S, Math.min(A, D));
		}
	}

	/**
	 * Get the currently calculated Levenshtein distance from the {@code initialString} to the already provided
	 * {@code finalString}.
	 * @return the Levenshtein distance.
	 */
	public int getCurrentDistance() {
		return curr[curr.length - 1];
	}

}
