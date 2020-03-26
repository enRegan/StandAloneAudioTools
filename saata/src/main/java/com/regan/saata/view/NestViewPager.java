package com.regan.saata.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.viewpager.widget.ViewPager;


/**
 * 自定义viewpager，用于解决首页Banner若干问题
 *
 * @author regan
 */
public class NestViewPager extends ViewPager {

    // 是否已经触发过移动事件
    private boolean hasMoveTriggered = false;

    public NestViewPager(Context context) {
        super(context);
    }

    public NestViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;
        // 下面遍历所有child的高度
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if (h > height) {// 采用最大的view的高度。
                height = h;
            }
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    PointF downPoint = new PointF();
    OnSingleTouchListener onSingleTouchListener;

    boolean isFromparent = false;
    boolean isFirst = true;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent evt) {
        switch (evt.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 记录按下时候的坐标
                isFromparent = true;
                downPoint.x = evt.getX();
                downPoint.y = evt.getY();
//			Log.d(Constant.TAG, "evt.getX() : " + evt.getX() + " evt.getY():  " + evt.getY());
                hasMoveTriggered = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isFromparent) {
                    if (isFirst) {
                        downPoint.x = evt.getX();
                        downPoint.y = evt.getY();
                        isFirst = false;
                    }
                }
//			Log.d(Constant.TAG, "downPoint.x : " + downPoint.x + " evt.getX():  " + evt.getX() + "   x : " + Math.abs(evt.getX() - downPoint.x) * 5 + "   y : " + Math.abs(evt.getY() - downPoint.y));
                break;
            case MotionEvent.ACTION_UP:
                isFromparent = false;
                isFirst = true;
                hasMoveTriggered = false;

                // 在up时判断是否按下和松手的坐标为一个点
                if (PointF.length(evt.getX() - downPoint.x, evt.getY() - downPoint.y) < (float) 5.0) {
                    onSingleTouch(this);
                    return true;
                }
                break;
        }
        return super.onTouchEvent(evt);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent evt) {
//		switch (evt.getAction()) {
//			case MotionEvent.ACTION_DOWN:
//				// 记录按下时候的坐标
//				downPoint.x = evt.getX();
//				downPoint.y = evt.getY();
//				Log.info(Constant.TAG_MORE, "onInterceptTouchEvent  evt.getX() : " + evt.getX() + " evt.getY():  " + evt.getY());
//				hasMoveTriggered = false;
//				// 先设置外层的两个view不拦截该view的事件、不可以下拉刷新
//				pullableScroll.setCanPullDown(false);
//				pullableScroll.requestDisallowInterceptTouchEvent(true);
//				pullToRefresh.requestDisallowInterceptTouchEvent(true);
//				break;
//			case MotionEvent.ACTION_MOVE:
//				Log.info(Constant.TAG_MORE, "onInterceptTouchEvent  downPoint.x : " + downPoint.x + " evt.getX():  " + evt.getX() + "   x : " + Math.abs(evt.getX() - downPoint.x) * 5 + "   y : " + Math.abs(evt.getY() - downPoint.y));
//				if (!hasMoveTriggered && pullableScroll != null) {
//					// 竖向移动大于横向移动
//					if (Math.abs(evt.getX() - downPoint.x) * 5 < Math.abs(evt.getY() - downPoint.y)) {
//						pullableScroll.setCanPullDown(true);
//						pullableScroll.requestDisallowInterceptTouchEvent(false);
//						pullToRefresh.requestDisallowInterceptTouchEvent(false);
//						hasMoveTriggered = true;
//					}else{
//						pullableScroll.setCanPullDown(false);
//						pullableScroll.requestDisallowInterceptTouchEvent(true);
//						pullToRefresh.requestDisallowInterceptTouchEvent(true);
//						hasMoveTriggered = false;
//					}
//				}
//				break;
//			case MotionEvent.ACTION_UP:
//				hasMoveTriggered = false;
//				if (pullableScroll != null) {
//					pullableScroll.setCanPullDown(true);
//					pullableScroll.requestDisallowInterceptTouchEvent(false);
//					pullToRefresh.requestDisallowInterceptTouchEvent(false);
//				}
//
//				// 在up时判断是否按下和松手的坐标为一个点
//				if (PointF.length(evt.getX() - downPoint.x, evt.getY() - downPoint.y) < (float) 5.0) {
//					onSingleTouch(this);
//					return true;
//				}
//				break;
//		}
        return super.onInterceptTouchEvent(evt);
    }

    public void onSingleTouch(View v) {
        if (onSingleTouchListener != null) {
            onSingleTouchListener.onSingleTouch(v);
        }
    }

    public interface OnSingleTouchListener {
        void onSingleTouch(View v);
    }

    /**
     * 设置单击事件监听
     *
     * @param onSingleTouchListener 监听器
     */
    public void setOnSingleTouchListener(OnSingleTouchListener onSingleTouchListener) {
        this.onSingleTouchListener = onSingleTouchListener;
    }

}
