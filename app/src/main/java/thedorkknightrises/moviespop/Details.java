package thedorkknightrises.moviespop;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by samri_000 on 3/20/2016
 */
public class Details extends AppCompatActivity {
    String title;
    String plot;
    String date;
    String poster;
    String vote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                title= extras.getString("title", "Title");
                plot= extras.getString("plot", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum");
                date= extras.getString("release_date", "01-01-1970");
                poster= extras.getString("poster", "https://c1.staticflickr.com/1/186/382004453_f4b2772254.jpg");
                vote= extras.getString("vote_avg", "0.0");
                update(title, plot, date, vote, poster);
            }
        }
        else
        {
            title= savedInstanceState.getString("title", "Title");
            plot= savedInstanceState.getString("plot", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum");
            date= savedInstanceState.getString("release_date", "01-01-1970");
            poster= savedInstanceState.getString("poster","https://c1.staticflickr.com/1/186/382004453_f4b2772254.jpg");
            vote= savedInstanceState.getString("vote_avg", "0.0");
            update(title, plot, date, vote, poster);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle state)
    {
        super.onSaveInstanceState(state);
        state.putString("title", title);
        state.putString("plot", plot);
        state.putString("release_date", date);
        state.putString("vote_avg", vote);
        state.putString("poster", poster);
    }

    public void update(String t, String p, String d, String v, String post)
    {
        CollapsingToolbarLayout c= (CollapsingToolbarLayout) findViewById(R.id.toolbar_collapse);
        c.setTitle(t);
        c.setExpandedTitleTextAppearance(R.style.TextAppearance_Medium);
        TextView plot= (TextView) findViewById(R.id.mplot);
        TextView date= (TextView) findViewById(R.id.mdate);
        TextView vote= (TextView) findViewById(R.id.mvote);
        ImageView poster= (ImageView) findViewById(R.id.poster);
        poster.setMinimumHeight(poster.getWidth());
        plot.setText(p);
        date.setText(d);
        vote.setText(v);
        Glide.with(this)
                .load(post)
                .into(poster);
    }
}
