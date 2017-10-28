package com.liftindia.app.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.liftindia.app.DirectionsJSONParser;
import com.liftindia.app.R;
import com.liftindia.app.activity.HomeActivity;
import com.liftindia.app.adapter.FavAdapter;
import com.liftindia.app.bean.FavoriteBean;
import com.liftindia.app.bean.LiftBean;
import com.liftindia.app.bean.SearchLiftBean;
import com.liftindia.app.helper.API;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.Helper;
import com.liftindia.app.helper.SharedPreference;
import com.liftindia.app.util.PlaceAutoFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class RequestLiftFragment extends Fragment implements GoogleMap.OnPolylineClickListener, OnMapReadyCallback, View.OnClickListener, GoogleMap.OnCameraChangeListener, GoogleMap.OnMarkerClickListener, HomeActivity.GetLocationUpdate, FavAdapter.onFavSelected {
    Activity activity;
    ProgressDialog progressDialog;
    int time = 1;
    Timer timer;
    String placeName;
    private static final int PLACE_PICKER_REQUEST = 1002;
    private static final int PLACE_PICKER_REQUEST1 = 1003;
    private GoogleMap googleMap;
    RelativeLayout rl_current_location;
    RelativeLayout rl_back;
    //    TextView tv_start, tv_end;
    private Marker sourceMarker, destMarker;
    LatLng latlongStart, latlongEnd;
    String source, destination, sourceLat, sourceLong, destinationLat, destinationLong;
    //    int numOfSeats;
    PolylineOptions lineOptions;
    private ArrayList<Polyline> pathPolygonList;
    JsonObject jsonObject;
    LinearLayout linearParent;
    Button btn_search, btn_seats;
    private String numberOfSeats = "";
    private String[] seatArray = {"1", "2", "3", "4", "5", "6", "7", "8"};
    Place place;
    ArrayList<LiftBean> liftBeanArrayList;
    int ZOOM = 15;
    PlaceAutoFragment startAutocompleteFragment, endAutocompleteFragment;
    private boolean isSelectingStart = true;
    private boolean isSelectingEnd = false;
    private boolean isToSelect = false;
    private HashMap<Marker, Integer> markerMap = new HashMap<>();
    private TextView mapInstruction;
    private Geocoder geocoder;
    private TextView favStart;
    private TextView favEnd;
    private ImageView favStartStar;
    private ImageView favEndStar;
    boolean fav = false;

    ImageView iv_pin_source;
    Future<String> futureIonHit;

    public static RequestLiftFragment newInstance() {
        return new RequestLiftFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_lift, container, false);
        linearParent = (LinearLayout) view.findViewById(R.id.linear_parent);
        rl_back = (RelativeLayout) view.findViewById(R.id.rl_back);
        activity = getActivity();

        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("Please wait, It might take some time");
        progressDialog.setMessage("Searching routes...");
        progressDialog.setCancelable(false);

        SupportMapFragment mapFragment = SupportMapFragment.newInstance(new GoogleMapOptions().zOrderOnTop(true));
        getChildFragmentManager().beginTransaction().replace(R.id.mapPlace, mapFragment).commit();
        mapFragment.getMapAsync(this);
        ((HomeActivity) activity).locationListener = RequestLiftFragment.this;
        rl_current_location = (RelativeLayout) view.findViewById(R.id.rl_current_location);
        btn_search = (Button) view.findViewById(R.id.btn_search);
        btn_seats = (Button) view.findViewById(R.id.btn_seats);
        btn_seats.setText("Number of Seats");
        iv_pin_source = (ImageView) view.findViewById(R.id.iv_pin_source);
        favStart = (TextView) view.findViewById(R.id.tv_fav_start);
        favEnd = (TextView) view.findViewById(R.id.tv_fav_end);
        favStartStar = (ImageView) view.findViewById(R.id.iv_fav_start);
        favEndStar = (ImageView) view.findViewById(R.id.iv_fav_end);

        favStartStar.getDrawable().setColorFilter(getResources().getColor(R.color.cbdbdbd), PorterDuff.Mode.SRC_ATOP);
        favEndStar.getDrawable().setColorFilter(getResources().getColor(R.color.cbdbdbd), PorterDuff.Mode.SRC_ATOP);

        favStart.setOnClickListener(this);
        favEnd.setOnClickListener(this);
        favStartStar.setOnClickListener(this);
        favEndStar.setOnClickListener(this);

        mapInstruction = (TextView) view.findViewById(R.id.map_instruction);
        mapInstruction.setOnClickListener(this);

        btn_seats.setOnClickListener(this);
        btn_search.setOnClickListener(this);
//        tv_start.setOnClickListener(this);
//        tv_end.setOnClickListener(this);
        btn_seats.setOnClickListener(this);
        rl_back.setOnClickListener(this);
        rl_current_location.setOnClickListener(this);

        (view.findViewById(R.id.ll_start)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAutocompleteFragment.editSearch.performClick();
            }
        });
        (view.findViewById(R.id.ll_end)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endAutocompleteFragment.editSearch.performClick();
            }
        });

        pathPolygonList = new ArrayList<>();

        geocoder = new Geocoder(getContext(), Locale.getDefault());

        startAutocompleteFragment = new PlaceAutoFragment();
        getChildFragmentManager().beginTransaction().add(R.id.start_fragment_container, startAutocompleteFragment).commit();
        startAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                startAutocompleteFragment.isPopupShowing = false;
                favStartStar.getDrawable().setColorFilter(getResources().getColor(R.color.cbdbdbd), PorterDuff.Mode.SRC_ATOP);
                favStartStar.setTag(false);
                isToSelect = true;
                String address = "";
                address = place.getAddress().toString();
                if (address.equalsIgnoreCase("")) {
                    address = "Unknown Source";
                }
                source = address;
                latlongStart = place.getLatLng();
//                latlongStart = new LatLng(27.187440793139366,77.98021472990513);
                sourceLat = String.valueOf(latlongStart.latitude);
                sourceLong = String.valueOf(latlongStart.longitude);
                if (isSelectingEnd) {
                    isSelectingEnd = !isSelectingEnd;
                }
                isSelectingStart = true;
                if (latlongStart != null) {
                    if (sourceMarker != null) {
                        markerMap.remove(sourceMarker);
                        sourceMarker.remove();
                        sourceMarker = null;
                    }
                }
                updateMap();
                adjustMap();
                mapInstruction.setVisibility(View.VISIBLE);
                if (pathPolygonList != null) {
                    for (Polyline polyline : pathPolygonList) {
                        polyline.remove();
                    }
                    pathPolygonList.clear();
                }
            }

            @Override
            public void onError(Status status) {

            }
        });
        endAutocompleteFragment = new PlaceAutoFragment();
        getChildFragmentManager().beginTransaction().add(R.id.end_fragment_container, endAutocompleteFragment).commit();
        endAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                endAutocompleteFragment.isPopupShowing = false;
                favEndStar.getDrawable().setColorFilter(getResources().getColor(R.color.cbdbdbd), PorterDuff.Mode.SRC_ATOP);
                favEndStar.setTag(false);
                isToSelect = true;
                String address = "";
                address = place.getAddress().toString();
                if (address.equalsIgnoreCase("")) {
                    address = "Unknown location";
                }
                destination = address;
                latlongEnd = place.getLatLng();
//                latlongEnd = new LatLng(27.210448,78.00514919999999);
                destinationLat = String.valueOf(latlongEnd.latitude);
                destinationLong = String.valueOf(latlongEnd.longitude);
                if (isSelectingStart) {
                    isSelectingStart = !isSelectingStart;
                    if (sourceMarker != null) {
                        markerMap.remove(sourceMarker);
                        sourceMarker.remove();
                        sourceMarker = null;
                    }
                    sourceMarker = googleMap.addMarker(new MarkerOptions().position(latlongStart).title("Source").icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin_green)));
                    markerMap.put(sourceMarker, 0);

                }
                iv_pin_source.setVisibility(View.GONE);
                isSelectingEnd = false;
                updateMap();
                adjustMap();
                mapInstruction.setVisibility(View.GONE);
                if (pathPolygonList != null) {
                    for (Polyline polyline : pathPolygonList) {
                        polyline.remove();
                    }
                    pathPolygonList.clear();
                }
            }

            @Override
            public void onError(Status status) {

            }
        });
        return view;
    }


    @Override
    public void onSelect(FavoriteBean bean, FavoriteBean.FavPlaceType placeType) {
        if (placeType == FavoriteBean.FavPlaceType.DESTINATION) {
            isSelectingStart = false;
            isSelectingEnd = false;
//            mapInstruction.setVisibility(View.VISIBLE);
            latlongEnd = new LatLng(bean.placelat.doubleValue(), bean.placelon.doubleValue());
            iv_pin_source.setVisibility(View.GONE);
            sourceMarker = googleMap.addMarker(new MarkerOptions().position(latlongStart).title("Source").icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin_green)));
            markerMap.put(sourceMarker, 0);
            updateEnd();
            favEndStar.getDrawable().setColorFilter(getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
            favEndStar.setTag(true);
            HomeActivity.favEndId = bean.favId;
        } else if (placeType == FavoriteBean.FavPlaceType.SOURCE) {
            isSelectingStart = true;
            isSelectingEnd = false;
            mapInstruction.setVisibility(View.VISIBLE);
            latlongStart = new LatLng(bean.placelat.doubleValue(), bean.placelon.doubleValue());
//            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latlongStart, ZOOM);
//            googleMap.moveCamera(cu);
            updateStart();
            favStartStar.getDrawable().setColorFilter(getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
            favStartStar.setTag(true);
            HomeActivity.favStartId = bean.favId;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                activity.onBackPressed();
                break;
            case R.id.btn_seats:
                showSeatChooser();
                break;
            case R.id.btn_search:
                if (validatePath()) {
                    try {
                        networkHit();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.rl_current_location:
                if (HomeActivity.latLng != null) {
                    CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(HomeActivity.latLng, ZOOM);
                    this.googleMap.animateCamera(yourLocation);
                }
                break;
            case R.id.map_instruction:
                if (sourceMarker != null) {
                    markerMap.remove(sourceMarker);
                    sourceMarker.remove();
                    sourceMarker = null;
                }
                sourceMarker = googleMap.addMarker(new MarkerOptions().position(latlongStart).title("Source").icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin_green)));
                markerMap.put(sourceMarker, 0);
                iv_pin_source.setVisibility(View.GONE);
                if (isSelectingStart) {
                    isSelectingStart = false;
                    isSelectingEnd = false;
                    updateStart();
                } else if (isSelectingEnd) {
                    isSelectingStart = false;
                    isSelectingEnd = false;
                    updateEnd();
                }
                mapInstruction.setVisibility(View.GONE);
                break;
            case R.id.tv_fav_start:
                showStartFavChooser();
//                showFavChooser(true);
                break;
            case R.id.tv_fav_end:
                showEndFavChooser();
//                showFavChooser(false);
                break;
            case R.id.iv_fav_start:
                showStartFavChooser();
//                makeFav(true, true);
                break;
            case R.id.iv_fav_end:
                showEndFavChooser();
//                makeFav(false, true);
                break;
        }
    }

    private void showFavChooser(final boolean isStart) {
        if (HomeActivity.favBeanStringList != null) {
            if (HomeActivity.favBeanStringList.size() > 0) {
                Helper.showFavPlace(isStart ? FavoriteBean.FavPlaceType.SOURCE : FavoriteBean.FavPlaceType.DESTINATION, getContext(), HomeActivity.favBeanStringList, this, isStart);
            } else {
                Toast.makeText(getContext(), "No Favorite Found", Toast.LENGTH_SHORT).show();
            }
        } else {
            try {
                String userid = Const.getUserId(activity);
                if (Helper.isConnected(activity)) {
                    ((HomeActivity) getActivity()).progress.show();
                    JsonObject object = new JsonObject();
                    object.addProperty(Const.USERID, userid);
                    futureIonHit = Ion.with(activity).load(API.API_GET_FAV).setJsonObjectBody(object).asString().setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String jsonString) {
                            ((HomeActivity) getActivity()).progress.hide();
                            if (jsonString != null && !jsonString.isEmpty()) {
                                Log.e("json", jsonString);
                                try {
                                    HomeActivity.favBeanStringList = Helper.getFavBeanfromJson(jsonString);
                                    showFavChooser(isStart);
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
    }

    public void showStartFavChooser() {
        ArrayList items;
        if (favStartStar.getTag() != null) {
            fav = (boolean) favStartStar.getTag();
        }
        if (fav) {
            items = new ArrayList();
            items.add("Remove this location from Favourites");
            items.add("Choose location from Favourites");
        } else {
            items = new ArrayList();
            items.add("Mark this location as Favourites");
            items.add("Choose location from Favourites");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.select_dialog_singlechoice, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
//        builder.setTitle("Select Vehicle Type");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int pos) {
                if (pos == 0) {
                    if (fav) {
                        deleteFav(true, startAutocompleteFragment.editSearch.getText().toString().trim());
                    } else {
                        if (startAutocompleteFragment.editSearch.getText().toString().equalsIgnoreCase("")) {
                            Helper.showSnackBar(((HomeActivity) activity).linearParent, "Please select a location first.");
                        } else {
                            makeFav(true, true);
                        }
                    }
                } else {
                    showFavChooser(true);
                }
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();
    }

    public void showEndFavChooser() {
        ArrayList items;
        if (favEndStar.getTag() != null) {
            fav = (boolean) favEndStar.getTag();
        }
        if (fav) {
            items = new ArrayList();
            items.add("Remove this location from Favourites");
            items.add("Choose location from Favourites");
        } else {
            items = new ArrayList();
            items.add("Mark this location as Favourites");
            items.add("Choose location from Favourites");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.select_dialog_singlechoice, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
//        builder.setTitle("Select Vehicle Type");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int pos) {
                if (pos == 0) {
                    if (fav) {
                        deleteFav(false, endAutocompleteFragment.editSearch.getText().toString().trim());
                    } else {
                        if (endAutocompleteFragment.editSearch.getText().toString().equalsIgnoreCase("")) {
                            Helper.showSnackBar(((HomeActivity) activity).linearParent, "Please select a location first.");
                        } else {
                            makeFav(false, true);
                        }
                    }
                } else {
                    showFavChooser(false);
                }
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();
    }

    private void deleteFav(final boolean isStart, final String s) {
        final String id = Helper.getIdByFavouriteName(HomeActivity.favBeanStringList, s);

        JsonObject req = new JsonObject();
        req.addProperty("id", id);
        if (Helper.isConnected(activity)) {
            ((HomeActivity) activity).progress.show();
            Log.e("json", req.toString());
            futureIonHit = Ion.with(activity).load(API.API_DELETE_FAV).setJsonObjectBody(req).asString().setCallback(new FutureCallback<String>() {

                @Override
                public void onCompleted(Exception e, String jsonString) {
                    ((HomeActivity) activity).progress.hide();
                    if (e == null) {
                        if (jsonString != null && !jsonString.isEmpty()) {
                            try {
                                Log.e("json", jsonString);

                                JSONObject jsonObject = new JSONObject(jsonString);
                                if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                    Helper.showSnackBar(((HomeActivity) activity).linearParent, jsonObject.optString(Const.MESSAGE));
                                    List<FavoriteBean> beanList = null;
                                    int pos = 0;
                                    beanList = HomeActivity.favBeanStringList;
                                    if (!id.equalsIgnoreCase("")) {
                                        pos = Helper.getPositionByFavouriteId(beanList, id);
                                        beanList.remove(pos);
                                    }
                                    if (isStart) {
                                        favStartStar.getDrawable().setColorFilter(getResources().getColor(R.color.cbdbdbd), PorterDuff.Mode.SRC_ATOP);
                                        favStartStar.setTag(false);
                                    } else {
                                        favEndStar.getDrawable().setColorFilter(getResources().getColor(R.color.cbdbdbd), PorterDuff.Mode.SRC_ATOP);
                                        favEndStar.setTag(false);
                                    }
                                } else {
                                    Helper.showSnackBar(((HomeActivity) activity).linearParent, jsonObject.optString(Const.MESSAGE));
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                //Helper.showSnackBar(linearParent, Const.INTERNAL_ERROR);
                                deleteFavRetry(Const.INTERNAL_ERROR, isStart, s);
                            }
                        } else {
                            //Helper.showSnackBar(linearParent, Const.POOR_INTERNET);
                            deleteFavRetry(Const.POOR_INTERNET, isStart, s);
                        }
                    } else {
                        e.printStackTrace();
                        //Helper.showSnackBar(linearParent, /*e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
                        deleteFavRetry(Const.POOR_INTERNET, isStart, s);
                    }
                }
            });
        } else {
            //Helper.showSnackBar(((HomeActivity) activity).linearParent, Const.NO_INTERNET);
            deleteFavRetry(Const.NO_INTERNET, isStart, s);
        }
    }

    private void makeFav(final boolean isStart, final boolean isFav) {
        try {
            String userid = Const.getUserId(activity);
            if (Helper.isConnected(activity)) {
//                ((HomeActivity) getActivity()).progress.show();
                JsonObject object = new JsonObject();
                object.addProperty(Const.USERID, userid);
                if (isStart) {
                    placeName = startAutocompleteFragment.editSearch.getText().toString();
                    object.addProperty("lat", latlongStart.latitude);
                    object.addProperty("long", latlongStart.longitude);
                    object.addProperty(Const.NAME, placeName);
                    object.addProperty("isFav", isFav ? 1 : 0);
                    object.addProperty(Const.TYPE, "sorce");
                } else {
                    placeName = endAutocompleteFragment.editSearch.getText().toString();
                    object.addProperty("lat", latlongEnd.latitude);
                    object.addProperty("long", latlongEnd.longitude);
                    object.addProperty(Const.NAME, placeName);
                    object.addProperty("isFav", isFav ? 1 : 0);
                    object.addProperty(Const.TYPE, "dest");
                }
                futureIonHit = Ion.with(activity).load(API.API_MAKE_FAV).setJsonObjectBody(object).asString().setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String jsonString) {
//                                ((HomeActivity) getActivity()).progress.hide();
                        if (jsonString != null && !jsonString.isEmpty()) {
                            Log.e("json", jsonString);
                            try {
                                JSONObject jsonObject = new JSONObject(jsonString);
                                boolean isSuccess = jsonObject.optBoolean(Const.IS_SUCCESS);
                                if (isSuccess) {
                                    FavoriteBean bean = new FavoriteBean();
                                    bean.favId = jsonObject.getString(Const.RESULT);
                                    bean.placelat = isStart ? new BigDecimal(latlongStart.latitude) : new BigDecimal(latlongEnd.latitude);
                                    bean.placelon = isStart ? new BigDecimal(latlongStart.longitude) : new BigDecimal(latlongEnd.longitude);
                                    bean.placeName = isStart ? startAutocompleteFragment.editSearch.getText().toString() : endAutocompleteFragment.editSearch.getText().toString();
                                    if (isStart) {
                                        favStartStar.getDrawable().setColorFilter(getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
                                        favStartStar.setTag(true);
                                        HomeActivity.favStartId = bean.favId;
                                    } else {
                                        favEndStar.getDrawable().setColorFilter(getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
                                        favEndStar.setTag(true);
                                        HomeActivity.favEndId = bean.favId;
                                    }
                                    if (HomeActivity.favBeanStringList != null) {
                                        HomeActivity.favBeanStringList.add(bean);
                                    } else {
                                        HomeActivity.favBeanStringList = new ArrayList<>();
                                        HomeActivity.favBeanStringList.add(bean);
                                    }
                                } else if (jsonObject.optString(Const.MESSAGE).equalsIgnoreCase("This place is already added as favourite place")) {
                                    if (isStart) {
                                        favStartStar.getDrawable().setColorFilter(getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
                                        favStartStar.setTag(true);
                                        HomeActivity.favStartId = Helper.getIdByFavouriteName(HomeActivity.favBeanStringList, placeName);
                                    } else {
                                        favEndStar.getDrawable().setColorFilter(getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
                                        favEndStar.setTag(true);
                                        HomeActivity.favEndId = Helper.getIdByFavouriteName(HomeActivity.favBeanStringList, placeName);
                                    }
                                }
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

    public void unFavourite(boolean isStart) {
        if (isStart) {
            favStartStar.getDrawable().setColorFilter(getResources().getColor(R.color.cbdbdbd), PorterDuff.Mode.SRC_ATOP);
            favStartStar.setTag(false);
            HomeActivity.favStartId = "";
        } else {
            favEndStar.getDrawable().setColorFilter(getResources().getColor(R.color.cbdbdbd), PorterDuff.Mode.SRC_ATOP);
            favEndStar.setTag(false);
            HomeActivity.favEndId = "";
        }
    }

    private boolean validatePath() {
//        source = tv_start.getText().toString().trim();
//        destination = tv_end.getText().toString().trim();
        source = startAutocompleteFragment.editSearch.getText().toString();
        destination = endAutocompleteFragment.editSearch.getText().toString();
        if (source.equalsIgnoreCase("") && destination.equalsIgnoreCase("")) {
            Helper.showSnackBar(linearParent, "Please Select Start/End Location");
            return false;
        }
        if (source.equalsIgnoreCase("") || source.equalsIgnoreCase("Unknown Source") || source.equalsIgnoreCase("Unknown Address")) {
            Helper.showSnackBar(linearParent, "Please Select Start Location");
            return false;
        }
        if (destination.equalsIgnoreCase("") || destination.equalsIgnoreCase("Unknown Source") || destination.equalsIgnoreCase("Unknown Address")) {
            Helper.showSnackBar(linearParent, "Please Select End Location");
            return false;
        }
        if (numberOfSeats.equalsIgnoreCase("")) {
            Helper.showSnackBar(linearParent, "Please Select Number Of Seats.");
            return false;
        }
//        if(Helper.calculateDistance(latlongEnd, HomeActivity.latLng) > 1000 * 1000){
//            Helper.showSnackBar(((HomeActivity) activity).linearParent, "You can't request ride for more than 1000 km.");
//            return false;
//        }
        return true;
    }

    public void updateMap() {
        if (latlongStart != null) {
            if (isSelectingStart) {
                iv_pin_source.setVisibility(View.VISIBLE);
            }
        }
        if (latlongEnd != null) {
            if (destMarker != null) {
                markerMap.remove(destMarker);
                destMarker.remove();
                destMarker = null;
            }
            destMarker = googleMap.addMarker(new MarkerOptions().position(latlongEnd).title("Destination").icon(BitmapDescriptorFactory.fromResource(R.mipmap.flag_green)));
            markerMap.put(destMarker, 1);
        }
        if (latlongStart != null && latlongEnd != null && !isSelectingStart && !isSelectingEnd) {
            updateRouteOption();
        }
    }


    private String getLocation(LatLng latLng) {
        String errorMessage = "";
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException ioException) {
            errorMessage = "";
        } catch (IllegalArgumentException illegalArgumentException) {
            errorMessage = "";
        }
        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "Unknown Address";
            }
            return errorMessage;
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();
            String addr;
            if (address != null) {
                addr = (address.getSubLocality() != null && address.getSubLocality().length() > 0 ? address.getSubLocality() : "") + (address.getLocality() != null && address.getLocality().length() > 0 ? " " + address.getLocality() : "")/* +
                        (address.getAdminArea() != null && address.getAdminArea().length() > 0 ? " " + address.getAdminArea() : "") +
                        (address.getCountryCode() != null && address.getCountryCode().length() > 0 ? " " + address.getCountryCode() : "")*/;
            } else {
                addr = "Unknown Address";
            }
            if (addr.isEmpty()) {
                addr = (address.getAdminArea() != null && address.getAdminArea().length() > 0 ? " " + address.getAdminArea() : "");
                addr = addr.isEmpty() ? "Unknown Address" : addr;
            }
            return addr;
        }
    }

    private void updateStart() {
        if (latlongStart != null) {
//            if (sourceMarker != null) {
//                markerMap.remove(sourceMarker);
//                sourceMarker.remove();
//                sourceMarker = null;
//            }
//            sourceMarker = googleMap.addMarker(new MarkerOptions().position(latlongStart).title("").icon(BitmapDescriptorFactory.fromResource(R.mipmap.green)));
//            markerMap.put(sourceMarker, 0);
            if (isSelectingStart) {
                iv_pin_source.setVisibility(View.VISIBLE);
            }
        }
        if (latlongStart != null && latlongEnd != null && !isSelectingStart && !isSelectingEnd) {
            updateRouteOption();
            adjustMap();
        }
//        if (!isSelectingStart) {
//            getAddress(latlongStart, 0);
//        }
        if (startAutocompleteFragment.editSearch != null) {
            startAutocompleteFragment.editSearch.setText(getLocation(latlongStart));
        }
    }

    private void updateEnd() {
        if (latlongEnd != null) {
            if (destMarker != null) {
                markerMap.remove(destMarker);
                destMarker.remove();
                destMarker = null;
            }
            destMarker = googleMap.addMarker(new MarkerOptions().position(latlongEnd).title("Destination").icon(BitmapDescriptorFactory.fromResource(R.mipmap.flag_green)));
            markerMap.put(destMarker, 1);
        }
        if (latlongStart != null && latlongEnd != null && !isSelectingStart && !isSelectingEnd) {
            updateRouteOption();
            adjustMap();
        }
//        if (!isSelectingEnd) {
//            getAddress(latlongEnd, 1);
//        }
        if (endAutocompleteFragment.editSearch != null) {
            endAutocompleteFragment.editSearch.setText(getLocation(latlongEnd));
        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        if (isSelectingStart) {
            isSelectingStart = false;
            isSelectingEnd = false;
            updateStart();
        } else if (isSelectingEnd) {
            isSelectingStart = false;
            isSelectingEnd = false;
            updateEnd();
        }
        mapInstruction.setVisibility(View.GONE);
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String address = "";
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                place = PlacePicker.getPlace(getActivity(), data);
                address = place.getAddress().toString();
                if (address.equalsIgnoreCase("")) {
                    address = "Unknown location";
                }
//                tv_start.setText(address);
                source = address;
                latlongStart = place.getLatLng();
                sourceLat = String.valueOf(latlongStart.latitude);
                sourceLong = String.valueOf(latlongStart.longitude);
                updateMap();
                adjustMap();
            }
        }
        if (requestCode == PLACE_PICKER_REQUEST1) {
            if (resultCode == Activity.RESULT_OK) {
                place = PlacePicker.getPlace(getActivity(), data);
                address = place.getAddress().toString();
                if (address.equalsIgnoreCase("")) {
                    address = "Unknown location";
                }
//                tv_end.setText(address);
                destination = address;
                latlongEnd = place.getLatLng();
                destinationLat = String.valueOf(latlongEnd.latitude);
                destinationLong = String.valueOf(latlongEnd.longitude);
                updateMap();
                adjustMap();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void adjustMap() {
        int padding = 100;//100
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (sourceMarker != null) {
            builder.include(sourceMarker.getPosition());
        }
        if (destMarker != null) {
            builder.include(destMarker.getPosition());
        }
        if (googleMap != null && (destMarker != null && sourceMarker != null) && !isSelectingStart && !isSelectingEnd) {
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(builder.build(), padding);
            googleMap.animateCamera(cu);
        } else if (googleMap != null && latlongStart != null && isSelectingStart) {
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latlongStart, ZOOM);
            googleMap.moveCamera(cu);
        } else if (googleMap != null && destMarker != null && isSelectingEnd) {
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(destMarker.getPosition(), ZOOM);
            googleMap.animateCamera(cu);
        } else if (googleMap != null && sourceMarker != null && !isToSelect) {
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(sourceMarker.getPosition(), ZOOM);
            googleMap.animateCamera(cu);
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (!isToSelect) {
            if (isSelectingStart) {
                latlongStart = cameraPosition.target;
                updateStart();
            } else if (isSelectingEnd) {
                latlongEnd = cameraPosition.target;
                updateEnd();
            }
        } else {
            isToSelect = !isToSelect;
        }
    }


    @Override
    public void onPolylineClick(Polyline polyline) {
        polyline.setColor(getResources().getColor(R.color.colorPrimary));
//        pathData = generateJsonForPath(polyline.getPoints());
        int selectedPosition = pathPolygonList.indexOf(polyline);
        for (int i = 0; i < pathPolygonList.size(); i++) {
            if (i != selectedPosition) {
                pathPolygonList.get(i).setColor(getResources().getColor(R.color.colorAccent));
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (this.googleMap != null) {
            this.googleMap.clear();
            this.googleMap.setOnCameraChangeListener(RequestLiftFragment.this);
            if (HomeActivity.latLngUserSelected != null) {
                latlongStart = HomeActivity.latLngUserSelected;
                if (latlongStart != null) {
                    updateStart();
                    adjustMap();
                    sourceLat = String.valueOf(latlongStart.latitude);
                    sourceLong = String.valueOf(latlongStart.longitude);
                }
            }
//            this.googleMap.setOnMarkerClickListener(RequestLiftFragment.this);
        }
    }

    @Override
    public void update(Location location) {
        if (location != null && googleMap != null) {
            if (latlongStart == null) {
                if (HomeActivity.latLngUserSelected != null) {
                    latlongStart = HomeActivity.latLngUserSelected;
                    if (latlongStart != null) {
                        updateStart();
                        adjustMap();
                    }
                }
            }
        }
    }

    private void updateRouteOption() {
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(getDirectionsUrl(latlongStart, latlongEnd));
//        new DT();
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
        String alternative = "alternatives=true";
        String mode = "mode=driving";
        String unit = "units=imperial";
        String key = "key=AIzaSyCpdz5wefjc6iaVhR5RhBqojrCa_V4faMY";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + unit + "&" /*+ waypoints + "&"*/ + alternative + "&" + sensor + "&" + key;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
//        url = "http://maps.googleapis.com/maps/api/directions/json?origin=46.839382,-100.771373&destination=46.791115,-100.763650&units=imperial&alternatives=true&sensor=false";
        return url;
    }

    private class DT {

        public DT() {
            futureIonHit = Ion.with(activity).load(getDirectionsUrl(latlongStart, latlongEnd)).asString().setCallback(new FutureCallback<String>() {
                @Override
                public void onCompleted(Exception e, String result) {
                    JSONObject jObject;
                    List<List<HashMap<String, String>>> routes = null;
                    try {
                        jObject = new JSONObject(result);
                        DirectionsJSONParser parser = new DirectionsJSONParser();

                        // Starts parsing data
                        routes = parser.parse(jObject);

                        if (routes != null) {
                            ArrayList<LatLng> points = null;
                            ArrayList<PolylineOptions> lineOptionsList = new ArrayList<>();

                            // Traversing through all the routes
                            for (int i = routes.size() - 1; i >= 0; i--) {
                                points = new ArrayList<LatLng>();
                                lineOptions = new PolylineOptions();

                                // Fetching i-th route
                                List<HashMap<String, String>> path = routes.get(i);

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
                                lineOptions.width(12);
                                lineOptions.width(getResources().getDimension(R.dimen.dpw10));
                                lineOptions.color(getResources().getColor(R.color.deselected_path_color));
                                lineOptionsList.add(lineOptions);
                            }

                            // Drawing polyline in the Google Map for the i-th route.
                            if (googleMap != null) {
                                pathPolygonList.clear();
                                for (int i = 0; i < lineOptionsList.size(); i++) {
                                    Polyline polyline = googleMap.addPolyline(lineOptionsList.get(i));
                                    polyline.setClickable(true);
                                    pathPolygonList.add(polyline);
                                }
                            }
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            });
        }
    }


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

    //A method to download json data from url
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


    //A class to parse the Google Places in JSON format
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
            try {
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
                        lineOptions.width(12);
                        lineOptions.color(getResources().getColor(R.color.deselected_path_color));
                        lineOptionsList.add(lineOptions);
                    }
                    //Drawing polyline in the Google Map for the i-th route.
                    if (googleMap != null) {
//                        for (int i = 0; i < pathPolygonList.size(); i++) {
//                            pathPolygonList.get(i).remove();
//                        }
//                        // mayank
//                        pathPolygonList.clear();
                        if (pathPolygonList != null) {
                            for (Polyline polyline : pathPolygonList) {
                                polyline.remove();
                            }
                            pathPolygonList.clear();
                        }
                        for (int i = 0; i < lineOptionsList.size(); i++) {
                            Polyline polyline = googleMap.addPolyline(lineOptionsList.get(i));
                            polyline.setClickable(true);
                            pathPolygonList.add(polyline);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void networkHit() throws IOException {
        final SearchLiftBean searchLiftBean = new SearchLiftBean();
        searchLiftBean.source = source;
        searchLiftBean.destination = destination;
        searchLiftBean.sourceLat = sourceLat;
        searchLiftBean.sourceLong = sourceLong;
        searchLiftBean.destinationLat = destinationLat;
        searchLiftBean.destinationLong = destinationLong;
        searchLiftBean.numberOfSeats = numberOfSeats;

        jsonObject = new JsonObject();
        jsonObject.addProperty(Const.SOURCE, source);
        jsonObject.addProperty(Const.DESTINATION, destination);
        jsonObject.addProperty(Const.SOURCE_LAT, sourceLat);
        jsonObject.addProperty(Const.SOURCE_LONG, sourceLong);
        jsonObject.addProperty(Const.DESTINATION_LAT_API_KEY, destinationLat);
        jsonObject.addProperty(Const.DESTINATION_LNG_API_KET, destinationLong);
        jsonObject.addProperty(Const.NUMBER_OF_SEATS, numberOfSeats);
        jsonObject.addProperty(Const.PAY_BY, ((HomeActivity) activity).payBy);//cash 0 mobiwkik 1

        String userid = Const.getUserId(activity);
        jsonObject.addProperty(Const.USERID, userid);

        jsonObject.addProperty(Const.GENDER, "");
        jsonObject.addProperty(Const.VEHICLE_TYPE, "");
        jsonObject.addProperty(Const.OWNERSHIP_TYPE, "");

        SharedPreference sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
        sharedPreference.putString(Const.NUMBER_OF_SEATS, numberOfSeats);
        sharedPreference.putString(Const.LOCATION_SEARCH_PARAMETER, sourceLat + "," + sourceLong + "#" + destinationLat + "," + destinationLong);


        if (Helper.isConnected(getActivity())) {
//            ((HomeActivity) activity).progress.show();
            progressDialog.show();
            TimerTask task = new RunMeTask();
            timer = new Timer();
            timer.schedule(task, 200, 20 * 1000);

//            JsonParser parser = new JsonParser();
//            jsonObject = parser.parse("{\"source\":\"Jaipur House Colony Agra\",\"destination\":\"Water Works\",\"sourceLat\":\"27.18429527834902\",\"sourceLong\":\"77.98011045902967\",\"destinationLat\":\"27.2036457\",\"destinationLong\":\"78.0302407\",\"numberOfSeats\":\"1\",\"userId\":\"39\",\"gender\":\"\",\"vehicleType\":\"\",\"ownershipType\":\"\"}").getAsJsonObject();

            Log.e("json", "API_SEARCH_LIFT input: "+jsonObject.toString());
            futureIonHit = Ion.with(getActivity()).load(API.API_SEARCH_LIFT).setTimeout(2 * 60 * 1000).setJsonObjectBody(jsonObject).asString().setCallback(new FutureCallback<String>() {
                @Override
                public void onCompleted(Exception e, String jsonString) {
//                            ((HomeActivity) activity).progress.hide();
                    progressDialog.hide();
                    if (e == null) {
                        if (jsonString != null && !jsonString.isEmpty()) {
                            try {
                                Log.e("json", "API_SEARCH_LIFT output: "+jsonString);
                                JSONObject jsonObject = new JSONObject(jsonString);
                                if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                    timer.cancel();
                                    JSONArray resultArray = jsonObject.optJSONArray(Const.RESULT);
                                    liftBeanArrayList = new ArrayList<>();
                                    int size = resultArray.length();
                                    if (size > 0) {
                                        for (int i = 0; i < size; i++) {
                                            JSONObject object = resultArray.optJSONObject(i);
                                            LiftBean liftBean = new LiftBean();
                                            liftBean.liftId = object.optString(Const.LIFT_ID);
                                            liftBean.userId = object.optString(Const.USERID);
                                            liftBean.source = object.optString(Const.SOURCE);
                                            liftBean.destination = object.optString(Const.DESTINATION);
                                            liftBean.sourceLatitude = object.optString("sourceLatitude");
                                            liftBean.sourceLongitude = object.optString("sourceLongitude");
                                            liftBean.destinationLatitude = object.optString("destinationLatitude");
                                            liftBean.destinationLongitude = object.optString("destinationLongitude");
                                            liftBean.startingMatchPoint = object.optString("startingMatchPoint");
                                            liftBean.endingMatchPoint = object.optString("endingMatchPoint");
                                            liftBean.name = object.optString(Const.NAME);
                                            liftBean.gender = object.optString(Const.GENDER);
                                            liftBean.vehicleType = object.optString(Const.VEHICLE_TYPE);
                                            liftBean.age = object.optString(Const.AGE);
                                            liftBean.reviews = object.optString(Const.REVIEWS);
                                            liftBean.rating = object.optString(Const.RATING);
                                            liftBean.rideOffered = object.optString("rideOffered");
                                            liftBean.pendingSeats = object.optString("pendingSeats");
                                            liftBean.routeMatch = object.optString("routeMatch");
                                            liftBean.price = object.optString(Const.PRICE);

                                            String imgurl = object.optString(Const.PROFILE_IMAGE);
                                            imgurl = Helper.getFormattedUrl(imgurl);

                                            liftBean.profileImage = imgurl;
//                                            liftBean.ownershipType = object.optString(Const.TYPE);
                                            liftBean.brand = object.optString(Const.BRAND);
                                            liftBean.model = object.optString(Const.MODEL);
                                            liftBean.phone = object.optString(Const.PHONE);
                                            liftBean.carNumber = object.optString(Const.RC_NUMBER);
                                            liftBean.createdDate = object.optString("createdDate");
                                            liftBean.smoking = object.optString(Const.SMOKING);
                                            liftBean.music = object.optString(Const.MUSIC);
                                            liftBean.eta = object.optString(Const.ETA);

//                                                    String estm_cost = object.optString("");
//                                                    if(!estm_cost.isEmpty()) {
//                                                        liftBean.estm_cost = Float.parseFloat(estm_cost);
//                                                    }
                                            String balance = object.optString("balance");
                                            if (!balance.isEmpty()) {
                                                liftBean.balance = Float.parseFloat(balance);
                                                liftBean.payBy = 1;
                                            }

                                            liftBean.path = Helper.getPathFromString(object.optString(Const.PATH));
                                            liftBean.matchedPath = Helper.getPathFromArray(object.optJSONArray("intersectPath"));
                                            liftBean.requesterPath = Helper.getPathFromArray(object.optJSONArray(Const.REQUESTER_PATH));
                                            liftBeanArrayList.add(liftBean);
//                                                SharedPreference sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
//                                                sharedPreference.putString(Const.NUMBER_OF_SEATS, numberOfSeats + "");
                                        }
                                        ((HomeActivity) activity).gotoSearchLiftFragment(liftBeanArrayList, searchLiftBean);
                                    } else {
                                        Helper.showSnackBar(linearParent, "No Lift Found for filtered criteria");
                                    }
                                } else {
                                    Helper.showSnackBar(linearParent, "No Lift Found for filtered criteria");
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                //Helper.showSnackBar(linearParent, Const.INTERNAL_ERROR);
                                networkHitRetry(Const.INTERNAL_ERROR);
                            }
                        } else {
                            //Helper.showSnackBar(linearParent, Const.POOR_INTERNET);
                            networkHitRetry(Const.POOR_INTERNET);
                        }
                    } else {
                        e.printStackTrace();
                        //Helper.showSnackBar(linearParent, /*e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
                        networkHitRetry(Const.POOR_INTERNET);
                    }
                }
            });
        } else {
            //Helper.showSnackBar(linearParent, Const.NO_INTERNET);
            networkHitRetry(Const.NO_INTERNET);
        }
    }

    public void showSeatChooser() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = layoutInflater.inflate(R.layout.dialog_select_seats, null);
        TextView one = (TextView) view.findViewById(R.id.one);
        TextView two = (TextView) view.findViewById(R.id.two);
        TextView three = (TextView) view.findViewById(R.id.three);
        TextView four = (TextView) view.findViewById(R.id.four);
        TextView five = (TextView) view.findViewById(R.id.five);
//        TextView six = (TextView) view.findViewById(R.id.six);
//        TextView seven = (TextView) view.findViewById(R.id.seven);
//        TextView eight = (TextView) view.findViewById(R.id.eight);

        alertDialogBuilder.setView(view).setCancelable(true);
        final AlertDialog dialog = alertDialogBuilder.create();

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_seats.setText(((TextView) v).getText().toString().trim());
                numberOfSeats = v.getTag().toString().trim();
                dialog.dismiss();
            }
        };

        one.setOnClickListener(onClickListener);
        two.setOnClickListener(onClickListener);
        three.setOnClickListener(onClickListener);
        four.setOnClickListener(onClickListener);
        five.setOnClickListener(onClickListener);
//        six.setOnClickListener(onClickListener);
//        seven.setOnClickListener(onClickListener);
//        eight.setOnClickListener(onClickListener);

        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.select_dialog_item, seatArray);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
        builder.setTitle("Select Seat");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                btn_seats.setText(seatArray[item]);
                numberOfSeats = seatArray[item];
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(true);*/
        dialog.show();
    }


    /*String address = "";

    public void getAddress(LatLng latLng, final int i) {

        try {
            Log.e("address latlng", latLng.latitude + ", " + latLng.longitude);
            if (Helper.isConnected(activity)) {
                Ion.with(activity)
                        .load("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLng.latitude + "," + latLng.longitude + "&sensor=true")
                        .asString()
                        .setCallback(new FutureCallback<String>() {
                            @Override
                            public void onCompleted(Exception e, String jsonString) {
                                if (jsonString != null && !jsonString.isEmpty()) {
                                    Log.e("json", jsonString);
                                    try {
                                        JSONObject jsonObject = new JSONObject(jsonString);
                                        if (i == 0 && startAutocompleteFragment.editSearch != null) {
                                            startAutocompleteFragment.editSearch.setText(getAddress(jsonObject));
                                        } else if (endAutocompleteFragment.editSearch != null) {
                                            endAutocompleteFragment.editSearch.setText(getAddress(jsonObject));
                                        }
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

    public String getAddress(JSONObject jsonObject) {
        try {
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
            return address;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown Location";
    }
*/
    @Override
    public void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }

    public class RunMeTask extends TimerTask {
        @Override
        public void run() {
            if (progressDialog != null && progressDialog.isShowing()) {
                if (time == 1) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.setMessage("Matching routes..");
                            time = 2;
                        }

                    });
                } else if (time == 2) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.setMessage("Preparing results ..");
                            time = 1;
                        }
                    });

                }
            }
        }
    }

    private void deleteFavRetry(String message, final boolean isStart, final String s) {
        if (isVisible()) {
            final Snackbar snackbar = Snackbar.make(linearParent, message, Snackbar.LENGTH_INDEFINITE);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.parseColor(Const.SNACKBAR_TEXT_COLOR));
            textView.setMaxLines(5);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    snackbar.dismiss();
                }
            }, 6000);
            snackbar.setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteFav(isStart, s);
                    snackbar.dismiss();
                }
            }).setActionTextColor(Const.SNACKBAR_ACTION_TEXT_COLOR);
            TextView textView1 = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_action);
            //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, Const.SNACKBAR_ACTION_MARGIN, 0, Const.SNACKBAR_ACTION_MARGIN);
            textView1.setLayoutParams(params);
            textView1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.snackbar_btn, null));
            snackbar.show();
        }
    }

    private void networkHitRetry(String message) {
        if (isVisible()) {
            final Snackbar snackbar = Snackbar.make(linearParent, message, Snackbar.LENGTH_INDEFINITE);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.parseColor(Const.SNACKBAR_TEXT_COLOR));
            textView.setMaxLines(5);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    snackbar.dismiss();
                }
            }, 6000);
            snackbar.setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        networkHit();
                        snackbar.dismiss();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).setActionTextColor(Const.SNACKBAR_ACTION_TEXT_COLOR);
            TextView textView1 = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_action);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, Const.SNACKBAR_ACTION_MARGIN, 0, Const.SNACKBAR_ACTION_MARGIN);
            textView1.setLayoutParams(params);
            textView1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.snackbar_btn, null));
            snackbar.show();
        }
    }

}
