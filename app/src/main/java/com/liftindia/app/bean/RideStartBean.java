package com.liftindia.app.bean;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by appsquadz on 17/11/15.
 */
public class RideStartBean {
    static RideStartBean rideStartBean;

    public static RideStartBean newInstance() {
        rideStartBean = new RideStartBean();
        return rideStartBean;
    }

    public static RideStartBean getInstance() {
        if (rideStartBean == null) {
            rideStartBean = new RideStartBean();
        }
        return rideStartBean;
    }

    public String liftId = "";
    public String lift_request_id = "";
    public String offererId = "";
    public String requesterId = "";
    public String name = "";
    public String age = "";
    public String rating = "";
    public String reviews = "";
    public String pickPoints = "";
    public String dropPoint = "";
    public String profileImage = "";
    public String rate = "";
    public LatLng tripStartLatLong;
    public LatLng tripStopLatLong;
    public String carNumber ="";
    public String carName ="";
    public int seats =0;
    public int liftStatus;
    public long startTime = 0l;
    public String lastLocationLatlng ="";

}
