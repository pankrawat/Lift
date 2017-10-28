package com.liftindia.app.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.liftindia.app.R;
import com.liftindia.app.activity.HomeActivity;
import com.liftindia.app.activity.OffererProfileActivity;
import com.liftindia.app.bean.LifterBean;
import com.liftindia.app.bean.RequestedListBean;
import com.liftindia.app.helper.API;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.Helper;
import com.liftindia.app.helper.Progress;
import com.liftindia.app.util.PicassoCache;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by sandeep on 04-04-2016.
 */
public class LifterDetialsAdapter extends RecyclerView.Adapter<LifterDetialsAdapter.MyViewHolder> {

    Activity activity;
    LayoutInflater inflator;
    ArrayList<RequestedListBean> dataList;
    int pos;
    Progress progress;
    LinearLayout linearParent;
    String liftId;

    public LifterDetialsAdapter(Activity activity, ArrayList<RequestedListBean> dataList, int position) {
        this.dataList = dataList;
        this.pos = position;
        this.activity = activity;
        this.inflator = activity.getLayoutInflater();
        progress = ((HomeActivity) activity).progress;
        linearParent = ((HomeActivity) activity).linearParent;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.profile_lifter_layout_1, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        LifterBean bean = dataList.get(pos).list.get(position);
        holder.tv_name.setText(bean.name);
        holder.tv_age.setText(bean.age + " Y | ");
        if (bean.reviews.isEmpty() || bean.reviews.equals("0") || bean.reviews.equals(null))
            holder.tv_review.setText("No Reviews");
        else if(bean.reviews.equals("1"))
            holder.tv_review.setText(bean.reviews + " Review");
        else
            holder.tv_review.setText(bean.reviews + " Reviews");

        holder.tv_seats.setText("No. of Seats " + dataList.get(pos).noOfSeats);

        holder.tv_profile.setText("Full Profile");
        holder.tv_profile.setTag(bean.userId);

        holder.ratingBar.setRating(Float.parseFloat(bean.ratings));
        holder.ratingBar.setTag(R.string.tag_liftId, dataList.get(position).liftId);

        String imageUrl = bean.profileImage;
        if (!imageUrl.equalsIgnoreCase("")) {
            PicassoCache.getPicassoInstance(activity).load(imageUrl).placeholder(R.mipmap.default_user).error(R.mipmap.default_user).into(holder.profile_image);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.get(pos).list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView profile_image;
        TextView tv_name, tv_age, tv_review, tv_seats, tv_profile;
        RatingBar ratingBar;

        public MyViewHolder(View itemView) {
            super(itemView);
            profile_image = (ImageView) itemView.findViewById(R.id.profile_image);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_age = (TextView) itemView.findViewById(R.id.tv_age);
            tv_review = (TextView) itemView.findViewById(R.id.tv_review);
            tv_seats = (TextView) itemView.findViewById(R.id.tv_seats);
            tv_profile = (TextView) itemView.findViewById(R.id.tv_profile);

//            ratingBar.setOnRatingBarChangeListener(this);
            tv_profile.setOnClickListener(this);

        }


//        @Override
//        public void onRatingChanged(RatingBar ratingBar, float ratin, boolean fromUser) {
//            rating = String.valueOf(Math.round(ratin));
//            toId = String.valueOf(ratingBar.getTag());
//            liftId = (String) ratingBar.getTag(R.string.tag_liftId);
//            rateUser();
//        }

//        public void rateUser() {
//            try {
//                if (Helper.isConnected(activity)) {
//                    progress.show();
//
//                    JsonObject jsonObject = new JsonObject();
//                    jsonObject.addProperty(Const.LIFT_ID, liftId);
//                    jsonObject.addProperty(Const.TO_ID, toId);
//                    jsonObject.addProperty(Const.FROM_ID, Const.getUserId(activity));
//                    jsonObject.addProperty(Const.RATING_COUNT, rating);
//                    jsonObject.addProperty(Const.REVIEW_COMMENTS, "");
//
//                    Log.e("obj", jsonObject.toString());
//
//                    Ion.with(activity).load(API.API_RATE_USER).setJsonObjectBody(jsonObject).asString().setCallback(new FutureCallback<String>() {
//
//                        @Override
//                        public void onCompleted(Exception e, String jsonString) {
//                            progress.hide();
//                        }
//                    });
//                } else {
//                    rateUserRetry(Const.NO_INTERNET);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                rateUserRetry(Const.INTERNAL_ERROR);
//            }
//        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_profile:
                    liftId = (String) ratingBar.getTag(R.string.tag_liftId);
                    Intent intent = new Intent(activity, OffererProfileActivity.class);///// checked
                    intent.putExtra(Const.USERID, String.valueOf(v.getTag()));
                    intent.putExtra(Const.LIFT_ID, liftId);
                    activity.startActivity(intent);
                    break;
            }
        }

//        private void rateUserRetry(String message) {
//            if (!activity.isFinishing()) {
//                final Snackbar snackbar = Snackbar.make(linearParent, message, Snackbar.LENGTH_INDEFINITE);
//                View sbView = snackbar.getView();
//                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
//                textView.setTextColor(Color.parseColor(Const.SNACKBAR_TEXT_COLOR));
//                textView.setMaxLines(5);
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        snackbar.dismiss();
//                    }
//                }, 6000);
//                snackbar.setAction("Check", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        rateUser();
//                        snackbar.dismiss();
//                    }
//                }).setActionTextColor(Const.SNACKBAR_ACTION_TEXT_COLOR);
//                TextView textView1 = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_action);
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                params.setMargins(0, Const.SNACKBAR_ACTION_MARGIN, 0, Const.SNACKBAR_ACTION_MARGIN);
//                textView1.setLayoutParams(params);
//                textView1.setBackground(ResourcesCompat.getDrawable(activity.getResources(), R.drawable.snackbar_btn, null));
//                snackbar.show();
//            }
//        }
    }
}
