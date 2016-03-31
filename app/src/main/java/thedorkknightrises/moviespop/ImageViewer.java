package thedorkknightrises.moviespop;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by samri_000 on 4/1/2016.
 */
public class ImageViewer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_viewer);

        //retrieves the thumbnail data
        Bundle bundle = getIntent().getExtras();

        String image = bundle.getString("image");

        Glide.with(this)
                .load(image)
                .placeholder(R.drawable.ic_photo_white_24px)
                .error(R.drawable.ic_photo_white_24px)
                .into((ImageView) findViewById(R.id.imageViewer));
    }

}
