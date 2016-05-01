package thedorkknightrises.moviespop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class Detail_Fragment extends android.app.Fragment {
    static Bundle bundle;
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
    ArrayList<TrailerObj> trailerList = new ArrayList<>();
    ArrayList<ReviewObj> rList = new ArrayList<>();
    RecyclerView tGrid;
    RecyclerView mReView;
    CoordinatorLayout coordinatorLayout;
    MovieDbHelper mHelper;

    public static Detail_Fragment newInstance(int index, Bundle state) {
        Detail_Fragment f = new Detail_Fragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);

        bundle = state;
        f.setArguments(args);

        return f;
    }

    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.detail, null);

        return v;
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
            String url = "https://www.themoviedb.org/movie/" + id;
            sharePage.putExtra(Intent.EXTRA_TEXT, url);
            sharePage.setType("text/plain");
            startActivity(Intent.createChooser(sharePage, "Share link via"));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(R.id.detailsCoordinatorLayout);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_detail);

        mHelper = new MovieDbHelper(getActivity());

        SharedPreferences pref = getActivity().getSharedPreferences("Prefs", getActivity().MODE_PRIVATE);
        sort = pref.getString("sort", "popular");

        if (savedInstanceState == null) {
            if (bundle != null) {
                id = bundle.getInt("id", 550);
                title = bundle.getString("title", "Title");
                plot = bundle.getString("overview", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum");
                date = bundle.getString("release_date", "01-01-1970");
                poster = bundle.getString("poster", "https://c1.staticflickr.com/1/186/382004453_f4b2772254.jpg");
                vote = bundle.getString("vote_avg", "0.0");
                bg = bundle.getString("bg", "https://c1.staticflickr.com/1/186/382004453_f4b2772254.jpg");
                update(title, plot, date, vote, poster, bg);
            }
        } else {
            id = savedInstanceState.getInt("id");
            title = savedInstanceState.getString("title", "Title");
            plot = savedInstanceState.getString("overview", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum");
            date = savedInstanceState.getString("release_date", "01-01-1970");
            poster = savedInstanceState.getString("poster", "https://c1.staticflickr.com/1/186/382004453_f4b2772254.jpg");
            vote = savedInstanceState.getString("vote_avg", "0.0");
            bg = savedInstanceState.getString("bg");
            update(title, plot, date, vote, poster, bg);
        }

        try {
            if (!sort.equals("fav")) {
                tGrid = (RecyclerView) getActivity().findViewById(R.id.tr_view);
                mReView = (RecyclerView) getActivity().findViewById(R.id.reviews);
            tGrid.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            mReView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            mReView.setNestedScrollingEnabled(false);
            mReView.setHasFixedSize(true);
                new FetchTrailers(getActivity(), tGrid, trailerList, id, title).execute("trailers");
                new FetchReviews(getActivity(), mReView, rList, id).execute("reviews");
            } else {
                getActivity().findViewById(R.id.detail_card_trailer).setVisibility(View.GONE);
                getActivity().findViewById(R.id.detail_card_review).setVisibility(View.GONE);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (toolbar != null) {
            toolbar.inflateMenu(R.menu.details_menu);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    onOptionsItemSelected(item);
                    return true;
                }
            });
        }

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        if (fab != null)
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnFabClick(v);
                }
            });
    }

    public void OnFabClick(View v) {
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

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt("id", id);
        state.putString("title", title);
        state.putString("overview", plot);
        state.putString("release_date", date);
        state.putString("vote_avg", vote);
        state.putString("poster", poster);
        state.putString("bg", bg);
    }

    public void update(String t, String p, String d, String v, String post, String back) {

        SharedPreferences pref = getActivity().getSharedPreferences("Prefs", getActivity().MODE_PRIVATE);


        paletteEnabled = pref.getBoolean("palette_enabled", true);
        bgEnabled = pref.getBoolean("bg_enabled", true);

        if (paletteEnabled) {
            if (bgEnabled)
                new getBitmap(back).execute();
            else new getBitmap(post).execute();
        }

        CollapsingToolbarLayout c = (CollapsingToolbarLayout) getActivity().findViewById(R.id.toolbar_collapse);

        try {
            c.setTitle(t);
            c.setExpandedTitleTextAppearance(R.style.TextAppearance_Medium);

            TextView plot = (TextView) getActivity().findViewById(R.id.mplot);
            TextView date = (TextView) getActivity().findViewById(R.id.mdate);
            TextView vote = (TextView) getActivity().findViewById(R.id.mvote);
            ImageView poster = (ImageView) getActivity().findViewById(R.id.poster);
            ImageView backdrop = (ImageView) getActivity().findViewById(R.id.bg_img);
            poster.setMinimumHeight(poster.getWidth());
            plot.setText(p);
            date.setText(d);
            vote.setText(v);
            Glide.with(this)
                    .load(post)
                    .crossFade(500)
                    .into(poster);
            if (bgEnabled) {
                Glide.with(this)
                        .load(back)
                        .crossFade(750)
                        .into(backdrop);
            }

            if (backdrop != null) {
                backdrop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences p = getActivity().getSharedPreferences("Prefs", getActivity().MODE_PRIVATE);
                        if (p.getBoolean("bg_enabled", true))
                            onPosterClick(v);
                        else {
                            ((AppBarLayout) getActivity().findViewById(R.id.app_bar_detail)).setExpanded(true, true);
                        }
                    }
                });
            }

            if (poster != null) {
                poster.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences p = getActivity().getSharedPreferences("Prefs", getActivity().MODE_PRIVATE);
                        onPosterClick(v);
                    }
                });
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (mHelper.movieExists(id)) {
            if ((getActivity().findViewById(R.id.fab)) != null) {
                ((FloatingActionButton) getActivity().findViewById(R.id.fab)).setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_white_24px));
            }
        }
    }

    public void onPosterClick(View v) {
        Intent i = new Intent(getActivity(), ImageViewer.class);

        //Pass the image title and url to DetailsActivity
        if (v == getActivity().findViewById(R.id.poster))
            i.putExtra("image", poster);
        else {
            AppBarLayout appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.app_bar_detail);
            appBarLayout.setExpanded(true);
            i.putExtra("image", bg);
        }

        SharedPreferences pref = getActivity().getSharedPreferences("Prefs", getActivity().MODE_PRIVATE);
        anim = pref.getBoolean("anim_enabled", true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && anim) {

            v.setTransitionName("image");
            Pair participants = new Pair<>(v, ViewCompat.getTransitionName(v));

            ActivityOptionsCompat transitionActivityOptions =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                            getActivity(), participants);

            ActivityCompat.startActivity(getActivity(),
                    i, transitionActivityOptions.toBundle());

        } else {
            ActivityOptionsCompat trans = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity());
            ActivityCompat.startActivity(getActivity(), i, trans.toBundle());
        }
    }


    public class getBitmap extends AsyncTask<String, Void, Bitmap> {
        String src;
        Bitmap bit;

        public getBitmap(String url) {
            src = url;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            try {
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bit = BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                e.printStackTrace();
                bit = null;
            }
            return bit;
        }

        @Override
        protected void onPostExecute(Bitmap bmp) {
            super.onPostExecute(bmp);

            try {

                Palette palette = Palette.generate(bmp);
                CollapsingToolbarLayout c = (CollapsingToolbarLayout) getActivity().findViewById(R.id.toolbar_collapse);

                if (palette.getDarkVibrantSwatch() != null) {
                    c.setContentScrimColor(palette.getDarkVibrantSwatch().getRgb());
                    c.setBackgroundColor(palette.getDarkVibrantSwatch().getRgb());
                    c.setStatusBarScrimColor(palette.getDarkVibrantSwatch().getRgb());
                } else {
                    c.setContentScrimColor(palette.getMutedSwatch().getRgb());
                    c.setBackgroundColor(palette.getMutedSwatch().getRgb());
                    c.setStatusBarScrimColor(palette.getMutedSwatch().getRgb());
                }
                FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
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
