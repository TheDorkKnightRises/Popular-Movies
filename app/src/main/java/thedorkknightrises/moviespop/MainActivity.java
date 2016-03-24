package thedorkknightrises.moviespop;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    GridView mGridView;
    public ArrayList<MovieObj> movieResults = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = getPreferences(Context.MODE_PRIVATE);
                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);
                //  If the activity has never started before...
                if (isFirstStart) {
                    //  Launch app intro
                    Intent i = new Intent(MainActivity.this, Intro.class);
                    startActivity(i);
                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();
                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);
                    //  Apply changes
                    e.apply();
                }
            }
        });
        // Start the thread
        t.start();

        mGridView=(GridView) findViewById(R.id.gridview);

        TextView label= (TextView) findViewById(R.id.label);

        SharedPreferences pref= getSharedPreferences("Prefs",MODE_PRIVATE);
        String s=pref.getString("sort","popular");
        if (s.equals("popular"))
            label.setText(getString(R.string.pop));
        else if (s.equals("top_rated"))
            label.setText(getString(R.string.high));
        else if (s.equals("fav"))
            label.setText(getString(R.string.fav));
        else if (s.equals("upcoming"))
            label.setText(getString(R.string.up));
        else if (s.equals("now_playing"))
            label.setText(R.string.now);

        if(savedInstanceState==null)
            populateMovies();
        else {
            movieResults=(ArrayList<MovieObj>) savedInstanceState.getSerializable("results");
            GridViewAdapter mAdapter= new GridViewAdapter(this, movieResults);
            mGridView.setAdapter(mAdapter);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle state)
    {
        super.onSaveInstanceState(state);

        state.putSerializable("results", movieResults);
    }

    public void onClick(View v)
    {
        int p=mGridView.getPositionForView(v);
        MovieObj m= (MovieObj) mGridView.getItemAtPosition(p);
        Intent i=new Intent(MainActivity.this, Details.class);
        i.putExtra("id", m.id);
        i.putExtra("title", m.title);
        i.putExtra("release_date", m.year);
        i.putExtra("vote_avg", m.vote_avg);
        i.putExtra("plot", m.plot);
        i.putExtra("poster", m.posterUrl);
        i.putExtra("bg", m.bgUrl);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh) {
            populateMovies();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        SharedPreferences pref= getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();

        TextView label= (TextView) findViewById(R.id.label);

        if (id == R.id.nav_fav) {
            edit.putString("sort", "fav");
            label.setText(getString(R.string.fav));
            edit.commit();
            populateMovies();
        } else if (id == R.id.nav_pop) {
            edit.putString("sort", "popular");
            label.setText(getString(R.string.pop));
            edit.commit();
            populateMovies();
        } else if (id == R.id.nav_high) {
            edit.putString("sort", "top_rated");
            label.setText(getString(R.string.high));
            edit.commit();
            populateMovies();
        } else if (id == R.id.nav_up) {
            edit.putString("sort", "upcoming");
            label.setText(getString(R.string.up));
            edit.commit();
            populateMovies();
        } else if (id == R.id.nav_now) {
            edit.putString("sort", "now_playing");
            label.setText(R.string.now);
            edit.commit();
            populateMovies();
        } else if (id == R.id.nav_about) {
            Intent i= new Intent(MainActivity.this, About.class);
            startActivity(i);
        } else if (id == R.id.nav_settings) {
            Intent i= new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(i);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void populateMovies() {

        new FetchSearchResults(this, mGridView).execute("discover");

    }


    public class FetchSearchResults extends AsyncTask<String, Void, ArrayList<MovieObj>> {

        SharedPreferences pref= getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        Context context;
        public GridViewAdapter searchAdapter;
        GridView gridView;

        Uri buildUri;

        public FetchSearchResults(Context c, GridView v) {
            super();
            context = c;
            gridView = v;
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
            final String API_KEY = getString(R.string.api_key);
            final String API_KEY_PARAM = "api_key";

            String sort;
            sort=pref.getString("sort", "popular");
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
                if(sort.equals("fav"))
                    error = getString(R.string.fav_error);
                else
                    error=getString(R.string.network);

                Snackbar snackbar= Snackbar.make(gridView, error, Snackbar.LENGTH_LONG);
                snackbar.setAction("Action", null);
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
                int totalResults= searchResult.getInt("total_results");
                if(totalResults==0) {
                    Snackbar snack = Snackbar.make(gridView, getString(R.string.no_results), Snackbar.LENGTH_LONG);
                    snack.setAction("Action", null);
                    snack.show();
                }
            JSONArray movieArray = searchResult.getJSONArray(LIST_NAME);

            String res=pref.getString("res", "w185");
            String res_bg=pref.getString("res_bg", "w780");
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

            if (movies != null) {
                searchAdapter = new GridViewAdapter(context, movies);
                gridView.setAdapter(searchAdapter);
            }

        }

    }


}
