package com.example.guildwars2mini;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ShowCharacterActivity extends AppCompatActivity {

    Character character;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_character);

        character = (Character) getIntent().getSerializableExtra(getString(R.string.character_extra));
        initView();
    }

    private void setGuildName(String name) {
        TextView guildName = findViewById(R.id.textView_value_char_guildName);
        guildName.setText(name);
    }

    private void setTitleName(String name) {
        TextView title = findViewById(R.id.textView_value_char_title);
        title.setText(name);
    }

    private View getItemSlotView(GameItem item) {
        View itemSlot = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_slot, null, false);
        ImageView imageView = itemSlot.findViewById(R.id.image_itemIcon);
        RelativeLayout rarityBorderLayout = itemSlot.findViewById(R.id.layout_icon_background);

        rarityBorderLayout.setBackgroundColor(item.getRarityColor());

        com.nostra13.universalimageloader.core.ImageLoader imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
        imageLoader.displayImage(item.getIconURL(), imageView);

        itemSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemDialog dialog = new ItemDialog(item, ShowCharacterActivity.this);
                dialog.openItemDialog();
            }
        });

        return itemSlot;
    }

    private void initView() {
        TextView name = findViewById(R.id.textView_value_char_name);
        TextView race = findViewById(R.id.textView_value_char_race);
        TextView gender = findViewById(R.id.textView_value_char_gender);
        TextView profession = findViewById(R.id.textView_value_char_profession);
        TextView level = findViewById(R.id.textView_value_char_level);
        TextView age = findViewById(R.id.textView_value_char_age);
        TextView dateCreated = findViewById(R.id.textView_value_char_dateCreated);
        TextView deaths = findViewById(R.id.textView_value_char_deaths);
        TextView craftingProfessions = findViewById(R.id.textView_value_char_craftingProfessions);
        LinearLayout characterInfoLayout = findViewById(R.id.layout_character_information);
        LinearLayout characterGearArmorLayout = findViewById(R.id.layout_character_gear_armor);
        LinearLayout characterGearWeaponLayout = findViewById(R.id.layout_character_gear_weapons);
        LinearLayout characterGearTrinketLayout = findViewById(R.id.layout_character_gear_trinkets);
        LinearLayout characterGearGatheringLayout = findViewById(R.id.layout_character_gear_gathering);
        ProgressBar progressBarEquipment = findViewById(R.id.progressBar_equipment);
        ProgressBar progressBarInventory = findViewById(R.id.progressBar_inventory);

        LinearLayout characterScreen = findViewById(R.id.linearLayout_characterScreen);
        GridView gridViewInventory = new InventoryGridView(getApplicationContext());
        characterScreen.addView(gridViewInventory);

        name.setText(character.getName());
        race.setText(character.getRace());
        gender.setText(character.getGender());
        profession.setText(character.getProfession());
        level.setText(String.valueOf(character.getLevel()));
        age.setText(String.valueOf((int) TimeUnit.SECONDS.toHours(character.getAge())));
        dateCreated.setText(character.getDateTimeCreated().toString().replace("T", " "));
        deaths.setText(String.valueOf(character.getDeaths()));
        craftingProfessions.setText("Not yet implemented");

        Bindable onFetchedGuildPresenting = new Bindable() {
            @Override
            public void execute(Object passed) {
                try {
                    JSONObject obj = (JSONObject) passed;
                    String guildName = obj.getString("name");
                    String guildTag = obj.getString("tag");
                    setGuildName("[" + guildTag + "] " + guildName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        character.getGuildPresenting(onFetchedGuildPresenting, getApplicationContext());

        Bindable onFetchedTitle = new Bindable() {
            @Override
            public void execute(Object passed) {
                try {
                    JSONObject obj = (JSONObject) passed;
                    String titleName = obj.getString("name");
                    setTitleName(titleName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        character.getTitle(onFetchedTitle, getApplicationContext());

        Bindable onFetchedEquipment = new Bindable() {
            @Override
            public void execute(Object passed) {
                HashMap<Integer, JSONObject> equipmentItems =  (HashMap<Integer, JSONObject>) passed;
                for (Map.Entry<Integer, JSONObject> equipmentSet : equipmentItems.entrySet()) {
                    // Prepare itemSlot object for every item

                    GameItem item = new GameItem(equipmentSet.getValue());
                    View itemSlot = getItemSlotView(item);

                    //Decide where the itemSlot goes
                    switch(item.getType()) {
                        case Armor:
                            characterGearArmorLayout.addView(itemSlot);
                            break;
                        case Weapon:
                            characterGearWeaponLayout.addView(itemSlot);
                            break;
                        case Gathering:
                            characterGearGatheringLayout.addView(itemSlot);
                            break;
                        default:
                            characterGearTrinketLayout.addView(itemSlot);
                            break;
                    }
                }
                progressBarEquipment.setVisibility(View.GONE);
            }
        };
        character.getEquipment(onFetchedEquipment, getApplicationContext());

        Activity thisActivity = this;
        Bindable onFetchInventory = new Bindable() {
            @Override
            public void execute(Object bagList) {
                //List<Bag> object is passed containing bags equipped on character
                List<Bag> bags = (List<Bag>) bagList;
                BagsToGridViewAdapter adapter = new BagsToGridViewAdapter(thisActivity, bags);
                gridViewInventory.setAdapter(adapter);

                progressBarInventory.setVisibility(View.GONE);
            }
        };
        character.getInventory(onFetchInventory, getApplicationContext());
    }
}