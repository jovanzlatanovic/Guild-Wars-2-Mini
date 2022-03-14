package com.example.guildwars2mini;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Bag {
    private Bag thisBag = this;
    private Bindable onBagInventoryFetched;
    private int bagItemId;
    private int amountOfSlots;
    private List<InventorySlot> inventory = new ArrayList<>();

    Bindable addFetchedSlotToInventory = new Bindable() {
        @Override
        public void execute(Object inventorySlot) {
            inventory.add((InventorySlot) inventorySlot);
            if (inventory.size() >= amountOfSlots)
                onBagInventoryFetched.execute(thisBag);
        }
    };

    public Bag(JSONObject bagJSON, Bindable onBagInventoryFetched, Context context) {
        try {
            this.onBagInventoryFetched = onBagInventoryFetched;

            if (bagJSON == null) {
                onBagInventoryFetched.execute(thisBag);
                return;
            }

            bagItemId = bagJSON.getInt("id");
            amountOfSlots = bagJSON.getInt("size");

            JSONArray inventoryJSON = bagJSON.getJSONArray("inventory");
            for (int i = 0; i < inventoryJSON.length(); i++) {
                Object slotObject = inventoryJSON.get(i);
                if (slotObject.equals(null)) {
                    InventorySlot slot = new InventorySlot(null, addFetchedSlotToInventory, context);
                } else {
                    JSONObject parsedSlotObject = (JSONObject) slotObject;
                    InventorySlot slot = new InventorySlot(parsedSlotObject, addFetchedSlotToInventory, context);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getBagItemId() {
        return bagItemId;
    }

    public int getAmountOfSlots() {
        return amountOfSlots;
    }

    public List<InventorySlot> getInventory() {
        return inventory;
    }
}
