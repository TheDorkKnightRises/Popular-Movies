package thedorkknightrises.moviespop;

import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

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
 * Created by samri_000 on 4/30/2016.
 */
public class SearchResultsActivity extends AppCompatActivity {
    public ArrayList<MovieObj> movieResults = new ArrayList<>();
    GridView gridView;
    private int mCurCheckPosition;
    private Boolean mDualPane;
    private Boolean changed = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View detailsFrame = findViewById(R.id.details);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        gridView = (GridView) this.findViewById(R.id.gridview);
        gridView.setNumColumns(1);

        ((TextView) findViewById(R.id.label)).setText(R.string.search_hint);

        if (savedInstanceState == null)
            handleIntent(getIntent());
        else {
            movieResults = (ArrayList<MovieObj>) savedInstanceState.getSerializable("results");
            GridViewAdapter mAdapter = new GridViewAdapter(this, movieResults);
            gridView.setAdapter(mAdapter);

            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
            changed = savedInstanceState.getBoolean("changed", false);
        }

        if (mDualPane)
            showDetails(mCurCheckPosition);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            changed = true;
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            if (!query.trim().equals(""))
                new SearchMovie(this, getSharedPreferences("Prefs", MODE_PRIVATE)).execute(query);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SuggestionsProvider.AUTHORITY, SuggestionsProvider.MODE);
            suggestions.saveRecentQuery(query, null);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt("curChoice", mCurCheckPosition);
        state.putBoolean("changed", changed);
        state.putSerializable("results", movieResults);
    }

    public void showDetails(int index) {
        mCurCheckPosition = index;

        if (mDualPane) {

            // Check what fragment is currently shown, replace if needed.
            Detail_Fragment details = (Detail_Fragment) getFragmentManager()
                    .findFragmentById(R.id.details);


            try {

                MovieObj m = (MovieObj) gridView.getItemAtPosition(index);

                Bundle bundle = new Bundle();
                bundle.putInt("id", m.getId());
                bundle.putString("title", m.getTitle());
                bundle.putString("release_date", m.getYear());
                bundle.putString("vote_avg", m.getRating());
                bundle.putString("overview", m.getOverview());
                bundle.putString("poster", m.getPosterUrl());
                bundle.putString("bg", m.getBackdropUrl());

                findViewById(R.id.blankText).setVisibility(View.GONE);


                if (details == null || details.getShownIndex() != index || changed) {

                    // Make new fragment to show this selection.
                    details = Detail_Fragment.newInstance(index, bundle);


                    // Execute a transaction, replacing any existing fragment
                    // with this one inside the frame.
                    FragmentTransaction ft = getFragmentManager()
                            .beginTransaction();
                    ft.replace(R.id.details, details);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.commit();
                }
            } catch (NullPointerException | IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

        }
    }

    public void showDetails(View v) {

        int index = gridView.getPositionForView(v);
        mCurCheckPosition = index;

        if (mDualPane) {

            // Check what fragment is currently shown, replace if needed.
            Detail_Fragment details = (Detail_Fragment) getFragmentManager()
                    .findFragmentById(R.id.details);

            findViewById(R.id.blankText).setVisibility(View.GONE);

            MovieObj m = (MovieObj) gridView.getItemAtPosition(index);

            Bundle bundle = new Bundle();
            bundle.putInt("id", m.getId());
            bundle.putString("title", m.getTitle());
            bundle.putString("release_date", m.getYear());
            bundle.putString("vote_avg", m.getRating());
            bundle.putString("overview", m.getOverview());
            bundle.putString("poster", m.getPosterUrl());
            bundle.putString("bg", m.getBackdropUrl());


            if (details == null || details.getShownIndex() != index || changed) {
                changed = false;

                // Make new fragment to show this selection.
                details = Detail_Fragment.newInstance(index, bundle);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                FragmentTransaction ft = getFragmentManager()
                        .beginTransaction();
                ft.replace(R.id.details, details);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();
            }

        } else {
            changed = true;

            MovieObj m = (MovieObj) gridView.getItemAtPosition(index);

            Intent i = new Intent(SearchResultsActivity.this, Details.class);
            i.putExtra("id", m.id);
            i.putExtra("title", m.title);
            i.putExtra("release_date", m.year);
            i.putExtra("vote_avg", m.vote_avg);
            i.putExtra("overview", m.overview);
            i.putExtra("poster", m.posterUrl);
            i.putExtra("bg", m.bgUrl);

            startActivity(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        android.support.v7.widget.SearchView searchView =
                (android.support.v7.widget.SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryRefinementEnabled(true);
        searchView.onActionViewExpanded();
        searchView.setIconified(false);

        return true;
    }

    public class SearchMovie extends AsyncTask<String, Void, ArrayList<MovieObj>> {

        public GridViewAdapter searchAdapter;
        Context context;
        Uri buildUri;
        String query;
        SharedPreferences pref;

        public SearchMovie(Context c, SharedPreferences spref) {
            super();
            context = c;
            pref = spref;
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
            } else query = params[0];
            HttpURLConnection client = null;
            BufferedReader bufferedReader = null;
            String searchJSONstr = null;
            final String SEARCH_BASE_URL =
                    "https://api.themoviedb.org/3/search/movie";
            final String API_KEY = context.getString(R.string.api_key);
            final String API_KEY_PARAM = "api_key";
            final String QUERY_PARAM = "query";

            buildUri = Uri.parse(SEARCH_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, query)
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
                String error = context.getString(R.string.network);

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

            try {
                searchAdapter = new GridViewAdapter(context, movies);
                gridView.setAdapter(searchAdapter);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
