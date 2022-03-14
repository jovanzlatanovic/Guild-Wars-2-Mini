package com.example.guildwars2mini;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ShowAccountWalletActivity extends AppCompatActivity {

    JSONArray JSONwallet;
    JSONArray currencies;
    Map<Integer, Integer> wallet = new HashMap<Integer, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_wallet);

        Bundle extras = getIntent().getExtras();
        try {
            JSONwallet = new JSONArray(extras.getString(getString(R.string.wallet_json_extra)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getWalletCurrencies();
    }

    private void AddCurrencyToView(String name, int value, String description, String iconURL) {
        View currencyView = getLayoutInflater().inflate(R.layout.wallet_currency_amount, null, false);
        TextView currencyName = (TextView) currencyView.findViewById(R.id.textView_currencyName);
        TextView currencyAmount = (TextView) currencyView.findViewById(R.id.textView_currencyAmount);
        ImageView imageView = (ImageView) currencyView.findViewById(R.id.image_currencyIcon);
        LinearLayout currencyDisplay = (LinearLayout) currencyView.findViewById(R.id.layout_currencyDisplay);

        currencyName.setText(name);
        currencyAmount.setText(NumberFormat.getNumberInstance(Locale.US).format(value));

        com.nostra13.universalimageloader.core.ImageLoader imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
        imageLoader.displayImage(iconURL, imageView);

        LinearLayout currencies_layout = (LinearLayout) findViewById(R.id.currencies_layout);
        currencies_layout.addView(currencyView);

        currencyDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ShowAccountWalletActivity.this, description, Toast.LENGTH_LONG).show();
            }
        });
    }

    // Adds the currencies from the jsonarray to the user view, displaying images, names of currencies, and values
    private void addWalletCurrenciesToView(JSONArray currencyArray) {
        int counter = 0;
        for (Map.Entry<Integer, Integer> currencyEntry : wallet.entrySet())
        {
            try {
                JSONObject currencyObject = currencyArray.getJSONObject(counter);
                String iconURL = currencyObject.getString("icon");
                String name = currencyObject.getString("name");
                String description = currencyObject.getString("description");

                int value = 0;

                value = currencyEntry.getValue();

                AddCurrencyToView(name, value, description, iconURL);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            counter += 1;
        }
    }

    private void getWalletCurrencies() {
        LoadingDialog loadingDialog = new LoadingDialog(this);

        Bindable onCurrencyFetched = new Bindable() {
            @Override
            public void execute(Object passed) {
                // Recieved JSONArray containing currency objects of what the user has in their wallet
                JSONArray currencyArray = (JSONArray) passed;

                // Remove the 'coin' currency which isn't required to be shown in the wallet
                currencyArray.remove(0);
                wallet.remove(1);

                addWalletCurrenciesToView(currencyArray);
                loadingDialog.dismissDialog();
            }
        };

        try {
            String currencyIdsString = "";

            for (int i = 0; i < JSONwallet.length(); i++) {
                JSONObject currency = JSONwallet.getJSONObject(i);
                int currencyID = currency.getInt("id");
                int value = currency.getInt("value");
                wallet.put(currencyID, value);

                currencyIdsString += String.valueOf(currencyID) + ",";
            }

            FetchJsonData fetchCurrencyProcess = new FetchJsonData(getString(R.string.request_currencies) + currencyIdsString);
            fetchCurrencyProcess.bind(onCurrencyFetched);

            loadingDialog.startLoadingDialog("Yoinking your wallet...");
            fetchCurrencyProcess.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}