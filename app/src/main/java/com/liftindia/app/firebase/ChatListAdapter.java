package com.liftindia.app.firebase;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Query;
import com.koushikdutta.ion.Ion;
import com.liftindia.app.R;
import com.liftindia.app.bean.Chat;
import com.liftindia.app.bean.FireBaseBean;
import com.liftindia.app.helper.SharedPreference;
import com.liftindia.app.specialview.CircleImageView;
import com.liftindia.app.util.PicassoCache;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class ChatListAdapter extends FireBaseListAdapter<Chat> {

    private String number;
    private Activity activity;
    private SharedPreference sharedPreference;
    private FireBaseBean bean;
//    private ProgressDialog dialog;

    public ChatListAdapter(Query ref, Activity activity, int layout, String number, FireBaseBean bean) {
        super(ref, Chat.class, layout, activity);
        this.number = number;
        this.activity = activity;
        sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
        this.bean = bean;
//        this.dialog = dialog;
//        if (this.dialog.isShowing()) {
//            this.dialog.dismiss();
//        }
    }


    /**
     * Bind an instance of the <code>Chat</code> class to our view. This method is called by <code>FirebaseListAdapter</code>
     * when there is a data change, and we are given an instance of a View that corresponds to the layout that we passed
     * to the constructor, as well as a single <code>Chat</code> instance that represents the current data to bind.
     *
     * @param view A view instance corresponding to the layout we passed to the constructor.
     * @param chat An instance representing the current state of a chat message
     */
    @Override
    protected void populateView(View view, Chat chat) {
        // Map a Chat object to an entry in our listview
//        if (dialog.isShowing()) {
//            dialog.dismiss();
//        }
        String num = chat.getSender();

        CircleImageView person_image_l = (CircleImageView) view.findViewById(R.id.person_image_l);
        CircleImageView person_image_r = (CircleImageView) view.findViewById(R.id.person_image_r);
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView time = (TextView) view.findViewById(R.id.time);
        TextView chat_container = (TextView) view.findViewById(R.id.chat_container);

        String sent_time = new SimpleDateFormat("dd MM yy hh:mm aa", Locale.ENGLISH).format(chat.getTime());
        chat_container.setText(chat.getMessage());
        time.setText(sent_time);
        if (num.equals(number)) {
            name.setText(sharedPreference.getString(FireConst.NAME, ""));
            String userImage = sharedPreference.getString(FireConst.PROFILE_IMAGE, "");
            if (userImage != null && !userImage.equalsIgnoreCase("")) {
                PicassoCache.getPicassoInstance(activity).load(userImage).placeholder(R.mipmap.ph).error(R.mipmap.ph).into(person_image_r);
            }
//            Ion.with(person_image_r)
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
//                    .load(sharedPreference.getString(FireConst.PROFILE_IMAGE, ""));
            person_image_r.setVisibility(View.VISIBLE);
            person_image_l.setVisibility(View.GONE);
        } else {
            name.setText(bean.userName);
            if (bean.userImage != null && !bean.userImage.equalsIgnoreCase("")) {
                PicassoCache.getPicassoInstance(activity).load(bean.userImage).placeholder(R.mipmap.ph).error(R.mipmap.ph).into(person_image_l);
            }
//            Ion.with(person_image_l)
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
            person_image_r.setVisibility(View.GONE);
            person_image_l.setVisibility(View.VISIBLE);
        }
    }
}
