package com.liftindia.app.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by appsquadz on 17/11/15.
 */
public class VehicleBrandBean implements Serializable {
    public String vehicleType = "";
    public String vehicleId = "";
    public ArrayList<BrandBean2> Brand = new ArrayList<>();
}
