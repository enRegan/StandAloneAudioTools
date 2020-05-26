package com.regan.saata.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.regan.saata.R;


/**
 * ViewPager 指示器控件（viewpager中有几页，底部就会显示几个点；若当前页是第一页，第一个点为选中状态）
 * <p>
 * 用法：首先在xml文件里面定义指示器控件，自定义属性请看attrs.xml文件：
 *
 * <ol>
 * <li>app:indicator_icon （指示器资源，<b>建议使用selector文件
 * ，且selector中使用android:state_selected或state_focused这两种形式</b> ，这样才有选中状态。）<br>
 * 例如下面：滑动到中间那个页面，中间那个指示器就显示drawable1，其余的显示drawable2<br>
 * ＜item android:state_selected="true" android:drawable= "drawable1" /＞<br>
 * ＜item android:state_selected="false" android:drawable= "drawable2" /＞
 *
 * <li>app:indicator_margin （每个指示器之间的间隔大小）
 * <li>app:indicator_smooth （boolean类型，viewpager滑动的同时指示器是否实时平滑移动）
 * </ol>
 * <p>
 * 然后再在代码中设置ViewPager <br>
 * mIndicatorView = (IndicatorView) findViewById(R.id.id_indicator); <br>
 * mIndicatorView.setViewPager(mViewPager);
 *
 * @author regan
 */
public class ViewPagerIndicatorView extends View implements ViewPager.OnPageChangeListener {

    // 指示器图标，这里是一个 drawable，包含两种状态：选中和非选中
    private Drawable mIndicator;

    // 指示器图标的大小，根据图标的宽和高来确定，选取较大者
    private int mIndicatorSize;
    private int mIndicatorMaxSize;

    // 整个指示器控件的宽度
    private int mWidth;

    // 图标加空格再加 padding的宽度
    private int mContextWidth;

    // 指示器图标的个数，就是当前ViwPager 的 item 个数
    private int mCount;

    // 每个指示器之间的间隔大小
    private int mMargin;

    // 当前 view 的 item，主要作用，是用于判断当前指示器的选中情况
    private int mSelectItem;

    // 指示器根据ViewPager 滑动的偏移量
    private float mOffset;

    // 指示器是否实时刷新(平滑移动)
    private boolean mSmooth;

    // 是否无限循环
    private boolean isInfinitLoop;

    // 因为ViewPager的pageChangeListener被占用了，所以需要定义一个 以便其他调用
    private ViewPager.OnPageChangeListener mPageChangeListener;

    public ViewPagerIndicatorView(Context context) {
        this(context, null);
    }

    public ViewPagerIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 通过 TypedArray获取自定义属性
        TypedArray typedArray = getResources().obtainAttributes(attrs, R.styleable.ViewPagerIndicatorView);
        // 获取自定义属性的个数
        int N = typedArray.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.ViewPagerIndicatorView_indicator_icon:
                    // 通过自定义属性拿到指示器
                    mIndicator = typedArray.getDrawable(attr);
                    break;
                case R.styleable.ViewPagerIndicatorView_indicator_margin:
                    float defaultMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
                            getResources().getDisplayMetrics());
                    mMargin = (int) typedArray.getDimension(attr, defaultMargin);
                    break;
                case R.styleable.ViewPagerIndicatorView_indicator_smooth:
                    mSmooth = typedArray.getBoolean(attr, false);
                    break;
                case R.styleable.ViewPagerIndicatorView_indicator_infinite:// 是否无限循环
                    isInfinitLoop = typedArray.getBoolean(attr, false);
                    break;
            }
        }
        // 使用完成之后记得回收
        typedArray.recycle();
        initIndicator(false);
        mIndicator.setState(FOCUSED_SELECTED_STATE_SET);
        initIndicator(true);
    }

    private void initIndicator(boolean isRectangle) {
        // 获取指示器的大小值。正方形的，也许切出一个长方形来了，这里做了处理不会变形的
        mIndicatorSize = Math.max(mIndicator.getIntrinsicWidth(), mIndicator.getIntrinsicHeight());
        // 设置指示器的边框
        int bottom = 0;
        if (isRectangle) {
            mIndicatorMaxSize = Math.max(mIndicator.getIntrinsicWidth(), mIndicator.getIntrinsicHeight());
            bottom = mIndicator.getIntrinsicHeight();
        } else {
            bottom = mIndicator.getIntrinsicHeight();
        }
        mIndicator.setBounds(0, 0, mIndicator.getIntrinsicWidth(), bottom);
    }

    /**
     * 测量View 的大小
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    /**
     * 测量宽度，计算当前View 的宽度
     *
     * @param widthMeasureSpec
     * @return
     */
    private int measureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int width;
        int desired = getPaddingLeft() + getPaddingRight() + mIndicatorSize * mCount + mMargin * (mCount - 1);
        mContextWidth = desired;
        if (mode == MeasureSpec.EXACTLY) {
            width = Math.max(desired, size);
        } else {
            if (mode == MeasureSpec.AT_MOST) {
                width = Math.min(desired, size);
            } else {
                width = desired;
            }
        }
        mWidth = width;
        return width;
    }

    private int measureHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        int height;
        if (mode == MeasureSpec.EXACTLY) {
            height = size;
        } else {
            int desired = getPaddingTop() + getPaddingBottom() + mIndicatorSize;
            if (mode == MeasureSpec.AT_MOST) {
                height = Math.min(desired, size);
            } else {
                height = desired;
            }
        }

        return height;
    }

    /**
     * 绘制指示器
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        // 如果ViewPager只有1项，则不绘制指示器
        if (mCount <= 1) {
            mIndicator.draw(canvas);
            return;
        }
        /*
         * 首先得保存画布的当前状态，如果未执行这个方法，等一下的restore()将会失效，canvas不知道恢复到什么状态，所以这个
         * save、restore都是成对出现的，这样就很好理解了。
         */
        canvas.save();

        // 先绘制未选中状态的
        // 这里开始就是计算需要绘制的位置，如果不好理解，拿起纸笔在纸上绘制一下，就一目了然了
        int left = mWidth / 2 - mContextWidth / 2 + getPaddingLeft();
        canvas.translate(left, getPaddingTop());
        for (int i = 0; i < mCount; i++) {
            // 因为drawable是一个selector文件，所以我们需要设置它的状态(也就是state)来获取相应的图片。 这里是获取未选中的图片
            mIndicator.setState(EMPTY_STATE_SET);
            initIndicator(false);
            // 绘制drawable
            mIndicator.draw(canvas);
            // 每绘制一个指示器，向右移动一次
            if (i == mSelectItem - 1) {
                canvas.translate(mIndicatorMaxSize + mMargin, 0);
            } else {
                canvas.translate(mIndicatorSize + mMargin, 0);
            }
        }

        // 然后绘制已选中状态的
        // 恢复画布的所有设置。也不是所有的啦，据google说法，就是matrix/clip只能恢复到最后调用save方法的位置。
        canvas.restore();

        // 计算绘制的位置
        float leftDraw = 0f;
        if (isInfinitLoop) {
            // viewpager无限循环的缘故，需要将mSelectItem - 1
            leftDraw = (mIndicatorSize + mMargin) * (mSelectItem - 1 + mOffset);
        } else {
            leftDraw = (mIndicatorSize + mMargin) * (mSelectItem + mOffset);
        }

        // 为什么要平移两次呢？为了好理解(先平移到第一页的位置，然后由第一页的位置平移到当前选中位置)
        canvas.translate(left, getPaddingTop());
        canvas.translate(leftDraw, 0);

        // 把Drawable的状态设为已选中状态，这样获取到的Drawable就是已选中的那张图片。
        mIndicator.setState(FOCUSED_SELECTED_STATE_SET);
        initIndicator(true);
        mIndicator.draw(canvas);
    }

    /**
     * 此ViewPager 一定是先设置了Adapter， 并且Adapter 需要所有数据，后续还不能 修改数据
     *
     * @param viewPager
     */
    public void setViewPager(ViewPager viewPager) {
        if (viewPager == null) {
            return;
        }
        PagerAdapter pagerAdapter = viewPager.getAdapter();
        if (pagerAdapter == null) {
            return;
        }
        mCount = pagerAdapter.getCount();
        if (isInfinitLoop) {
            // 由于ViewPager无限循环的缘故，viewpager中的页面会多2个，所以需要-2；当viewpager里只有1个或本就没数据的时候，就不需要indicator了，所以不显示
            if (mCount >= 4) {
                mCount -= 2;
                viewPager.setOnPageChangeListener(this);
                mSelectItem = viewPager.getCurrentItem();
                mIndicator.clearColorFilter();

                // 获取控件宽度
                int desired = getPaddingLeft() + getPaddingRight() + mIndicatorSize * mCount + mMargin * (mCount - 1);
                mContextWidth = desired;
            } else {
                mCount = 0;
                // 设置indicator的属性为清除，待会调用invalidate()时会清除canvas
                mIndicator.setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            }
        } else {
            viewPager.setOnPageChangeListener(this);
            mSelectItem = viewPager.getCurrentItem();
        }

        invalidate();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    invalidate();
                    break;

                default:
                    break;
            }
        }
    };

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        this.mPageChangeListener = listener;
    }

    /**
     * @param position             当前页
     * @param positionOffset       位置偏移量，范围[0,1]
     * @param positionOffsetPixels 位置像素，范围[0,屏幕宽度)
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mSmooth) {
            mSelectItem = position;
            mOffset = positionOffset;
            invalidate();
        }

        if (mPageChangeListener != null) {
            mPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        mSelectItem = position;
        if (isInfinitLoop) {
            // 滑动到了首页，跳转到末页
            if (mSelectItem < 1) {
                mSelectItem = mCount;
            } else if (mSelectItem >= mCount + 1) {
                mSelectItem = 1;
            }
        }
        mHandler.obtainMessage(0).sendToTarget();

        if (mPageChangeListener != null) {
            mPageChangeListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
//		if(state == 0) {
//			// 滑动到了首页，跳转到末页
//			if (mSelectItem < 1) {
//				mSelectItem = mCount;
//			} else if (mSelectItem >= mCount + 1) {
//				mSelectItem = 1;
//			}
//			mHandler.obtainMessage(0).sendToTarget();
//		}

        if (mPageChangeListener != null) {
            mPageChangeListener.onPageScrollStateChanged(state);
        }
    }

}
