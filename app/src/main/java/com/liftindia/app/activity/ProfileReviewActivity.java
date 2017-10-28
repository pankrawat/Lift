package com.liftindia.app.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liftindia.app.R;
import com.liftindia.app.adapter.ProfileReviewListAdapter;
import com.liftindia.app.bean.ProfileBean;

import java.text.DecimalFormat;
import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by sandeep on 09-04-2016.
 */
public class ProfileReviewActivity extends Activity {
    Activity activity;
    RelativeLayout rl_back;
    int position;
    ProfileBean profileBean;
    TextView tv_rating, tv_reviews, tv_experience, tv_total_reviews;
    //    ImageView star1,star2,star3,star4,star5;
    RatingBar ratingBar;

    ListView profile_reivew_list;
    ProfileReviewListAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_review);

        activity = this;
        rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        profile_reivew_list = (ListView) findViewById(R.id.profile_reivew_list);
        tv_rating = (TextView) findViewById(R.id.tv_rating);
        tv_reviews = (TextView) findViewById(R.id.tv_reviews);
        tv_experience = (TextView) findViewById(R.id.tv_experience);
        tv_total_reviews = (TextView) findViewById(R.id.tv_total_reviews);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
//        star1=(ImageView)findViewById(R.id.star1);
//        star2=(ImageView)findViewById(R.id.star2);
//        star3=(ImageView)findViewById(R.id.star3);
//        star4=(ImageView)findViewById(R.id.star4);
//        star5=(ImageView)findViewById(R.id.star5);
        if (getIntent().hasExtra("profileBean")) {
            profileBean = (ProfileBean) getIntent().getSerializableExtra("profileBean");
        }
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setValue();
    }

    private void setValue() {
        String starRating = profileBean.rating.isEmpty() ? "0" : profileBean.rating;
//        setStar(starRating);
        if (starRating.equals("0"))
            ratingBar.setVisibility(View.GONE);
        else {
            ratingBar.setVisibility(View.VISIBLE);
            ratingBar.setRating(Float.parseFloat(starRating));

        }
        tv_rating.setText(String.format("%.1f", Float.parseFloat(starRating)));
        if(profileBean.totalReviews.equalsIgnoreCase("1"))
        tv_reviews.setText(profileBean.totalReviews + " Review");
        else
            tv_reviews.setText(profileBean.totalReviews + " Reviews");

        try {

            DecimalFormat df = new DecimalFormat("#");
            Number formate = df.parse(profileBean.rating);
            tv_experience.setText(experience(formate.floatValue()));


        } catch (Exception e) {
            Log.e("rating error", e.toString());
            e.printStackTrace();
        }

        tv_total_reviews.setText(Html.fromHtml("<b>Reviews(" + profileBean.totalReviews + ")</b>"));
        adapter = new ProfileReviewListAdapter(this, profileBean.experienceArrayList);
        profile_reivew_list.setAdapter(adapter);
    }

    private String experience(float exepereince) {
        if (exepereince==0) {
            return "N/A";
        }
        if (exepereince<2 && exepereince>0) {
            return "Very Bad";
        }
        if (exepereince<3 && exepereince>=2) {
            return "Bad";
        }
        if (exepereince<4 && exepereince>=3) {
            return "Average";
        }
        if (exepereince<5 && exepereince>=4) {
            return "Good";
        }
        if (exepereince==5) {
            return "Excellent";
        }
        return null;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}

