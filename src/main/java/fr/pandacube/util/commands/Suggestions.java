package fr.pandacube.util.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@FunctionalInterface
public interface Suggestions<S> {
	
	
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
	
	
	
	
	
	public static <S> Suggestions<S> empty() { return (s, ti, t, a) -> Collections.emptyList(); }
	
	
	public static <S> Suggestions<S> fromCollection(Collection<String> suggestions) {
		return (s, ti, token, a) -> collectFilteredStream(suggestions.stream(), token);
	}
	
	public static <S> Suggestions<S> fromArray(String... suggestions) {
		return (s, ti, token, a) -> collectFilteredStream(Arrays.stream(suggestions), token);
	}
	
	
	public static <E extends Enum<E>, S> Suggestions<S> fromEnum(Class<E> enumClass) {
		return fromEnumValues(enumClass.getEnumConstants());
	}
	
	public static <E extends Enum<E>, S> Suggestions<S> fromEnum(Class<E> enumClass, boolean lowerCase) {
		return fromEnumValues(lowerCase, enumClass.getEnumConstants());
	}
	
	@SafeVarargs
	public static <E extends Enum<E>, S> Suggestions<S> fromEnumValues(E... enumValues) {
		return fromEnumValues(false, enumValues);
	}
	
	@SafeVarargs
	public static <E extends Enum<E>, S> Suggestions<S> fromEnumValues(boolean lowerCase, E... enumValues) {
		return (s, ti, token, a) -> {
			Stream<String> st = Arrays.stream(enumValues).map(Enum::name);
			if (lowerCase)
				st = st.map(String::toLowerCase);
			return collectFilteredStream(st, token);
		};
	}
	
	
	
	/**
	 * Create a {@link Suggestions} that support greedy strings argument using the suggestion from this {@link Suggestions}.
	 * @param args all the arguments currently in the buffer
	 * @param index the index of the first argument of the greedy string argument
	 * @return
	 */
	public default Suggestions<S> greedyString(int index) { 
		
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
	
	
	
}