package com.liftindia.app.fragment;


import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.liftindia.app.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class HIWRequesterFragment extends Fragment implements View.OnClickListener {

    Activity activity;
    View view;
    ImageView imageView1;
    ImageView imageView2;
    ImageView imageView3;
    ImageView imageView4;

    public HIWRequesterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_requester, container, false);
        activity = getActivity();

        imageView1 = (ImageView) view.findViewById(R.id.imageView1);
        imageView2 = (ImageView) view.findViewById(R.id.imageView2);
        imageView3 = (ImageView) view.findViewById(R.id.imageView3);
        imageView4 = (ImageView) view.findViewById(R.id.imageView4);

        imageView1.setOnClickListener(this);
        imageView2.setOnClickListener(this);
        imageView3.setOnClickListener(this);
        imageView4.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView1:
                displayImageZoom(R.drawable.req_1);
                break;
            case R.id.imageView2:
                displayImageZoom(R.drawable.req_2);
                break;
            case R.id.imageView3:
                displayImageZoom(R.drawable.req_3);
                break;
            case R.id.imageView4:
                displayImageZoom(R.mipmap.search_plus);
                break;
        }
    }

    private void displayImageZoom(int id) {
        View zoomLayout = activity.getLayoutInflater().inflate(R.layout.image_zoom, null, false);
        ImageView imageZoom = (ImageView) zoomLayout.findViewById(R.id.imageZoom);

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(zoomLayout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        imageZoom.setImageResource(id);

        dialog.show();
//        Window window = dialog.getWindow();
//        window.setLayout(420, 420);

    }
}
