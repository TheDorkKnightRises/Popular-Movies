package thedorkknightrises.moviespop;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static SwipeRefreshLayout swipeRefreshLayout;
    public static CoordinatorLayout coordinatorLayout;
    public static NavigationView navigationView;
    public static Boolean mDualPane;
    public static Boolean widthFlag;
    public static ArrayList<MovieObj> movieResults = new ArrayList<>();
    private static GridView mGridView;
    public Boolean changed = false;
    public SQLiteDatabase db;
    public MovieDbHelper mHelper;
    SharedPreferences pref;
    int mCurCheckPosition = 0;
    private TextView label;
    private BroadcastReceiver receiver;

    public static boolean isConnected(Context context) {
        if (context == null) {
            return true;
        }
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    public static CoordinatorLayout getLayout() {
        return coordinatorLayout;
    }

    public static SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    public static NavigationView getNavigationView() {
        return navigationView;
    }

    public static Boolean getmDualPane() {
        return mDualPane;
    }

    public static Boolean getWidthFlag() {
        if (widthFlag) mGridView.setNumColumns(1);
        return widthFlag;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        final String action = intent.getAction();
        int id = 0;
        if (Intent.ACTION_VIEW.equals(action)) {
            final List<String> segments = intent.getData().getPathSegments();
            if (segments.size() >= 1) {
                final String str = segments.get(1);
                int end = (str.indexOf('-') == (-1)) ? str.length() : str.indexOf('-');
                try {
                    id = Integer.parseInt(str.substring(0, end));

                    // Check what fragment is currently shown, replace if needed.
                    Detail_Fragment details = (Detail_Fragment) getFragmentManager()
                            .findFragmentById(R.id.details);
                    FragmentTransaction ft = getFragmentManager()
                            .beginTransaction();
                    new FetchMovie(this, findViewById(R.id.blankText), details, ft, id, getSharedPreferences("Prefs", MODE_PRIVATE)).execute("fetch");
                } catch (NumberFormatException e) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.AppTheme_PopupOverlay);
                    dialog.setMessage(R.string.invalid_url)
                            .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton(R.string.browser, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Uri uri = Uri.parse("https://www.themoviedb.org/movie/" + str);
                                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                                    builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));
                                    CustomTabsIntent customTabsIntent = builder.build();
                                    CustomTabActivityHelper.openCustomTab(MainActivity.this, customTabsIntent, uri,
                                            new CustomTabActivityHelper.CustomTabFallback() {
                                                @Override
                                                public void openUri(Activity activity, Uri uri) {
                                                    Intent i = new Intent(Intent.ACTION_VIEW, uri);
                                                    startActivity(i);
                                                }
                                            });
                                }
                            })
                            .show();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View detailsFrame = findViewById(R.id.details);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mHelper = new MovieDbHelper(this);

        pref = getSharedPreferences("Prefs", MODE_PRIVATE);


        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = pref.getBoolean("firstStart", true);
                //  If the activity has never started before...
                if (isFirstStart) {
                    //  Launch app intro
                    Intent i = new Intent(MainActivity.this, Intro.class);
                    startActivity(i);
                    //  Make a new preferences editor
                    SharedPreferences.Editor e = pref.edit();
                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);
                    //  Apply changes
                    e.apply();
                }
                db = mHelper.getReadableDatabase();
            }
        });
        // Start the thread
        t.start();

        widthFlag = (this.getResources().getConfiguration().screenWidthDp < 720);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        mGridView = (GridView) findViewById(R.id.gridview);
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem;
            private View v= findViewById(R.id.main_toolbar);
            private View content= findViewById(R.id.contentPane);
            private Boolean b= false;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(mLastFirstVisibleItem<firstVisibleItem&&!b)
                {
                    float d= -toolbar.getBottom();
                    v.animate().translationY(d).start();
                    content.animate().translationY(d).start();

                    b=true;
                }
                if(mLastFirstVisibleItem>firstVisibleItem&&b)
                {
                    v.animate().translationY(0).start();
                    content.animate().translationY(0).start();

                    b=false;
                }
                mLastFirstVisibleItem=firstVisibleItem;
            }
        });

        String sort = pref.getString("sort", "popular");


        if (savedInstanceState == null) {
            populateMovies();
            if (getIntent().getAction() == Intent.ACTION_VIEW)
                onNewIntent(getIntent());
        }
        else {
            movieResults = (ArrayList<MovieObj>) savedInstanceState.getSerializable("results");
            GridViewAdapter mAdapter = new GridViewAdapter(this, movieResults);
            mGridView.setAdapter(mAdapter);

            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
            changed = savedInstanceState.getBoolean("changed", false);
        }

        if (mDualPane)
            showDetails(mCurCheckPosition);

        label = (TextView) findViewById(R.id.label);

        switch (sort) {
            case "popular":
                label.setText(getString(R.string.pop));
                break;
            case "top_rated":
                label.setText(getString(R.string.high));
                break;
            case "fav":
                label.setText(getString(R.string.fav));
                break;
            case "upcoming":
                label.setText(getString(R.string.up));
                break;
            case "now_playing":
                label.setText(R.string.now);
                break;
        }

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (isConnected(getApplicationContext()) || pref.getString("sort", "popular").equals("fav")) {
                        populateMovies();
                    } else {
                        Snackbar.make(coordinatorLayout, R.string.refresh_error, Snackbar.LENGTH_LONG)
                                .show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        IntentFilter i = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isInitialStickyBroadcast()) {
                    // Ignore this call to onReceive, as this is the sticky broadcast
                } else {
                    // Connectivity state has changed
                    if (isConnected(getBaseContext())) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, R.string.conn, Snackbar.LENGTH_LONG);
                        if (!pref.getString("sort", "popular").equals("fav"))
                            snackbar.setAction(R.string.refresh, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    populateMovies();
                                }
                            });
                        snackbar.show();
                    } else if (!isConnected(getBaseContext())) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, R.string.no_conn, Snackbar.LENGTH_LONG);
                        if (!pref.getString("sort", "popular").equals("fav"))
                            snackbar.setAction(R.string.fav, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SharedPreferences.Editor edit = pref.edit();
                                    edit.putString("sort", "fav");
                                    label.setText(getString(R.string.fav));
                                    edit.commit();
                                    populateMovies();
                                }
                            });
                        snackbar.show();
                    }
                }
            }
        };
        registerReceiver(receiver, i);
        super.onResume();

        View header = navigationView.getHeaderView(0);
        ImageView acc_image = (ImageView) header.findViewById(R.id.acc_image_nav);
        Glide.with(this)
                .load(new File(getApplication().getCacheDir(), pref.getString("acc_image", "") + ".png"))
                .error(R.drawable.ic_account_circle_white_64dp)
                .into(acc_image);

        if (!pref.getString("session_id", "0").equals("0")) {
            ((TextView) header.findViewById(R.id.headerText1)).setText(getResources().getString(R.string.signed_in) + pref.getString("username", ""));
            ((TextView) header.findViewById(R.id.headerText2)).setText(getResources().getString(R.string.sign_out_nav));
        } else {
            ((TextView) header.findViewById(R.id.headerText1)).setText(getResources().getString(R.string.sign_in_nav));
            ((TextView) header.findViewById(R.id.headerText2)).setText(getResources().getString(R.string.nav_sec));
        }
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
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


                MovieObj m = (MovieObj) mGridView.getItemAtPosition(index);

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

        int index = mGridView.getPositionForView(v);
        mCurCheckPosition = index;

        if (mDualPane) {

            // Check what fragment is currently shown, replace if needed.
            Detail_Fragment details = (Detail_Fragment) getFragmentManager()
                    .findFragmentById(R.id.details);

            findViewById(R.id.blankText).setVisibility(View.GONE);

            MovieObj m = (MovieObj) mGridView.getItemAtPosition(index);

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

            MovieObj m = (MovieObj) mGridView.getItemAtPosition(index);

            Intent i = new Intent(MainActivity.this, Details.class);
            i.putExtra("id", m.id);
            i.putExtra("title", m.title);
            i.putExtra("release_date", m.year);
            i.putExtra("vote_avg", m.vote_avg);
            i.putExtra("overview", m.overview);
            i.putExtra("poster", m.posterUrl);
            i.putExtra("bg", m.bgUrl);
            Boolean anim = pref.getBoolean("anim_enabled", true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && anim) {

                v.setTransitionName("poster");
                Pair participants = new Pair<>(v, ViewCompat.getTransitionName(v));

                ActivityOptionsCompat transitionActivityOptions =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                MainActivity.this, participants);

                ActivityCompat.startActivity(MainActivity.this,
                        i, transitionActivityOptions.toBundle());

            } else {
                startActivity(i);
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.AppTheme_PopupOverlay);
            dialog.setMessage(R.string.exit)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            exit();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    public void exit()
    {
        super.onBackPressed();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        SharedPreferences.Editor edit = pref.edit();

        label = (TextView) findViewById(R.id.label);

        switch (id) {
            case R.id.nav_fav:
                edit.putString("sort", "fav");
                label.setText(getString(R.string.fav));
                edit.commit();
                populateMovies();
                break;
            case R.id.nav_pop:
                edit.putString("sort", "popular");
                label.setText(getString(R.string.pop));
                edit.commit();
                populateMovies();
                break;
            case R.id.nav_high:
                edit.putString("sort", "top_rated");
                label.setText(getString(R.string.high));
                edit.commit();
                populateMovies();
                break;
            case R.id.nav_up:
                edit.putString("sort", "upcoming");
                label.setText(getString(R.string.up));
                edit.commit();
                populateMovies();
                break;
            case R.id.nav_now:
                edit.putString("sort", "now_playing");
                label.setText(R.string.now);
                edit.commit();
                populateMovies();
                break;
            case R.id.nav_about: {
                Intent i = new Intent(MainActivity.this, About.class);
                startActivity(i);
                break;
            }
            case R.id.nav_settings: {
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
                break;
            }
        }
        changed = true;
        mCurCheckPosition = 0;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryRefinementEnabled(true);

        return true;
    }

    public void login(View v) {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        Intent i = new Intent(MainActivity.this, Login.class);
        startActivity(i);
    }

    public void populateMovies() {

        mGridView.removeAllViewsInLayout();
        String sort = pref.getString("sort", "popular");
        if (sort.equals("fav")) {
            movieResults = mHelper.getAllMovies();
            GridViewAdapter mAdapter = new GridViewAdapter(this, movieResults);
            mGridView.setAdapter(mAdapter);
            swipeRefreshLayout.setRefreshing(false);
            if (movieResults.isEmpty()) {
                Snackbar.make(coordinatorLayout, R.string.no_fav, Snackbar.LENGTH_LONG).show();
            }
        } else new FetchSearchResults(this, mGridView, movieResults, pref).execute("discover");

    }
}