package com.liftindia.app.bean;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by appsquadz on 17/11/15.
 */
public class PaymentDueBean {
    static PaymentDueBean paymentDueBean;

    public static PaymentDueBean newInstance() {
        paymentDueBean = new PaymentDueBean();
        return paymentDueBean;
    }

    public static PaymentDueBean getInstance() {
        if (paymentDueBean == null) {
            paymentDueBean = new PaymentDueBean();
        }
        return paymentDueBean;
    }

    public String liftId = "";
    public String offererId = "";
    public String mobile = "";
    public String email = "";
    public float amount = 0f;
    public float distance = 0f;

    public String rate = "";
    public long timeTaken = 0l;

    public String numberOfSeat = "";
    public boolean fromPush = false;
    public boolean isPaymentSuccess = false;
}
