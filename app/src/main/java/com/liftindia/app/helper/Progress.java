package com.liftindia.app.helper;

import android.app.Dialog;
import android.content.Context;

import com.liftindia.app.R;

public class Progress extends Dialog {

    public Progress(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        setContentView(R.layout.progress_layout);
        super.setCancelable(false);

    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void hide() {
        super.hide();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }


}
