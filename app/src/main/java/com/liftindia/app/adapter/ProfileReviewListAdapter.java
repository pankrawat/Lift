package com.liftindia.app.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.liftindia.app.R;
import com.liftindia.app.bean.ExperienceBean;
import com.liftindia.app.activity.ProfileReviewActivity;
import com.liftindia.app.helper.Helper;
import com.liftindia.app.util.PicassoCache;
import com.squareup.picasso.Picasso;
//import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by sandeep on 09-04-2016.
 */
public class ProfileReviewListAdapter extends BaseAdapter {
    ArrayList<ExperienceBean> experienceArrayList;
    Activity activity;
    LayoutInflater layoutInflater;

    //    TextView tv_name,tv_datetime,tv_exeperience,tv_rating;
//    ImageView /*star1,star2,star3,star4,star5,*/profileImage;
//    RatingBar ratingBarGreen, ratingBarRed, ratingBarYellow;
    public ProfileReviewListAdapter(Activity activity, ArrayList<ExperienceBean> experienceArrayList) {
        this.experienceArrayList = experienceArrayList;
        this.activity = activity;
        this.layoutInflater = activity.getLayoutInflater();
    }

    class ViewHolder {
        TextView tv_name;
        TextView tv_datetime;
        TextView tv_exeperience;
        TextView tv_rating;

        ImageView profileImage;
        RatingBar ratingBarGreen;
        RatingBar ratingBarRed;
        RatingBar ratingBarYellow;
    }

    @Override
    public int getCount() {
        return experienceArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.review_profile_listview, parent, false);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_datetime = (TextView) convertView.findViewById(R.id.tv_datetime);
            holder.tv_exeperience = (TextView) convertView.findViewById(R.id.tv_experience);
            holder.tv_rating = (TextView) convertView.findViewById(R.id.tv_rating);
            holder.ratingBarGreen = (RatingBar) convertView.findViewById(R.id.ratingBarGreen);
            holder.ratingBarRed = (RatingBar) convertView.findViewById(R.id.ratingBarRed);
            holder.ratingBarYellow = (RatingBar) convertView.findViewById(R.id.ratingBarYellow);

            holder.profileImage = (ImageView) convertView.findViewById(R.id.profile_image);

            ExperienceBean bean = experienceArrayList.get(position);

            String imageUrl = bean.profileImage;
            if (!imageUrl.equalsIgnoreCase("")) {
                PicassoCache.getPicassoInstance(activity).load(imageUrl).placeholder(R.mipmap.default_user).error(R.mipmap.default_user).into(holder.profileImage);
            }
            holder.tv_name.setText(bean.name);
            holder.tv_datetime.setText(Helper.getFormattedDate(bean.reviewDate + " " + bean.reviewTime));
            if (bean.comments.toString().trim().isEmpty()) {
                holder.tv_exeperience.setVisibility(View.GONE);
            } else {
                holder.tv_exeperience.setVisibility(View.VISIBLE);
                holder.tv_exeperience.setText(bean.comments.toString());
            }
            if (bean.rating.equals("0")||bean.rating.equals("")||bean.rating.isEmpty()) {
                holder.tv_rating.setVisibility(View.GONE);
            } else
                holder.tv_rating.setText(bean.rating);

            try {
                setStar(holder, bean.rating);
            } catch (Exception e) {
            }

        }
        return convertView;
    }

    public void setStar(ViewHolder holder, String rating) {
        if (rating.isEmpty()) {
            rating = "0";
        }
        switch (rating) {
            case "0:":
                holder.ratingBarRed.setVisibility(View.VISIBLE);
                break;
            case "1":
            case "2":
                holder.ratingBarRed.setVisibility(View.VISIBLE);
                holder.ratingBarRed.setRating(Float.parseFloat(rating));
                break;
            case "3":
                holder.ratingBarYellow.setVisibility(View.VISIBLE);
                holder.ratingBarYellow.setRating(Float.parseFloat(rating));
                break;
            case "4":
            case "5":
                holder.ratingBarGreen.setVisibility(View.VISIBLE);
                holder.ratingBarGreen.setRating(Float.parseFloat(rating));
                break;
        }
    }
}
