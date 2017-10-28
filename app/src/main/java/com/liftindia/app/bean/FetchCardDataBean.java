package com.liftindia.app.bean;

import java.io.Serializable;

/**
 * Created by Admin on 12/13/2016.
 */

public class FetchCardDataBean implements Serializable {

    String cardHolderName = "", first4 = "", last4 = "", cardId = "", cardType = "", formattedCardNum = "";
    boolean clicked = false;

    public boolean isShowCvvLayout() {
        return showCvvLayout;
    }

    public void setShowCvvLayout(boolean showCvvLayout) {
        this.showCvvLayout = showCvvLayout;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    boolean showCvvLayout = false;

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getFirst4() {
        return first4;
    }

    public void setFirst4(String first4) {
        this.first4 = first4;
    }

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getFormattedCardNum() {
        return formattedCardNum;
    }

    public void setFormattedCardNum(String formattedCardNum) {
        this.formattedCardNum = formattedCardNum;
    }
}
