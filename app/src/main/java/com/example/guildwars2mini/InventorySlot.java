package com.example.guildwars2mini;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class InventorySlot {
    private InventorySlot thisSlot = this; //Needed to reference this in bindable
    private Bindable onFinished; //Used to return the inventory slot to a bindable
    private boolean isEmpty;
    private int amountOfItems; //minimum 1, maximum 250
    private GameItem item;
    //List<Integer> infusionsIds; //Optional, list of infusion ids, resolved against /v2/items
    //List<Integer> upgradesIds; //Optional, list of upgrade ids, resolved against /v2/items
    //int skinId; //Optional, skin id of item if transmuted, resolved against /v2/skins
    //ItemStats stats; //TODO: implement object ItemStats

    Bindable setFetchedItem = new Bindable() {
        @Override
        public void execute(Object itemJSON) {
            item = new GameItem((JSONObject) itemJSON);
            onFinished.execute(thisSlot);
        }
    };

    public InventorySlot(JSONObject inventorySlotObject, Bindable onFinished, Context context) {
        if (inventorySlotObject == null || inventorySlotObject.equals(null)) {
            isEmpty = true;
            amountOfItems = -1;
            onFinished.execute(this);
            return;
        }

        this.onFinished = onFinished;

        //Always have to be available, if not empty
        int itemId = -1;
        try {
            itemId = inventorySlotObject.getInt("id");
            amountOfItems = inventorySlotObject.getInt("count");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        // If itemId is non existent, stop further construction
        if (itemId == -1)
            return;

        // Run process for fetching item game object
        FetchJsonData fetch = new FetchJsonData(context.getString(R.string.request_item) + String.valueOf(itemId));
        fetch.bind(setFetchedItem);
        fetch.execute();
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public int getAmountOfItems() {
        return amountOfItems;
    }

    public GameItem getItem() {
        if (isEmpty)
            return null;
        return item;
    }
}
