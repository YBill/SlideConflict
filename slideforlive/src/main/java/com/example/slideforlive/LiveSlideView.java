package com.example.slideforlive;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by Bill on 2017/5/9.
 */

public final class LiveSlideView extends ViewGroup {

    private GestureDetector detector;
    private Scroller scroller;

    /**
     * 标记是否快速的滑动
     */
    private boolean isFling;
    /**
     * 记录当前View的id
     */
    private int currentId = 1;
    /**
     * 记录上一个View的id
     */
    private int previousId = 1;
    /**
     * 第一次手指按下的X坐标
     */
    private int firstX = 0;
    /**
     * 第一次手指按下的Y坐标
     */
    private int firstY = 0;
    /**
     * 第一次按下的X坐标
     */
    private int firstDownX;

    public LiveSlideView(Context context) {
        this(context, null);
    }

    public LiveSlideView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiveSlideView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() != 2) {
            throw new IllegalStateException("LiveSlideView can host must one direct child");
        }
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            view.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            view.layout((i - 1) * getWidth(), 0, i * getWidth(), getHeight());
        }
    }

    private void init(Context context) {
        View transparentView = new View(context);
        transparentView.setBackgroundColor(Color.RED);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        this.addView(transparentView, 0, params);

        scroller = new Scroller(getContext());
        detector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {
            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float distanceX, float distanceY) {
                // 手指滑动
                if (Math.abs(distanceY) - Math.abs(distanceX) > 0) {
                    return false;
                }

                if (currentId == 1 && distanceX > 0 && (motionEvent.getX() > motionEvent1.getX())) {
                    return false;
                }


                if (currentId == 0 && distanceX < 0 && (motionEvent.getX() < motionEvent1.getX())) {
                    return false;
                }

                scrollBy((int) distanceX, 0);
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                // 快速滑动
                if (Math.abs(velocityY) - Math.abs(velocityX) > 0) {
                    return false;
                }
                isFling = true;
                if (velocityX > 0 && currentId > 0) { // 快速向右滑动
                    currentId--;
                } else if (velocityX < 0 && currentId < getChildCount() - 1) {// 快速向左滑动
                    currentId++;
                }
                moveToDest(currentId);

                return false;
            }
        });

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 让手势识别器记录按下事件，防止左右滑动页面跳动的"bug"
                detector.onTouchEvent(ev);
                firstX = (int) ev.getX();
                firstY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int diffX = (int) Math.abs(ev.getX() - firstX);
                int diffY = (int) Math.abs(ev.getY() - firstY);

                if (diffX > diffY && diffX > 10) {
                    intercepted = true;
                } else {
                    intercepted = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                firstDownX = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if (!isFling) { // 没有快速滑动的情况下
                    int nextId;
                    if (event.getX() - firstDownX > getWidth() / 2) {
                        nextId = (currentId - 1) <= 0 ? 0 : currentId - 1;
                    } else if (firstDownX - event.getX() > getWidth() / 2) {
                        nextId = currentId + 1;
                    } else {
                        nextId = currentId;
                    }
                    moveToDest(nextId);
                }

                isFling = false;

                break;
        }
        return true;
    }

    /**
     * 控制视图的移动
     *
     * @param nextId
     */
    private void moveToDest(int nextId) {
        currentId = (nextId >= 0) ? nextId : 0;
        currentId = (nextId <= getChildCount() - 1) ? nextId : (getChildCount() - 1);

        if (previousId != currentId) {
            if (pageChangedListener != null) {
                if (currentId == 1)
                    pageChangedListener.viewStatus(true);
                else
                    pageChangedListener.viewStatus(false);
            }
        }
        previousId = currentId;

        int distanceX = (currentId - 1) * getWidth() - getScrollX();
        scroller.startScroll(getScrollX(), 0, distanceX, 0);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            int newX = scroller.getCurrX();
            scrollTo(newX, 0);
            invalidate();
        }
    }

    public interface PageChangedListener {
        void viewStatus(boolean show);
    }

    private PageChangedListener pageChangedListener;


    /////////////////////////////////下面为对外API///////////////////////////////////

    /**
     * 滑动回调
     *
     * @param pageChangedListener
     */
    public void setOnPageChangedListener(PageChangedListener pageChangedListener) {
        this.pageChangedListener = pageChangedListener;
    }


    /**
     * view是否显示 true：显示
     *
     * @return
     */
    public boolean isViewShow() {
        if (currentId == 1)
            return true;
        return false;
    }

    /**
     * 设置滑动状态
     *
     * @param show true: 显示
     */
    public void setViewShow(boolean show) {
        if (show && currentId == 0) {
            moveToDest(1);
        } else if (!show && currentId == 1) {
            moveToDest(0);
        }
    }

}
