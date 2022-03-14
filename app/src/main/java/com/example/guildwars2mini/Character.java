package com.example.guildwars2mini;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Character implements Serializable {
    private String name;
    private String race;
    private String gender;
    private String profession;
    private String guildId;
    private String titleId;
    private int level;
    private int age;
    private int deaths;
    private LocalDateTime dateTimeCreated;
    private List<Integer> equipmentIds = new ArrayList<>();
    private String bagsJSON; //Is converted to string to be serializable

    public Character(JSONObject characterJSON) throws JSONException {
        // Information initialization
        name = characterJSON.getString("name");
        race = characterJSON.getString("race");
        gender = characterJSON.getString("gender");
        profession = characterJSON.getString("profession");
        level = characterJSON.getInt("level");
        guildId = characterJSON.getString("guild");
        age = characterJSON.getInt("age");
        deaths = characterJSON.getInt("deaths");
        titleId = characterJSON.getString("title");

        // Date setting
        String dateTimeString = characterJSON.getString("created");
        String date = dateTimeString.substring(0, 10);
        String time = dateTimeString.substring(11, 16);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        dateTimeCreated = LocalDateTime.parse(date + " " + time, formatter);

        // Equipment setting
        JSONArray JSONequipmentArray = characterJSON.getJSONArray("equipment");

        for (int i = 0; i < JSONequipmentArray.length(); i++) {
            JSONObject item = JSONequipmentArray.getJSONObject(i);
            equipmentIds.add(item.getInt("id"));

        // Bag setting
            bagsJSON = characterJSON.getJSONArray("bags").toString();
        }
    }

    private GameItem getItem() {
        return null;
    }

    public String getName() {
        return name;
    }

    public String getRace() {
        return race;
    }

    public String getGender() {
        return gender;
    }

    public String getProfession() {
        return profession;
    }

    public int getLevel() {
        return level;
    }

    public void getGuildPresenting(Bindable onResponse, Context context) {
        FetchJsonData fetch = new FetchJsonData(context.getString(R.string.request_guild) + guildId);
        fetch.bind(onResponse);
        fetch.execute();
    }

    public int getAge() {
        return age;
    }

    public LocalDateTime getDateTimeCreated() {
        return dateTimeCreated;
    }

    public int getDeaths() {
        return deaths;
    }

    public String getCraftingSkills() {
        return null;
    }

    public void getTitle(Bindable onResponse, Context context) {
        FetchJsonData fetch = new FetchJsonData(context.getString(R.string.request_title) + titleId);
        fetch.bind(onResponse);
        fetch.execute();
    }

    // Bindable returns HashMap<Integer, JSONObject> object on response execute.
    public void getEquipment(Bindable onResponse, Context context) {
        Bindable onAllItemsFetched = new Bindable() {
            @Override
            public void execute(Object passed) {
                onResponse.execute(passed);
            }
        };

        CounterNotifier notifier = new CounterNotifier(equipmentIds.size(), onAllItemsFetched);

        for (int itemId : equipmentIds) {
            FetchJsonData fetch = new FetchJsonData(context.getString(R.string.request_item) + String.valueOf(itemId));
            fetch.addCounter(notifier, ObjectResultType.COUNTABLE);
            fetch.execute();
        }
    }

    //onResponse passes List<Bag> inventory
    public void getInventory(Bindable onResponse, Context context) {
        List<Bag> inventory = new ArrayList<>();

        Bindable onAllBagsFetched = new Bindable() {
            @Override
            public void execute(Object passed) {
                onResponse.execute(inventory);
            }
        };

        JSONArray bagsJSONArray = null;
        try {
            bagsJSONArray = new JSONArray(bagsJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CounterNotifier notifier = new CounterNotifier(bagsJSONArray.length(), onAllBagsFetched);

        Bindable onFetchSingleBag = new Bindable() {
            @Override
            public void execute(Object bag) {
                inventory.add((Bag) bag);
                notifier.countDown();
            }
        };

        for (int i = 0; i < bagsJSONArray.length(); i++) {
            JSONObject bagJSON = null;
            try {
                bagJSON = bagsJSONArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            new Bag(bagJSON, onFetchSingleBag, context);
            //FetchJsonData fetch = new FetchJsonData(context.getString(R.string.request_item) + String.valueOf(itemId));
            //fetch.addCounter(notifier, ObjectResultType.COUNTABLE);
            //fetch.execute();
        }
    }
}
