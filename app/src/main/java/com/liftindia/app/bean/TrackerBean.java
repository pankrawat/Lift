package com.liftindia.app.bean;

import android.location.Location;

import java.io.Serializable;

/**
 * Created by appsquadz on 17/11/15.
 */
public class TrackerBean implements Serializable {
    public String liftId = "";
    public String name = "";
    public String imageUrl = "";
    public String age = "";
    public String reviews = "";
    public String rating= "";
    public String requesterId= "";
    public String pickPoints= "";
    public String dropPoint= "";
    public String rate= "";
    public String time= "";
    public String timeTaken= "0";
    public String totalPrice= "";
    public String seats= "";
    public float distanceInMeterFloat = 0.0f;
//    public Location lastFetchedLocation;
//    public Location currentlyFetchedLocation;
    public String fbFriends = "";
    public String mobile = "";
    public String designation = "";
    public String userId = "";
    public String connections = "";
    public double latitude = 0.0;
    public double longitude = 0.0;
    public String liftRequestId="";
}
