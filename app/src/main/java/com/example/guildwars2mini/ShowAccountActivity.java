package com.example.guildwars2mini;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Space;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShowAccountActivity extends AppCompatActivity {

    String apiKey;
    JSONObject account;
    JSONArray wallet;
    LinearLayout charactersView;
    CounterNotifier charactersCounterNotifier;
    List<Character> characterList = new ArrayList<>();

    @Override
    public void onBackPressed() {
        FetchJsonData.cancelAll();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_account);

        Bundle extras = getIntent().getExtras();
        apiKey = extras.getString(getString(R.string.api_key_extra));

        // Get views
        TextView text_accountName = findViewById(R.id.textView_value_accountName);
        TextView text_gold = findViewById(R.id.textView_value_gold);
        TextView text_silver = findViewById(R.id.textView_value_silver);
        TextView text_copper = findViewById(R.id.textView_value_copper);
        TextView text_karma = findViewById(R.id.textView_value_karma);
        TextView text_gem = findViewById(R.id.textView_value_gem);
        charactersView = findViewById(R.id.characters_view);

        // Call the daily fractals function which adds the view to this activity
        addDailyFractalsView();

        // Call the weekly raids function which adds the view to this activity
        addWeeklyRaidsView();

        // Start fetching character process
        fetchCharacters();

        // Setup the wallet onclick event
        LinearLayout layout_wallet_button = findViewById(R.id.layout_wallet_button);
        layout_wallet_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wallet == null)
                    return;

                Intent showAccountWalletIntent = new Intent(ShowAccountActivity.this, ShowAccountWalletActivity.class);
                showAccountWalletIntent.putExtra(getString(R.string.wallet_json_extra), wallet.toString());
                startActivity(showAccountWalletIntent);
            }
        });

        // Bindable wallet setup
        Bindable onWalletFetched = new Bindable() {
            @Override
            public void execute(Object passed) {
                wallet = (JSONArray) passed;
                Coin coins = new Coin();
                int karmaNumber = 0;
                int gemNumber = 0;

                try {
                    int coinsNumber = 0;

                    for (int i = 0; i < wallet.length(); i++) {
                        JSONObject currency = wallet.getJSONObject(i);
                        if (wallet.getJSONObject(i).getInt("id") == 1) {
                            coinsNumber = wallet.getJSONObject(i).getInt("value");
                            coins = new ApiCoinAdapter(coinsNumber).getCoin();
                        }
                        if (wallet.getJSONObject(i).getInt("id") == 2) {
                            karmaNumber = wallet.getJSONObject(i).getInt("value");
                        }
                        if (wallet.getJSONObject(i).getInt("id") == 4) {
                            gemNumber = wallet.getJSONObject(i).getInt("value");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                text_gold.setText(NumberFormat.getNumberInstance(Locale.US).format(coins.getGold()));
                text_silver.setText(String.valueOf(coins.getSilver()));
                text_copper.setText(String.valueOf(coins.getCopper()));
                text_karma.setText(NumberFormat.getNumberInstance(Locale.US).format(karmaNumber));
                text_gem.setText(NumberFormat.getNumberInstance(Locale.US).format(gemNumber));
            }
        };

        // Extract gold and account name from jsons
        try {
            account = new JSONObject(extras.getString(getString(R.string.account_json_extra)));
            text_accountName.setText(account.getString("name"));

            FetchJsonData fetchWalletProcess = new FetchJsonData(getString(R.string.request_wallet) + apiKey);
            fetchWalletProcess.bind(onWalletFetched);
            fetchWalletProcess.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Setup the account inventory onclick event
        //TODO
    }

    private void addDailyFractalsView() {
        ProgressBar bar = findViewById(R.id.progressBar_dailyFractalsLoading);
        LinearLayout slot = findViewById(R.id.slot_dailyFractalsToday);
        slot.addView(new DailyFractalsView(apiKey, bar, getApplicationContext()));
    }

    private void addWeeklyRaidsView() {
        ProgressBar bar = findViewById(R.id.progressBar_weeklyRaidsLoading);
        LinearLayout slot = findViewById(R.id.slot_weeklyRaids);
        slot.addView(new WeeklyRaidsView(apiKey, bar, getApplicationContext()));
    }

    private void addCharacterButton(Character character) {
        View characterButtonView = getLayoutInflater().inflate(R.layout.character_button, null, false);
        TextView characterName = (TextView) characterButtonView.findViewById(R.id.textView_characterName);
        TextView characterLevel = (TextView) characterButtonView.findViewById(R.id.textView_level);
        LinearLayout imageProfile = (LinearLayout) characterButtonView.findViewById(R.id.image_profile);
        ImageView imageProfession = (ImageView) characterButtonView.findViewById(R.id.image_profession);

        // Listener for clicking character icon
        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showAccountIntent = new Intent(ShowAccountActivity.this, ShowCharacterActivity.class);
                showAccountIntent.putExtra(getString(R.string.character_extra), character);
                startActivity(showAccountIntent);
            }
        });

        characterName.setText(character.getName());
        characterLevel.setText(String.valueOf(character.getLevel()));

        String race = character.getRace();
        String gender = character.getGender();

        PlayableRaces raceEnum = PlayableRaces.valueOf(race);
        Gender genderEnum = Gender.valueOf(gender);
        switch (raceEnum) {
            case Asura:
                if (genderEnum == Gender.Female)
                    imageProfile.setBackground(getDrawable(R.drawable.asura_female));
                else
                    imageProfile.setBackground(getDrawable(R.drawable.asura_male));
                break;
            case Charr:
                if (genderEnum == Gender.Female)
                    imageProfile.setBackground(getDrawable(R.drawable.char_female));
                else
                    imageProfile.setBackground(getDrawable(R.drawable.char_male));
                break;
            case Human:
                if (genderEnum == Gender.Female)
                    imageProfile.setBackground(getDrawable(R.drawable.human_female));
                else
                    imageProfile.setBackground(getDrawable(R.drawable.human_male));
                break;
            case Norn:
                if (genderEnum == Gender.Female)
                    imageProfile.setBackground(getDrawable(R.drawable.norn_female));
                else
                    imageProfile.setBackground(getDrawable(R.drawable.norn_male));
                break;
            case Sylvari:
                if (genderEnum == Gender.Female)
                    imageProfile.setBackground(getDrawable(R.drawable.sylvari_female));
                else
                    imageProfile.setBackground(getDrawable(R.drawable.sylvari_male));
                break;
        }

        String profession = character.getProfession();
        Profession professionEnum = Profession.valueOf(profession);
        switch (professionEnum) {
            case Elementalist:
                imageProfession.setImageDrawable(getDrawable(R.drawable.icon48_elementalist));
                break;
            case Engineer:
                imageProfession.setImageDrawable(getDrawable(R.drawable.icon48_engineer));
                break;
            case Guardian:
                imageProfession.setImageDrawable(getDrawable(R.drawable.icon48_guardian));
                break;
            case Mesmer:
                imageProfession.setImageDrawable(getDrawable(R.drawable.icon48_mesmer));
                break;
            case Necromancer:
                imageProfession.setImageDrawable(getDrawable(R.drawable.icon48_necromancer));
                break;
            case Ranger:
                imageProfession.setImageDrawable(getDrawable(R.drawable.icon48_ranger));
                break;
            case Revenant:
                imageProfession.setImageDrawable(getDrawable(R.drawable.icon48_revenant));
                break;
            case Thief:
                imageProfession.setImageDrawable(getDrawable(R.drawable.icon48_thief));
                break;
            case Warrior:
                imageProfession.setImageDrawable(getDrawable(R.drawable.icon48_warrior));
                break;
        }

        charactersView.addView(characterButtonView);
    }

    private void setupCharacterLoadingBar(int amountOfCharacters) {
        ProgressBar characterLoading = findViewById(R.id.progressBar_characterLoading);

        charactersCounterNotifier = new CounterNotifier(amountOfCharacters, null);
        charactersCounterNotifier.addProgressBar(characterLoading);
    }

    private void fetchCharacters() {
        Bindable onCharacterFetched = new Bindable() {
            @Override
            public void execute(Object passed) {
                try {
                    Character character = new Character((JSONObject) passed);
                    addCharacterButton(character);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                charactersCounterNotifier.countDown();
            }
        };

        Bindable onCharactersFetched = new Bindable() {
            @Override
            public void execute(Object passed) {
                try {
                    JSONArray characterNames = (JSONArray) passed;
                    setupCharacterLoadingBar(characterNames.length());

                    for (int i = 0; i < characterNames.length(); i++) {
                        String characterName = characterNames.getString(i);
                        FetchJsonData fetchCharacterProcess = new FetchJsonData(getString(R.string.request_character) + characterName + getString(R.string.access_token) + apiKey);
                        fetchCharacterProcess.bind(onCharacterFetched);
                        fetchCharacterProcess.execute();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        FetchJsonData fetchProcess = new FetchJsonData(getString(R.string.request_characters) + apiKey);
        fetchProcess.bind(onCharactersFetched);
        fetchProcess.execute();
    }
}