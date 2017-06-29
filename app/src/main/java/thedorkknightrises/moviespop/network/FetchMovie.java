package thedorkknightrises.moviespop.network;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;

import thedorkknightrises.moviespop.Detail_Fragment;
import thedorkknightrises.moviespop.Details;
import thedorkknightrises.moviespop.MainActivity;
import thedorkknightrises.moviespop.R;

/**
 * Created by samri_000 on 5/1/2016.
 */
public class FetchMovie extends AsyncTask<String, Void, Bundle> {

    Context context;
    Bundle bundle;
    SharedPreferences pref;
    Uri buildUri;
    int id;
    View view;
    Detail_Fragment details;
    ProgressDialog progressDialog;
    FragmentTransaction ft;

    public FetchMovie(Context c, View v, Detail_Fragment detail_fragment, FragmentTransaction fragmentTransaction, int i, SharedPreferences sPref) {
        super();
        id = i;
        view = v;
        details = detail_fragment;
        ft = fragmentTransaction;
        context = c;
        pref = sPref;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getResources().getString(R.string.loading));
        progressDialog.show();
    }

    @Override
    protected Bundle doInBackground(String... params) {
        bundle = new Bundle();
        if (params.length == 0) {
            return null;
        }
        HttpURLConnection client = null;
        BufferedReader bufferedReader = null;
        String searchJSONstr = null;
        final String SEARCH_BASE_URL =
                "https://api.themoviedb.org/3/movie";
        final String API_KEY = context.getString(R.string.moviedb_api_key);
        final String API_KEY_PARAM = "api_key";

        buildUri = Uri.parse(SEARCH_BASE_URL).buildUpon()
                .appendPath(Integer.toString(id))
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
            searchJSONstr = buffer.toString();
            Log.d("JSON Str", searchJSONstr);
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

        try

        {
            return getSearchDataFromJson(searchJSONstr);
        } catch (
                Exception e
                )

        {
            String error;
            error = context.getString(R.string.network);

            e.printStackTrace();
        }

        return null;
    }

    private Bundle getSearchDataFromJson(String searchJSONstr)
            throws Exception {
        final String MOVIE_TITLE = "original_title";
        final String MOVIE_YEAR = "release_date";
        final String VOTE_AVG = "vote_average";
        final String MOVIE_POSTER_URL = "poster_path";
        final String BACKDROP_URL = "backdrop_path";
        final String MOVIE_PLOT = "overview";

        JSONObject movieObject = new JSONObject(searchJSONstr);

        String res = pref.getString("res", "w185");
        String res_bg = pref.getString("res_bg", "w780");
        Log.d("movieArray", searchJSONstr);

        String title = movieObject.getString(MOVIE_TITLE);
        String posterUrl = movieObject.getString(MOVIE_POSTER_URL);
        String year = movieObject.getString(MOVIE_YEAR);
        String plot = movieObject.getString(MOVIE_PLOT);
        String vote_avg = movieObject.getString(VOTE_AVG);
        String bgUrl = movieObject.getString(BACKDROP_URL);

        if (!year.equals("")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            year = simpleDateFormat.format(Date.valueOf(year));
        }

        bundle.putInt("id", id);
        bundle.putString("title", title);
        bundle.putString("overview", plot);
        bundle.putString("release_date", year);
        bundle.putString("vote_avg", vote_avg);
        bundle.putString("poster", "https://image.tmdb.org/t/p/" + res + posterUrl);
        bundle.putString("bg", "https://image.tmdb.org/t/p/" + res_bg + bgUrl);

        return bundle;
    }

    @Override
    protected void onPostExecute(Bundle bundle) {
        super.onPostExecute(bundle);

        if (MainActivity.getmDualPane()) {

            try {

                view.setVisibility(View.GONE);

                // Make new fragment to show this selection.
                details = Detail_Fragment.newInstance(0, bundle);


                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                ft.replace(R.id.details, details);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else {
            Intent i = new Intent(context, Details.class);
            i.putExtra("id", bundle.getInt("id"));
            i.putExtra("title", bundle.getString("title"));
            i.putExtra("release_date", bundle.getString("release_date"));
            i.putExtra("vote_avg", bundle.getString("vote_avg"));
            i.putExtra("overview", bundle.getString("overview"));
            i.putExtra("poster", bundle.getString("poster"));
            i.putExtra("bg", bundle.getString("bg"));
            context.startActivity(i);
        }

        progressDialog.dismiss();
    }
}
