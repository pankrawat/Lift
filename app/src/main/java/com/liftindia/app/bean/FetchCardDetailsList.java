package com.liftindia.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Admin on 12/13/2016.
 */

public class FetchCardDetailsList implements Serializable {

    List<FetchCardDataBean> list = null;

    public List<FetchCardDataBean> getList() {
        return list;
    }

    public void setList(List<FetchCardDataBean> list) {
        this.list = list;
    }
}
