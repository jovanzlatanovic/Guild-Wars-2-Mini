package com.example.guildwars2mini;

import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.HashMap;

// Decreases a counter with a function call "countDown()", once it reaches 0 it executes a Bindable
// Originally intended to be used by async functions

// Bindable onFinish passed object is of type HashMap<ObjectResultType, Object>
public class CounterNotifier {
    int counter;
    boolean done = false;
    Bindable onFinish;
    ProgressBar progressBar;
    static HashMap<Object, Object> resultsMap = new HashMap<Object, Object>();

    // startingNumber is what the counter will be set to
    // onFinish is the bindable to be executed once counter reaches 0, can be null
    public CounterNotifier(int startingNumber, Bindable onFinish) {
        this.counter = startingNumber;
        this.onFinish = onFinish;
    }

    // Adds a progress bar to keep track of the counters progress
    public void addProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
        this.progressBar.setMax(counter);
    }

    // Decrements the counter and returns the current value, executes onFinish if it reaches 0
    // Parameter is a result object from the caller.
    public int countDown(ObjectResultType resultType, Object passed) {
        counter -= 1;
        updateProgressBar();

        // The COUNTABLE check is for HashMap results that do not require
        // the map to have multiple result object types, but rather
        // every map value is of the same object, while keys are integers.

        if (passed != null)
            if (resultType == ObjectResultType.COUNTABLE)
                resultsMap.put(counter, passed);
            else
                resultsMap.put(resultType, passed);

        if (counter <= 0)
            finish();
        return counter;
    }

    // Decrements the counter and returns the current value
    public int countDown() {
        counter -= 1;
        updateProgressBar();

        if (counter <= 0)
            finish();

        return counter;
    }

    private void updateProgressBar() {
        if (progressBar == null)
            return;
        progressBar.incrementProgressBy(1);
    }

    private void finish() {
        if (onFinish != null)
            onFinish.execute(resultsMap);
        resultsMap.clear();
        done = true;
    }

    public boolean isFinished() {
        return done;
    }

}
