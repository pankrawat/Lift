package com.liftindia.app.specialview;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by apps on 12/4/16.
 */
public class DrawableClickableTextView extends TextView {


    private Drawable drawableRight;
    private Drawable drawableLeft;
    private Drawable drawableTop;
    private Drawable drawableBottom;

    private int actionX, actionY;

    private DrawableClickListener clickListener;

    public DrawableClickableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DrawableClickableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableClickableTextView(Context context) {
        super(context);
    }

    @Override
    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        if (right != null) {
            drawableRight = right;
        }

        if (left != null) {
            drawableLeft = left;
        }

        if (top != null) {
            drawableTop = top;
        }

        if (bottom != null) {
            drawableBottom = bottom;
        }

        super.setCompoundDrawables(left, top, right, bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            actionX = (int) event.getX();
            actionY = (int) event.getY();

            if (drawableBottom != null && drawableBottom.getBounds().contains(actionX, actionY)) {
                clickListener.onClick(DrawableClickListener.DrawablePosition.BOTTOM);
                return super.onTouchEvent(event);
            }

            if (drawableTop != null && drawableTop.getBounds().contains(actionX, actionY)) {
                clickListener.onClick(DrawableClickListener.DrawablePosition.TOP);
                return super.onTouchEvent(event);
            }

            if (drawableLeft != null && drawableLeft.getBounds().contains(actionX, actionY)) {
                clickListener.onClick(DrawableClickListener.DrawablePosition.LEFT);
                return super.onTouchEvent(event);
            }

            if (drawableRight != null && drawableRight.getBounds().contains(actionX, actionY)) {
                clickListener.onClick(DrawableClickListener.DrawablePosition.RIGHT);
                return super.onTouchEvent(event);
            }
            clickListener.onClick();
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void finalize() throws Throwable {
        drawableRight = null;
        drawableBottom = null;
        drawableLeft = null;
        drawableTop = null;
        super.finalize();
    }

    public void setDrawableClickListener(DrawableClickListener listener) {
        this.clickListener = listener;
    }

   public interface DrawableClickListener {

        public static enum DrawablePosition {TOP, BOTTOM, LEFT, RIGHT}

        public void onClick(DrawablePosition target);

        public void onClick();
    }
}


