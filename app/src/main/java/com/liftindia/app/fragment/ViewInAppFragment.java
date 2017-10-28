package com.liftindia.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.liftindia.app.DirectionsJSONParser;
import com.liftindia.app.R;
import com.liftindia.app.activity.HomeActivity;
import com.liftindia.app.activity.OffererProfileActivity;
import com.liftindia.app.firebase.ChatActivity;
import com.liftindia.app.firebase.FireConst;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.DbAdapter;
import com.liftindia.app.helper.Helper;
import com.liftindia.app.helper.SharedPreference;
import com.liftindia.app.util.PicassoCache;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ViewInAppFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {

    View view;
    Activity activity;
    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;

    ImageView iv_current_location;
    ImageView iv_profile;
    ImageView iv_msg;
    RatingBar ratingBar;
    TextView tv_name;
    TextView tv_age;
    TextView tv_reviews;
    TextView tv_designation;
    TextView tv_connections;
    TextView tv_fb_friends;
    TextView tv_pickup_location;
    TextView tv_drop_location;
    Button btn_full_profile;
    Button btn_reject;
    Button btn_accept;
    Button btn_call;
    private ArrayList<LatLng> pathData;
    LatLng dropPoint, pickPoint;
    private PolylineOptions lineOptions;
    private ArrayList<Polyline> pathPolygonList;

    public ViewInAppFragment() {
        // Required empty public constructor
    }

    public static ViewInAppFragment newInstance() {
        ViewInAppFragment fragment = new ViewInAppFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_lift_request, container, false);
        activity = getActivity();

        SharedPreference sp = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_OFFERER_DETAILS);
        String source = sp.getString(Const.SOURCE_NAME, "");

        double slat = sp.getDouble(Const.SOURCE_LAT, 0.0);
        double slng = sp.getDouble(Const.SOURCE_LONG, 0.0);
        if (slat != 0.0 || slng != 0.0) {
            pickPoint = new LatLng(slat, slng);
        }
        String destination = sp.getString(Const.DESTINATION_NAME, "");
        double dlat = sp.getDouble(Const.DESTINATION_LATI, 0.0);
        double dlng = sp.getDouble(Const.DESTINATION_LNG, 0.0);
        if (dlat != 0.0 || dlng != 0.0) {
            dropPoint = new LatLng(dlat, dlng);
        }


        mapFragment = SupportMapFragment.newInstance(new GoogleMapOptions().zOrderOnTop(true));
        getChildFragmentManager().beginTransaction().replace(R.id.mapPlace, mapFragment).commit();
        mapFragment.getMapAsync(this);
        iv_current_location = (ImageView) view.findViewById(R.id.iv_current_location);
        iv_profile = (ImageView) view.findViewById(R.id.iv_profile);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_age = (TextView) view.findViewById(R.id.tv_age);
        tv_reviews = (TextView) view.findViewById(R.id.tv_reviews);
        tv_designation = (TextView) view.findViewById(R.id.tv_designation);
        tv_connections = (TextView) view.findViewById(R.id.tv_connections);
        tv_fb_friends = (TextView) view.findViewById(R.id.tv_fb_friends);
        tv_pickup_location = (TextView) view.findViewById(R.id.tv_pickup_location);
        tv_drop_location = (TextView) view.findViewById(R.id.tv_drop_location);
        iv_msg = (ImageView) view.findViewById(R.id.iv_msg);
        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        btn_full_profile = (Button) view.findViewById(R.id.btn_full_profile);
        btn_reject = (Button) view.findViewById(R.id.btn_reject);
        btn_accept = (Button) view.findViewById(R.id.btn_accept);
        btn_call = (Button) view.findViewById(R.id.btn_call);

        btn_full_profile.setOnClickListener(this);
        btn_reject.setOnClickListener(this);
        btn_accept.setOnClickListener(this);
        btn_call.setOnClickListener(this);
        iv_msg.setOnClickListener(this);
        pathPolygonList = new ArrayList<>();

        if (!((HomeActivity) activity).imageUrl.equalsIgnoreCase("")) {
//            Glide.with(activity).load(((HomeActivity) activity).imageUrl).placeholder(R.mipmap.default_user).error(R.mipmap.default_user).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(iv_profile);
            PicassoCache.getPicassoInstance(activity).load(((HomeActivity) activity).imageUrl).placeholder(R.mipmap.default_user).error(R.mipmap.default_user).into(iv_profile);
        }
        tv_name.setText(((HomeActivity) activity).name);
        tv_age.setText(((HomeActivity) activity).age + " Y");
        if (((HomeActivity) activity).reviews.isEmpty() || ((HomeActivity) activity).reviews.equals("0") || ((HomeActivity) activity).reviews.equals(null))
            tv_reviews.setText(((HomeActivity) activity).reviews + "No Reviews");
        else if (((HomeActivity) activity).reviews.equals("1"))
            tv_reviews.setText(((HomeActivity) activity).reviews + " Review");
        else
            tv_reviews.setText(((HomeActivity) activity).reviews + " Reviews");

        tv_designation.setText(((HomeActivity) activity).designation);
        if (((HomeActivity) activity).designation.equals("")) {
            tv_designation.setVisibility(View.GONE);
        }
        setAddress();

        if (((HomeActivity) activity).connections.trim().equals("0") || ((HomeActivity) activity).connections.trim().equals(null) || ((HomeActivity) activity).connections.trim().isEmpty())
            tv_connections.setVisibility(View.GONE);
        else tv_connections.setText("Connections - " + ((HomeActivity) activity).connections);

        if (((HomeActivity) activity).fbFriends.trim().equals("0") || ((HomeActivity) activity).fbFriends.trim().equals(null) || ((HomeActivity) activity).fbFriends.trim().isEmpty())
            tv_fb_friends.setVisibility(View.GONE);
        else tv_fb_friends.setText("Facebook Friends - " + ((HomeActivity) activity).fbFriends);

        if (((HomeActivity) activity).rating.isEmpty() || ((HomeActivity) activity).rating.equals("0") || ((HomeActivity) activity).rating.equals(null))
            ratingBar.setVisibility(View.GONE);
        else ratingBar.setRating(Float.parseFloat(((HomeActivity) activity).rating));

        getSource(((HomeActivity) activity).pick);
        getDestination(((HomeActivity) activity).drop);
        return view;
    }

    public void setAddress() {
        tv_pickup_location.setText(((HomeActivity) activity).pickAddress);
        tv_drop_location.setText(((HomeActivity) activity).dropAddress);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_current_location:
                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(HomeActivity.latLng, 10);
                this.googleMap.animateCamera(yourLocation);
                break;
            case R.id.btn_full_profile:
                Intent intent = new Intent(activity, OffererProfileActivity.class);////checked
                intent.putExtra(Const.USERID, ((HomeActivity) activity).requesterId);
                intent.putExtra(Const.LIFT_ID, ((HomeActivity) activity).liftId);
                startActivity(intent);
                break;
            case R.id.btn_reject:
                ((HomeActivity) activity).action = "2";
                if (((HomeActivity) activity).validate()) {
                    ((HomeActivity) activity).networkHit(((HomeActivity) activity).linearParent);//
                }
                break;
            case R.id.btn_call:
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ((HomeActivity) activity).mobile)));
                break;
            case R.id.btn_accept:
                ((HomeActivity) activity).action = "";
                if (((HomeActivity) activity).validate()) {
                    ((HomeActivity) activity).newNetworkHit(((HomeActivity) activity).linearParent, true, true);
                }
                break;
            case R.id.iv_msg:
                Intent inte = new Intent(activity, ChatActivity.class);
                inte.putExtra(FireConst.CHAT_WITH_USER, ((HomeActivity) activity).requesterId);
                startActivity(inte);
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", ((HomeActivity) activity).mobile, null)));
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        int padding = 100;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (this.googleMap != null) {
            this.googleMap.clear();
            if (((HomeActivity) activity).pick != null && ((HomeActivity) activity).drop != null) {
                googleMap.addMarker(new MarkerOptions().position(((HomeActivity) activity).pick).title(((HomeActivity) activity).name + "'s Source").icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin_blue)));
                googleMap.addMarker(new MarkerOptions().position(((HomeActivity) activity).drop).title(((HomeActivity) activity).name + "'s Destination").icon(BitmapDescriptorFactory.fromResource(R.mipmap.flag_blue)));
            }


            if (dropPoint != null) {
                googleMap.addMarker(new MarkerOptions().position(dropPoint).title("Your Destination").icon(BitmapDescriptorFactory.fromResource(R.mipmap.flag_green))).showInfoWindow();
            }

            if (pickPoint != null) {
                googleMap.addMarker(new MarkerOptions().position(pickPoint).title("Your Source").icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin_green))).showInfoWindow();
            }

            builder.include(((HomeActivity) activity).pick);
            builder.include(((HomeActivity) activity).drop);
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(builder.build(), padding);
            googleMap.animateCamera(cu);

            updatePath(); //routes of offerer PS: Within drawPath() routes of requester is Drawn

        }
    }

    private void updatePath() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!DbAdapter.getInstance(activity).working) {
                    new GetPathFromDb().execute();
                } else {
                    updatePath();
                }
            }
        }, 1000);
    }

    public void getSource(LatLng latLng) {
        try {
            Log.e("address latlng", latLng.latitude + ", " + latLng.longitude);
            if (Helper.isConnected(activity)) {
                Ion.with(activity)
                        .load("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLng.latitude + "," + latLng.longitude + "&sensor=true")
                        .setTimeout(45 * 1000)
                        .asString()
                        .setCallback(new FutureCallback<String>() {
                            @Override
                            public void onCompleted(Exception e, String jsonString) {
                                if (jsonString != null && !jsonString.isEmpty()) {
                                    Log.e("json", jsonString);
                                    try {
                                        JSONObject jsonObject = new JSONObject(jsonString);
                                        JSONArray addressArray = jsonObject.optJSONArray("results");
                                        JSONObject addressObject = addressArray.optJSONObject(0);
                                        JSONArray array = addressObject.optJSONArray("address_components");
                                        String address = "";
                                        JSONObject object;
                                        for (int i = 0; i < 3; i++) {
                                            object = array.optJSONObject(i);
                                            if (i == 0) {
                                                address = object.optString("short_name");
                                            } else {
                                                if (address.equalsIgnoreCase("unnamed road")) {
                                                    address = object.optString("short_name");
                                                } else {
                                                    address = address + ", " + object.optString("short_name");
                                                }
                                            }
                                        }
                                        tv_pickup_location.setText(address);
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }
                                }

                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getDestination(LatLng latLng) {
        try {
            Log.e("address latlng", latLng.latitude + ", " + latLng.longitude);
            if (Helper.isConnected(activity)) {
                Ion.with(activity)
                        .load("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLng.latitude + "," + latLng.longitude + "&sensor=true")
                        .setTimeout(45 * 1000)
                        .asString()
                        .setCallback(new FutureCallback<String>() {
                            @Override
                            public void onCompleted(Exception e, String jsonString) {
                                if (jsonString != null && !jsonString.isEmpty()) {
                                    Log.e("json", jsonString);
                                    try {
                                        JSONObject jsonObject = new JSONObject(jsonString);
                                        JSONArray addressArray = jsonObject.optJSONArray("results");
                                        JSONObject addressObject = addressArray.optJSONObject(0);
                                        JSONArray array = addressObject.optJSONArray("address_components");
                                        String address = "";
                                        JSONObject object;
                                        for (int i = 0; i < 3; i++) {
                                            object = array.optJSONObject(i);
                                            if (i == 0) {
                                                address = object.optString("short_name");
                                            } else {
                                                if (address.equalsIgnoreCase("unnamed road")) {
                                                    address = object.optString("short_name");
                                                } else {
                                                    address = address + ", " + object.optString("short_name");
                                                }
                                            }
                                        }
                                        tv_drop_location.setText(address);
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }
                                }

                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void updateRouteOption() {
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(getDirectionsUrl(((HomeActivity) activity).pick, ((HomeActivity) activity).drop));
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Waypoints
        String waypoints = "";
        String alternative = "alternatives=false";
        String mode = "mode=driving";
        String unit = "units=imperial";
        String key = "key=AIzaSyCpdz5wefjc6iaVhR5RhBqojrCa_V4faMY";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + unit + "&" /*+ waypoints + "&"*/ + alternative + "&" + sensor + "&" + key;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        // url = "http://maps.googleapis.com/maps/api/directions/json?origin=46.839382,-100.771373&destination=46.791115,-100.763650&units=imperial&alternatives=true&sensor=false";
        return url;
    }


    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.e("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.e("downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public class GetPathFromDb extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            pathData = DbAdapter.getInstance(activity).getPath();
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.e("pathData2", pathData.size() + "");
            if (isVisible()) {
                drawPath();
            }
            super.onPostExecute(aVoid);
        }
    }

    private void drawPath() {
        if (pathData.size() > 0) {
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.addAll(pathData);
            polylineOptions.width(25);
            polylineOptions.color(getResources().getColor(R.color.selected_path_color));
            if (isVisible()) {
                Polyline polyline = googleMap.addPolyline(polylineOptions);
                updateRouteOption(); //routes of requester
            }
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            if (result != null) {
                ArrayList<LatLng> points = null;
                ArrayList<PolylineOptions> lineOptionsList = new ArrayList<>();
                // Traversing through all the routes
                for (int i = result.size() - 1; i >= 0; i--) {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();
                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);
                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                    }
                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(15);
                    lineOptions.color(getResources().getColor(R.color.matched_path_color));
                    lineOptionsList.add(lineOptions);
                }
                // Drawing polyline in the Google Map for the i-th route.
                if (googleMap != null) {
                    for (int i = 0; i < pathPolygonList.size(); i++) {
                        pathPolygonList.get(i).remove();
                    }
                    pathPolygonList.clear();
                    for (int i = 0; i < lineOptionsList.size(); i++) {
                        Polyline polyline = googleMap.addPolyline(lineOptionsList.get(i));
                        pathPolygonList.add(polyline);
                    }
                }
            }
        }
    }
}
