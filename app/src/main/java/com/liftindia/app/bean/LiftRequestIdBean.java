package com.liftindia.app.bean;

import java.util.ArrayList;

/**
 * Created by Cbc-03 on 05/04/17.
 */

public class LiftRequestIdBean {
    private static LiftRequestIdBean liftbean;
    private String LiftRequesId;
    private String RequesterId;
    private String OffererId;
    private String LiftId;

    public static LiftRequestIdBean NewLiftRequestIdInstance() {
        if (liftbean == null) liftbean = new LiftRequestIdBean();
        return liftbean;
    }


    public String getLiftRequesId() {
        return LiftRequesId;
    }

    public void setLiftRequesId(String liftRequesId) {
        LiftRequesId = liftRequesId;
    }

    public String getRequesterId() {
        return RequesterId;
    }

    public void setRequesterId(String requesterId) {
        RequesterId = requesterId;
    }

    public String getOffererId() {
        return OffererId;
    }

    public void setOffererId(String offererId) {
        OffererId = offererId;
    }

    public String getLiftId() {
        return LiftId;
    }

    public void setLiftId(String liftId) {
        LiftId = liftId;
    }
}
