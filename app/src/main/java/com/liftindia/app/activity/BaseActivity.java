package com.liftindia.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.LinearLayout;

import com.liftindia.app.R;
import com.liftindia.app.fragment.DrawerMenuFragment;
import com.linkedin.platform.LISessionManager;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

public class BaseActivity extends FragmentActivity {

    public static MenuDrawer mDrawer;
    public DrawerMenuFragment menuFrag;
    public LinearLayout linearParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDrawer = MenuDrawer.attach(BaseActivity.this, MenuDrawer.Type.OVERLAY, Position.LEFT, MenuDrawer.MENU_DRAG_WINDOW);
        mDrawer.setContentView(R.layout.activity_home);
        linearParent=(LinearLayout) mDrawer.findViewById(R.id.linearParent);
        mDrawer.setMenuView(R.layout.leftpanel_frame);
        mDrawer.setDropShadowEnabled(false);
        mDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_NONE);

        FragmentManager fManager = getSupportFragmentManager();
        FragmentTransaction fTransaction = fManager.beginTransaction();
        menuFrag = new DrawerMenuFragment();
        fTransaction.replace(R.id.menu_frame, menuFrag);
        fTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3672) {//linked In{
            LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);

        }
    }
}
