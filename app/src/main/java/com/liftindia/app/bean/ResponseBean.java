package com.liftindia.app.bean;

import java.io.Serializable;

public class ResponseBean implements Serializable {
    String amount = "";
    String id = "";
    String status = "";
    String errorMsg = "";

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public void setAmount(String string) {
        this.amount = string;
    }

    public void setId(String string) {
        this.id = string;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {

        return id;
    }

    public String getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }
}
