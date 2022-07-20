package fr.pandacube.lib.core.search;

import java.util.Set;

public interface SearchResult {
	
	Set<String> getSearchKeywords();
	
	Set<String> getSuggestionKeywords();

}
