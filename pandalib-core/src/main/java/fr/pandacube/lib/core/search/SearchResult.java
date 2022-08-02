package fr.pandacube.lib.core.search;

import java.util.Set;

/**
 * An entry in the {@link SearchEngine}.
 */
public interface SearchResult {

	/**
	 * Returns the keywords corresponding to this search result.
	 * @return the keywords corresponding to this search result.
	 */
	Set<String> getSearchKeywords();

	/**
	 * Returns the keywords to suggest corresponding to this search result.
	 * It may be different from the search keywords.
	 * @return the keywords to suggest corresponding to this search result.
	 */
	Set<String> getSuggestionKeywords();

}
