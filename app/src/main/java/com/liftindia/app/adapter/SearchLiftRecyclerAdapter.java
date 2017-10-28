package com.liftindia.app.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.liftindia.app.R;
import com.liftindia.app.activity.HomeActivity;
import com.liftindia.app.activity.OffererProfileActivity;
import com.liftindia.app.bean.LiftBean;
import com.liftindia.app.bean.SearchLiftBean;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.Helper;
import com.liftindia.app.helper.SharedPreference;
import com.liftindia.app.util.PicassoCache;

import java.util.ArrayList;

/**
 * Created by sandeep on 30-03-2016.
 */
public class SearchLiftRecyclerAdapter extends RecyclerView.Adapter<SearchLiftRecyclerAdapter.VuHolder> {
    Activity activity;
    LayoutInflater inflator;
    ArrayList<LiftBean> liftBeanArrayList;
    LinearLayout screenshot;
    SearchLiftBean searchLiftBean;


    public SearchLiftRecyclerAdapter(Activity activity, ArrayList<LiftBean> liftBeanArrayList, LinearLayout screen, SearchLiftBean searchLiftBean) {
        this.liftBeanArrayList = liftBeanArrayList;
        this.activity = activity;
        screenshot = screen;
        this.searchLiftBean = searchLiftBean;
        this.inflator = activity.getLayoutInflater();

    }

    @Override
    public VuHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.request_lift_list_item, parent, false);

        return new VuHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final VuHolder holder, final int position) {
        String imageUrl = liftBeanArrayList.get(position).profileImage;
        if (!imageUrl.equalsIgnoreCase("")) {
//            Glide.with(activity).load(imageUrl).placeholder(R.mipmap.default_user).error(R.mipmap.default_user).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.iv_profile);
            PicassoCache.getPicassoInstance(activity).load(imageUrl).placeholder(R.mipmap.default_user).error(R.mipmap.default_user).into(holder.iv_profile);
        }
        holder.tv_name.setText(liftBeanArrayList.get(position).name);
        holder.tv_year.setText(liftBeanArrayList.get(position).age + " Y");
        if (liftBeanArrayList.get(position).rating.isEmpty() || liftBeanArrayList.get(position).rating.equals("0") || liftBeanArrayList.get(position).rating.equals(null) || liftBeanArrayList.get(position).rating.equals("null")) {
            liftBeanArrayList.get(position).rating = "0";
            holder.ratingBar.setVisibility(View.GONE);
        } else {
            try {
                holder.ratingBar.setVisibility(View.VISIBLE);
                holder.ratingBar.setRating(Float.parseFloat(liftBeanArrayList.get(position).rating));
            } catch (NumberFormatException e) {
            }
        }
        if (liftBeanArrayList.get(position).reviews.isEmpty() || liftBeanArrayList.get(position).reviews.equals("0") || liftBeanArrayList.get(position).reviews.equals(null))
            holder.tv_review.setText("No Reviews");
        else if (liftBeanArrayList.get(position).reviews.equals("1"))
            holder.tv_review.setText(liftBeanArrayList.get(position).reviews + " Review");
        else {
            holder.tv_review.setText(liftBeanArrayList.get(position).reviews + " Reviews");
        }
        holder.tv_ride.setText("Ride Offered - " + liftBeanArrayList.get(position).rideOffered);
        holder.tv_seat.setText(liftBeanArrayList.get(position).pendingSeats + " Seats");
        holder.tv_car.setText(liftBeanArrayList.get(position).brand + " " + liftBeanArrayList.get(position).model);

        if (liftBeanArrayList.get(position).vehicleType.equalsIgnoreCase(Const.WHEELER4)) {
            holder.tv_car.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.car_right, 0, 0, 0);
        } else if (liftBeanArrayList.get(position).vehicleType.equalsIgnoreCase(Const.WHEELER3)) {
            holder.tv_car.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.auto_right, 0, 0, 0);
        } else if (liftBeanArrayList.get(position).vehicleType.equalsIgnoreCase(Const.WHEELER2)) {
            holder.tv_car.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.bike_right, 0, 0, 0);
        } else {
            holder.tv_car.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.car_right, 0, 0, 0);
        }

        if (liftBeanArrayList.get(position).eta.isEmpty()) {
            liftBeanArrayList.get(position).eta = "1";
        }
        holder.tv_time.setText(Helper.calculateTime(Integer.parseInt(liftBeanArrayList.get(position).eta)));
        holder.tv_price.setText(liftBeanArrayList.get(position).price + "/km");
        holder.tv_route_match.setText("Route Match " + liftBeanArrayList.get(position).routeMatch + "%");
        holder.parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
                int width = displayMetrics.widthPixels;
                int height = displayMetrics.heightPixels;
                screenshot.setDrawingCacheEnabled(true);
                screenshot.buildDrawingCache();
                Log.e("Entered Animation"," bitmap started");

                Bitmap bitmap = Bitmap.createBitmap(screenshot.getDrawingCache(), 0, 0, (int) (width * .09), screenshot.getDrawingCache().getHeight());
                SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).putString("bitmap", Helper.bitmapToString(bitmap));
                screenshot.setDrawingCacheEnabled(false);
                ((HomeActivity) activity).seats = searchLiftBean.numberOfSeats;
                Log.e("Entered Animation"," sending detail");

                ((HomeActivity) activity).gotoSendRequestLiftFragment(liftBeanArrayList, position, searchLiftBean);
            }
        });
        holder.tv_full_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, OffererProfileActivity.class);////checked
                intent.putExtra(Const.USERID, liftBeanArrayList.get(position).userId);
                intent.putExtra(Const.LIFT_ID, liftBeanArrayList.get(position).liftId);
                activity.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return liftBeanArrayList.size();
    }

    public class VuHolder extends RecyclerView.ViewHolder {
        CardView parent_layout;
        ImageView iv_profile/*, star1, star2, star3*/;
        RatingBar ratingBar;
        TextView tv_seat, tv_name, tv_year, tv_review, tv_ride, tv_car, tv_passing, tv_time, tv_full_profile, tv_price, tv_route_match;

        public VuHolder(View view) {
            super(view);
            parent_layout = (CardView) view.findViewById(R.id.parent_layout);
            iv_profile = (ImageView) view.findViewById(R.id.iv_profile);

            ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
            tv_full_profile = (TextView) view.findViewById(R.id.tv_full_profile);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_year = (TextView) view.findViewById(R.id.tv_year);
            tv_review = (TextView) view.findViewById(R.id.tv_review);
            tv_ride = (TextView) view.findViewById(R.id.tv_ride);
            tv_seat = (TextView) view.findViewById(R.id.tv_seat);
            tv_car = (TextView) view.findViewById(R.id.tv_car);
            tv_passing = (TextView) view.findViewById(R.id.tv_passing);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            tv_price = (TextView) view.findViewById(R.id.tv_price);
            tv_route_match = (TextView) view.findViewById(R.id.tv_route_match);
        }
    }

}
