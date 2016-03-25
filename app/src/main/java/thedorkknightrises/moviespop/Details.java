package thedorkknightrises.moviespop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (savedInstanceState==null)
        {
            Bundle extras= getIntent().getExtras();
            if(extras!=null)
            {
                id= extras.getInt("id");
                title= extras.getString("title", "Title");
                plot= extras.getString("plot", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum");
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
            plot= savedInstanceState.getString("plot", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum");
            date= savedInstanceState.getString("release_date", "01-01-1970");
            poster= savedInstanceState.getString("poster","https://c1.staticflickr.com/1/186/382004453_f4b2772254.jpg");
            vote= savedInstanceState.getString("vote_avg", "0.0");
            bg= savedInstanceState.getString("bg");
            update(title, plot, date, vote, poster, bg);
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
            ImageView poster = (ImageView) findViewById(R.id.poster);
            Intent sharePage = new Intent(Intent.ACTION_SEND);
            String url= "https://www.themoviedb.org/movie/"+id;
            sharePage.putExtra(Intent.EXTRA_TEXT, url);
            sharePage.setType("text/plain");
            startActivity(Intent.createChooser(sharePage, "Share link via"));
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onSaveInstanceState(Bundle state)
    {
        super.onSaveInstanceState(state);
        state.putInt("id", id);
        state.putString("title", title);
        state.putString("plot", plot);
        state.putString("release_date", date);
        state.putString("vote_avg", vote);
        state.putString("poster", poster);
        state.putString("bg", bg);
    }

    public void update(String t, String p, String d, String v, final String post, String back)  {

        SharedPreferences pref= getSharedPreferences("Prefs",MODE_PRIVATE);

        new getBitmap(post).execute();

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
                .error(R.drawable.ic_photo_white_24px)
                .into(poster);
        if(pref.getBoolean("bg_enabled", true)) {
            Glide.with(this)
                    .load(back)
                    .crossFade(750)
                    .into(backdrop);
        }

        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppBarLayout appBarLayout= (AppBarLayout) findViewById(R.id.app_bar_detail);
                appBarLayout.setExpanded(true);
            }
        });

    }

    public void OnFabClick(View v)
    {
        Snackbar.make(v, R.string.fav_error, Snackbar.LENGTH_LONG).show();
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
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bit= BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                e.printStackTrace();
                bit= null;
            }
            return bit;
        }

        @Override
        protected void onPostExecute(Bitmap bmp)
        {
            super.onPostExecute(bmp);
            try {
                Palette palette= Palette.generate(bmp);
                CollapsingToolbarLayout c= (CollapsingToolbarLayout) findViewById(R.id.toolbar_collapse);

                if(palette.getDarkVibrantSwatch()!=null) {
                    c.setContentScrimColor(palette.getDarkVibrantSwatch().getRgb());
                    c.setBackgroundColor(palette.getDarkVibrantSwatch().getRgb());
                    c.setStatusBarScrimColor(palette.getDarkVibrantSwatch().getRgb());
                }
                else {
                    c.setContentScrimColor(palette.getDarkMutedSwatch().getRgb());
                    c.setBackgroundColor(palette.getDarkMutedSwatch().getRgb());
                    c.setStatusBarScrimColor(palette.getDarkMutedSwatch().getRgb());
                }
                FloatingActionButton fab= (FloatingActionButton) findViewById(R.id.fab);
                if(palette.getVibrantSwatch()!=null)
                    fab.setBackgroundTintList(ColorStateList.valueOf(palette.getVibrantSwatch().getRgb()));
                else if (palette.getLightVibrantSwatch()!=null)
                    fab.setBackgroundTintList(ColorStateList.valueOf(palette.getLightVibrantSwatch().getRgb()));
                else if (palette.getDarkVibrantSwatch()!=null)
                    fab.setBackgroundTintList(ColorStateList.valueOf(palette.getDarkVibrantSwatch().getRgb()));
                else
                    fab.setBackgroundTintList(ColorStateList.valueOf(palette.getLightMutedColor(Color.parseColor("#ff315b"))));

                if(palette.getDarkMutedSwatch()!=null)
                    fab.setRippleColor(palette.getDarkMutedSwatch().getRgb());
                else if(palette.getMutedSwatch()!=null)
                    fab.setRippleColor(palette.getMutedSwatch().getRgb());
                findViewById(R.id.detail_scroll).setBackgroundColor(palette.getDarkMutedSwatch().getRgb());

            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                Snackbar.make(findViewById(R.id.fab), R.string.network, Snackbar.LENGTH_LONG).show();
            }
        }
    }

}
