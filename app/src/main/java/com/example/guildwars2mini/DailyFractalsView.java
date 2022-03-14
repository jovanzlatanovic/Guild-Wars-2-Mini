package com.example.guildwars2mini;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;

public class DailyFractalsView extends CustomLinearLayout {

    public DailyFractalsView(String apiKey, ProgressBar progressBar, @NonNull Context context) {
        super(apiKey, null, context);
        progressBar.setProgress(100); //TODO: implement so progress bar is automatically filled when loading
    }

    @Override
    void initView() {
        Bindable onFetchedDailyAchievments = new Bindable() {
            @Override
            public void execute(Object passed) {
                JSONObject dailyAchievs = (JSONObject) passed;
                try {
                    String dailyFractalIds = "";
                    JSONArray dailyFractals = dailyAchievs.getJSONArray("fractals");
                    for (int i = 0; i < dailyFractals.length(); i++) {
                        JSONObject dailyObject = dailyFractals.getJSONObject(i);
                        dailyFractalIds += String.valueOf(dailyObject.getInt("id") + ",");
                    }
                    getDailyFractals(dailyFractalIds);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        FetchJsonData fetchDailyAchievmentsProcess = new FetchJsonData(getContext().getString(R.string.request_achievments_daily));
        fetchDailyAchievmentsProcess.bind(onFetchedDailyAchievments);
        fetchDailyAchievmentsProcess.execute();
    }

    private void getDailyFractals(String dailyFractalIdsString) {
        Bindable onFetchedFractals = new Bindable() {
            @Override
            public void execute(Object passed) {
                JSONArray dailyFractals = (JSONArray) passed;
                addDailyFractalsToView(dailyFractals);
            }
        };

        FetchJsonData fetchFractalsProcess = new FetchJsonData(getContext().getString(R.string.request_achievments) + dailyFractalIdsString);
        fetchFractalsProcess.bind(onFetchedFractals);
        fetchFractalsProcess.execute();
    }

    private void addDailyFractalsToView(JSONArray dailyFractals) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE );
        HashSet<String> addedDailyFractalTier = new HashSet<>();

        for (int i = 0; i < dailyFractals.length(); i++) {
            String factalNameString = "";
            try {
                factalNameString = dailyFractals.getJSONObject(i).getString("name");
            } catch (JSONException e) {
                factalNameString = "Exception occured";
                e.printStackTrace();
            }

            String text_daily_tier = getContext().getString(R.string.text_daily_tier);
            String dailyFractalNameShortened = factalNameString.substring(text_daily_tier.length() + 3);

            if (factalNameString.contains(text_daily_tier)) {
                if (!addedDailyFractalTier.contains(dailyFractalNameShortened)) {
                    addedDailyFractalTier.add(dailyFractalNameShortened);
                } else {
                    continue;
                }
            }

            // setujem sliku i boju teksta zato sto iz nekog razloga se slika ne prikazuje a tekst je crn
            View dailyFractalView = inflater.inflate(R.layout.achievment_view, null, false);
            TextView fractalName = dailyFractalView.findViewById(R.id.textView_achievmentName);
            if(addedDailyFractalTier.contains(dailyFractalNameShortened)) {
                fractalName.setText("Daily Fractal " + dailyFractalNameShortened);
            } else {
                fractalName.setText(factalNameString);
            }

            fractalName.setTextColor(Color.WHITE);
            ImageView fractalImage = dailyFractalView.findViewById(R.id.image_achievmentType);
            fractalImage.setImageDrawable(getContext().getDrawable(R.drawable.icon_daily_fractals));

            mainLayout.addView(dailyFractalView);
        }
    }
}
