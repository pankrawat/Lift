package com.liftindia.app.fragment;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.liftindia.app.DirectionsJSONParser;
import com.liftindia.app.R;
import com.liftindia.app.activity.HomeActivity;
import com.liftindia.app.adapter.FavAdapter;
import com.liftindia.app.bean.FavoriteBean;
import com.liftindia.app.caldroid.CaldroidFragment;
import com.liftindia.app.caldroid.CaldroidListener;
import com.liftindia.app.caldroid.DataMap;
import com.liftindia.app.caldroid.DateMap;
import com.liftindia.app.caldroid.MonthMap;
import com.liftindia.app.helper.API;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.DbAdapter;
import com.liftindia.app.helper.Helper;
import com.liftindia.app.helper.SharedPreference;
import com.liftindia.app.util.PlaceAutoFragment;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;

//import android.app.DatePickerDialog;
//import android.app.TimePickerDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class OfferLiftFragment extends Fragment implements GoogleMap.OnPolylineClickListener, View.OnClickListener, TimePickerDialog.OnTimeSetListener, OnMapReadyCallback, GoogleMap.OnCameraChangeListener, GoogleMap.OnMarkerClickListener, HomeActivity.GetLocationUpdate, FavAdapter.onFavSelected, DatePickerDialog.OnDateSetListener, CaldroidFragment.OnSelectionDoneListener {
    Activity activity;
    JsonObject jsonObject;
    String placeName;

    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int PLACE_PICKER_REQUEST1 = 2;
    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    RelativeLayout rl_current_location;
    Button btn_ride_details;
    Button ride_now;
    Button ride_later;
    RelativeLayout rl_back;
    LinearLayout linearParent;
    LinearLayout ll_start;
    LinearLayout ll_end;

    ImageView iv_pin_source;

    //    TextView tv_start;
    //    TextView tv_end;
    TextView tv_price;
    TextView tv_offer;
    private Marker sourceMarker, destMarker;
    LatLng latlongStart, latlongEnd;
    PolylineOptions lineOptions;
    private ArrayList<Polyline> pathPolygonList;

    private String userId = "";
    private String source = "";
    private String destination = "";
    private String liftDate = "";
    private String liftTime = "";
    private String price = "";
    private String numberOfSeats = "";
    private String isPending = "";
    private JsonArray pathData;
    int ZOOM = 15;
    private PlaceAutoFragment startAutocompleteFragment, endAutocompleteFragment;

    TimePickerDialog timePickerDialog;

    private boolean isSelectingStart = true;
    private boolean isSelectingEnd = false;
    private HashMap<Marker, Integer> markerMap = new HashMap<>();
    private boolean isToSelect = false;

    private TextView mapInstruction;
    SharedPreference sharedPreference;
    private Geocoder geocoder;
    String vehicleId = "";
    String vehicleType = "";

    private TextView favStart;
    private TextView favEnd;
    private ImageView favStartStar;
    private ImageView favEndStar;

    String isRecurringRide = "0";
    String recurringDays = "0,0,0,0,0,0,0";
    boolean fav = false;
    boolean dialogIsCancelled = false;

    public Future<String> futureIonHit;

    public OfferLiftFragment() {
        // Required empty public constructor
    }

    public static OfferLiftFragment newInstance(String vehicleId, String vehicleType) {
        OfferLiftFragment fragment = new OfferLiftFragment();
        Bundle args = new Bundle();
        args.putSerializable(Const.VEHICLE_ID, vehicleId);
        args.putSerializable(Const.VEHICLE_TYPE, vehicleType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            vehicleId = getArguments().getString(Const.VEHICLE_ID);
            vehicleType = getArguments().getString(Const.VEHICLE_TYPE);

            getArguments().remove(Const.VEHICLE_ID);
            getArguments().remove(Const.VEHICLE_TYPE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (futureIonHit != null) {
            futureIonHit.cancel();
        }
//        getActivity().getFragmentManager().popBackStack();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        View view = inflater.inflate(R.layout.fragment_offer_lift, container, false);
        btn_ride_details = (Button) view.findViewById(R.id.btn_ride_details);
        rl_back = (RelativeLayout) view.findViewById(R.id.rl_back);
        linearParent = (LinearLayout) view.findViewById(R.id.linearParent);
        ll_start = (LinearLayout) view.findViewById(R.id.ll_start);
        ll_end = (LinearLayout) view.findViewById(R.id.ll_end);
        iv_pin_source = (ImageView) view.findViewById(R.id.iv_pin_source);
        mapInstruction = (TextView) view.findViewById(R.id.map_instruction);
        mapInstruction.setOnClickListener(this);

        //        tv_start = (TextView) view.findViewById(R.id.tv_start);
//        tv_end = (TextView) view.findViewById(R.id.tv_end);
        rl_current_location = (RelativeLayout) view.findViewById(R.id.rl_current_location);
        pathPolygonList = new ArrayList<>();

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

        mapFragment = SupportMapFragment.newInstance(new GoogleMapOptions().zOrderOnTop(true));
        getChildFragmentManager().beginTransaction().replace(R.id.mapPlace, mapFragment).commit();
        mapFragment.getMapAsync(this);

        sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        rl_current_location.setOnClickListener(this);

        btn_ride_details.setOnClickListener(this);
        rl_back.setOnClickListener(this);

        ((HomeActivity) activity).locationListener = OfferLiftFragment.this;


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
                    address = "Unknown location";
                }
                latlongStart = place.getLatLng();
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
                latlongEnd = place.getLatLng();
                if (isSelectingStart) {
                    isSelectingStart = !isSelectingStart;
                    if (sourceMarker != null) {
                        markerMap.remove(sourceMarker);
                        sourceMarker.remove();
                        sourceMarker = null;
                    }
                    sourceMarker = googleMap.addMarker(new MarkerOptions().position(latlongStart).title("Source").icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin_red)));
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


        ll_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAutocompleteFragment.editSearch.performClick();
            }
        });
        ll_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endAutocompleteFragment.editSearch.performClick();
            }
        });
        return view;
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
            destMarker = googleMap.addMarker(new MarkerOptions().position(latlongEnd).title("Destination").icon(BitmapDescriptorFactory.fromResource(R.mipmap.flag_red)));
            markerMap.put(destMarker, 1);

        }
        if (latlongStart != null && latlongEnd != null && !isSelectingStart && !isSelectingEnd) {
            updateRouteOption();
        }
    }

    private void updateStart() {
        if (latlongStart != null) {
//            if (sourceMarker != null) {
//                markerMap.remove(sourceMarker);
//                sourceMarker.remove();
//                sourceMarker = null;
//            }
//            sourceMarker = googleMap.addMarker(new MarkerOptions().position(latlongStart).title("").icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin)));
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
            destMarker = googleMap.addMarker(new MarkerOptions().position(latlongEnd).title("Destination").icon(BitmapDescriptorFactory.fromResource(R.mipmap.flag_red)));
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
    public void onSelect(FavoriteBean bean, FavoriteBean.FavPlaceType placeType) {
        if (placeType == FavoriteBean.FavPlaceType.DESTINATION) {
            isSelectingEnd = false;
            isSelectingStart = false;
            latlongEnd = new LatLng(bean.placelat.doubleValue(), bean.placelon.doubleValue());
            iv_pin_source.setVisibility(View.GONE);
            sourceMarker = googleMap.addMarker(new MarkerOptions().position(latlongStart).title("Source").icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin_red)));
            markerMap.put(sourceMarker, 0);
            updateEnd();
            favEndStar.getDrawable().setColorFilter(getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
            favEndStar.setTag(true);
            HomeActivity.favEndId = bean.favId;
        } else if (placeType == FavoriteBean.FavPlaceType.SOURCE) {
            isSelectingEnd = false;
            isSelectingStart = true;
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
    public void onPolylineClick(Polyline polyline) {
//
//        polyline.setColor(ContextCompat.getColor(activity, R.color.colorPrimary));
        pathData = generateJsonForPath(polyline.getPoints());
        int selectedPosition = pathPolygonList.indexOf(polyline);
        lineOptions = new PolylineOptions();
        lineOptions.addAll(polyline.getPoints());
        lineOptions.width(12);
        lineOptions.color(getResources().getColor(R.color.selected_path_color));

        polyline.remove();
        pathPolygonList.remove(selectedPosition);
        Polyline polyline1 = googleMap.addPolyline(lineOptions);
        polyline1.setClickable(true);
// mayank
        pathPolygonList.add(selectedPosition, polyline1);
        for (int i = 0; i < pathPolygonList.size(); i++) {
            if (i != selectedPosition) {
                pathPolygonList.get(i).setColor(ContextCompat.getColor(activity, R.color.deselected_path_color));
            }
        }
    }

    public JsonArray generateJsonForPath(List<LatLng> latLngList) {
        Log.e("latLngList", latLngList.toString());
        JsonArray jsonArray = new JsonArray();
        for (LatLng latLng : latLngList) {
            JsonObject jsonObject = new JsonObject();
            try {
                jsonObject.addProperty(Const.LAT, latLng.latitude);
                jsonObject.addProperty(Const.LONG, latLng.longitude);
                jsonArray.add(jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                activity.onBackPressed();
                break;
            case R.id.btn_ride_details:
                if (validatePath()) {
                    displayAlertDialog();
                }
                break;
            case R.id.rl_current_location:
                if (HomeActivity.latLng != null) {
                    CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(HomeActivity.latLng, ZOOM);
                    this.googleMap.animateCamera(yourLocation);
                }
                break;
            case R.id.map_instruction:
                mapInstruction();
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

    private void mapInstruction() {
        if (sourceMarker != null) {
            markerMap.remove(sourceMarker);
            sourceMarker.remove();
            sourceMarker = null;
        }
        sourceMarker = googleMap.addMarker(new MarkerOptions().position(latlongStart).title("Source").icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin_red)));
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
    }

    public void showStartFavChooser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View alertLayout = layoutInflater.inflate(R.layout.theme_header_dialog, null);

        ListView lv = (ListView) alertLayout.findViewById(R.id.lv);
        TextView title = (TextView) alertLayout.findViewById(R.id.tv_head);
        title.setText("Favourite Locations");
        builder.setView(alertLayout).setCancelable(true);


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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.layout_dialog_item, items);
        lv.setAdapter(adapter);
        final AlertDialog dialog = builder.create();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
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




      /*  builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
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
        });*/
       /* final AlertDialog dialog = builder.create();
        dialog.setCancelable(true);*/
        dialog.show();

    }

    public void showEndFavChooser() {

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View alertLayout = layoutInflater.inflate(R.layout.theme_header_dialog, null);

        ListView lv = (ListView) alertLayout.findViewById(R.id.lv);
        TextView title = (TextView) alertLayout.findViewById(R.id.tv_head);
        title.setText("Favourite Locations");
        builder.setView(alertLayout).setCancelable(true);

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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.layout_dialog_item, items);
        lv.setAdapter(adapter);
        final AlertDialog dialog = builder.create();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == 0) {
                    if (fav) {
                        deleteFav(false, endAutocompleteFragment.editSearch.getText().toString().trim());
                    } else {
                        if (endAutocompleteFragment.editSearch.getText().toString().equalsIgnoreCase("")) {
                            Helper.showSnackBar(((HomeActivity) activity).linearParent, "Please select a location first");
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



       /* ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.select_dialog_singlechoice, items);
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
        dialog.setCancelable(true);*/
        dialog.show();
    }

    private void deleteFav(final boolean isStart, final String s) {
        final String id = Helper.getIdByFavouriteName(HomeActivity.favBeanStringList, s);

        JsonObject req = new JsonObject();
        req.addProperty("id", id);
        if (Helper.isConnected(activity)) {
            ((HomeActivity) activity).progress.show();
            Log.e("json", req.toString());
            futureIonHit = Ion.with(activity)
                    .load(API.API_DELETE_FAV)
                    .setTimeout(45 * 1000)
                    .setJsonObjectBody(req)
                    .asString()
                    .setCallback(new FutureCallback<String>() {

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
                                        //   Helper.showSnackBar(linearParent, Const.INTERNAL_ERROR);
                                        deleteFavRetry(Const.INTERNAL_ERROR, isStart, s);
                                    }
                                } else {
                                    //   Helper.showSnackBar(linearParent, Const.POOR_INTERNET);
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

    private void showFavChooser(final boolean isStart) {
        if (HomeActivity.favBeanStringList != null) {
            if (HomeActivity.favBeanStringList.size() > 0) {
                Helper.showFavPlace(isStart ? FavoriteBean.FavPlaceType.SOURCE : FavoriteBean.FavPlaceType.DESTINATION, getContext(), HomeActivity.favBeanStringList, this, isStart);
            } else {
                Toast.makeText(getContext(), "No Favourites found", Toast.LENGTH_SHORT).show();
            }

        } else {
            try {
                String userid = Const.getUserId(activity);
                if (Helper.isConnected(activity)) {
                    ((HomeActivity) getActivity()).progress.show();
                    JsonObject object = new JsonObject();
                    object.addProperty(Const.USERID, userid);
                    futureIonHit = Ion.with(activity)
                            .load(API.API_GET_FAV)
                            .setJsonObjectBody(object)
                            .asString()
                            .setCallback(new FutureCallback<String>() {
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
                futureIonHit = Ion.with(activity)
                        .load(API.API_MAKE_FAV)
                        .setJsonObjectBody(object)
                        .asString()
                        .setCallback(new FutureCallback<String>() {
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (this.googleMap != null) {
            this.googleMap.clear();

            this.googleMap.setOnPolylineClickListener(OfferLiftFragment.this);
            this.googleMap.setOnCameraChangeListener(OfferLiftFragment.this);
            if (HomeActivity.latLngUserSelected != null) {
                latlongStart = HomeActivity.latLngUserSelected;
                if (latlongStart != null) {
                    updateStart();
                    adjustMap();
                }
            }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String address = "";
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlacePicker.getPlace(getActivity(), data);
                address = place.getAddress().toString();
                if (address.equalsIgnoreCase("")) {
                    address = "Unknown location";
                }
//                tv_start.setText(address);
                latlongStart = place.getLatLng();
                updateMap();
                adjustMap();
            }
        }
        if (requestCode == PLACE_PICKER_REQUEST1) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlacePicker.getPlace(getActivity(), data);
                address = place.getAddress().toString();
                if (address.equalsIgnoreCase("")) {
                    address = "Unknown location";
                }
//                tv_end.setText(address);
                latlongEnd = place.getLatLng();
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
        if (googleMap != null && (destMarker != null && sourceMarker != null) && !isSelectingStart && !isSelectingEnd) {// for showing the path of the route ++ padding in it.
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(builder.build(), padding);
            googleMap.animateCamera(cu);
        } else if (googleMap != null && latlongStart != null && isSelectingStart) { //for initialising the map/start location
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


    private void updateRouteOption() {
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(getDirectionsUrl(latlongStart, latlongEnd));
//         new DT();
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
//        String str_origin = "origin=28.628949995082,77.378991879523";
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
//        String str_dest = "destination=28.6180024,77.2793719";
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
//        url = "https://maps.googleapis.com/maps/api/directions/json?origin=28.628949995082,77.378991879523&destination=28.6180024,77.2793719&units=imperial&alternatives=true&sensor=false&key=AIzaSyCpdz5wefjc6iaVhR5RhBqojrCa_V4faMY";
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
                            HashMap<String, String> point
                                    = path.get(j);
                            double lat = Double.parseDouble(point.get("lat"));
                            double lng = Double.parseDouble(point.get("lng"));
                            LatLng position = new LatLng(lat, lng);
                            points.add(position);
                        }
                        // Adding all the points in the route to LineOptions
                        lineOptions.addAll(points);
                        lineOptions.width(12);
                        if (i == 0) {
                            lineOptions.color(getResources().getColor(R.color.selected_path_color));
                        } else {
                            lineOptions.color(getResources().getColor(R.color.deselected_path_color));
                        }
                        lineOptionsList.add(lineOptions);
                    }
                    // Drawing polyline in the Google Map for the i-th route.
                    if (googleMap != null) {
//                        for (int i = 0; i < pathPolygonList.size(); i++) {
//                            pathPolygonList.get(i).remove();
//                        }
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
                            if (i == lineOptionsList.size() - 1) {
                                pathData = generateJsonForPath(polyline.getPoints());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getLocation(LatLng latLng) {
        String errorMessage = "";

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    latLng.latitude,
                    latLng.longitude,
                    1);
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
                addr = (address.getSubLocality() != null && address.getSubLocality().length() > 0 ? address.getSubLocality() : "") +
                        (address.getLocality() != null && address.getLocality().length() > 0 ? " " + address.getLocality() : "");
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

    public void displayAlertDialog() {
        LayoutInflater inflater = getLayoutInflater(null);
        View alertLayout = inflater.inflate(R.layout.dialog_price_seat, null);
        RelativeLayout rl_cancel = (RelativeLayout) alertLayout.findViewById(R.id.rl_cancel);
        ride_now = (Button) alertLayout.findViewById(R.id.ride_now);
        ride_later = (Button) alertLayout.findViewById(R.id.ride_later);
        final Button post_details = (Button) alertLayout.findViewById(R.id.post_details);
        final RelativeLayout rl_price = (RelativeLayout) alertLayout.findViewById(R.id.rl_price);
        final RelativeLayout rl_offer_seat = (RelativeLayout) alertLayout.findViewById(R.id.rl_offer_seat);

        tv_price = (TextView) alertLayout.findViewById(R.id.tv_price);
        tv_offer = (TextView) alertLayout.findViewById(R.id.tv_offer);

//        tv_price.setText(prices[0]);
//        tv_offer.setText(seatArray[0]);
//
//        price = priceArray[0];
//        numberOfSeats = seatArray[0];

        price = "";
        numberOfSeats = "";

        rl_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPriceChooser();
            }
        });
        rl_offer_seat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSeatChooser();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
        builder.setView(alertLayout);
        builder.setCancelable(true);
        final AlertDialog dialog = builder.create();
        dialog.show();

        rl_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ride_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ride_now.setBackgroundDrawable(getResources().getDrawable(R.drawable.color_change));
                ride_later.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_shape));
                post_details.setClickable(true);

                Calendar calendar = Calendar.getInstance();
                liftDate = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE);

                liftTime = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
                isPending = "No";
                /*if (!Helper.isRecurringDialogShowing) {
                    Helper.showRecurringSelectionDialog(activity, onRecurringDone);
                }*/
                showCalenderDialog();
            }
        });
        ride_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ride_later.setBackgroundDrawable(getResources().getDrawable(R.drawable.color_change));
                ride_now.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_shape));
                post_details.setClickable(true);

                isPending = "Yes";

                //showDatePicker();
                showCalenderDialog();
            }
        });
        post_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (isPending.equalsIgnoreCase("no") && Helper.calculateDistance(latlongStart, HomeActivity.latLng) > 1000) {//1000
//                        Helper.showSnackBar(((HomeActivity) activity).linearParent, "You are far away from Start Location, Please reach there before starting.");
                    msgDialog("", "You are far away from Start Location, Press continue to update Start location with your Current location.");
                } else if (Helper.calculateDistance(latlongEnd, HomeActivity.latLng) > 1000 * 1000) {
                    Helper.showSnackBar(((HomeActivity) activity).linearParent, "You can't offer ride for more than 1000 km.");
                } else {
                    if (validate()) {
                        networkHit();
                    }
                }
            }
        });
    }

    public void msgDialog(String title, String msg) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
//        alertDialogBuilder.setTitle(title);
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(true)
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        latlongStart = HomeActivity.latLng;
                        isSelectingStart = true;
                        mapInstruction();
//                        if (sourceMarker != null) {
//                            markerMap.remove(sourceMarker);
//                            sourceMarker.remove();
//                            sourceMarker = null;
//                        }
//                        sourceMarker = googleMap.addMarker(new MarkerOptions().position(latlongStart).title("Source").icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin_red)));
//                        markerMap.put(sourceMarker, 0);
//                        updateStart();
//                        adjustMap();
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (validate()) {
//                                    networkHit();
//                                }
//                            }
//                        }, 3000);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

//    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
//        @Override
//        public void onDateSet(DatePicker arg0, int y, int m, int d) {
//
//
//        }
//    };

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        if (isFutureDate(year, monthOfYear, dayOfMonth)) {
            liftDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
            showTimePicker();
        } else {
            Helper.showSnackBar(linearParent, "Please do not select past date.");
            showDatePicker();
        }
    }

    private boolean isFutureDate(int y, int m, int d) {
        Calendar c = Calendar.getInstance();
        if (y < c.get(Calendar.YEAR)) {
            return false;
        }
        if (m < c.get(Calendar.MONTH)) {
            return false;
        } else if (m == c.get(Calendar.MONTH)) {
            if (d < c.get(Calendar.DAY_OF_MONTH)) {
                return false;
            }
        }
        return true;
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
//        DatePickerDialog datePickerDialog = new DatePickerDialog(activity, R.style.DialogTheme, myDateListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
//        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
//        datePickerDialog.show();

        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setAccentColor(ContextCompat.getColor(activity, R.color.colorPrimary));
        dpd.show(activity.getFragmentManager(), "Datepickerdialog");
    }

    private void showTimePicker() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);

        timePickerDialog = TimePickerDialog.newInstance(this, hour, minute, second, false);
        timePickerDialog.setAccentColor(ContextCompat.getColor(activity, R.color.colorPrimary));
        timePickerDialog.show(activity.getFragmentManager(), "Timepickerdialog");

//        timePickerDialog = new TimePickerDialog(getActivity(), R.style.DialogTheme, OfferLiftFragment.this, hour, minute, DateFormat.is24HourFormat(getActivity()));
//        timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                if (ride_now != null) {
//                    ride_now.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_shape));
//                }
//                if (ride_now != null) {
//                    ride_later.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_shape));
//                }
//
//            }
//        });
//        if (!timePickerDialog.isShowing()) {
//            timePickerDialog.show();
//        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (timePickerDialog != null) {
            timePickerDialog.dismiss();
        }
        Calendar calendar = Calendar.getInstance();
        if (sdf.format(calendar.getTime()).equals(liftDate)) {
            Log.e("json", "Today's date: " + liftDate);
        }
        if ((sdf.format(calendar.getTime()).equals(liftDate)) &&
                ((calendar.get(Calendar.HOUR_OF_DAY) > hourOfDay) ||
                        (calendar.get(Calendar.HOUR_OF_DAY) == hourOfDay && calendar.get(Calendar.MINUTE) > minute))) {
            Helper.showSnackBar(linearParent, "Time must be greater than current time");
            showTimePicker();
        } else {
            liftTime = hourOfDay + ":" + minute;
            /*if (!Helper.isRecurringDialogShowing) {
                Helper.showRecurringSelectionDialog(activity, onRecurringDone);
            }*/
            //commenting to show it before timepicker dialog
            //showCalenderDialog();
        }
    }

    private Helper.OnRecurringDone onRecurringDone = new Helper.OnRecurringDone() {
        @Override
        public void onRecurringSelected(boolean isRecurring, boolean[] daysSelected) {
            recurringDays = "";
            if (isRecurring) {
                isRecurringRide = "1";
                for (int i = 0; i < daysSelected.length; i++) {
                    if (i == daysSelected.length - 1) {
                        if (daysSelected[i]) {
                            recurringDays += "1";
                        } else {
                            recurringDays += "0";
                        }
                    } else {
                        if (daysSelected[i]) {
                            recurringDays += "1,";
                        } else {
                            recurringDays += "0,";
                        }
                    }
                }
            } else {
                isRecurringRide = "0";
                recurringDays = "0,0,0,0,0,0,0";
            }


        }
    };

    public void rideAlertDialog() {
        LayoutInflater inflater1 = getLayoutInflater(null);
        View alertLayout = inflater1.inflate(R.layout.ride_now, null);
        ImageView cancel = (ImageView) alertLayout.findViewById(R.id.cancel);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
        builder.setView(alertLayout);
        builder.setCancelable(true);
        final AlertDialog dialog = builder.create();
        dialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialogIsCancelled = true;
                if (isPending.equalsIgnoreCase("no")) {
                    ((HomeActivity) activity).gotoTrackerFragment();
                    sharedPreference.putString("vehicleType", vehicleType);
                } else {
                    activity.onBackPressed();
                }
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!dialogIsCancelled) {
                    dialog.dismiss();
                    if (isPending.equalsIgnoreCase("no")) {
                        ((HomeActivity) activity).gotoTrackerFragment();
                    } else {
                        activity.onBackPressed();
                    }
                }
            }
        }, 5000);
    }

    //    public void  rideLaterDialog(){
//        LayoutInflater inflater2 = getLayoutInflater(null);
//        View alertLayout = inflater2.inflate(R.layout.ride_later, null);
//        TextView done_view= (TextView) alertLayout.findViewById(R.id.done);
//        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
//        builder.setView(alertLayout);
//        builder.setCancelable(false);
//        final AlertDialog dialog = builder.create();
//        dialog.show();
//        done_view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//    }


    private boolean validatePath() {
//        source = tv_start.getText().toString().trim();
//        destination = tv_end.getText().toString().trim();

        source = startAutocompleteFragment.editSearch.getText().toString();
        destination = endAutocompleteFragment.editSearch.getText().toString();

        if (source.equalsIgnoreCase("") || source.equalsIgnoreCase("Unknown Source") || source.equalsIgnoreCase("Unknown Address")) {
            Helper.showSnackBar(linearParent, "Please Select Start Location");
            return false;
        }
        if (destination.equalsIgnoreCase("") || destination.equalsIgnoreCase("Unknown Source") || destination.equalsIgnoreCase("Unknown Address")) {
            Helper.showSnackBar(linearParent, "Please Select End Location");
            return false;
        }
        if (pathData == null) {
            Helper.showSnackBar(linearParent, "Please select one path");
            return false;
        }
        return true;
    }

    private boolean validate() {
        jsonObject = new JsonObject();

//        source = tv_start.getText().toString().trim();
//        destination = tv_end.getText().toString().trim();
        source = startAutocompleteFragment.editSearch.getText().toString();
        destination = endAutocompleteFragment.editSearch.getText().toString();

        if (source.equalsIgnoreCase("") || source.equalsIgnoreCase("Unknown Source") || source.equalsIgnoreCase("Unknown Address")) {
            Helper.showSnackBar(linearParent, "Please Select Start Location");
            return false;
        }
        if (destination.equalsIgnoreCase("") || destination.equalsIgnoreCase("Unknown Source") || destination.equalsIgnoreCase("Unknown Address")) {
            Helper.showSnackBar(linearParent, "Please Select End Location");
            return false;
        }
        if (isPending.equalsIgnoreCase("")) {
            Helper.showSnackBar(linearParent, "Choose Ride Now or Ride Later first");
            return false;
        }
        if (liftDate.equalsIgnoreCase("")) {
            Helper.showSnackBar(linearParent, "Please select date");
            return false;
        }
        if (liftTime.equalsIgnoreCase("")) {
            Helper.showSnackBar(linearParent, "Please select time");
            return false;
        }
        if (price.equalsIgnoreCase("")) {
            Helper.showSnackBar(linearParent, "Please select price");
            return false;
        }
        if (numberOfSeats.equalsIgnoreCase("")) {
            Helper.showSnackBar(linearParent, "Please select number of seats");
            return false;
        }

        source = startAutocompleteFragment.editSearch.getText().toString().trim();
        destination = endAutocompleteFragment.editSearch.getText().toString().trim();

        userId = Const.getUserId(activity);
        jsonObject.addProperty(Const.USERID, userId);
        jsonObject.addProperty(Const.VEHICLE_ID, vehicleId);
        jsonObject.addProperty(Const.SOURCE, source);
        jsonObject.addProperty(Const.DESTINATION, destination);
        jsonObject.addProperty(Const.SOURCE_LAT, latlongStart.latitude);
        jsonObject.addProperty(Const.SOURCE_LONG, latlongStart.longitude);
        jsonObject.addProperty(Const.DESTINATION_LAT_API_KEY, latlongEnd.latitude);
        jsonObject.addProperty(Const.DESTINATION_LNG_API_KET, latlongEnd.longitude);
        jsonObject.addProperty(Const.LIFT_DATE, liftDate);
        jsonObject.addProperty(Const.LIFT_TIME, liftTime);
        jsonObject.addProperty(Const.PRICE, price);
        jsonObject.addProperty(Const.NUMBER_OF_SEATS, numberOfSeats);
        jsonObject.addProperty(Const.IS_PENDING, isPending);
        jsonObject.add(Const.PATH, pathData);
        jsonObject.addProperty(Const.IS_RECURRING, isRecurringRide);
        jsonObject.addProperty(Const.RECURRING_DAYS, recurringDays);

        SharedPreference sp = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_OFFERER_DETAILS);
        sp.putString(Const.SOURCE_NAME, source);
//        sp.putString(Const.SOURCE_LAT, latlongStart.latitude + "");
//        sp.putString(Const.SOURCE_LONG, latlongStart.longitude + "");
        sp.putDouble(Const.SOURCE_LAT, latlongStart.latitude);
        sp.putDouble(Const.SOURCE_LONG, latlongStart.longitude);
        sp.putString(Const.DESTINATION_NAME, destination);

//        sp.putString(Const.DESTINATION_LATI, latlongEnd.latitude + "");
//        sp.putString(Const.DESTINATION_LNG, latlongEnd.longitude + "");
        sp.putDouble(Const.DESTINATION_LATI, latlongEnd.latitude);
        sp.putDouble(Const.DESTINATION_LNG, latlongEnd.longitude);
//        Log.e("pathData", pathData.size()+"");
        new Thread(new Runnable() {
            public void run() {

                DbAdapter.getInstance(activity).setPath(pathData);
            }
        }).start();

        return true;
    }

    private void networkHit() {
        if (Helper.isConnected(activity)) {
            ((HomeActivity) activity).progress.show();
            Log.e("json", "API_ADD_LIFT input: " + jsonObject.toString());
            futureIonHit = Ion.with(activity)
                    .load(API.API_ADD_LIFT)
                    .setTimeout(45 * 1000)
                    .setJsonObjectBody(jsonObject)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String jsonString) {
                            ((HomeActivity) activity).progress.hide();
                            if (e == null) {
                                if (jsonString != null && !jsonString.isEmpty()) {
                                    try {
                                        Log.e("json", "API_ADD_LIFT output: " + jsonString);
                                        JSONObject jsonObject = new JSONObject(jsonString);
                                        if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                            if (isPending.equalsIgnoreCase("no")) {
                                                HomeActivity.isShareLocation = true;
                                                sharedPreference.putBoolean("shareLocation", true);
                                                sharedPreference.putBoolean(Const.IS_RIDE_ACTIVE, true);


                                                sharedPreference.putString(Const.LIFT_ID, jsonObject.optString(Const.RESULT));
                                                sharedPreference.putString("LIFT_ID", jsonObject.optString(Const.RESULT));
                                                sharedPreference.putLong("autoCancelLocationUpdateTime", System.currentTimeMillis());
                                                sharedPreference.putString(Const.IS_OFFERER, "1");
                                                //((HomeActivity) activity).autoCancelLocationUpdateIfNoLiftRequest();
                                            }
                                            rideAlertDialog();
                                        } else {
                                            Helper.showSnackBar(linearParent, jsonObject.optString(Const.MESSAGE));
                                            isPending = "";
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                        //Helper.showSnackBar(linearParent, Const.INTERNAL_ERROR);
                                        networkHitRetry(Const.INTERNAL_ERROR);
                                        isPending = "";
                                    }
                                } else {
                                    Helper.showSnackBar(linearParent, Const.POOR_INTERNET);
                                    networkHitRetry(Const.POOR_INTERNET);
                                    isPending = "";
                                }
                            } else {
                                e.printStackTrace();
                                //Helper.showSnackBar(linearParent, /*e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
                                networkHitRetry(Const.POOR_INTERNET);
                                isPending = "";
                            }
                        }
                    });
        } else {
            //Helper.showSnackBar(linearParent, Const.NO_INTERNET);
            networkHitRetry(Const.NO_INTERNET);
        }
    }

    public void showPriceChooser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = layoutInflater.inflate(R.layout.dialog_select_price, null);
        TextView one = (TextView) view.findViewById(R.id.one);
        TextView two = (TextView) view.findViewById(R.id.two);
        TextView three = (TextView) view.findViewById(R.id.three);
        TextView four = (TextView) view.findViewById(R.id.four);

        alertDialogBuilder.setView(view).setCancelable(true);
        final AlertDialog dialog = alertDialogBuilder.create();

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_price.setText(((TextView) v).getText().toString().trim());
                        price = v.getTag().toString().trim();
                        dialog.dismiss();
                    }
                });
            }
        };

        one.setOnClickListener(onClickListener);
        two.setOnClickListener(onClickListener);
        three.setOnClickListener(onClickListener);
        four.setOnClickListener(onClickListener);


       /* ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.select_dialog_item, prices);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
        builder.setTitle("Select Price");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                tv_price.setText(prices[item]);
                price =*//* "1";*//*priceArray[item];
                dialog.dismiss();
            }
        });*/
//        final AlertDialog dialog = builder.create();
//        dialog.setCancelable(true);
        dialog.show();
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
        if (vehicleType.equals("2-Wheeler")) {
            one.setVisibility(View.VISIBLE);
            two.setVisibility(View.GONE);
            three.setVisibility(View.GONE);
            four.setVisibility(View.GONE);
            five.setVisibility(View.GONE);
        } else if (vehicleType.equals("3-Wheeler")) {
            one.setVisibility(View.VISIBLE);
            two.setVisibility(View.VISIBLE);
            three.setVisibility(View.VISIBLE);
            four.setVisibility(View.GONE);
            five.setVisibility(View.GONE);
        }

        alertDialogBuilder.setView(view).setCancelable(true);
        final AlertDialog dialog = alertDialogBuilder.create();

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_offer.setText(((TextView) v).getText().toString().trim());
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


       /* ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.select_dialog_item, seatArray);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
        builder.setTitle("Select Seat");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                tv_offer.setText(seatArray[item]);
                numberOfSeats = seatArray[item];
                dialog.dismiss();
            }
        });*/
//        final AlertDialog dialog = builder.create();
//        dialog.setCancelable(true);
        dialog.show();
    }

    final CaldroidListener listener = new CaldroidListener() {
        @Override
        public void onSelectDate(Date date, View view) {

        }
    };

    private void showCalenderDialog() {

        // Setup caldroid to use as dialog
//        CaldroidNonDialogFragment dialogCaldroidFragment = new CaldroidNonDialogFragment();
        CaldroidFragment dialogCaldroidFragment = new CaldroidFragment();

        dialogCaldroidFragment.setCaldroidListener(listener);

        final String dialogTag = "CALDROID_DIALOG_FRAGMENT";
        Bundle bundle = new Bundle();
        bundle.putString(CaldroidFragment.DIALOG_TITLE,
                "Select available dates");
        dialogCaldroidFragment.setArguments(bundle);
        dialogCaldroidFragment.clearSelectedDates();
        dialogCaldroidFragment.setListener(this);
        dialogCaldroidFragment.show(getFragmentManager(), dialogTag);
//        getSupportFragmentManager().beginTransaction().add(R.id.home_container, dialogCaldroidFragment).addToBackStack(null).commit();

//        if (from.equalsIgnoreCase("details")) {
//            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.detail_container, dialogCaldroidFragment).addToBackStack(null).commit();
//        } else {
//            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.home_container, dialogCaldroidFragment).addToBackStack(null).commit();
//        }
    }

    @Override
    public void onSelectionDone(DataMap dbmap) {
        String dates = hashmapToString(dbmap);
        if (dates != null) {
            Log.e("dates", dates);
            String str = "";
            if (dates.indexOf(",") > 0) {
                str = dates.substring(0, dates.indexOf(","));
            } else {
                str = dates;
            }
            recurringDays = dates.substring(dates.indexOf(",") + 1);
            if (!recurringDays.isEmpty()) {
                isRecurringRide = "1";
            }
            if (isPending.equalsIgnoreCase("Yes")) {
                liftDate = str;
                showTimePicker();
            }
        }
    }

    public String hashmapToString(DataMap dataMap) {
        StringBuilder stringBuilder = new StringBuilder("");
        TreeMap<Integer, String> dates = new TreeMap<>();
        if (dataMap != null) {
            if (dataMap.getYearMap() != null && dataMap.getYearMap().size() > 0) {
                Set<Integer> yearset = dataMap.getYearMap().keySet();
                Iterator<Integer> iterator = yearset.iterator();
                while (iterator.hasNext()) {
                    Integer yearkey = iterator.next();

                    MonthMap month_map = dataMap.getYearMap().get(yearkey);
                    if (month_map != null && month_map.getMonthMap() != null && month_map.getMonthMap().size() > 0) {
                        //go for dates

                        Iterator<Integer> iterator2 = month_map.getMonthMap().keySet().iterator();
                        while (iterator2.hasNext()) {
                            Integer month_key = iterator2.next();
                            DateMap date_map = month_map.getMonthMap().get(month_key);
                            if (date_map != null && date_map.getDateMap() != null && date_map.getDateMap().size() > 0) {

                                Iterator<Integer> iterator3 = date_map.getDateMap().keySet().iterator();
                                while (iterator3.hasNext()) {
                                    Integer date_key = iterator3.next();

                                    String date = yearkey + "-" + ((month_key < 10) ? "0" + month_key : month_key) + "-" + ((date_key < 10) ? "0" + date_key : date_key);
                                    int key = yearkey * 10000 + month_key * 100 + date_key;

                                    dates.put(key, date);

                                }
                            }
                        }
                    }
                }
            }
        }

        if (dates.size() > 0) {

            Iterator<Integer> it = dates.keySet().iterator();

            while (it.hasNext()) {
                stringBuilder.append("," + dates.get(it.next()));
            }
            stringBuilder.deleteCharAt(0);
            return stringBuilder.toString();
        }
        return null;
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
                    networkHit();
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