package com.liftindia.app.bean;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by deepti on 1/4/16.
 */
public class RouteDetails {
    static RouteDetails routeDetails;

    public static RouteDetails newInstance() {
        routeDetails = new RouteDetails();
        return routeDetails;
    }

    public static RouteDetails getInstance() {
        if (routeDetails == null) {
            routeDetails = new RouteDetails();
        }
        return routeDetails;
    }
    public void setRouteDetails(RouteDetails r){
        routeDetails = r;
    }

    public boolean isOffer = false;
    public String liftId = "";
    public String offererId = "";
    public String vehicleId = "";
    public String liftDate = "";
    public String liftTime = "";
    public String price = "";
    public String numberOfSeats = "";
    public String source_from = "";
    public String source_to = "";
    public LatLng source;
    public LatLng destination;
    public List<LatLng> path;

}
