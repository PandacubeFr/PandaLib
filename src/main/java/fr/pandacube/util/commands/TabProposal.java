package fr.pandacube.util.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@FunctionalInterface
public interface TabProposal {
	
	
	public abstract List<String> getProposal(String token);
	
	
	public static Predicate<String> filter(String token) {
		return (proposal) -> proposal.toLowerCase().startsWith(token.toLowerCase());
	}
	
	public static List<String> filterStream(Stream<String> stream, String token) {
		return stream.filter(filter(token)).sorted().collect(Collectors.toList());
	}
	
	
	
	

	public static TabProposal empty() { return t -> Collections.emptyList(); }
	
	
	public static <E extends Enum<E>> TabProposal fromEnum(Class<E> enumClass) {
		return fromEnumValues(enumClass.getEnumConstants());
	}
	
	@SafeVarargs
	public static <E extends Enum<E>> TabProposal fromEnumValues(E... enumValues) {
		return fromStream(Arrays.stream(enumValues).map(Enum::name));
	}

	public static TabProposal fromCollection(Collection<String> proposals) { 
		return fromStream(proposals.stream());
	}
	
	public static TabProposal fromIntRange(int startIncluded, int endIncluded) {
		return fromStream(IntStream.rangeClosed(startIncluded, endIncluded).mapToObj(Integer::toString));
	}

	public static TabProposal fromStream(Stream<String> proposals) { 
		return t -> filterStream(proposals, t);
	}
	
	/**
	 * Allow tab completion to supply proposal from multi-args (arguments with space,
	 * generally the last argument of a command) parameters
	 * @param args all the arguments currently in the buffer
	 * @param index the index of the first argument of the multi-args parameter
	 * @param proposals all possible proposals for the multi-args parameter
	 * @return
	 */
	public static TabProposal withMultiArgsLastParam(String[] args, int index, Collection<String> proposals) { 
		String lastParamToken = AbstractCommand.getLastParams(args, index);
		String[] splittedToken = lastParamToken.split(" ", -1);	
		int currentTokenPosition = splittedToken.length - 1;
		String[] previousTokens = Arrays.copyOf(splittedToken, currentTokenPosition);
		
		return token -> {
			List<String> currentTokenProposal = new ArrayList<>();
			for (String p : proposals) {
				String[] splittedProposal = p.split(" ", -1);
				if (splittedProposal.length <= currentTokenPosition)
					continue;
				if (!Arrays.equals(Arrays.copyOf(splittedToken, currentTokenPosition), previousTokens))
					continue;
				if (splittedProposal[currentTokenPosition].isEmpty())
					continue;
				
				if (filter(token).test(splittedProposal[currentTokenPosition]))
					currentTokenProposal.add(splittedProposal[currentTokenPosition]);
			}
			return currentTokenProposal;
		};
	}
}