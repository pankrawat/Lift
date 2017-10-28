package com.liftindia.app.specialview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.liftindia.app.R;

///////////////////////////////////////////////////////////////
// <attr name="autoScaleTextViewStyle" format="reference" /> //
//         <declare-styleable name="AutoScaleTextView">      //
//         <attr name="minTextSize" format="dimension" />    //
//         </declare-styleable>                              //
///////////////////////////////////////////////////////////////

public class FontAutoScaleTextView extends TextView {
    private Paint textPaint;

    private float preferredTextSize;
    private float minTextSize;

    private Typeface typefaceMedium, typefaceRegular, typefaceBold, typefaceLite;

    public FontAutoScaleTextView(Context context) {
        this(context, null);
        loadTypeFace(context);
        init(null);
    }

    public FontAutoScaleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.autoScaleTextViewStyle);
        loadTypeFace(context);
        init(attrs);
    }

    public FontAutoScaleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        loadTypeFace(context);
        init(attrs);

        this.textPaint = new Paint();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AutoScaleTextView, defStyle, 0);
        this.minTextSize = a.getDimension(R.styleable.AutoScaleTextView_minTextSize, 10f);
        a.recycle();

        this.preferredTextSize = this.getTextSize();
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

    /**
     * Set the minimum text size for this view
     *
     * @param minTextSize The minimum text size
     */
    public void setMinTextSize(float minTextSize) {
        this.minTextSize = minTextSize;
    }

    /**
     * Resize the text so that it fits
     *
     * @param text      The text. Neither <code>null</code> nor empty.
     * @param textWidth The width of the TextView. > 0
     */
    private void refitText(String text, int textWidth) {
        if (textWidth <= 0 || text == null || text.length() == 0)
            return;

        // the width
        int targetWidth = textWidth - this.getPaddingLeft() - this.getPaddingRight();

        final float threshold = 0.5f; // How close we have to be

        this.textPaint.set(this.getPaint());

        while ((this.preferredTextSize - this.minTextSize) > threshold) {
            float size = (this.preferredTextSize + this.minTextSize) / 2;
            this.textPaint.setTextSize(size);
            if (this.textPaint.measureText(text) >= targetWidth)
                this.preferredTextSize = size; // too big
            else
                this.minTextSize = size; // too small
        }
        // Use min size so that we undershoot rather than overshoot
        this.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.minTextSize);
    }

    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
        this.refitText(text.toString(), this.getWidth());
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldwidth, int oldheight) {
        if (width != oldwidth)
            this.refitText(this.getText().toString(), width);
    }

}