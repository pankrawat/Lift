package com.liftindia.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.code.linkedinapi.schema.Share;
import com.liftindia.app.R;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.SharedPreference;
import com.liftindia.app.util.OnSwipeTouchListener;

public class SplashActivity extends AppCompatActivity {

    boolean splash1Done;
    boolean isSkipped;
    ViewPager mViewPager;
    MyPagerAdapter mPagerAdapter;
    private int[] layouts;
    private TextView[] dots;
    private LinearLayout dotsLayout;
    SharedPreference preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
        preference = SharedPreference.getInstance(this, SharedPreference.PREF_TYPE_GENERAL);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        setClickListener();

        layouts = new int[]{R.layout.activity_splash, R.layout.activity_splash1};

        addBottomDots(0);

        mPagerAdapter = new MyPagerAdapter();
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int current = getItem(+1);
                if (current < layouts.length) {
                    mViewPager.setCurrentItem(current);
                }
            }
        };
        handler.postDelayed(runnable, 3000);

        mViewPager.setOnTouchListener(new OnSwipeTouchListener(SplashActivity.this) {
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                if (mViewPager.getCurrentItem() == 1) {
                    switchToMainActivity();
                } else {
                    mViewPager.setCurrentItem(1);
                }
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                mViewPager.setCurrentItem(0);
            }
        });
    }

    private int getItem(int i) {
        return mViewPager.getCurrentItem() + i;
    }

    private void setClickListener() {
        Button skipButton = (Button) findViewById(R.id.skip_button);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToMainActivity();
                isSkipped = true;
            }
        });
    }

    private void switchToMainActivity() {
        preference.putBoolean(Const.TO_SHOW_SPLASH, false);
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] activeColor = getResources().getIntArray(R.array.array_dot_active);
        int[] inactiveColor = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(inactiveColor[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(activeColor[currentPage]);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {


        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    public class MyPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    @Override
    public void onBackPressed() {
        System.exit(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isSkipped = true;
    }


}