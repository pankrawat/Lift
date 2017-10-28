package com.liftindia.app.bean;

import com.liftindia.app.bean.OfferedListBean;
import com.liftindia.app.bean.RequestedListBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sandeep on 11-04-2016.
 */
public class Bean implements Serializable {
    public ArrayList<OfferedListBean> offererList = new ArrayList<>();
    public ArrayList<RequestedListBean> requesterList = new ArrayList<>();
}
