package com.example.guildwars2mini;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class InventoryGridView extends GridView {
    public InventoryGridView(Context context) {
        super(context);
        init();
    }

    public InventoryGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InventoryGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public InventoryGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init() {
        int dpValue = 30;
        float spacing = dpValue / getContext().getResources().getDisplayMetrics().density;

        this.setNumColumns(7);
        this.setHorizontalSpacing((int)spacing);
        this.setVerticalSpacing((int)spacing);
        this.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
