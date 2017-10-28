package com.liftindia.app.bean;

/**
 * Created by Ashish Gaur on 11/29/2016.
 */
public class BankBean {

    private String bankKey;
    private String bankName;

    public BankBean(String bankKey, String bankName) {
        this.bankKey = bankKey;
        this.bankName = bankName;
    }

    public String getBankKey() {
        return bankKey;
    }

    public void setBankKey(String bankKey) {
        this.bankKey = bankKey;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    @Override
    public String toString() {
        return bankName;
    }

    @Override
    public boolean equals(Object o) {
        boolean temp = false;
        if (o instanceof BankBean){
            BankBean bankBean = (BankBean)o;
            if (bankBean.getBankName().equals(bankName) && bankBean.getBankKey().equals(bankKey)){
                temp = true;
            }else {
                temp = false;
            }
        }
        return temp;
    }
}
