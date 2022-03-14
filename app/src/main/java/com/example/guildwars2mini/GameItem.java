package com.example.guildwars2mini;

import android.graphics.Color;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GameItem {
    private int id;
    private String chatLink;
    private String name;
    private String iconURL;
    private String description;
    private ItemType type;
    private ItemRarity rarity;
    private int requiredLevel;
    private Coin vendorValue;
    private int defaultSkinId;
    private List<ItemFlag> flags = new ArrayList<ItemFlag>();

    public GameItem(JSONObject ItemJSON) {
        try {
            this.id = ItemJSON.getInt("id");
            this.chatLink = ItemJSON.getString("chat_link");
            this.name = ItemJSON.getString("name");
            this.iconURL = ItemJSON.getString("icon");
            this.type = ItemType.valueOf(ItemJSON.getString("type"));
            this.rarity = ItemRarity.valueOf(ItemJSON.getString("rarity"));;
            this.requiredLevel = ItemJSON.getInt("level");
            this.vendorValue = new Coin(ItemJSON.getInt("vendor_value"));

            JSONArray flagArray = ItemJSON.getJSONArray("flags");
            for (int i = 0; i < flagArray.length(); i++) {
                flags.add(parseFlag(flagArray.getString(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // The description getter has been seperated from the try catch above because
        // the "description" is an optional value from the API, not always returned.
        try {
            this.description = ItemJSON.getString("description");
        } catch (JSONException e) {
            this.description = "";
        }
    }

    private ItemFlag parseFlag(String flag) {
        try {
            return ItemFlag.valueOf(flag);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getId() {
        return id;
    }

    public String getChatLink() {
        return chatLink;
    }

    public String getName() {
        return name;
    }

    public String getIconURL() {
        return iconURL;
    }

    public String getDescription() {
        if (description == null)
            return "";
        return description;
    }

    public ItemType getType() {
        if (type == null)
            return ItemType.Unknown;
        return type;
    }

    public ItemRarity getRarity() {
        if (rarity == null)
            return ItemRarity.Junk;
        return rarity;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public Coin getVendorValue() {
        return vendorValue;
    }

    public int getDefaultSkinId() {
        return defaultSkinId;
    }

    public List<ItemFlag> getFlags() {
        return flags;
    }

    public int getRarityColor() {
        String defaultGray = "#AAAAAA";

        if (rarity == null)
            return Color.parseColor(defaultGray);

        switch (rarity) {
            case Basic:
                return Color.parseColor("#FFFFFF");
            case Fine:
                return Color.parseColor("#62A4DA");
            case Masterwork:
                return Color.parseColor("#1A9306");
            case Rare:
                return Color.parseColor("#FCB00D");
            case Exotic:
                return Color.parseColor("#FFA405");
            case Ascended:
                return Color.parseColor("#FB3E8D");
            case Legendary:
                return Color.parseColor("#4C139D");
            default:
                return Color.parseColor(defaultGray);
        }
    }
}
