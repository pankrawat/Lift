package com.liftindia.app.bean;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by apps on 12/4/16.
 */
public class FavoriteBean implements Serializable {

    public String favId;
    public String placeName;
    public BigDecimal placelat;
    public BigDecimal placelon;

    public enum FavPlaceType {
        SOURCE,
        DESTINATION
    }


}
