package com.liftindia.app.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sandeep on 07-04-2016.
 */
public class OfferedListBean implements Serializable{
    public String liftId;
    public String date;
    public String time;
    public String source_place;
    public String dest_place;
    public String paymentStatus;
    public String isEnded;
    public String totalPrice;
    public String rate;
    public List<RequesterBean> list = new ArrayList<>();
//    public String status;
//    public String distance;
//    public String noOfSeats;
//    public String price;
//    public String liftMainStatus;

}
