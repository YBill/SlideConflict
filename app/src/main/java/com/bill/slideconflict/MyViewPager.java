package com.bill.slideconflict;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by Bill on 2016/12/7.
 */

public class MyViewPager extends ViewGroup {

    /**
     * 手势识别器
     */
    private GestureDetector detector;
    private int fristDownX; // 第一次按下的X坐标
    private int currentId; // 记录当前View的id
    private Scroller scroller; // 动画工具
    /**
     * 标记是否快速的滑动
     */
    private boolean isFling;

    /**
     * 第一次手指按下的X坐标
     */
    private int firstX = 0;
    /**
     * 第一次手指按下的Y坐标
     */
    private int firstY = 0;

    public MyViewPager(Context context) {
        super(context);
        init();
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
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
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float distanceX, float vdistanceY) {
                // 手指滑动
                scrollBy((int) distanceX, 0);
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            view.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            view.layout(0 + i * getWidth(), 0, getWidth() + i * getWidth(), getHeight());
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 让手势识别器记录按下事件，防止左右滑动页面跳动的“bug”
                detector.onTouchEvent(ev);
                firstX = (int) ev.getX();
                firstY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                // 手指横向移动的位移绝对值
                int diffX = (int) Math.abs(ev.getX() - firstX);
                // 手指竖直移动的位移绝对值
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
                fristDownX = (int) event.getX();
                break;
            case MotionEvent.ACTION_UP:
                if (!isFling) { // 没有快速滑动的情况下，我们按照距离来判断
                    int nextId = 0; // 记录下一个View的id
                    if (event.getX() - fristDownX > getWidth() / 2) {
                        // 手指离开点的X轴坐标-firstDownX > 屏幕宽度的一半，左移
                        nextId = (currentId - 1) <= 0 ? 0 : currentId - 1;
                    } else if (fristDownX - event.getX() > getWidth() / 2) {
                        // 手指离开点的X轴坐标 - firstDownX < 屏幕宽度的一半，右移
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
    public void moveToDest(int nextId) {
        // nextId的合理范围是，nextId >=0 && nextId <= getChildCount()-1
        currentId = (nextId >= 0) ? nextId : 0;
        currentId = (nextId <= getChildCount() - 1)
                ? nextId
                : (getChildCount() - 1);

        if (listener != null)
            listener.moveToDest(currentId);

        // 视图移动,太直接了，没有动态过程
        // scrollTo(currentId * getWidth(), 0);
        // 要移动的距离 = 最终的位置 - 现在的位置
        int distanceX = currentId * getWidth() - getScrollX();
        // 设置运行的时间
        scroller.startScroll(getScrollX(), 0, distanceX, 0);
        // 刷新视图
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
        void moveToDest(int position);
    }

    private PageChangedListener listener;

    public void setOnPageChangedListener(PageChangedListener listener) {
        this.listener = listener;
    }

}
