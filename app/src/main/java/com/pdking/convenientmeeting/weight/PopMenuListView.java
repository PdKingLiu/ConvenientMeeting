package com.pdking.convenientmeeting.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

/**
 * @author liupeidong
 * Created on 2019/2/25 14:06
 */
public class PopMenuListView extends ListView {


    public PopMenuListView(Context context) {
        super(context);
    }

    public PopMenuListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PopMenuListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    heightMeasureSpec);
            int w = child.getMeasuredWidth();
            if (w > width) width = w;
        }

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width + getPaddingLeft() + getPaddingRight
                (), MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
