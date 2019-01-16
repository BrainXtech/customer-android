package com.kustomer.kustomersdk.Views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by Junaid on 3/13/2018.
 */

public class KUSSquareFrameLayout extends FrameLayout {

    //region LifeCycle
    public KUSSquareFrameLayout(Context context) {
        super(context);
    }

    public KUSSquareFrameLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public KUSSquareFrameLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
    //endregion
}
