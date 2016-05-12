package thedorkknightrises.moviespop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by samri_000 on 4/1/2016.
 */
public class ImageViewer extends AppCompatActivity {

    String image;
    String title;
    Bitmap bmp;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_viewer);

        //retrieves the thumbnail data
        Bundle bundle = getIntent().getExtras();

        image = bundle.getString("image");
        title = bundle.getString("title");


        Glide.with(this)
                .load(image)
                .error(R.drawable.ic_photo_white_24px)
                .into((ImageView) findViewById(R.id.imageViewer));

    }

    public void shareImage(View v) {
        progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.processing));
        progress.show();
        new getBitmap(image).execute("bitmap");
    }

    public void close(View v) {
        onBackPressed();
    }

    public class getBitmap extends AsyncTask<String, Void, Boolean> {
        String src;
        File file = new File(getApplicationContext().getCacheDir(), image.substring(image.lastIndexOf('/'), image.length() - 4) + ".png");

        public getBitmap(String url) {
            src = url;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            if (Looper.getMainLooper().equals(null))
                Looper.prepare();

            try {
                bmp = Glide
                        .with(getBaseContext())
                        .load(src)
                        .asBitmap()
                        .into(-1, -1)
                        .get();
                FileOutputStream fOut = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
                file.setReadable(true, false);
                return true;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (!b) {
                progress.dismiss();
                Toast.makeText(getApplicationContext(), R.string.no_png, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "Image from " + title + "\n" + image);
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, "Share image link via"));
                Toast.makeText(getApplicationContext(), R.string.no_png, Toast.LENGTH_SHORT).show();
            } else {
                progress.dismiss();
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "Image from " + title);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                intent.setType("image/png");
                startActivity(Intent.createChooser(intent, "Share image via"));
            }
        }


    }

}
