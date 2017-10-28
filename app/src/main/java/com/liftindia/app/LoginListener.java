package com.liftindia.app;

import android.support.annotation.Nullable;

/**
 * Created by motary on 10/7/15.
 */
public interface LoginListener {
    public void onSuccess(int loginType, @Nullable String email, String name, String id, String image, @Nullable String gender, @Nullable String userName, String fbFriends, String connections, String company, String post);
    public void onError(@Nullable String message);
}
