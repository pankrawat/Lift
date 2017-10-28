package com.liftindia.app.firebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.liftindia.app.R;
import com.liftindia.app.bean.FireBaseBean;
import com.liftindia.app.specialview.CircleImageView;
import com.liftindia.app.util.PicassoCache;

import java.util.Vector;

/**
 * Created by apps on 16/2/16.
 */
public class ChatItemAdapter extends BaseAdapter {
    private Context context;
    private Vector<FireBaseBean> list;

    public ChatItemAdapter(Context context, Vector<FireBaseBean> bean) {
        this.context = context;
        list = bean;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.chat_item, null);
            viewHolder.chat_pp = (CircleImageView) convertView.findViewById(R.id.chat_pp);
            viewHolder.chat_person_name = (TextView) convertView.findViewById(R.id.chat_person_name);
            viewHolder.chat_person_text = (TextView) convertView.findViewById(R.id.chat_person_text);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        FireBaseBean bean = list.get(position);
        if (bean.userImage != null && !bean.userImage.equalsIgnoreCase("")) {
            PicassoCache.getPicassoInstance(context).load(bean.userImage).placeholder(R.mipmap.ph).error(R.mipmap.ph).into(viewHolder.chat_pp);
//            Ion.with(viewHolder.chat_pp)
//                    .placeholder(R.mipmap.ph)
//                    .error(R.mipmap.ph)
////                    .transform(new Transform() {
////                        @Override
////                        public Bitmap transform(Bitmap b) {
////                            return Utils.createCircleBitmap(b);
////                        }
////
////                        @Override
////                        public String key() {
////                            return null;
////                        }
////                    })
//                    .load(bean.userImage);
        }
        if (bean.userName != null && !bean.userName.equalsIgnoreCase("")) {
            viewHolder.chat_person_name.setText(bean.userName);
        }
        if (bean.msg != null && !bean.msg.equalsIgnoreCase("")) {
            viewHolder.chat_person_text.setVisibility(View.VISIBLE);
            viewHolder.chat_person_text.setText(bean.msg);
        } else {
            viewHolder.chat_person_text.setVisibility(View.GONE);
        }

        return convertView;
    }

    public static class ViewHolder {
        CircleImageView chat_pp;
        TextView chat_person_name, chat_person_text;
    }
}
