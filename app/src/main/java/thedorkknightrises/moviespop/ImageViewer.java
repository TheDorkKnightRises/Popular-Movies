package thedorkknightrises.moviespop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by samri_000 on 4/1/2016.
 */
public class ImageViewer extends AppCompatActivity {

    String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_viewer);

        //retrieves the thumbnail data
        Bundle bundle = getIntent().getExtras();

        image = bundle.getString("image");

        Glide.with(this)
                .load(image)
                .error(R.drawable.ic_photo_white_24px)
                .into((ImageView) findViewById(R.id.imageViewer));
    }

    public void shareImage(View v) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_TEXT, image);
        share.setType("text/plain");
        startActivity(Intent.createChooser(share, "Share image link via"));
    }

    public void close(View v) {
        onBackPressed();
    }

}
