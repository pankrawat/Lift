package com.liftindia.app.bean;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by appsquadz on 17/11/15.
 */
public class LiftBean implements Serializable {
    public String liftId = "";
    public String source = "";
    public String destination = "";
    public String sourceLatitude = "";
    public String sourceLongitude = "";
    public String destinationLatitude = "";
    public String destinationLongitude = "";
    public String startingMatchPoint = "";
    public String endingMatchPoint = "";
    public Set<LatLng> path;
    public Set<LatLng> requesterPath;
    public Set<LatLng> matchedPath;
    public String name = "";
    public String gender = "";
    public String age = "";
    public String reviews = "";
    public String rating = "";
    public String rideOffered = "";
    public String pendingSeats = "";
    public String userId = "";
    public String routeMatch = "";
    public String price = "";
    public String profileImage = "";
    public String vehicleType = "";
    public String ownershipType = "";
    public String brand = "";
    public String model = "";
    public String carNumber = "";
    public String phone = "";
    public String createdDate = "";
    public String smoking = "";
    public String music = "";
    public String eta = "";
    public float estm_cost = 0.0f;
    public float balance = 0.0f;
    public int payBy = 0;//0 = Cash and 1 = Mobikwik
}
