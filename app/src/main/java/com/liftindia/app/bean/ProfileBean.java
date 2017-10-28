package com.liftindia.app.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by appsquadz on 17/11/15.
 */
public class ProfileBean implements Serializable{
    private static ProfileBean profileBean;

    public String liftId = "";
    public String name = "";
    public String gender = "";
    public String age = "";
    public String totalReviews = "";
    public String rating = "0";
    public String fk_userId = "";
    public String commonRoute = "";
    public String fbFriends= "";
    public String connections= "";
    public String profileImage = "";
    public String brand = "";
    public String model = "";
    public String carName = "";
    public String carNumber = "";
    public String type = "";
    public String phone = "";
    public String createDate = "";
    public String vehicleType="";
    public String smoking = "";
    public String music = "";
    public String isPhoneVerified = "";
    public String isIdVerified = "";
    public String isDlVerified = "";
    public String isRcVerified = "";
    public String carStatus="";
    public ArrayList<ExperienceBean> experienceArrayList = new ArrayList<>();

}
