package com.liftindia.app.bean;

/**
 * Created by appsquadz on 17/11/15.
 */
public class PaymentCompleteBean {
    static PaymentCompleteBean paymentCompleteBean;

    public static PaymentCompleteBean newInstance() {
        paymentCompleteBean = new PaymentCompleteBean();
        return paymentCompleteBean;
    }

    public static PaymentCompleteBean getInstance() {
        if (paymentCompleteBean == null) {
            paymentCompleteBean = new PaymentCompleteBean();
        }
        return paymentCompleteBean;
    }

    public String liftId = "";
    public String userId = "";
    public String name = "";
    public String requestername = "";
    public String source = "";
    public String destination = "";
    public long pickTime = 0l;
    public long dropTime = 0l;
    public float distance = 0f;
    public String rate = "";
    public String timeTaken = "0";
    //    public float amount = 0f;
    public String orderId = "";
    public int numberOfSeat = 0;
    public boolean fromPush = false;
    //    public boolean isPaymentSuccess = false;
    public String total_amount = "0.0";
    public String total_paid = "0.0";
    public String total_due = "0.0";
    public String liftRequestId="";
    public String requesterId="";
}
