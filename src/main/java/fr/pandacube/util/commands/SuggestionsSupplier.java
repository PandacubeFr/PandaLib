package fr.pandacube.util.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.pandacube.util.ListUtil;

@FunctionalInterface
public interface SuggestionsSupplier<S> {
	
	/**
	 * Number of suggestion visible at once without having to scroll
	 */
	public static int VISIBLE_SUGGESTION_COUNT = 10;
	
	
	public abstract List<String> getSuggestions(S sender, int tokenIndex, String token, String[] args);
	
	
	
	
	
	
	public static Predicate<String> filter(String token) {
		return suggestion -> suggestion.toLowerCase().startsWith(token.toLowerCase());
	}
	
	/**
	 * Filter the provided {@link Stream} of string according to the provided token, using the filter returned by {@link #filter(String)},
	 * then returns the strings collected into a {@link List}.
	 * 
	 * This methods consume the provided stream, so will not be usable anymore.
	 */
	public static List<String> collectFilteredStream(Stream<String> stream, String token) {
		return stream.filter(filter(token)).sorted().collect(Collectors.toList());
	}
	
	
	
	
	
	public static <S> SuggestionsSupplier<S> empty() { return (s, ti, t, a) -> Collections.emptyList(); }
	
	
	public static <S> SuggestionsSupplier<S> fromCollection(Collection<String> suggestions) {
		return (s, ti, token, a) -> collectFilteredStream(suggestions.stream(), token);
	}
	
	public static <S> SuggestionsSupplier<S> fromArray(String... suggestions) {
		return (s, ti, token, a) -> collectFilteredStream(Arrays.stream(suggestions), token);
	}
	
	
	public static <E extends Enum<E>, S> SuggestionsSupplier<S> fromEnum(Class<E> enumClass) {
		return fromEnumValues(enumClass.getEnumConstants());
	}
	
	public static <E extends Enum<E>, S> SuggestionsSupplier<S> fromEnum(Class<E> enumClass, boolean lowerCase) {
		return fromEnumValues(lowerCase, enumClass.getEnumConstants());
	}
	
	@SafeVarargs
	public static <E extends Enum<E>, S> SuggestionsSupplier<S> fromEnumValues(E... enumValues) {
		return fromEnumValues(false, enumValues);
	}
	
	@SafeVarargs
	public static <E extends Enum<E>, S> SuggestionsSupplier<S> fromEnumValues(boolean lowerCase, E... enumValues) {
		return (s, ti, token, a) -> {
			Stream<String> st = Arrays.stream(enumValues).map(Enum::name);
			if (lowerCase)
				st = st.map(String::toLowerCase);
			return collectFilteredStream(st, token);
		};
	}
	

	/**
	 * Create a {@link SuggestionsSupplier} that suggest numbers according to the provided range.
	 * 
	 * The current implementation only support range that include either -1 or 1.
	 * @param min
	 * @param max
	 * @return
	 */
	public static <S> SuggestionsSupplier<S> fromIntRange(int min, int max) {
		return fromLongRange(min, max);
	}
	
	
	
	
	/**
	 * Create a {@link SuggestionsSupplier} that suggest numbers according to the provided range.
	 * 
	 * The current implementation only support range that include either -1 or 1.
	 * @param min
	 * @param max
	 * @return
	 */
	public static <S> SuggestionsSupplier<S> fromLongRange(long min, long max) {
		if (max < min) {
			throw new IllegalArgumentException("min should be less or equals than max");
		}
		return (s, ti, token, a) -> {
			try {
				List<Long> proposedValues = new ArrayList<>();
				if (token.length() == 0) {
					long start = Math.max(Math.max(Math.min(-4, max - 9), min), -9);
					long end = Math.min(Math.min(start + 9, max), 9);
					ListUtil.addLongRangeToList(proposedValues, start, end);
				}
				else if (token.length() == 1) {
					if (token.charAt(0) == '0') {
						if (min > 0 || max < 0) {
							return Collections.emptyList();
						}
						else
							return Collections.singletonList(token);
					}
					else if (token.charAt(0) == '-') {
						ListUtil.addLongRangeToList(proposedValues, Math.max(-9, min), -1);
					}
					else {
						long lToken = Long.parseLong(token);
						if (lToken > max) {
							return Collections.emptyList();
						}
						
						lToken *= 10;
						if (lToken > max) {
							return Collections.singletonList(token);
						}
						
						ListUtil.addLongRangeToList(proposedValues, lToken, Math.min(lToken + 9, max));
					}
				}
				else {
					long lToken = Long.parseLong(token);
					if (lToken < min || lToken > max) {
						return Collections.emptyList();
					}
					
					lToken *= 10;
					if (lToken < min || lToken > max) {
						return Collections.singletonList(token);
					}
					
					if (lToken < 0) {
						ListUtil.addLongRangeToList(proposedValues, Math.max(lToken - 9, min), lToken);
					}
					else {
						ListUtil.addLongRangeToList(proposedValues, lToken, Math.min(lToken + 9, max));
					}
				}
				
				return collectFilteredStream(proposedValues.stream().map(i -> i.toString()), token);
			} catch (NumberFormatException e) {
				return Collections.emptyList();
			}
		};
	}
	
	/**
	 * Create a {@link SuggestionsSupplier} that support greedy strings argument using the suggestion from this {@link SuggestionsSupplier}.
	 * @param args all the arguments currently in the buffer
	 * @param index the index of the first argument of the greedy string argument
	 * @return
	 */
	public default SuggestionsSupplier<S> greedyString(int index) { 
		
		return (s, ti, token, args) -> {
			
			if (ti < index)
				return Collections.emptyList();
			
			String gToken = AbstractCommand.getLastParams(args, index);
			String[] splitGToken = gToken.split(" ", -1);	
			int currentTokenPosition = splitGToken.length - 1;
			String[] prevWordsGToken = Arrays.copyOf(splitGToken, currentTokenPosition);
			
			String[] argsWithMergedGreedyToken = Arrays.copyOf(args, index + 1);
			argsWithMergedGreedyToken[index] = gToken;
			
			List<String> currentTokenProposal = new ArrayList<>();
			for (String suggestion : getSuggestions(s, index, gToken, argsWithMergedGreedyToken)) {
				String[] splitSuggestion = suggestion.split(" ", -1);
				if (splitSuggestion.length <= currentTokenPosition)
					continue;
				if (!Arrays.equals(Arrays.copyOf(splitGToken, currentTokenPosition), prevWordsGToken))
					continue;
				if (splitSuggestion[currentTokenPosition].isEmpty())
					continue;
				
				currentTokenProposal.add(splitSuggestion[currentTokenPosition]);
			}
			return currentTokenProposal;
		};
	}
	
	
	
	public default SuggestionsSupplier<S> requires(Predicate<S> check) {
		return (s, ti, to, a) -> {
			return check.test(s) ? getSuggestions(s, ti, to, a) : Collections.emptyList();
		};
	}
	
	
}