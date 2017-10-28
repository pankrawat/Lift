package com.liftindia.app.caldroid;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.liftindia.app.R;

import java.util.List;

/**
 * Customize the weekday gridview
 */
public class WeekdayArrayAdapter extends ArrayAdapter<String> {
    public static int textColor = Color.LTGRAY;
    private float textsize;

    public WeekdayArrayAdapter(Context context, int textViewResourceId,
                               List<String> objects) {
        super(context, textViewResourceId, objects);
        textsize = context.getResources().getDimension(R.dimen.sp14);
    }

    // To prevent cell highlighted when clicked
    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    // Set color to gray and text size to 12sp
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // To customize text size and color
        TextView textView = (TextView) super.getView(position, convertView,
                parent);
        String item = getItem(position);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textsize);
        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.darkGreen));
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

}
