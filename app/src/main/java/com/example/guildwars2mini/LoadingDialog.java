package com.example.guildwars2mini;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

class LoadingDialog {
    private Activity activity;
    private AlertDialog dialog;
    private int completionCounter = 0;

    private Bindable dismissBindable = new Bindable() {
        @Override
        public void execute(Object passed) {
            completionCounter -= 1;
            if (completionCounter <= 0)
                dismissDialog();
        }
    };

    LoadingDialog(Activity myActivity) {
        activity = myActivity;
    }

    public void startLoadingDialog(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        View loadingLayoutView = inflater.inflate(R.layout.loading_dialog, null);
        TextView loadingText = loadingLayoutView.findViewById(R.id.loadingText);
        loadingText.setText(text);

        builder.setView(loadingLayoutView);
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }

    public Bindable getDismissBindable() {
        completionCounter += 1;
        return dismissBindable;
    }
}
