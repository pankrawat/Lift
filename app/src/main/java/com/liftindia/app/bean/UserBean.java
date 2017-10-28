package com.liftindia.app.bean;

import java.io.Serializable;


public class UserBean implements Serializable {
    static UserBean userBean;
    public String name = "";
    public String password = "";
    public String email = "";
    public String cCode = "";
    public String mobileNumber = "";
    public String uniqueId = "";
    public String userId;
    public String profilePicUrl = "";
    public String accountType = "0";
    public String dateOfBirth = "";
    public String gender = "";
    public String responseMsg = "";
    public String responseCode = "";
    public String verified;
    public String homeAddress = "";
    public String businessAddress = "";
    public String otp = "";
    public boolean isSuccess;

    public static UserBean getObect() {
        if (userBean == null) {
            userBean = new UserBean();
        }
        return userBean;
    }

    public static void resetData() {
        userBean = null;
    }

    public static void copyData(UserBean user) {
        userBean = getObect();
        userBean.cCode = user.cCode;
        userBean.mobileNumber = user.mobileNumber;
        userBean.profilePicUrl = user.profilePicUrl;
        userBean.email = user.email;
        userBean.name = user.name;
        userBean.userId = user.uniqueId;
        userBean.accountType = user.accountType;
    }
}
