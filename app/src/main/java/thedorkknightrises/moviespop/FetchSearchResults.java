package thedorkknightrises.moviespop;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by samri_000 on 3/26/2016.
 */
public class FetchSearchResults extends AsyncTask<String, Void, ArrayList<MovieObj>> {

    public GridViewAdapter searchAdapter;
    Context context;
    GridView gridView;
    SharedPreferences pref;
    ArrayList<MovieObj> movieResults;
    Uri buildUri;

    public FetchSearchResults(Context c, GridView v, ArrayList<MovieObj> movieRes, SharedPreferences sPref) {
        super();
        context = c;
        gridView = v;
        pref= sPref;
        movieResults= movieRes;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (!movieResults.isEmpty()) {
            movieResults.clear();
        }
    }

    @Override
    protected ArrayList<MovieObj> doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }
        HttpURLConnection client = null;
        BufferedReader bufferedReader = null;
        String searchJSONstr = null;
        final String SEARCH_BASE_URL =
                "https://api.themoviedb.org/3/movie";
        final String API_KEY = context.getString(R.string.api_key);
        final String API_KEY_PARAM = "api_key";

        String sort;
        sort = pref.getString("sort", "popular");
        buildUri = Uri.parse(SEARCH_BASE_URL).buildUpon()
                .appendPath(sort)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();
        URL url = null;

        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }

        Log.d("URL", url.toString());
        try {
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("GET");
            client.connect();
            InputStream inputStream = client.getInputStream();
            if (inputStream == null)
                return null;
            StringBuilder buffer = new StringBuilder();


            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                buffer.append(line + '\n');
            }

            if (buffer.length() == 0)
                return null;
            searchJSONstr = buffer.toString();
            Log.d("JSON Str", searchJSONstr);
        } catch (ProtocolException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (client != null) {
                client.disconnect();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.e("Main", "Error closing stream", e);
                }
            }
        }

        try

        {
            return getSearchDataFromJson(searchJSONstr);
        } catch (
                Exception e
                )

        {
            String error;
                error = context.getString(R.string.network);

            Snackbar snackbar = Snackbar.make(gridView, error, Snackbar.LENGTH_LONG);
            snackbar.show();
            e.printStackTrace();
        }

        return null;
    }

    private ArrayList<MovieObj> getSearchDataFromJson(String searchJSONstr)
            throws Exception {
        final String LIST_NAME = "results";
        final String MOVIE_ID = "id";
        final String MOVIE_TITLE = "original_title";
        final String MOVIE_YEAR = "release_date";
        final String VOTE_AVG = "vote_average";
        final String MOVIE_POSTER_URL = "poster_path";
        final String BACKDROP_URL = "backdrop_path";
        final String MOVIE_PLOT = "overview";

        JSONObject searchResult = new JSONObject(searchJSONstr);
        int totalResults = searchResult.getInt("total_results");
        if (totalResults == 0) {
            Snackbar snack = Snackbar.make(gridView, context.getString(R.string.no_results), Snackbar.LENGTH_LONG);
            snack.setAction("Action", null);
            snack.show();
        }
        JSONArray movieArray = searchResult.getJSONArray(LIST_NAME);

        String res = pref.getString("res", "w185");
        String res_bg = pref.getString("res_bg", "w780");
        Log.d("movieArray", movieArray.toString());

        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject movieObject = movieArray.getJSONObject(i);
            int id = movieObject.getInt(MOVIE_ID);
            String title = movieObject.getString(MOVIE_TITLE);
            String posterUrl = movieObject.getString(MOVIE_POSTER_URL);
            String year = movieObject.getString(MOVIE_YEAR);
            String plot = movieObject.getString(MOVIE_PLOT);
            String vote_avg = movieObject.getString(VOTE_AVG);
            String bgUrl = movieObject.getString(BACKDROP_URL);

            movieResults.add(new MovieObj(id, title, year, vote_avg, "https://image.tmdb.org/t/p/" + res + posterUrl, plot, "https://image.tmdb.org/t/p/" + res_bg + bgUrl));
        }

        return movieResults;

    }

    @Override
    protected void onPostExecute(ArrayList<MovieObj> movies) {
        super.onPostExecute(movies);

        MainActivity.getSwipeRefreshLayout().setRefreshing(false);
        try {
            searchAdapter = new GridViewAdapter(context, movies);
            gridView.setAdapter(searchAdapter);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
