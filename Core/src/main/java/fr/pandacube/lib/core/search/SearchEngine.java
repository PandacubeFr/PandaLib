package fr.pandacube.lib.core.search;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import fr.pandacube.lib.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Utility class to manage searching among a set of
 * SearchResult instances, using case insensitive
 * keywords.
 */
public class SearchEngine<R extends SearchResult> {

	private final Map<String, Set<R>> searchKeywordsResultMap = new HashMap<>();
	private final Map<R, Set<String>> resultsSearchKeywordsMap = new HashMap<>();

	private final Map<String, Set<R>> suggestionsKeywordsResultMap = new HashMap<>();
	private final Map<R, Set<String>> resultsSuggestionsKeywordsMap = new HashMap<>();

	private final Set<R> resultSet = new HashSet<>();
	
	private final Cache<Set<String>, List<String>> suggestionsCache;
	
	public SearchEngine(int suggestionsCacheSize) {
		suggestionsCache = CacheBuilder.newBuilder()
				.maximumSize(suggestionsCacheSize)
				.build();
	}
	
	public synchronized void addResult(R result) {
		if (result == null) 
			throw new IllegalArgumentException("Provided result cannot be null.");
		if (resultSet.contains(result))
			return;
		
		Set<String> searchKw;
		try {
			searchKw = result.getSearchKeywords();
			Objects.requireNonNull(searchKw, "SearchResult instance must provide a non null set of search keywords");
			searchKw = searchKw.stream()
					.filter(Objects::nonNull)
					.map(String::toLowerCase)
					.collect(Collectors.toSet());
		} catch (Exception e) {
			Log.severe(e);
			return;
		}
		
		Set<String> suggestsKw;
		try {
			suggestsKw = result.getSuggestionKeywords();
			Objects.requireNonNull(suggestsKw, "SearchResult instance must provide a non null set of suggestions keywords");
			suggestsKw = new HashSet<>(suggestsKw);
			suggestsKw.removeIf(Objects::isNull);
		} catch (Exception e) {
			Log.severe(e);
			return;
		}
		
		resultSet.add(result);
		
		for (String skw : searchKw) {
			searchKeywordsResultMap.computeIfAbsent(skw, s -> new HashSet<>()).add(result);
		}
		
		resultsSearchKeywordsMap.put(result, searchKw);
		
		resultsSuggestionsKeywordsMap.put(result, suggestsKw);
		
		for (String skw : suggestsKw) {
			suggestionsKeywordsResultMap.computeIfAbsent(skw, s -> new HashSet<>()).add(result);
		}
		
		suggestionsCache.invalidateAll();
	}
	
	public synchronized void removeResult(R result) {
		if (result == null || !resultSet.contains(result))
			return;

		resultSet.remove(result);
		
		Set<String> searchKw = resultsSearchKeywordsMap.remove(result);
		if (searchKw != null) {
			for (String skw : searchKw) {
				Set<R> set = searchKeywordsResultMap.get(skw);
				if (set == null)
					continue;
				set.remove(result);
				if (set.isEmpty())
					searchKeywordsResultMap.remove(skw);
			}
		}
		
		Set<String> suggestsKw = resultsSearchKeywordsMap.remove(result);
		if (suggestsKw != null) {
			for (String skw : suggestsKw) {
				Set<R> set = suggestionsKeywordsResultMap.get(skw);
				if (set == null)
					continue;
				set.remove(result);
				if (set.isEmpty())
					suggestionsKeywordsResultMap.remove(skw);
			}
		}
		
		resultsSuggestionsKeywordsMap.remove(result);
		
		suggestionsCache.invalidateAll();
	}
	
	public synchronized Set<R> search(Set<String> searchTerms) {
		if (searchTerms == null)
			searchTerms = new HashSet<>();
		
		Set<R> retainedResults = new HashSet<>(resultSet);
		for (String term : searchTerms) {
			retainedResults.retainAll(search(term));
		}
		
		return retainedResults;
	}
	
	public synchronized Set<R> search(String searchTerm) {
		if (searchTerm == null || searchTerm.isEmpty()) {
			return new HashSet<>(resultSet);
		}
		searchTerm = searchTerm.toLowerCase();
		Set<R> retainedResults = new HashSet<>();
		for (String skw : searchKeywordsResultMap.keySet()) {
			if (skw.contains(searchTerm)) {
				retainedResults.addAll(new ArrayList<>(searchKeywordsResultMap.get(skw)));
			}
		}
		
		return retainedResults;
	}
	
	public synchronized List<String> suggestKeywords(List<String> prevSearchTerms) {
		if (prevSearchTerms == null || prevSearchTerms.isEmpty()) {
			return new ArrayList<>(suggestionsKeywordsResultMap.keySet());
		}
		Set<String> lowerCaseSearchTerm = prevSearchTerms.stream()
				.map(String::toLowerCase)
				.collect(Collectors.toSet());
		
		try {
			return suggestionsCache.get(lowerCaseSearchTerm, () -> {
				Set<R> prevResults = search(lowerCaseSearchTerm);
				
				Set<String> suggestions = new HashSet<>();
				for (R prevRes : prevResults) {
					suggestions.addAll(new ArrayList<>(resultsSuggestionsKeywordsMap.get(prevRes)));
				}
				
				suggestions.removeIf(s -> {
					for (String st : lowerCaseSearchTerm)
						if (s.contains(st))
							return true;
					return false;
				});
				
				return new ArrayList<>(suggestions);
			});
		} catch (ExecutionException e) {
			Log.severe(e);
			return new ArrayList<>(suggestionsKeywordsResultMap.keySet());
		}
		
		
	}
	
	// TODO sort results
	
}
