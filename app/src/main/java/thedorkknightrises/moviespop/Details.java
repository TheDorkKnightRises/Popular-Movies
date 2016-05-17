package thedorkknightrises.moviespop;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import thedorkknightrises.moviespop.db.MovieDbHelper;
import thedorkknightrises.moviespop.network.FetchReviews;
import thedorkknightrises.moviespop.network.FetchTrailers;

/**
 * Created by samri_000 on 3/20/2016
 */
public class Details extends AppCompatActivity {
    int id;
    String title;
    String plot;
    String date;
    String poster;
    String vote;
    String bg;
    String sort;
    Boolean anim;
    Boolean paletteEnabled;
    Boolean bgEnabled;
    SharedPreferences pref;
    ArrayList<TrailerObj> trailerList = new ArrayList<>();
    ArrayList<ReviewObj> rList = new ArrayList<>();
    RecyclerView tGrid;
    RecyclerView mReView;
    CoordinatorLayout coordinatorLayout;
    MovieDbHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail);

        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.detailsCoordinatorLayout);

        mHelper = new MovieDbHelper(this);

        if (savedInstanceState==null)
        {
            Bundle extras= getIntent().getExtras();
            if(extras!=null)
            {
                id= extras.getInt("id");
                title= extras.getString("title", "Title");
                plot = extras.getString("overview", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum");
                date= extras.getString("release_date", "01-01-1970");
                poster= extras.getString("poster", "https://c1.staticflickr.com/1/186/382004453_f4b2772254.jpg");
                vote= extras.getString("vote_avg", "0.0");
                bg= extras.getString("bg");
                update(title, plot, date, vote, poster, bg);
            }
        }
        else
        {
            id= savedInstanceState.getInt("id");
            title= savedInstanceState.getString("title", "Title");
            plot = savedInstanceState.getString("overview", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum");
            date= savedInstanceState.getString("release_date", "01-01-1970");
            poster= savedInstanceState.getString("poster","https://c1.staticflickr.com/1/186/382004453_f4b2772254.jpg");
            vote= savedInstanceState.getString("vote_avg", "0.0");
            bg= savedInstanceState.getString("bg");
            update(title, plot, date, vote, poster, bg);
        }

        pref = getSharedPreferences("Prefs", MODE_PRIVATE);
        sort = pref.getString("sort", "popular");
        anim = pref.getBoolean("anim_enabled", true);

        if (MainActivity.isConnected(this)) {
            tGrid = (RecyclerView) findViewById(R.id.tr_view);
            mReView = (RecyclerView) findViewById(R.id.reviews);
            tGrid.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            mReView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            mReView.setNestedScrollingEnabled(false);
            mReView.setHasFixedSize(true);

            new FetchTrailers(this, tGrid, trailerList, id, title).execute("trailers");
            new FetchReviews(this, mReView, rList, id).execute("reviews");
        } else {
            findViewById(R.id.detail_card_trailer).setVisibility(View.GONE);
            findViewById(R.id.detail_card_review).setVisibility(View.GONE);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null)
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnFabClick(v);
                }
            });
    }

    @Override
    protected void onStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && anim) {
            Slide slide = new Slide(Gravity.BOTTOM);
            slide.addTarget(R.id.detail_card);
            slide.addTarget(R.id.app_bar_detail);
            slide.addTarget(R.id.detail_scroll);
            slide.addTarget(R.id.fab);
            slide.addTarget(R.id.detailsCoordinatorLayout);
            slide.setInterpolator(new LinearOutSlowInInterpolator());
            getWindow().setEnterTransition(slide);
            getWindow().setExitTransition(slide);
            getWindow().setReenterTransition(slide);

            setupEnterAnimation();
        }
        super.onStart();

    }

    public void onPosterClick(View v) {
        Intent i = new Intent(Details.this, ImageViewer.class);

        //Pass the image title and url to DetailsActivity
        if (v == findViewById(R.id.poster))
            i.putExtra("image", poster);
        else {
            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_detail);
            appBarLayout.setExpanded(true);
            i.putExtra("image", bg);
        }
        i.putExtra("title", title);

        anim = pref.getBoolean("anim_enabled", true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && anim) {

            v.setTransitionName("image");
            Pair participants = new Pair<>(v, ViewCompat.getTransitionName(v));

            ActivityOptionsCompat transitionActivityOptions =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                            Details.this, participants);

            ActivityCompat.startActivity(Details.this,
                    i, transitionActivityOptions.toBundle());

        } else {
            ActivityOptionsCompat trans = ActivityOptionsCompat.makeSceneTransitionAnimation(Details.this);
            ActivityCompat.startActivity(Details.this, i, trans.toBundle());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int itemId = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (itemId == R.id.share_details) {
            Intent sharePage = new Intent(Intent.ACTION_SEND);
            String url = title + " at The Movie Database\nhttps://www.themoviedb.org/movie/" + id;
            sharePage.putExtra(Intent.EXTRA_TEXT, url);
            sharePage.setType("text/plain");
            startActivity(Intent.createChooser(sharePage, "Share link via"));
        } else if (itemId == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle state)
    {
        super.onSaveInstanceState(state);
        state.putInt("id", id);
        state.putString("title", title);
        state.putString("overview", plot);
        state.putString("release_date", date);
        state.putString("vote_avg", vote);
        state.putString("poster", poster);
        state.putString("bg", bg);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            finish();
        }
    }

    public void update(String t, String p, String d, String v, String post, String back) {

        pref = getSharedPreferences("Prefs", MODE_PRIVATE);

        paletteEnabled = pref.getBoolean("palette_enabled", true);
        bgEnabled = pref.getBoolean("bg_enabled", true);

        if (paletteEnabled) {
            if (bgEnabled)
                new getBitmap(back).execute();
            else new getBitmap(post).execute();
        }

        CollapsingToolbarLayout c= (CollapsingToolbarLayout) findViewById(R.id.toolbar_collapse);
        c.setTitle(t);
        c.setExpandedTitleTextAppearance(R.style.TextAppearance_Medium);

        TextView plot= (TextView) findViewById(R.id.mplot);
        TextView date= (TextView) findViewById(R.id.mdate);
        TextView vote= (TextView) findViewById(R.id.mvote);
        ImageView poster= (ImageView) findViewById(R.id.poster);
        ImageView backdrop= (ImageView) findViewById(R.id.bg_img);
        poster.setMinimumHeight(poster.getWidth());

        plot.setText(p);
        date.setText(d);
        vote.setText(v);

        Glide.with(this)
                .load(post)
                .crossFade(500)
                .into(poster);
        if(pref.getBoolean("bg_enabled", true)) {
            Glide.with(this)
                    .load(back)
                    .crossFade(750)
                    .into(backdrop);
        }

        if (backdrop != null) {
            backdrop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences p = getSharedPreferences("Prefs", MODE_PRIVATE);
                    if (p.getBoolean("bg_enabled", true))
                        onPosterClick(v);
                    else {
                        ((AppBarLayout) findViewById(R.id.app_bar_detail)).setExpanded(true, true);
                    }
                }
            });
        }
        if (mHelper.movieExists(id)) {
            if (findViewById(R.id.fab) != null) {
                ((FloatingActionButton) findViewById(R.id.fab)).setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_white_24px));
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.transition);
        transition.setDuration(300);
        getWindow().setSharedElementEnterTransition(transition);
        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
            }
            @Override
            public void onTransitionEnd(Transition transition) {
            }
            @Override
            public void onTransitionCancel(Transition transition) {
            }
            @Override
            public void onTransitionPause(Transition transition) {
            }
            @Override
            public void onTransitionResume(Transition transition) {
            }
        });
    }

    public void OnFabClick(View v)
    {
        if (mHelper.movieExists(id)) {
            mHelper.deleteMovie(id);
            ((FloatingActionButton) v).setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_white_24px));
            Snackbar.make(coordinatorLayout, R.string.removed, Snackbar.LENGTH_SHORT).show();
        } else {
            mHelper.addMovie(id, title, plot, date, vote, poster, bg);
            ((FloatingActionButton) v).setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_white_24px));
            Snackbar.make(coordinatorLayout, R.string.added, Snackbar.LENGTH_SHORT).show();
        }
    }


    public class getBitmap extends AsyncTask<String, Void, Bitmap>
    {
        String src;
        Bitmap bit;

        public getBitmap(String url)
        {
            src= url;
        }
        @Override
        protected Bitmap doInBackground(String... params) {

            try {
                bit = Glide
                        .with(getBaseContext())
                        .load(src)
                        .asBitmap()
                        .into(-1, -1)
                        .get();
            } catch (final ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            return bit;
        }

        @Override
        protected void onPostExecute(Bitmap bmp)
        {
            super.onPostExecute(bmp);

            try {

                Palette palette = Palette.generate(bmp);
                CollapsingToolbarLayout c = (CollapsingToolbarLayout) findViewById(R.id.toolbar_collapse);

                if (palette.getDarkVibrantSwatch() != null) {
                    c.setContentScrimColor(palette.getDarkVibrantSwatch().getRgb());
                    c.setBackgroundColor(palette.getDarkVibrantSwatch().getRgb());
                    c.setStatusBarScrimColor(palette.getDarkVibrantSwatch().getRgb());
                } else {
                    c.setContentScrimColor(palette.getMutedSwatch().getRgb());
                    c.setBackgroundColor(palette.getMutedSwatch().getRgb());
                    c.setStatusBarScrimColor(palette.getMutedSwatch().getRgb());
                }
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                if (palette.getVibrantSwatch() != null)
                    fab.setBackgroundTintList(ColorStateList.valueOf(palette.getVibrantSwatch().getRgb()));
                else if (palette.getLightVibrantSwatch() != null)
                    fab.setBackgroundTintList(ColorStateList.valueOf(palette.getLightVibrantSwatch().getRgb()));
                else if (palette.getDarkVibrantSwatch() != null)
                    fab.setBackgroundTintList(ColorStateList.valueOf(palette.getDarkVibrantSwatch().getRgb()));
                else
                    fab.setBackgroundTintList(ColorStateList.valueOf(palette.getLightMutedColor(Color.parseColor("#ff315b"))));

                if (palette.getDarkMutedSwatch() != null)
                    fab.setRippleColor(palette.getDarkMutedSwatch().getRgb());
                else if (palette.getMutedSwatch() != null)
                    fab.setRippleColor(palette.getMutedSwatch().getRgb());
                coordinatorLayout.setBackgroundColor(palette.getDarkMutedSwatch().getRgb());

            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                if (coordinatorLayout != null && !sort.equals("fav"))
                    Snackbar.make(coordinatorLayout, R.string.content, Snackbar.LENGTH_LONG).show();
            }
        }
    }
}