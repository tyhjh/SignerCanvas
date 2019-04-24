package com.dhht.canvas;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class ObservableScrollView extends ScrollView {

    private OnScrolldListener mOnScrolldListener;

    public ObservableScrollView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public ObservableScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        // TODO Auto-generated method stub
        View view = (View) getChildAt(getChildCount() - 1);
        int diff = (view.getBottom() - (getHeight() + getScrollY()));

        if (diff == 0 && mOnScrolldListener != null) {
            mOnScrolldListener.onBottomReached();
        } else if (getScrollY() == 0 && mOnScrolldListener != null) {
            mOnScrolldListener.onTopReached();
        }

        super.onScrollChanged(l, t, oldl, oldt);
    }
    

    public OnScrolldListener getOnScrolldListener() {
        return mOnScrolldListener;
    }

    public void setOnScrolldListener(OnScrolldListener mOnScrolldListener) {
        this.mOnScrolldListener = mOnScrolldListener;
    }



    /**
     * Event listener.
     */
    public interface OnScrolldListener {

        public void onTopReached();

        public void onBottomReached();
    }

}