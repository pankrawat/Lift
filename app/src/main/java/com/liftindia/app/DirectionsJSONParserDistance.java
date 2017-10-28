package com.liftindia.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import com.liftindia.app.helper.Const;

public class DirectionsJSONParserDistance {
    /**
     * Receives a JSONObject and returns a list of lists containing latitude and
     * longitude
     */
    public String parse(JSONObject jObject) {
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONObject jDistance = null;

        try {
            jRoutes = jObject.getJSONArray("routes");
            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();
                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    /** Getting distance from the json data */
                    jDistance = ((JSONObject) jLegs.get(j)).getJSONObject(Const.DISTANCE);
                    HashMap<String, String> hmDistance = new HashMap<String, String>();
                    hmDistance.put(Const.DISTANCE, jDistance.getString("text"));

                    return jDistance.getString("text");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
