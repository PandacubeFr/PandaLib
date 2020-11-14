package fr.pandacube.util.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import fr.pandacube.util.Log;

/**
 * Utility class to manage searching among a set of
 * SearchResult instances, using case insensitive
 * keywords.
 */
public class SearchEngine<R extends SearchResult> {

	Map<String, Set<R>> searchKeywordsResultMap = new HashMap<>();
	Map<R, Set<String>> resultsSearchKeywordsMap = new HashMap<>();

	Map<String, Set<R>> suggestionsKeywordsResultMap = new HashMap<>();
	Map<R, Set<String>> resultsSuggestionsKeywordsMap = new HashMap<>();
	
	Set<R> resultSet = new HashSet<>();
	
	private Cache<Set<String>, List<String>> suggestionsCache;
	
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
			Preconditions.checkNotNull(searchKw, "SearchResult instance must provide a non null set of search keywords");
			searchKw = searchKw.stream()
					.filter(e -> e != null)
					.map(String::toLowerCase)
					.collect(Collectors.toSet());
		} catch (Exception e) {
			Log.severe(e);
			return;
		}
		
		resultSet.add(result);
		
		for (String skw : searchKw) {
			searchKeywordsResultMap.computeIfAbsent(skw, s -> new HashSet<>()).add(result);
		}
		
		resultsSearchKeywordsMap.put(result, searchKw);
		
		Set<String> suggestsKw;
		try {
			suggestsKw = result.getSuggestionKeywords();
			Preconditions.checkNotNull(suggestsKw, "SearchResult instance must provide a non null set of suggestions keywords");
			suggestsKw.removeIf(e -> e == null);
		} catch (Exception e) {
			Log.severe(e);
			return;
		}
		
		resultsSuggestionsKeywordsMap.put(result, new HashSet<>(suggestsKw));
		
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
		for (String skw : searchKw) {
			Set<R> set = searchKeywordsResultMap.get(skw);
			set.remove(result);
			if (set.isEmpty())
				searchKeywordsResultMap.remove(skw);
		}
		
		Set<String> suggestsKw = resultsSearchKeywordsMap.remove(result);
		for (String skw : suggestsKw) {
			Set<R> set = suggestionsKeywordsResultMap.get(skw);
			set.remove(result);
			if (set.isEmpty())
				suggestionsKeywordsResultMap.remove(skw);
		}
		
		resultsSuggestionsKeywordsMap.remove(result);
		
		suggestionsCache.invalidateAll();
	}
	
	public synchronized Set<R> search(Set<String> searchTerms) {
		if (searchTerms == null)
			searchTerms = new HashSet<String>();
		
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
			return suggestionsCache.get(lowerCaseSearchTerm, (Callable<List<String>>) () -> {
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
