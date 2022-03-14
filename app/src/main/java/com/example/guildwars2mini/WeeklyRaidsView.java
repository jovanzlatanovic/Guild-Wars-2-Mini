package com.example.guildwars2mini;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WeeklyRaidsView extends CustomLinearLayout {

    JSONArray raidList;
    JSONArray completedRaidList;

    public WeeklyRaidsView(String apiKey, ProgressBar progressBar, @NonNull Context context) {
        super(apiKey, null, context);
        progressBar.setProgress(100); //TODO: implement so progress bar is automatically filled when loading
    }

    @Override
    void initView() {
        // Executed once all json data has been aquired;
        Bindable onAllDataFetched = new Bindable() {
            @Override
            public void execute(Object passed) {
                HashMap<ObjectResultType, Object> resultMap = (HashMap<ObjectResultType, Object>) passed;

                for (Map.Entry<ObjectResultType, Object> entry : resultMap.entrySet()) {
                    switch (entry.getKey()) {
                        case RAIDS_ALL:
                            raidList = (JSONArray) entry.getValue();
                            break;
                        case RAIDS_COMPLETED:
                            completedRaidList = (JSONArray) entry.getValue();
                            break;
                    }
                }

                drawAllRaids();
            }
        };

        CounterNotifier notifier = new CounterNotifier(2, onAllDataFetched);

        getAllRaids(notifier);
        getAccountCompletedRaids(notifier);
    }

    //TODO: ova funkcija je uzasna (3 for petlje!), molim te proveri kako ovo bolje uraditi, pretty please xoxo
    //TODO: continue work using raid_view.xml and inflator
    private void drawRaidWing(JSONObject raidJSON) {
        View raidView = LayoutInflater.from(getContext()).inflate(R.layout.raid_view, null, false);
        TextView raidName = (TextView) raidView.findViewById(R.id.textView_raidName);
        LinearLayout raidViewLayout = (LinearLayout) raidView.findViewById(R.id.layout_raidView);

        try {
            String nameId = raidJSON.getString("id");
            raidName.setText(convertIdToName(nameId));

            JSONArray raidWingsJSON = raidJSON.getJSONArray("wings");
            for (int currentWing = 0; currentWing < raidWingsJSON.length(); currentWing++) {
                JSONObject wingJSON = raidWingsJSON.getJSONObject(currentWing);

                String wingID = wingJSON.getString("id");
                JSONArray wingEvents = wingJSON.getJSONArray("events");

                /*View wingEventView = LayoutInflater.from(getContext()).inflate(R.layout.achievment_view, null, false);
                TextView wingEventName = (TextView) wingEventView.findViewById(R.id.textView_achievmentName);
                wingEventName.setText(wingID);
                wingEventName.setTextColor(Color.WHITE);*/

                LinearLayout wingLayout = new LinearLayout(getContext());
                wingLayout.setOrientation(LinearLayout.VERTICAL);

                for (int currentWingEvent = 0; currentWingEvent < wingEvents.length(); currentWingEvent++) {
                    View wingEventView = LayoutInflater.from(getContext()).inflate(R.layout.achievment_view, null, false);
                    ImageView raidImage = (ImageView) wingEventView.findViewById(R.id.image_achievmentType);
                    raidImage.setImageDrawable(getContext().getDrawable(R.drawable.icon_boss));
                    TextView wingEventName = (TextView) wingEventView.findViewById(R.id.textView_achievmentName);

                    String wingEventID = wingEvents.getJSONObject(currentWingEvent).getString("id");
                    String wingEventType = wingEvents.getJSONObject(currentWingEvent).getString("type");

                    wingEventName.setText(convertIdToName(wingEventID));
                    if (completedRaidList.toString().contains("\"" + wingEventID + "\"")) {
                        raidImage.setImageDrawable(getContext().getDrawable(R.drawable.icon_boss_completed));
                        wingEventName.setTextColor(Color.GREEN);
                    }
                    else
                        wingEventName.setTextColor(Color.WHITE);

                    wingLayout.addView(wingEventView);
                }

                TextView textViewWingId = new TextView(getContext());
                textViewWingId.setText(convertIdToName(wingID));
                textViewWingId.setTextColor(Color.WHITE);
                textViewWingId.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

                raidViewLayout.addView(textViewWingId);
                raidViewLayout.addView(wingLayout);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mainLayout.addView(raidView);
    }

    private String convertIdToName(String idValue) {
        String[] array = idValue.split("_");
        String nameValue = "";
        for (String word : array) {
            nameValue += word.substring(0,1).toUpperCase() + word.substring(1).toLowerCase() + " ";
        }
        nameValue = nameValue.substring(0, nameValue.length() - 1);
        return nameValue;
    }

    private void drawAllRaids() {
        Bindable onFetchedRaid = new Bindable() {
            @Override
            public void execute(Object passed) {
                drawRaidWing((JSONObject) passed);
            }
        };

        try {
            for (int i = 0; i < raidList.length(); i++) {
                FetchJsonData fetchRaidFromID = new FetchJsonData(getContext().getString(R.string.request_raids) + "/" + raidList.getString(i));
                fetchRaidFromID.bind(onFetchedRaid);
                fetchRaidFromID.execute();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Gets list of all available raid ids in guild wars 2
    private void getAllRaids(CounterNotifier notifier) {
        FetchJsonData fetchRaidsProcess = new FetchJsonData(getContext().getString(R.string.request_raids));
        fetchRaidsProcess.addCounter(notifier, ObjectResultType.RAIDS_ALL);
        fetchRaidsProcess.execute();
    }

    // Gets a list of all raids the account of the provided api key has completed this week
    private void getAccountCompletedRaids(CounterNotifier notifier) {
        FetchJsonData fetchRaidsProcess = new FetchJsonData(getContext().getString(R.string.request_user_completed_raids) + apiKey);
        fetchRaidsProcess.addCounter(notifier, ObjectResultType.RAIDS_COMPLETED);
        fetchRaidsProcess.execute();
    }
}
