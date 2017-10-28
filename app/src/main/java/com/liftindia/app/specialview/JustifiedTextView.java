package com.liftindia.app.specialview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.webkit.WebView;

import com.liftindia.app.R;

/**
 * Created by sandeep on 29-03-2016.
 */
public class JustifiedTextView extends WebView {
    public JustifiedTextView(Context context) {
        super(context);
    }
    public JustifiedTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JustifiedTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        if (attrs != null) {
            final TypedValue tv = new TypedValue();
            final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.JustifiedTextView, defStyle, 0);
            if (ta != null) {
                ta.getValue(R.styleable.JustifiedTextView_text, tv);

                if (tv.resourceId > 0) {
                    final String text = context.getString(tv.resourceId).replace("\n", "<br />");
                    loadDataWithBaseURL("file:///android_asset/",
                            "<html><head>" +
                                    "<link rel=\"stylesheet\" type=\"text/css\" href=\"justified_textview.css\" />" +
                                    "</head><body>" + text + "</body></html>",

                            "text/html", "UTF8", null);
                    setTransparentBackground();
                }
            }
        }
    }

    public void setTransparentBackground() {
        try {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        } catch (final NoSuchMethodError e) {
        }

        setBackgroundColor(Color.TRANSPARENT);
        setBackgroundDrawable(null);
        setBackgroundResource(0);
    }
}
