package com.riversidecorps.rebuy;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.riversidecorps.rebuy.adapter.messageAdapter;


public class SlidingMenu extends HorizontalScrollView {


    //swipe bar ratio
    private static final float radio = 0.43f;
    private final int mScreenWidth;
    private final int mMenuWidth;


    private boolean once = true;
    private boolean isOpen;

    public SlidingMenu(final Context context, AttributeSet attrs) {
        super(context, attrs);
        mScreenWidth = ScreenUtil.getScreenWidth(context);
        mMenuWidth = (int) (mScreenWidth * radio);
        setOverScrollMode(View.OVER_SCROLL_NEVER);
        setHorizontalScrollBarEnabled(false);
    }

    /**
     * close menu
     */

    public void closeMenu() {
        this.smoothScrollTo(0, 0);
        isOpen = false;
    }

    /**
     * check whether menu is open
     */
    public boolean isOpen() {
        return isOpen;
    }


    /**
     * get adapter
     */
    private messageAdapter getAdapter() {
        View view = this;
        while (true) {
            view = (View) view.getParent();
            if (view instanceof RecyclerView) {
                break;
            }
        }
        return (messageAdapter) ((RecyclerView) view).getAdapter();
    }

    /**
     * record this view when open menu ，ez to close next time
     */
    private void onOpenMenu() {
        getAdapter().holdOpenMenu(this);
        isOpen = true;
    }

    /**
     * when touch one item，close last item
     */
    private void closeOpenMenu() {
        if (!isOpen) {
            getAdapter().closeOpenMenu();
        }
    }

    /**
     * get sliding item
     */
    public SlidingMenu getScrollingMenu() {
        return getAdapter().getScrollingMenu();
    }

    /**
     * set this item to be the sliding item
     */
    public void setScrollingMenu(SlidingMenu scrollingMenu) {
        getAdapter().setScrollingMenu(scrollingMenu);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (once) {
            LinearLayout wrapper = (LinearLayout) getChildAt(0);
            wrapper.getChildAt(0).getLayoutParams().width = mScreenWidth;
            wrapper.getChildAt(1).getLayoutParams().width = mMenuWidth;
            once = false;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (getScrollingMenu() != null && getScrollingMenu() != this) {
            return false;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downTime = System.currentTimeMillis();
                closeOpenMenu();
                setScrollingMenu(this);
                break;
            case MotionEvent.ACTION_UP:
                setScrollingMenu(null);
                int scrollX = getScrollX();
                if (System.currentTimeMillis() - downTime <= 100 && scrollX == 0) {
                    if (mCustomOnClickListener != null) {
                        mCustomOnClickListener.onClick();
                    }
                    return false;
                }
                if (Math.abs(scrollX) > mMenuWidth / 2) {
                    this.smoothScrollTo(mMenuWidth, 0);
                    onOpenMenu();
                } else {
                    this.smoothScrollTo(0, 0);
                }
                return false;
        }
        return super.onTouchEvent(ev);
    }
    long downTime = 0;


    public interface CustomOnClickListener {
        void onClick();
    }

    private CustomOnClickListener mCustomOnClickListener;

    public void setCustomOnClickListener(CustomOnClickListener listener) {
        this.mCustomOnClickListener = listener;
    }


}
