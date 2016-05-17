package thedorkknightrises.moviespop.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import thedorkknightrises.moviespop.MovieObj;

/**
 * Created by samri_000 on 5/1/2016.
 */
public class MovieDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Movies.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MovieDb.MovieEntry.TABLE_NAME + " (" +
                    MovieDb.MovieEntry._ID + " INTEGER PRIMARY KEY," +
                    MovieDb.MovieEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    MovieDb.MovieEntry.COLUMN_NAME_OVERVIEW + TEXT_TYPE + COMMA_SEP +
                    MovieDb.MovieEntry.COLUMN_NAME_YEAR + TEXT_TYPE + COMMA_SEP +
                    MovieDb.MovieEntry.COLUMN_NAME_VOTE + TEXT_TYPE + COMMA_SEP +
                    MovieDb.MovieEntry.COLUMN_NAME_POSTER + TEXT_TYPE + COMMA_SEP +
                    MovieDb.MovieEntry.COLUMN_NAME_BACKDROP + TEXT_TYPE + " )";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MovieDb.MovieEntry.TABLE_NAME;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addMovie(int id, String title, String plot, String date, String vote, String poster, String bg) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MovieDb.MovieEntry._ID, id);
        values.put(MovieDb.MovieEntry.COLUMN_NAME_TITLE, title);
        values.put(MovieDb.MovieEntry.COLUMN_NAME_OVERVIEW, plot);
        values.put(MovieDb.MovieEntry.COLUMN_NAME_YEAR, date);
        values.put(MovieDb.MovieEntry.COLUMN_NAME_VOTE, vote);
        values.put(MovieDb.MovieEntry.COLUMN_NAME_POSTER, poster);
        values.put(MovieDb.MovieEntry.COLUMN_NAME_BACKDROP, bg);

        db.insert(MovieDb.MovieEntry.TABLE_NAME, null, values);
        Log.d("DB", "Added");
        db.close();
    }

    public Boolean movieExists(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(MovieDb.MovieEntry.TABLE_NAME, new String[]{MovieDb.MovieEntry._ID}, MovieDb.MovieEntry._ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor.getCount() == 0) {
            cursor.close();
            db.close();
            return false;
        } else {
            cursor.close();
            db.close();
            return true;
        }
    }

    public void deleteMovie(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MovieDb.MovieEntry.TABLE_NAME, MovieDb.MovieEntry._ID + " = ?",
                new String[]{String.valueOf(id)});
        Log.d("DB", "Deleted");
        db.close();
    }

    public ArrayList<MovieObj> getAllMovies() {
        ArrayList<MovieObj> mList = new ArrayList<MovieObj>();
        String selectQuery = "SELECT  * FROM " + MovieDb.MovieEntry.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MovieObj movieObj = new MovieObj(0, "", "", "", "", "", "");
                movieObj.setId(Integer.parseInt(cursor.getString(0)));
                movieObj.setTitle(cursor.getString(1));
                movieObj.setOverview(cursor.getString(2));
                movieObj.setYear(cursor.getString(3));
                movieObj.setRating(cursor.getString(4));
                movieObj.setPosterUrl(cursor.getString(5));
                movieObj.setBackdropUrl(cursor.getString(6));

                mList.add(movieObj);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return mList;
    }
}
