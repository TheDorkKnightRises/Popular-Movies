package thedorkknightrises.moviespop;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    GridView mGridView;
    public ArrayList<MovieObj> movieResults = new ArrayList<>();
    Boolean anim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
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

        mGridView= (GridView) findViewById(R.id.gridview);

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
        SharedPreferences pref = getSharedPreferences("Prefs", MODE_PRIVATE);
        anim = pref.getBoolean("anim_enabled", true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && anim) {

            try {

                v.setTransitionName("poster");
                Pair participants = new Pair<>(v, ViewCompat.getTransitionName(v));

                ActivityOptionsCompat transitionActivityOptions =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                MainActivity.this, participants);

                ActivityCompat.startActivity(MainActivity.this,
                        i, transitionActivityOptions.toBundle());
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                ActivityOptionsCompat trans = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this);
                ActivityCompat.startActivity(MainActivity.this, i, trans.toBundle());
            }
        } else {
            startActivity(i);
        }
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

            if(isConnected(this)) {
                mGridView.removeAllViewsInLayout();
                populateMovies();
            } else {
                Snackbar snack = Snackbar.make(mGridView, R.string.refresh_error, Snackbar.LENGTH_LONG);
                snack.setAction("Action", null);
            }
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

        new FetchSearchResults(this, mGridView, movieResults, getSharedPreferences("Prefs", MODE_PRIVATE)).execute("discover");

    }

    public static boolean isConnected(Context context) {
        if (context == null) {
            return true;
        }
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }


}
