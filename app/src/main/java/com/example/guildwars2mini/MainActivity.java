package com.example.guildwars2mini;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private JSONObject account = null;
    private Map<String, String> savedAccountKeys = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setErrorText("");

        setupImageLoader();

        // Loading of api keys saved into shared preferences
        savedAccountKeys = loadApiKeys();
        setupSpinnerSavedKeys();

        requestQueue = Volley.newRequestQueue(this);
        Button enterButton = findViewById(R.id.button_enter);
        Button clearTextButton = findViewById(R.id.button_clearApiKey);
        Spinner spinner_savedKeys = findViewById(R.id.spinner_savedKeys);
        EditText editText_ApiKey = (EditText) findViewById(R.id.editText_API_KEY);
        ImageView imageView_shoutout = (ImageView) findViewById(R.id.imageView_shoutout);

        LoadingDialog loadingDialog = new LoadingDialog(MainActivity.this);

        Bindable onDataFetched = new Bindable() {
            @Override
            public void execute(Object passed) {
                loadingDialog.dismissDialog();
                if (passed == null) {
                    setErrorText("Invalid API key");
                    return;
                }
                if (checkAPIValid((JSONObject) passed));
                    account = (JSONObject) passed;
                APIValid();
            }
        };

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String apiKey = editText_ApiKey.getText().toString();

                FetchJsonData process = new FetchJsonData(getString(R.string.request_account) + apiKey);
                process.bind(onDataFetched);

                loadingDialog.startLoadingDialog("Preparing your account...");
                process.execute();
            }
        });

        clearTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_ApiKey.setText("");
            }
        });

        spinner_savedKeys.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedAccountId = (String)parent.getItemAtPosition(position);
                editText_ApiKey.setText(savedAccountKeys.get(selectedAccountId));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        imageView_shoutout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.shoutout_address)));
                startActivity(browserIntent);
            }
        });
    }

    private void setupImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageOnFail(R.drawable.icon_image_load_error)
                .showImageOnLoading(R.drawable.icon_image_loading)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
               .defaultDisplayImageOptions(defaultOptions)
               .build();

        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);
    }

    private void setupSpinnerSavedKeys() {
        Spinner spinner_savedKeys = findViewById(R.id.spinner_savedKeys);

        if (savedAccountKeys == null) {
            spinner_savedKeys.setVisibility(View.INVISIBLE);
            return;
        }

        String[] accountNameArray = new String[savedAccountKeys.size()];
        Set<String> accountIdSet = savedAccountKeys.keySet();

        SharedPreferences app_settings = getSharedPreferences(getString(R.string.shared_preferences_app_settings), MODE_PRIVATE);
        String lastAccountId = app_settings.getString(getString(R.string.setting_last_account), null);
        int lastAccountId_index = 0;

        int i = 0;
        for (String accountId : accountIdSet) {
            accountNameArray[i] = accountId;

            if (accountId.equals(lastAccountId))
                lastAccountId_index = i;

            i++;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, accountNameArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_savedKeys.setAdapter(adapter);

        spinner_savedKeys.setSelection(lastAccountId_index);
    }

    private boolean checkAPIValid(JSONObject account) {
        try {
            account.getString("name");
            return true;
        } catch (JSONException e) {
            try {
                String errorText = account.getString("text");
                setErrorText(errorText);
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
                setErrorText("Unkown error, check your connection");
            }
        }

        return false;
    }

    // Saves the key value pair into shared preferences, returns false if the account is already saved
    private void saveApiKey(String accountName, String apiKey) {
        if (!savedAccountKeys.containsKey(accountName)) {
            // If the account id isn't in saved accounts, save it
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_saved_keys), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(accountName, apiKey);
            editor.apply();
        }

        // Make the last entered key the first one to get selected on next app run
        SharedPreferences app_settings = getSharedPreferences(getString(R.string.shared_preferences_app_settings), MODE_PRIVATE);
        SharedPreferences.Editor editor = app_settings.edit();

        editor.putString(getString(R.string.setting_last_account), accountName);
        editor.apply();
    }

    // Returns a hashmap containing an account id (format: Username.1234; key), and the api key for that account (value)
    private Map<String, String> loadApiKeys() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_saved_keys), MODE_PRIVATE);
        Map<String, ?> data = sharedPreferences.getAll();

        Map<String, String> accountKeyPairs = new HashMap<String, String>();
        for (Map.Entry<String, ?> entry : data.entrySet()) {
            accountKeyPairs.put(entry.getKey(), (String)entry.getValue());
        }

        return accountKeyPairs;
    }

    private void setErrorText(String text) {
        TextView errorText = (TextView) findViewById(R.id.textView_error);
        errorText.setText(text);
    }

    private void APIValid() {
        Intent showAccountIntent = new Intent(this, ShowAccountActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText_API_KEY);
        String apiKey = editText.getText().toString();
        showAccountIntent.putExtra(getString(R.string.api_key_extra), apiKey);
        showAccountIntent.putExtra(getString(R.string.account_json_extra), account.toString());

        // Attempt to save api key to shared preferences
        try {
            saveApiKey(account.getString("name"), apiKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        startActivity(showAccountIntent);
    }

}