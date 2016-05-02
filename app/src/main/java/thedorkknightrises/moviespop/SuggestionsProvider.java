package thedorkknightrises.moviespop;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by samri_000 on 5/2/2016.
 */
public class SuggestionsProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "thedorkknightrises.moviespop.SuggestionsProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SuggestionsProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
