package com.waracle.androidtest;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//Switch to MVVM architecture for cleaner modularity and easier testing
public class MainActivityModel extends AndroidViewModel {
    private static final String JSON_URL = "https://gist.githubusercontent.com/hart88/198f29ec5114a3ec3460/" +
            "raw/8dd19a88f9b8d24c23d9960f3300d0c917a4f07c/cake.json";
    private final CakeLiveData cakes;

    public MainActivityModel(Application application) {
        super(application);
        cakes = new CakeLiveData();
    }

    LiveData<List<Cake>> getCakes() {
        return cakes;
    }

    public class CakeLiveData extends LiveData<List<Cake>>{
        CakeLiveData() {
            new DownloadCakeDataTask().execute();
        }

        /*Asynctasks are prone to memory leaks and generally not recommended, however the reason
          there can be no leaks here is that the AndroidViewModel only keeps a reference to the
          application Context, not an Activity Context which could present a leak.
          This is why the lint check does not apply here either.

          Still, if I could use third party libraries, I would use Retrofit for network operations.*/
        @SuppressLint("StaticFieldLeak")
        private class DownloadCakeDataTask extends AsyncTask<Void, Void, List<Cake>> {

            @Override
            protected List<Cake> doInBackground(Void... voids) {
                try {
                    JSONArray array = loadData();
                    ArrayList<Cake> tempList = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        Cake cake = new Cake();
                        JSONObject obj = array.getJSONObject(i);
                        cake.setTitle(obj.getString("title"));
                        cake.setDescription(obj.getString("desc"));
                        try {
                            cake.setImageData(ImageLoader.loadImageData(obj.getString("image")));
                        } catch (IOException e) {
                            Log.e(getClass().getSimpleName(), "Error loading image data");
                        }
                        tempList.add(cake);
                    }
                    return tempList;
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<Cake> list) {
                cakes.postValue(list);
            }
        }
    }

    private static JSONArray loadData() throws IOException, JSONException {
        URL url = new URL(JSON_URL);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            // Can you think of a way to improve the performance of loading data
            // using HTTP headers???

            // Also, Do you trust any utils thrown your way????

            byte[] bytes = StreamUtils.readUnknownFully(in);

            // Read in charset of HTTP content.
            String charset = parseCharset(urlConnection.getRequestProperty("Content-Type"));

            // Convert byte array to appropriate encoded string.
            String jsonText = new String(bytes, charset);

            // Read string as JSON.
            return new JSONArray(jsonText);
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Returns the charset specified in the Content-Type of this header,
     * or the HTTP default (ISO-8859-1) if none can be found.
     */
    public static String parseCharset(String contentType) {
        if (contentType != null) {
            String[] params = contentType.split(",");
            for (int i = 1; i < params.length; i++) {
                String[] pair = params[i].trim().split("=");
                if (pair.length == 2) {
                    if (pair[0].equals("charset")) {
                        return pair[1];
                    }
                }
            }
        }
        return "UTF-8";
    }
}
