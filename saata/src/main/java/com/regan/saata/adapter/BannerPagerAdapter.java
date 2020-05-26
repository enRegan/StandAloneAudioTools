package com.regan.saata.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.regan.saata.R;
import com.regan.saata.util.PxUtils;
import com.regan.saata.view.NiceImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页Banner的适配器，可以无限循环
 *
 * @author regan
 */
public class BannerPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {

    private Context mContext;

    private ViewPager vPager;

    private List<ImageView> ivList = new ArrayList<>();

    private int currPosition = 0;

    private int[] screenDisplay;

    private Bitmap defaultBitmap;

    public BannerPagerAdapter(Context context, ViewPager vPager, List<Integer> list) {
        this.mContext = context;
        this.vPager = vPager;

        // 设置默认图片
        defaultBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.home_banner_1);
        int width = defaultBitmap.getWidth();

        setBannerList(list);

//		this.vPager.setOnPageChangeListener(this);
    }

    @Override
    public int getCount() {
        return ivList.size() - 1;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = ivList.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (ivList.size() > position) {
            container.removeView(ivList.get(position));
        }
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public int getItemPosition(Object object) {
        // 如果item的位置没有发生变化，会返回POSITION_UNCHANGED；如果返回POSITION_NONE则表示该位置的item已经不存在了。默认实现是返回UNCHANGED，但是这里有可能会有变化，所以返回NONE
        return POSITION_NONE;
    }

    @Override
    public void onPageSelected(int position) {
        currPosition = position;
    }

    /**
     * @param state 状态1：滑动中；2：滑动完成；0：什么都不做
     */
    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == 0) {
            int size = ivList.size();
            // 滑动到了首页，跳转到末页
            if (currPosition < 1) {
                currPosition = size - 2;
            } else if (currPosition >= size - 1) {
                currPosition = 1;
            }
            vPager.setCurrentItem(currPosition, false);
        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    public void setBannerList(List<Integer> list) {
        if (list == null) {
            return;
        }
        this.ivList.clear();

        // 如果banner大于1项，则需要循环轮播【最开头加上最后一个banner，最末尾加上第一个banner作为缓冲】
        if (list.size() > 1) {
            Integer bannerVO_first = list.get(list.size() - 1);
            this.ivList.add(createImageView(bannerVO_first));

            for (Integer bannerVO : list) {
                this.ivList.add(createImageView(bannerVO));
            }

            Integer bannerVO_last = list.get(0);
            this.ivList.add(createImageView(bannerVO_last));

//			vPager.setCurrentItem(1, false);
        } else {
            for (Integer bannerVO : list) {
                this.ivList.add(createImageView(bannerVO));
            }
//			vPager.setCurrentItem(0, false);
        }
    }

    /**
     * 根据bannerVO中的信息(imageUrl)，创建ImageView
     *
     * @return Banner
     */
    private ImageView createImageView(int picSrc) {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        NiceImageView ivB = new NiceImageView(mContext);
        ivB.setLayoutParams(params);
        ivB.setAdjustViewBounds(true);
        ivB.setCornerRadius(PxUtils.dp2px(mContext, 4));
        ivB.setImageResource(picSrc);

        return ivB;
    }

}
