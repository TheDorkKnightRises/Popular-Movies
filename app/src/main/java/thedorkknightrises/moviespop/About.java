package thedorkknightrises.moviespop;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.aboutlibraries.LibsBuilder;

/**
 * Created by samri_000 on 3/20/2016.
 */
public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
    }

    public void click(View v)
    {
        String uri;
        if(v.equals(findViewById(R.id.fork)))
            uri="https://github.com/TheDorkKnightRises/Popular-Movies";
        else if(v.equals(findViewById(R.id.site)))
            uri="https://thedorkknightrises.github.io/";
        else if(v.equals(findViewById(R.id.git)))
            uri="https://github.com/TheDorkKnightRises/";
        else
            uri="https://plus.google.com/u/0/+SamriddhaBasu";
        Intent i= new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(i);
    }

    public void oss(View v)
    {
        new LibsBuilder()
                .withActivityTitle(getString(R.string.oss))
                .withActivityTheme(R.style.AboutLibrariesTheme)
                //start the activity
                .start(this);
    }

}
