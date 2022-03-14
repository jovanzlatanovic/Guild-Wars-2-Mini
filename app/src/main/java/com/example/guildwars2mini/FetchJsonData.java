package com.example.guildwars2mini;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

//napraviti rucno thread todo
//odvojiti karaktere za kasnije ucitavanje
//execute on executor za resavanje

//TODO: Async task is deprecated, use something else

public class FetchJsonData extends AsyncTask<Void, Void, Void> {
    private static List<FetchJsonData> fetchingTracker = new ArrayList<>();

    private Object fetchedJson = null;
    private List<Bindable> boundList = new ArrayList<Bindable>();
    private String url = null;
    private CounterNotifier counter = null;
    private ObjectResultType resultType = null;

    public FetchJsonData(String url) {
        this.url = url;
    }

    // Bind a Bindable object which has a function that will be executed upon fetching the JSON.
    public void bind(Bindable boundObject) {
        boundList.add(boundObject);
    }

    // Adds a CounterNotifier object from which a single function is called onPostExecute.
    // Must provide the type of result of what kind of object is passed
    public void addCounter(CounterNotifier counter, ObjectResultType resultType) {
        this.counter = counter;
        this.resultType = resultType;
    }

    // Cancels all processes that are executing in fetchingTracker list.
    public static void cancelAll() {
        Iterator<FetchJsonData> iterator = fetchingTracker.iterator();
        while (iterator.hasNext()) {
            iterator.next().cancel(true);
            iterator.remove();
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL requestUrl = new URL(url);

            HttpURLConnection httpURLConnection = (HttpURLConnection) requestUrl.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String data = "";
            String line = "";
            while (line != null) {
                line = bufferedReader.readLine();
                data = data + line;
            }

            if (data.substring(0, 1).equals("{"))
                fetchedJson = new JSONObject(data);
            else if (data.substring(0, 1).equals("["))
                fetchedJson = new JSONArray(data);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // Error occurs when invalid api key
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        fetchingTracker.add(this);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        fetchingTracker.remove(this);
        if (boundList.size() > 0) {
            for (Bindable bindable : boundList) {
                bindable.execute(fetchedJson);
            }
        }
        if (counter != null)
            counter.countDown(resultType, fetchedJson);
    }
}
