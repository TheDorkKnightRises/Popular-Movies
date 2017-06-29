package thedorkknightrises.moviespop.network;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import thedorkknightrises.moviespop.R;
import thedorkknightrises.moviespop.TrailerAdapter;
import thedorkknightrises.moviespop.TrailerObj;

/**
 * Created by samri_000 on 4/14/2016.
 */
public class FetchTrailers extends AsyncTask<String, Void, ArrayList<TrailerObj>> {

    public TrailerAdapter trAdapter;
    Context context;
    RecyclerView trView;
    ArrayList<TrailerObj> trResults;
    int mId;
    Uri buildUri;
    String mTitle;

    public FetchTrailers(Context c, RecyclerView v, ArrayList<TrailerObj> trRes, int movieId, String title) {
        super();
        context = c;
        trView = v;
        trResults = trRes;
        mId = movieId;
        mTitle = title;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (!trResults.isEmpty()) {
            trResults.clear();
        }
    }

    @Override
    protected ArrayList<TrailerObj> doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }
        HttpURLConnection client = null;
        BufferedReader bufferedReader = null;
        String searchJSONstr = null;
        final String SEARCH_BASE_URL =
                "https://api.themoviedb.org/3/movie/";
        final String API_KEY = context.getString(R.string.moviedb_api_key);
        final String API_KEY_PARAM = "api_key";

        buildUri = Uri.parse(SEARCH_BASE_URL).buildUpon()
                .appendPath(String.valueOf(mId))
                .appendPath("videos")
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
            e.printStackTrace();
        }

        return null;
    }

    private ArrayList<TrailerObj> getSearchDataFromJson(String searchJSONstr)
            throws Exception {
        final String LIST_NAME = "results";
        final String TRAILER_KEY = "key";
        final String TRAILER_NAME = "name";

        JSONObject searchResult = new JSONObject(searchJSONstr);
        JSONArray trArray = searchResult.getJSONArray(LIST_NAME);

        Log.d("trArray", trArray.toString());

        for (int i = 0; i < trArray.length(); i++) {
            JSONObject movieObject = trArray.getJSONObject(i);
            String title = movieObject.getString(TRAILER_NAME);
            String key = movieObject.getString(TRAILER_KEY);

            trResults.add(new TrailerObj(title, key));
        }

        return trResults;

    }

    @Override
    protected void onPostExecute(ArrayList<TrailerObj> trailers) {
        super.onPostExecute(trailers);

        try {
            trAdapter = new TrailerAdapter(trailers, context, mTitle);
            trView.setAdapter(trAdapter);
            trView.setVisibility(View.VISIBLE);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

}
