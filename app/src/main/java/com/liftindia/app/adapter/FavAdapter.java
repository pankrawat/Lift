package com.liftindia.app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.liftindia.app.R;
import com.liftindia.app.activity.HomeActivity;
import com.liftindia.app.bean.FavoriteBean;
import com.liftindia.app.helper.API;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.Helper;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by apps on 12/4/16.
 */
public class FavAdapter extends BaseAdapter implements View.OnClickListener {

    private Context context;
    private List<FavoriteBean> beanList;
    private LayoutInflater inflater;
    boolean isStart;
    LinearLayout linearParent;

    public FavAdapter(Context context, List<FavoriteBean> beanList, final boolean isStart) {
        this.context = context;
        this.beanList = beanList;
        this.isStart = isStart;
        inflater = LayoutInflater.from(context);
        linearParent = ((HomeActivity) context).linearParent;
    }

    @Override
    public int getCount() {
        return beanList.size();
    }

    @Override
    public Object getItem(int position) {
        return beanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spinner_text_view, null);
            TextView text = (TextView) convertView.findViewById(R.id.textView);
            ImageView cross = (ImageView) convertView.findViewById(R.id.cross);
            text.setText(beanList.get(position).placeName);

            cross.setTag(beanList.get(position).favId);
            cross.setOnClickListener(this);
        }
        return convertView;
    }

    @Override
    public void onClick(View v) {
        final String id = v.getTag().toString();
        JsonObject req = new JsonObject();
        req.addProperty("id", id);
        if (Helper.isConnected(context)) {
            ((HomeActivity) context).progress.show();
            Log.e("json", req.toString());
            Ion.with(context).load(API.API_DELETE_FAV).setJsonObjectBody(req).asString().setCallback(new FutureCallback<String>() {

                @Override
                public void onCompleted(Exception e, String jsonString) {
                    ((HomeActivity) context).progress.hide();
                    if (e == null) {
                        if (jsonString != null && !jsonString.isEmpty()) {
                            try {
                                Log.e("json", jsonString);

                                JSONObject jsonObject = new JSONObject(jsonString);
                                if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                    List<FavoriteBean> beanList = null;
                                    int pos = 0;
                                    beanList = HomeActivity.favBeanStringList;
                                    pos = Helper.getPositionByFavouriteId(beanList, id);
                                    beanList.remove(pos);
                                    notifyDataSetChanged();
                                    if (id.equalsIgnoreCase(HomeActivity.favStartId)) {
                                        ((HomeActivity) context).unFavourite(true);
                                    }
                                    if (id.equalsIgnoreCase(HomeActivity.favEndId)) {
                                        ((HomeActivity) context).unFavourite(false);
                                    }
                                    if (beanList.size() == 0) {
                                        if (((HomeActivity) context).dialog != null) {
                                            ((HomeActivity) context).dialog.dismiss();
                                        }
                                    }

                                } else {
                                    Helper.showSnackBar(linearParent, jsonObject.optString(Const.MESSAGE));
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                Helper.showSnackBar(linearParent, Const.INTERNAL_ERROR);
                            }
                        } else {
                            Helper.showSnackBar(linearParent, Const.POOR_INTERNET);
                        }
                    } else {
                        e.printStackTrace();
                        Helper.showSnackBar(linearParent, /*e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
                    }
                }
            });
        } else {
            Helper.showSnackBar(((HomeActivity) context).linearParent, Const.NO_INTERNET);
        }
    }


    public interface onFavSelected {
        void onSelect(FavoriteBean favoriteBean, FavoriteBean.FavPlaceType placeType);
    }

}
