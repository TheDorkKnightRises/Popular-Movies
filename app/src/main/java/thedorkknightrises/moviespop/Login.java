package thedorkknightrises.moviespop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by samri_000 on 4/30/2016.
 */
public class Login extends Activity {
    SharedPreferences pref;
    ImageView pic;
    TextView name;
    Boolean logged_in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_layout);

        pref = getSharedPreferences("Prefs", MODE_PRIVATE);
        pic = (ImageView) findViewById(R.id.acc_image);
        name = (TextView) findViewById(R.id.acc_name);
        logged_in = !pref.getString("session_id", "0").equals("0");

        name.setText(pref.getString("username", getResources().getString(R.string.user_blank)));
        if (logged_in) {
            Glide.with(this)
                    .load(new File(getApplication().getCacheDir(), pref.getString("acc_image", "") + ".png"))
                    .error(R.drawable.ic_account_circle_white_64dp)
                    .into(pic);
            ((TextView) findViewById(R.id.sign_in)).setText(R.string.sign_out);
            ((TextView) findViewById(R.id.acc_id)).setText(getResources().getString(R.string.uid) + pref.getString("uid", ""));
        }
    }

    public void more(View view) {
        View v = findViewById(R.id.acc_desc_text);
        if (v != null) {
            if (v.getVisibility() == View.GONE) {
                ((TextView) view).setText(R.string.acc_hide_more);
                v.setVisibility(View.VISIBLE);
            } else {

                ((TextView) view).setText(R.string.acc_more);
                v.setVisibility(View.GONE);
            }
        }
    }

    public void login(View v) {
        if (!logged_in)
            new TMDbLogin(this).execute("login");
        else
            logout();
    }

    public void logout() {
        SharedPreferences.Editor edit = pref.edit();
        File dir = getApplication().getCacheDir();
        File file = new File(dir, pref.getString("acc_image", "") + ".png");
        file.delete();
        edit.putString("session_id", "0");
        edit.putString("username", getResources().getString(R.string.user_blank));
        edit.putString("acc_image", "");
        edit.commit();

        finish();
    }

    public class TMDbLogin extends AsyncTask<String, Void, String> {

        Context context;
        Uri buildUri;

        public TMDbLogin(Context c) {
            super();
            context = c;
        }


        @Override
        protected String doInBackground(String... params) {
            if (Looper.getMainLooper() == null)
                Looper.prepare();
            if (params.length == 0) {
                return null;
            }
            HttpURLConnection client = null;
            BufferedReader bufferedReader = null;
            String JSONstr = null;
            final String BASE_URL =
                    "http://api.themoviedb.org/3/authentication/token/new";
            final String API_KEY = context.getString(R.string.moviedb_api_key);
            final String API_KEY_PARAM = "api_key";

            buildUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, API_KEY)
                    .build();
            URL url = null;

            try {
                url = new URL(buildUri.toString());
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }

            Log.d("URL", url.toString());
            try {
                client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("GET");
                client.connect();
                InputStream inputStream = client.getInputStream();
                if (inputStream == null)
                    return null;
                StringBuilder buffer = new StringBuilder();


                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    buffer.append(line + '\n');
                }

                if (buffer.length() == 0)
                    return null;
                JSONstr = buffer.toString();
                Log.d("JSON Str", JSONstr);
            } catch (ProtocolException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                if (client != null) {
                    client.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        Log.e("Main", "Error closing stream", e);
                    }
                }
            }

            try {
                String token = getTokenFromJson(JSONstr);
                Log.d("Auth", token);
                return token;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private String getTokenFromJson(String JSONstr)
                throws Exception {
            final String TOKEN = "request_token";

            JSONObject tokenResult = new JSONObject(JSONstr);
            String token = tokenResult.getString(TOKEN);
            return token;
        }

        @Override
        protected void onPostExecute(String token) {
            super.onPostExecute(token);
            Uri uri = Uri.parse("https://www.themoviedb.org/authenticate/" + token).buildUpon()
                    .appendQueryParameter("redirect_to", "pomov://log.in/" + token)
                    .build();

            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);

            finish();
        }

    }

}
