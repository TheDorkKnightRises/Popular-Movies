package thedorkknightrises.moviespop.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import thedorkknightrises.moviespop.Login;
import thedorkknightrises.moviespop.R;

/**
 * Created by samri_000 on 5/13/2016.
 */
public class LoginStub extends Activity {
    SharedPreferences pref;
    ProgressDialog progress;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        final String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            final List<String> segments = intent.getData().getPathSegments();
            if (segments.size() == 1) {
                final String str = segments.get(0);
                new Session(this).execute(str);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.processing));
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        pref = getSharedPreferences("Prefs", MODE_PRIVATE);

        if (getIntent().getAction() == Intent.ACTION_VIEW)
            onNewIntent(getIntent());

    }

    public class Session extends AsyncTask<String, Void, String> {

        Context context;
        Uri buildUri;

        public Session(Context c) {
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
            String token = params[0];
            HttpURLConnection client = null;
            BufferedReader bufferedReader = null;
            String JSONstr = null;
            final String BASE_URL =
                    "http://api.themoviedb.org/3/authentication/session/new";
            final String API_KEY = context.getString(R.string.moviedb_api_key);
            final String API_KEY_PARAM = "api_key";
            final String TOKEN_PARAM = "request_token";
            final String TOKEN = token;

            buildUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, API_KEY)
                    .appendQueryParameter(TOKEN_PARAM, TOKEN)
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
                String session = getSessionFromJson(JSONstr);
                Log.d("Session", session);
                return session;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private String getSessionFromJson(String JSONstr)
                throws Exception {
            final String SESSION = "session_id";

            JSONObject tokenResult = new JSONObject(JSONstr);
            String session_id = tokenResult.getString(SESSION);

            return session_id;
        }

        @Override
        protected void onPostExecute(String session) {
            super.onPostExecute(session);
            if (session != null) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("session_id", session);
                editor.commit();
                Log.d("Session ID", session);
                new User(context).execute(session);
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.AppTheme_PopupOverlay);
                dialog.setMessage(R.string.unauth)
                        .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                progress.dismiss();
                                finish();
                            }
                        })
                        .show();
            }
        }
    }

    public class User extends AsyncTask<String, Void, String> {

        Context context;
        Uri buildUri;

        public User(Context c) {
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
            String session = params[0];
            HttpURLConnection client = null;
            BufferedReader bufferedReader = null;
            String JSONstr = null;
            final String BASE_URL =
                    "http://api.themoviedb.org/3/account";
            final String API_KEY = context.getString(R.string.moviedb_api_key);
            final String API_KEY_PARAM = "api_key";
            final String SESSION_PARAM = "session_id";
            final String SESSION = session;

            buildUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, API_KEY)
                    .appendQueryParameter(SESSION_PARAM, SESSION)
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
                return getUserFromJson(JSONstr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private String getUserFromJson(String JSONstr)
                throws Exception {
            final String NAME = "username";
            final String ID = "id";
            final String PIC = "avatar";
            final String GRAVATAR = "gravatar";
            final String HASH = "hash";

            JSONObject userResult = new JSONObject(JSONstr);
            String name = userResult.getString(NAME);
            String id = userResult.getString(ID);
            JSONObject avatar = userResult.getJSONObject(PIC);
            JSONObject gravatar = avatar.getJSONObject(GRAVATAR);
            String url = gravatar.getString(HASH);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("acc_image", url);
            editor.putString("uid", id);
            editor.commit();

            try {
                File file = new File(getApplicationContext().getCacheDir(), url + ".png");

                Bitmap bmp = Glide
                        .with(getBaseContext())
                        .load("http://www.gravatar.com/avatar/" + url)
                        .asBitmap()
                        .skipMemoryCache(true)
                        .into(-1, -1)
                        .get();

                FileOutputStream fOut = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
                file.setReadable(true, false);
                file.setWritable(true, false);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return name;
        }

        @Override
        protected void onPostExecute(String name) {
            super.onPostExecute(name);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("username", name);
            editor.commit();
            Log.d("User logged in", name);

            Intent i = new Intent(LoginStub.this, Login.class);
            startActivity(i);
            progress.dismiss();

            Toast.makeText(context, getResources().getString(R.string.signed_in) + name, Toast.LENGTH_SHORT).show();

            finish();
        }
    }
}
