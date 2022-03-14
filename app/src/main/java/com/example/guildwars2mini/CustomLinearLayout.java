package com.example.guildwars2mini;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class CustomLinearLayout extends FrameLayout {
    protected String apiKey;
    protected LinearLayout mainLayout;
    private ProgressBar progressBar;
    CounterNotifier progressBarCounterNotifier;

    public CustomLinearLayout(String apiKey, ProgressBar progressBar, @NonNull Context context) {
        super(context);
        init(apiKey, progressBar);
    }

    public CustomLinearLayout(String apiKey, ProgressBar progressBar, @NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(apiKey, progressBar);
    }

    public CustomLinearLayout(String apiKey, ProgressBar progressBar, @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(apiKey, progressBar);
    }

    public CustomLinearLayout(String apiKey, ProgressBar progressBar, @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(apiKey, progressBar);
    }

    private final void init(String apiKey, ProgressBar progressBar) {
        this.apiKey = apiKey;
        inflate(getContext(), R.layout.inflate_me_view, this);
        mainLayout = findViewById(R.id.inflateme_mainLayout);

        if (progressBar != null) {
            progressBarCounterNotifier = new CounterNotifier(progressBar.getMax(), null);
            progressBarCounterNotifier.addProgressBar(progressBar);
            this.progressBar = progressBar;
        }
        initView();
    }

    abstract void initView();

    protected int convertDpToPixels(int dpValue) {
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (dpValue*scale + 0.5f);
        return dpAsPixels;
    }

    // Fills the progress bar by one progress point; if counterNotifier reaches max or progress bar isn't added, does nothing
    protected void fillProgressBar() {
        if (this.progressBar != null && !this.progressBarCounterNotifier.isFinished()) {
            this.progressBarCounterNotifier.countDown();
        }
    }

    protected void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    protected void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }
}
