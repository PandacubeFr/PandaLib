package fr.pandacube.lib.core.search;

import java.util.Set;

public interface SearchResult {
	
	public Set<String> getSearchKeywords();
	
	public Set<String> getSuggestionKeywords();

}
