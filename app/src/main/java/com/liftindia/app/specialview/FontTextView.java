package com.liftindia.app.specialview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.liftindia.app.R;


public class FontTextView extends TextView {

    private Typeface typefaceMedium, typefaceRegular, typefaceBold, typefaceLite;

    public FontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadTypeFace(context);
        init(attrs);
    }

    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadTypeFace(context);
        init(attrs);
    }

    public FontTextView(Context context) {
        super(context);
        loadTypeFace(context);
        init(null);
    }

    public enum FONT_VAL {
        REGULAR(2), BOLD(3), LIGHT(0), /*MEDIUM(1),*/ NONE(4);
        private final int ID;

        FONT_VAL(final int id) {
            this.ID = id;
        }

        public int getId() {
            return ID;
        }
    }

    private void init(AttributeSet attrs) {
//        if (attrs != null) {
//            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.NewTextView);
//            String fontName = a.getString(R.styleable.NewTextView_font);
//            if (fontName != null) {
//                Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + fontName);
//                setTypeface(myTypeface);
//            } else {
//                setTypeface(typefaceRegular);
//            }
//            a.recycle();
//        } else {
//            setTypeface(typefaceRegular);
//        }

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FontStyle);
            FONT_VAL font_val = FONT_VAL.NONE;
            try {
                for (FONT_VAL mode : FONT_VAL.values()) {
                    if (a.getInt(R.styleable.FontStyle_font, 2) == mode.getId()) {
                        font_val = mode;
                        break;
                    }
                }
                if (font_val == FONT_VAL.REGULAR) {
                    setTypeface(typefaceRegular);
                } else if (font_val == FONT_VAL.BOLD) {
                    setTypeface(typefaceBold);
                } else if (font_val == FONT_VAL.LIGHT) {
                    setTypeface(typefaceLite);
//                } else if (font_val == FONT_VAL.MEDIUM){
//                    setTypeface(typefaceMedium);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            a.recycle();
        } else {
            setTypeface(typefaceRegular);
        }
    }

    public void loadTypeFace(Context mContext) {
//        if (typefaceMedium == null)
//            typefaceMedium = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Medium.ttf");
        if (typefaceBold == null)
            typefaceBold = Typeface.createFromAsset(mContext.getAssets(), "fonts/Comfortaa-Bold.ttf");
        if (typefaceRegular == null)
            typefaceRegular = Typeface.createFromAsset(mContext.getAssets(), "fonts/Comfortaa-Regular.ttf");
        if (typefaceLite == null)
            typefaceLite = Typeface.createFromAsset(mContext.getAssets(), "fonts/Comfortaa-Light.ttf");
    }
}