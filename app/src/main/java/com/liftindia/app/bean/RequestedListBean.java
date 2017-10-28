package com.liftindia.app.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sandeep on 07-04-2016.
 */
public class RequestedListBean {
    public String liftId;
    public String date;
    public String time;
    public String source;
    public String destination;
    public String rate;
    public String distance;
    public String noOfSeats;
    public String status;
    public List<LifterBean> list = new ArrayList<>();
}
