package com.example.android.apitest;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ListView list_view;

    ArrayList<HashMap<String, String>> newsitemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newsitemList = new ArrayList<>();
        list_view = (ListView) findViewById(R.id.list);

        new GetNews().execute();
    }

    private class GetNews extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            String url = "PUT_THE_API_HERE";


            String jsonString = "";
            try {
                jsonString =sh.makeHttpRequest(createUrl(url));
            } catch (IOException e) {
                return null;
            }

            Log.e(TAG, "Response from url: " + jsonString);
            if (jsonString != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonString);
                    JSONObject resonse = jsonObj.getJSONObject("response");
                    JSONArray results = resonse.getJSONArray("results");

                    for (int i = 0; i < results.length(); i++) {
                        JSONObject c = results.getJSONObject(i);
                        String sectionName = c.getString("sectionName");
                        String webTitle = c.getString("webTitle");
                        String webPublicationDate = c.getString("webPublicationDate");

                        HashMap<String, String> newsitem = new HashMap<>();

                        newsitem.put("sectionName", sectionName);
                        newsitem.put("webTitle", webTitle);
                        newsitem.put("webPublicationDate", webPublicationDate);
                        newsitemList.add(newsitem);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server.",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                return null;
            }
            return url;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ListAdapter adapter = new SimpleAdapter(MainActivity.this, newsitemList,
                    R.layout.list_item, new String[]{"sectionName", "webTitle", "webPublicationDate"},
                    new int[]{R.id.textView1, R.id.textView2, R.id.textView3});
            list_view.setAdapter(adapter);
        }
    }
}
