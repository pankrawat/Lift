package com.liftindia.app.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.liftindia.app.R;
import com.liftindia.app.activity.HomeActivity;
import com.liftindia.app.bean.RouteDetails;
import com.liftindia.app.helper.API;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.DbAdapter;
import com.liftindia.app.helper.Helper;
import com.liftindia.app.helper.Progress;
import com.liftindia.app.helper.SharedPreference;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by deepti on 30/3/16.
 */
public class PendingRequestListAdapter extends RecyclerView.Adapter<PendingRequestListAdapter.MyViewHolder> {

    Activity activity;
    String date_details, time_details, source_from, source_to, price_details, seat_details, source_latlong, dest_latlong;
    LayoutInflater layoutInflater;
    List<RouteDetails> list;
    JsonObject jsonObject;
    LinearLayout linearParent;
    Progress progress;

    public PendingRequestListAdapter(Activity activity, List<RouteDetails> list) {
        this.activity = activity;
        this.layoutInflater = activity.getLayoutInflater();
        this.list = list;
        linearParent = ((HomeActivity) activity).linearParent;
        progress = ((HomeActivity) activity).progress;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.offerer_pending_request_list_item, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        date_details = list.get(position).liftDate;
        time_details = list.get(position).liftTime;
        source_from = list.get(position).source_from;
        source_to = list.get(position).source_to;
        price_details = list.get(position).price;
        seat_details = list.get(position).numberOfSeats;

        holder.date_time.setText(Helper.getFormattedDate(date_details + " " + time_details));
        holder.source.setText(source_from);
        holder.destination.setText(source_to);
        holder.price.setText(price_details + "/KM");
        holder.seat.setText(seat_details + " Seats ");

        if (list.get(position).isOffer) {
            holder.btn_re_request.setVisibility(View.GONE);
            holder.offerer_title.setText("Offered Lift");
            holder.view_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RouteDetails routeDetails = RouteDetails.newInstance();
                    routeDetails.setRouteDetails(list.get(position));
                    ((HomeActivity) activity).gotoPendingRequestFragment(false, true);
                }
            });

            holder.edit_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RouteDetails routeDetails = RouteDetails.newInstance();
//                routeDetails.source_from = (list.get(position).source_from);
//                routeDetails.source_to = (list.get(position).source_to);
//                routeDetails.source = (list.get(position).source);
//                routeDetails.destination = (list.get(position).destination);
//                RouteDetails.setRoute_details(list.get(position));
                    routeDetails.setRouteDetails(list.get(position));
                    ((HomeActivity) activity).gotoPendingRequestFragment(true, true);
                }
            });
            holder.delete_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vehicleDeleteDialog(position);
                }
            });
        } else {
            holder.edit_details.setVisibility(View.GONE);
            holder.delete_details.setVisibility(View.GONE);
            holder.offerer_title.setText("Requested Lift");

            holder.view_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RouteDetails routeDetails = RouteDetails.newInstance();
                    routeDetails.setRouteDetails(list.get(position));
                    ((HomeActivity) activity).gotoPendingRequestFragment(false, false);
                }
            });

            holder.btn_re_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validate(position)) {
                        networkHit();
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        Button view_details, edit_details, delete_details, btn_re_request;
        TextView source, destination, date_time, price, seat, offerer_title;

        public MyViewHolder(View itemView) {
            super(itemView);

            edit_details = (Button) itemView.findViewById(R.id.edit_details);
            delete_details = (Button) itemView.findViewById(R.id.delete_details);
            view_details = (Button) itemView.findViewById(R.id.view_details);
            btn_re_request = (Button) itemView.findViewById(R.id.btn_re_request);
            source = (TextView) itemView.findViewById(R.id.from_location);
            destination = (TextView) itemView.findViewById(R.id.to_location);
            date_time = (TextView) itemView.findViewById(R.id.date_time);
            price = (TextView) itemView.findViewById(R.id.price_details);
            seat = (TextView) itemView.findViewById(R.id.seat_details);
            offerer_title = (TextView) itemView.findViewById(R.id.offerer_title);
        }
    }

    public void vehicleDeleteDialog(final int pos) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));

        alertDialogBuilder
                .setMessage("Are you sure to delete this Ride?")
                .setCancelable(false)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int ids) {
                        deleteRide(pos);
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void deleteRide(final int position) {
        JsonObject req = new JsonObject();
        req.addProperty(Const.LIFT_ID, list.get(position).liftId);
        if (Helper.isConnected(activity)) {
            progress.show();
            Log.e("json", req.toString());
            Ion.with(activity)
                    .load(API.DELETE_PENDING_LIFT)
                    .setTimeout(45 * 1000)
                    .setJsonObjectBody(req)
                    .asString()
                    .setCallback(new FutureCallback<String>() {

                        @Override
                        public void onCompleted(Exception e, String jsonString) {
                            progress.hide();
                            if (e == null) {
                                if (jsonString != null && !jsonString.isEmpty()) {
                                    try {
                                        Log.e("json", jsonString);

                                        JSONObject jsonObject = new JSONObject(jsonString);
                                        if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                            Helper.showSnackBar(linearParent, jsonObject.optString(Const.MESSAGE));
                                            list.remove(position);
                                            notifyItemRemoved(position);
                                            notifyItemRangeRemoved(position,list.size());
                                        } else {
                                            Helper.showSnackBar(linearParent, jsonObject.optString(Const.MESSAGE));
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                        //Helper.showSnackBar(linearParent, Const.INTERNAL_ERROR);
                                        deleteRideRetry(Const.INTERNAL_ERROR, position);
                                    }
                                } else {
                                    //Helper.showSnackBar(linearParent, Const.POOR_INTERNET);
                                    deleteRideRetry(Const.POOR_INTERNET, position);
                                }
                            } else {
                                e.printStackTrace();
                                //Helper.showSnackBar(linearParent, /*e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
                                deleteRideRetry(Const.POOR_INTERNET, position);
                            }
                        }
                    });
        } else {
            //Helper.showSnackBar(linearParent, Const.NO_INTERNET);
            deleteRideRetry(Const.NO_INTERNET, position);
        }
    }

    private void deleteRideRetry(String message, final int position) {
        final Snackbar snackbar = Snackbar.make(linearParent, message, Snackbar.LENGTH_INDEFINITE);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.parseColor(Const.SNACKBAR_TEXT_COLOR));
        textView.setMaxLines(5);
        Handler handler = new Handler();
        /*handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar.dismiss();
            }
        }, 8000);*/
        snackbar.setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRide(position);
            }
        }).setActionTextColor(Const.SNACKBAR_ACTION_COLOR);
        snackbar.show();
    }

    private boolean validate(int position) {
        jsonObject = new JsonObject();
        String requesterId = "";
        DbAdapter dbAdapter = DbAdapter.getInstance(activity);
        Cursor cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_PROFILE);
        for (int i = 0; i < cursor.getCount(); i++) {
            requesterId = cursor.getString(cursor.getColumnIndex(Const.USERID));
            cursor.moveToNext();
        }
        SharedPreference sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
        if (requesterId.equalsIgnoreCase("")) {
            requesterId = sharedPreference.getString(Const.USERID, "");
        }
        jsonObject.addProperty(Const.LIFT_ID, list.get(position).liftId);
        jsonObject.addProperty(Const.OFFERER_ID, list.get(position).offererId);
        jsonObject.addProperty(Const.REQUESTER_ID, requesterId);
        jsonObject.addProperty(Const.ACTION, "");
        jsonObject.addProperty(Const.NUMBER_OF_SEATS, list.get(position).numberOfSeats);
        jsonObject.addProperty(Const.PICKUP_POINT, list.get(position).source.latitude + "," + list.get(position).source.longitude);
        jsonObject.addProperty(Const.DROP_POINT, list.get(position).destination.latitude + "," + list.get(position).destination.longitude);
        jsonObject.addProperty(Const.SOURCE, list.get(position).source_from);
        jsonObject.addProperty(Const.DESTINATION, list.get(position).source_to);
        jsonObject.addProperty("payBy", SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_WALLET).getString("payBy", "0"));
        return true;
    }

    private void networkHit() {
        if (Helper.isConnected(activity)) {
            progress.show();
            Log.e("json", jsonObject.toString());
            Ion.with(activity)
                    .load(API.ADD_LIFT_REQUESTED)
                    .setTimeout(45 * 1000)
                    .setJsonObjectBody(jsonObject)
                    .asString()
                    .setCallback(new FutureCallback<String>() {

                        @Override
                        public void onCompleted(Exception e, String jsonString) {
                            progress.hide();
                            if (e == null) {
                                if (jsonString != null && !jsonString.isEmpty()) {
                                    try {
                                        Log.e("json", jsonString);

                                        JSONObject jsonObject = new JSONObject(jsonString);
                                        if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                            SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).putString(Const.IS_OFFERER, "");
                                            Helper.showSnackBar(linearParent, "Request sent successfully.");
                                        } else {
                                            Helper.showSnackBar(linearParent, jsonObject.optString(Const.MESSAGE));
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                        //Helper.showSnackBar(linearParent, Const.INTERNAL_ERROR);
                                        networkHitRetry(Const.INTERNAL_ERROR);
                                    }
                                } else {
                                    //Helper.showSnackBar(linearParent, Const.POOR_INTERNET);
                                    networkHitRetry(Const.POOR_INTERNET);
                                }
                            } else {
                                e.printStackTrace();
                                //Helper.showSnackBar(linearParent, /*e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
                                networkHitRetry(Const.POOR_INTERNET);
                            }
                        }
                    });
        } else {
            //Helper.showSnackBar(linearParent, Const.NO_INTERNET);
            networkHitRetry(Const.NO_INTERNET);
        }
    }

    private void networkHitRetry(String message) {
        if (!activity.isFinishing()) {
            final Snackbar snackbar = Snackbar.make(linearParent, message, Snackbar.LENGTH_INDEFINITE);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.parseColor(Const.SNACKBAR_TEXT_COLOR));
            textView.setMaxLines(5);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    snackbar.dismiss();
                }
            }, 6000);
            snackbar.setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    networkHit();
                    snackbar.dismiss();
                }
            }).setActionTextColor(Const.SNACKBAR_ACTION_TEXT_COLOR);
            TextView textView1 = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_action);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, Const.SNACKBAR_ACTION_MARGIN, 0, Const.SNACKBAR_ACTION_MARGIN);
            textView1.setLayoutParams(params);
            textView1.setBackground(ResourcesCompat.getDrawable(activity.getResources(), R.drawable.snackbar_btn, null));
            snackbar.show();
        }
    }
}

