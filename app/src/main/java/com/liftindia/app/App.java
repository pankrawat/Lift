package com.liftindia.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;

import com.firebase.client.Firebase;
import com.liftindia.app.firebase.FirebaseNotificationService;
import com.liftindia.app.helper.Const;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by appsquadz on 4/7/16.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//                .setDefaultFontPath(getResources().getString(R.string.font_regular))
                .setDefaultFontPath("fonts/Comfortaa-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
       /* String user_id = Const.getUserId(this);
        if(!user_id.isEmpty()) {
            startService(new Intent(this.getApplicationContext(), FirebaseNotificationService.class));
        }*/
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
