package com.example.guildwars2mini;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ItemDialog {
    private Activity activity;
    private AlertDialog dialog;
    private GameItem item;

    ItemDialog(GameItem item, Activity myActivity) {
        activity = myActivity;
        this.item = item;
    }

    public void openItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        String requiredLevelString = "Required level: ";

        View layoutView = inflater.inflate(R.layout.item_dialog, null);
        TextView itemName = layoutView.findViewById(R.id.item_name);
        TextView itemRarity = layoutView.findViewById(R.id.item_rarity);
        TextView itemType = layoutView.findViewById(R.id.item_type);
        TextView itemRequredLevel = layoutView.findViewById(R.id.item_requiredLevel);
        TextView itemDescription = layoutView.findViewById(R.id.item_description);
        TextView itemPriceGold = layoutView.findViewById(R.id.item_value_gold);
        TextView itemPriceSilver = layoutView.findViewById(R.id.item_value_silver);
        TextView itemPriceCopper = layoutView.findViewById(R.id.item_value_silver);
        ImageView itemImageView = layoutView.findViewById(R.id.item_image);
        LinearLayout itemPriceLayout = layoutView.findViewById(R.id.layout_vendorPrice);

        itemName.setText(item.getName());
        itemName.setTextColor(item.getRarityColor());
        itemRarity.setText(item.getRarity().toString());
        itemType.setText(item.getType().toString());
        itemRequredLevel.setText(requiredLevelString + String.valueOf(item.getRequiredLevel()));
        itemDescription.setText(item.getDescription());

        com.nostra13.universalimageloader.core.ImageLoader imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
        imageLoader.displayImage(item.getIconURL(), itemImageView);

        if (item.getVendorValue() == null)
            itemPriceLayout.setVisibility(View.INVISIBLE);
        else {
            itemPriceGold.setText(String.valueOf(item.getVendorValue().getGold()));
            itemPriceSilver.setText(String.valueOf(item.getVendorValue().getSilver()));
            itemPriceCopper.setText(String.valueOf(item.getVendorValue().getCopper()));
        }

        builder.setView(layoutView);
        builder.setCancelable(true);

        dialog = builder.create();
        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }
}
