package com.example.guildwars2mini;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BagsToGridViewAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private Activity activity;
    private List<Bag> bags;
    private List<InventorySlot> bagglessInventory = new ArrayList<>();

    public BagsToGridViewAdapter(Activity activity, List<Bag> bags) {
        this.context = activity.getApplicationContext();
        this.bags = bags;
        this.activity = activity;
        for (Bag bag : bags) {
            for (InventorySlot slot : bag.getInventory()) {
                bagglessInventory.add(slot);
            }
        }
    }

    @Override
    public int getCount() {
        return bagglessInventory.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_slot, null);
        }

        ImageView imageView = convertView.findViewById(R.id.image_itemIcon);
        RelativeLayout rarityBorderLayout = convertView.findViewById(R.id.layout_icon_background);
        TextView textAmount = convertView.findViewById(R.id.textView_itemAmount);

        InventorySlot slot = bagglessInventory.get(position);
        GameItem item = slot.getItem();
        if (item == null) {
            rarityBorderLayout.setBackgroundColor(0x5e4f4c);
            imageView.setImageDrawable(context.getDrawable(R.drawable.empty_item_slot));
        } else {
            rarityBorderLayout.setBackgroundColor(item.getRarityColor());
            com.nostra13.universalimageloader.core.ImageLoader imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
            imageLoader.displayImage(item.getIconURL(), imageView);

            int amountOfItems = slot.getAmountOfItems();
            if (amountOfItems > 1) {
                textAmount.setVisibility(View.VISIBLE);
                textAmount.setText(String.valueOf(amountOfItems));
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ItemDialog dialog = new ItemDialog(item, activity);
                    dialog.openItemDialog();
                }
            });
        }

        return convertView;
    }
}
