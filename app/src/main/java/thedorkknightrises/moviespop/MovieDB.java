package thedorkknightrises.moviespop;

import android.provider.BaseColumns;

/**
 * Created by samri_000 on 5/1/2016.
 */
public final class MovieDb {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public MovieDb() {
    }

    /* Inner class that defines the table contents */
    public static abstract class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "results";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_OVERVIEW = "overview";
        public static final String COLUMN_NAME_YEAR = "year";
        public static final String COLUMN_NAME_VOTE = "vote";
        public static final String COLUMN_NAME_POSTER = "posterUrl";
        public static final String COLUMN_NAME_BACKDROP = "bgUrl";
    }
}