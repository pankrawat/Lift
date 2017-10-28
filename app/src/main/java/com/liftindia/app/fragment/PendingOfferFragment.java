package com.liftindia.app.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
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
import com.liftindia.app.bean.RouteDetails;
import com.liftindia.app.helper.API;
import com.liftindia.app.helper.Const;
import com.liftindia.app.helper.DbAdapter;
import com.liftindia.app.helper.Helper;
import com.liftindia.app.helper.Progress;
import com.liftindia.app.helper.SharedPreference;
import com.liftindia.app.util.PlaceAutoFragment;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PendingOfferFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraChangeListener, View.OnClickListener, FavAdapter.onFavSelected {
    Activity activity;
    String placeName;
    Progress progress;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int PLACE_PICKER_REQUEST1 = 2;
    private GoogleMap googleMap;
    ImageView current_location;
    Button btn_start_now, btn_cancel, btn_re_request;
    RelativeLayout rl_back;
    RelativeLayout rl_current_location;
    //    TextView tv_start, tv_end;
    private Marker sourceMarker, destMarker;
    LatLng latlongStart, latlongEnd;
    PolylineOptions lineOptions;
    JsonObject jsonObject;
    Double source_lat, source_long;
    Double desti_lat, desti_long;
    private List<LatLng> path;
    private JsonArray pathData;
    //    private Vector<Object> pathList = new Vector<>();
    boolean isChangeEnable = false;
    boolean isPathSelected = false;
    LinearLayout ll_start, ll_end, linearParent;
    private String liftId = "";
    private String offererId = "";
    private String vehicleId = "";
    private String userId = "";
    private String source = "";
    private String destination = "";
    private String liftDate = "";
    private String liftTime = "";
    private String price = "";
    private String numberOfSeats = "";
    private String isPending = "";
    private ArrayList<Polyline> pathPolygonList;
    RouteDetails routeDetails;

    PlaceAutoFragment startAutocompleteFragment, endAutocompleteFragment;
    private boolean isSelectingStart = false;
    private boolean isSelectingEnd = false;
    private boolean isToSelect = false;
    private HashMap<Marker, Integer> markerMap = new HashMap<>();
    private boolean isToEdit = false;

    private TextView mapInstruction;

    private Geocoder geocoder;

    private TextView favStart;
    private TextView favEnd;
    private ImageView favStartStar;
    private ImageView favEndStar;
    SharedPreference sharedPreference;

    String isRecurringRide = "0";
    String recurringDays = "";
    boolean fav = false;
    ImageView iv_pin_source;
    private boolean isFirstTime = true, isOffer = false;
    Future<String> futureIonHit;

    public PendingOfferFragment() {
        // Required empty public constructor
    }

    public static PendingOfferFragment newInstance() {
        return new PendingOfferFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offerer_pending_request, container, false);
        activity = getActivity();
        linearParent = ((HomeActivity) activity).linearParent;
        progress = ((HomeActivity) activity).progress;
        progress.setCancelable(true);
        progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (futureIonHit != null) {
                    futureIonHit.cancel();
                }
            }
        });
        routeDetails = RouteDetails.getInstance();
        rl_back = (RelativeLayout) view.findViewById(R.id.rl_back);
        rl_current_location = (RelativeLayout) view.findViewById(R.id.rl_current_location);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(new GoogleMapOptions().zOrderOnTop(true));
        getChildFragmentManager().beginTransaction().replace(R.id.mapPlace, mapFragment).commit();
        mapFragment.getMapAsync(this);
        ll_start = (LinearLayout) view.findViewById(R.id.ll_start);
        ll_end = (LinearLayout) view.findViewById(R.id.ll_end);
        iv_pin_source = (ImageView) view.findViewById(R.id.iv_pin_source);

        btn_start_now = (Button) view.findViewById(R.id.btn_start_now);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_re_request = (Button) view.findViewById(R.id.btn_re_request);
        mapInstruction = (TextView) view.findViewById(R.id.map_instruction);

        favStart = (TextView) view.findViewById(R.id.tv_fav_start);
        favEnd = (TextView) view.findViewById(R.id.tv_fav_end);
        favStartStar = (ImageView) view.findViewById(R.id.iv_fav_start);
        favEndStar = (ImageView) view.findViewById(R.id.iv_fav_end);
        favStartStar.getDrawable().setColorFilter(activity.getResources().getColor(R.color.cbdbdbd), PorterDuff.Mode.SRC_ATOP);
        favEndStar.getDrawable().setColorFilter(activity.getResources().getColor(R.color.cbdbdbd), PorterDuff.Mode.SRC_ATOP);

        favStart.setOnClickListener(this);
        favEnd.setOnClickListener(this);
        favStartStar.setOnClickListener(this);
        favEndStar.setOnClickListener(this);
        btn_re_request.setOnClickListener(this);

        sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);

        geocoder = new Geocoder(getContext(), Locale.getDefault());

        if (getArguments() != null) {
            isToEdit = getArguments().getBoolean("isEdit");
            isOffer = getArguments().getBoolean("isOffer");
            if (isOffer) {
                btn_re_request.setVisibility(View.GONE);
                vehicleId = routeDetails.vehicleId;
                path = routeDetails.path;
                pathData = generateJsonForPath(path);
                if (!isToEdit) {
                    btn_cancel.setVisibility(View.VISIBLE);
                } else {
                    btn_cancel.setVisibility(View.GONE);
                    isChangeEnable = true;
                }
            } else {
                path = routeDetails.path;
                pathData = generateJsonForPath(path);
                btn_cancel.setVisibility(View.GONE);
                btn_start_now.setVisibility(View.GONE);
                offererId = routeDetails.offererId;
            }
        }


        pathPolygonList = new ArrayList<>();

        latlongStart = routeDetails.source;
        source_lat = latlongStart.latitude;
        source_long = latlongStart.longitude;

        latlongEnd = routeDetails.destination;
        desti_lat = latlongEnd.latitude;
        desti_long = latlongEnd.longitude;

        source = routeDetails.source_from;
        destination = routeDetails.source_to;
        liftDate = routeDetails.liftDate;

        price = routeDetails.price;
        numberOfSeats = routeDetails.numberOfSeats;
        liftId = routeDetails.liftId;


//        tv_start.setText(RouteDetails.getObject().getSource_from());
//        tv_end.setText(RouteDetails.getObject().getSource_to());
        Log.e("LatLng", latlongStart.toString() + latlongEnd.toString());


        btn_start_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helper.calculateDistance(latlongStart, HomeActivity.latLng) > 1000) {
//                    Helper.showSnackBar(linearParent, "You are far away from Start Location, Please reach there before starting.");
                    msgDialog("", "You are far away from Start Location, Press continue to update Start location with your Current location.");
                } else if (Helper.calculateDistance(latlongEnd, HomeActivity.latLng) > 1000 * 1000) {
                    Helper.showSnackBar(linearParent, "You can't offer ride for more than 1000 km.");
                } else {
                    if (validate()) {
//                        networkHit();
                        networkHitpending();
                    }
                }

//                if (!Helper.isRecurringDialogShowing) {
//                    Helper.showRecurringSelectionDialog(activity, onRecurringDone);
//                }
            }
        });

        rl_back.setOnClickListener(this);
        rl_current_location.setOnClickListener(this);

        startAutocompleteFragment = new PlaceAutoFragment();
        getChildFragmentManager().beginTransaction().add(R.id.start_fragment_container, startAutocompleteFragment).commit();
        startAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                startAutocompleteFragment.isPopupShowing = false;
                favStartStar.getDrawable().setColorFilter(activity.getResources().getColor(R.color.cbdbdbd), PorterDuff.Mode.SRC_ATOP);
                favStartStar.setTag(false);
                isToSelect = true;
                String address = "";
                address = place.getAddress().toString();
                if (address.equalsIgnoreCase("")) {
                    address = "Unknown location";
                }
                source = address;
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
                favEndStar.getDrawable().setColorFilter(activity.getResources().getColor(R.color.cbdbdbd), PorterDuff.Mode.SRC_ATOP);
                favEndStar.setTag(false);
                isToSelect = true;
                String address = "";
                address = place.getAddress().toString();
                if (address.equalsIgnoreCase("")) {
                    address = "Unknown location";
                }
                destination = address;
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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startAutocompleteFragment.editSearch.setText(RouteDetails.getInstance().source_from);
                endAutocompleteFragment.editSearch.setText(RouteDetails.getInstance().source_to);
                if (!isToEdit) {
                    startAutocompleteFragment.editSearch.setEnabled(false);
                    endAutocompleteFragment.editSearch.setEnabled(false);
                }
            }
        }, 500);

        if (!isToEdit) {
            ll_start.setEnabled(false);
            ll_end.setEnabled(false);
            ll_start.setAlpha(0.5f);
            ll_end.setAlpha(0.5f);
        }

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vehicleDeleteDialog();
            }
        });
        mapInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
        return view;
    }

    public void vehicleDeleteDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
        alertDialogBuilder
                .setMessage("Are you sure to delete this Ride?")
                .setCancelable(false)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int ids) {
                        cancel_ride();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void cancel_ride() {
        JsonObject req = new JsonObject();
        req.addProperty(Const.LIFT_ID, routeDetails.liftId);
        if (Helper.isConnected(activity)) {
            progress.show();
            Log.e("Cancel Lift input", req.toString());
            Ion.with(activity)
                    .load(API.DELETE_PENDING_LIFT)
                    .setTimeout(45 * 1000)
                    .setJsonObjectBody(req)
                    .asString()
                    .setCallback(new FutureCallback<String>() {

                        @Override
                        public void onCompleted(Exception e, String jsonString) {
                            progress.hide();
                            if (e == null) {
                                if (jsonString != null && !jsonString.isEmpty()) {
                                    try {
                                        Log.e("Cancel Lift output", jsonString);

                                        JSONObject jsonObject = new JSONObject(jsonString);
                                        if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                            Helper.showSnackBar(linearParent, jsonObject.optString(Const.MESSAGE));
                                            ((HomeActivity) activity).go2Home();
// list.remove(position);
//                                                notifyItemRemoved(position);
//                                                notifyItemRangeRemoved(position, list.size());
//                                                notifyItemRangeRemoved(position, list.size());
                                        } else {
                                            Helper.showSnackBar(linearParent, jsonObject.optString(Const.MESSAGE));
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                        deleteRideRetry(Const.INTERNAL_ERROR);
                                    }
                                } else {
                                    deleteRideRetry(Const.POOR_INTERNET);
                                }
                            } else {
                                e.printStackTrace();
                                deleteRideRetry(Const.POOR_INTERNET);
                            }
                        }
                    });
        } else {
            deleteRideRetry(Const.NO_INTERNET);
        }
    }

    private void deleteRideRetry(String message) {
        final Snackbar snackbar = Snackbar.make(linearParent, message, Snackbar.LENGTH_INDEFINITE);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.parseColor(Const.SNACKBAR_TEXT_COLOR));
        textView.setMaxLines(5);
        Handler handler = new Handler();
        /*handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar.dismiss();
            }
        }, 8000);*/
        snackbar.setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel_ride();
            }
        }).setActionTextColor(Const.SNACKBAR_ACTION_COLOR);
        snackbar.show();
    }


    public void updateStartOnMapReady() {
        if (latlongStart != null) {
            if (sourceMarker != null) {
                sourceMarker.remove();
                sourceMarker = null;
            }
            sourceMarker = googleMap.addMarker(new MarkerOptions().position(latlongStart).title("Source").icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin_red)));
            markerMap.put(sourceMarker, 0);
//            if(isSelectingStart) {
//                iv_pin.setVisibility(View.VISIBLE);
//            }
        }
//        if (latlongEnd != null) {
//            if (destMarker != null) {
//                destMarker.remove();
//                destMarker = null;
//            }
//            destMarker = googleMap.addMarker(new MarkerOptions().position(latlongEnd).title("Destination").icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin)));
//            markerMap.put(destMarker, 1);
//        }
        if (latlongStart != null && latlongEnd != null && !isSelectingStart && !isSelectingEnd) {
            updateRouteOption();
        }
    }

    public void updateMap() {
        if (latlongStart != null) {
//            if (sourceMarker != null) {
//                sourceMarker.remove();
//                sourceMarker = null;
//            }
//            sourceMarker = googleMap.addMarker(new MarkerOptions().position(latlongStart).title("Source").icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin)));
//            markerMap.put(sourceMarker, 0);
            if (isSelectingStart) {
                iv_pin_source.setVisibility(View.VISIBLE);
            }
        }
        if (latlongEnd != null) {
            if (destMarker != null) {
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

    private Helper.OnRecurringDone onRecurringDone = new Helper.OnRecurringDone() {
        @Override
        public void onRecurringSelected(boolean isRecurring, boolean[] daysSelected) {
            if (isRecurring) {
                isRecurringRide = "1";
            } else {
                isRecurringRide = "0";
            }
            recurringDays = "";
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

//            if (validate()) {
//                networkHit();
//            }
        }
    };

    public void msgDialog(String title, String msg) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
//        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(msg).setCancelable(false).setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                latlongStart = HomeActivity.latLng;
                if (sourceMarker != null) {
                    markerMap.remove(sourceMarker);
                    sourceMarker.remove();
                    sourceMarker = null;
                }
                sourceMarker = googleMap.addMarker(new MarkerOptions().position(latlongStart).title("Source").icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin_red)));
                markerMap.put(sourceMarker, 0);
                updateStart();
                updateRouteOption();
                adjustMap();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (validate()) {
//                            networkHit();
                            networkHitpending();
                        }
                    }
                }, 5000);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
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
                        makeFav(true, true);
                    }
                } else {
                    showFavChooser(true);
                }
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = builder.create();
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
                        makeFav(false, true);
                    }
                } else {
                    showFavChooser(false);
                }
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteFav(final boolean isStart, final String s) {
        final String id = Helper.getIdByFavouriteName(HomeActivity.favBeanStringList, s);
        if (id.equalsIgnoreCase("")) {
            return;
        }

        JsonObject req = new JsonObject();
        req.addProperty("id", id);
        if (Helper.isConnected(activity)) {
            progress.show();
            Log.e("json", req.toString());
            futureIonHit = Ion.with(activity).load(API.API_DELETE_FAV).setJsonObjectBody(req).asString().setCallback(new FutureCallback<String>() {

                @Override
                public void onCompleted(Exception e, String jsonString) {
                    progress.hide();
                    if (e == null) {
                        if (jsonString != null && !jsonString.isEmpty()) {
                            try {
                                Log.e("json", jsonString);

                                JSONObject jsonObject = new JSONObject(jsonString);
                                if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                    Helper.showSnackBar(linearParent, jsonObject.optString(Const.MESSAGE));
                                    List<FavoriteBean> beanList = null;
                                    int pos = 0;
                                    beanList = HomeActivity.favBeanStringList;
                                    if (!id.equalsIgnoreCase("")) {
                                        pos = Helper.getPositionByFavouriteId(beanList, id);
                                        beanList.remove(pos);
                                    }
                                    if (isStart) {
                                        favStartStar.getDrawable().setColorFilter(activity.getResources().getColor(R.color.cbdbdbd), PorterDuff.Mode.SRC_ATOP);
                                        favStartStar.setTag(false);
                                    } else {
                                        favEndStar.getDrawable().setColorFilter(activity.getResources().getColor(R.color.cbdbdbd), PorterDuff.Mode.SRC_ATOP);
                                        favEndStar.setTag(false);
                                    }
                                } else {
                                    Helper.showSnackBar(linearParent, jsonObject.optString(Const.MESSAGE));
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                //Helper.showSnackBar(linearParent, Const.INTERNAL_ERROR);
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
            //Helper.showSnackBar(linearParent, Const.NO_INTERNET);
            deleteFavRetry(Const.NO_INTERNET, isStart, s);
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

    private void makeFav(final boolean isStart, final boolean isFav) {
        try {
            String userid = Const.getUserId(activity);
            if (Helper.isConnected(activity)) {
                ((HomeActivity) getActivity()).progress.show();
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
                        ((HomeActivity) getActivity()).progress.hide();
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
                                        favStartStar.getDrawable().setColorFilter(activity.getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
                                        favStartStar.setTag(true);
                                        HomeActivity.favStartId = bean.favId;
                                    } else {
                                        favEndStar.getDrawable().setColorFilter(activity.getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
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
                                        favStartStar.getDrawable().setColorFilter(activity.getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
                                        favStartStar.setTag(true);
                                        HomeActivity.favStartId = Helper.getIdByFavouriteName(HomeActivity.favBeanStringList, placeName);
                                    } else {
                                        favEndStar.getDrawable().setColorFilter(activity.getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
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
            favStartStar.getDrawable().setColorFilter(activity.getResources().getColor(R.color.cbdbdbd), PorterDuff.Mode.SRC_ATOP);
            favStartStar.setTag(false);
            HomeActivity.favStartId = "";
        } else {
            favEndStar.getDrawable().setColorFilter(activity.getResources().getColor(R.color.cbdbdbd), PorterDuff.Mode.SRC_ATOP);
            favEndStar.setTag(false);
            HomeActivity.favEndId = "";
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                activity.onBackPressed();
                break;
            case R.id.rl_current_location:
                if (HomeActivity.latLng != null) {
                    CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(HomeActivity.latLng, ZOOM);
                    this.googleMap.animateCamera(yourLocation);
                }
                break;
            case R.id.btn_re_request:
                if (validateRequest()) {
                    networkHitReRequest();
                }
                break;
            case R.id.tv_fav_start:
                showStartFavChooser();
                break;
            case R.id.tv_fav_end:
                showEndFavChooser();
                break;
            case R.id.iv_fav_start:
                showStartFavChooser();
                break;
            case R.id.iv_fav_end:
                showEndFavChooser();
                break;
        }
    }

    @Override
    public void onSelect(FavoriteBean bean, FavoriteBean.FavPlaceType placeType) {
        if (placeType == FavoriteBean.FavPlaceType.DESTINATION) {
            isSelectingStart = false;
            isSelectingEnd = false;
//            mapInstruction.setVisibility(View.VISIBLE);
            latlongEnd = new LatLng(bean.placelat.doubleValue(), bean.placelon.doubleValue());
            iv_pin_source.setVisibility(View.GONE);
            sourceMarker = googleMap.addMarker(new MarkerOptions().position(latlongStart).title("Source").icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin_red)));
            markerMap.put(sourceMarker, 0);
            updateEnd();
            favEndStar.getDrawable().setColorFilter(activity.getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
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
            favStartStar.getDrawable().setColorFilter(activity.getResources().getColor(R.color.darkGreen), PorterDuff.Mode.SRC_ATOP);
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

    int ZOOM = 15;

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

    private void updateRouteOption() {
        if (path != null) {
            lineOptions = new PolylineOptions();
            lineOptions.addAll(path);
            lineOptions.width(12);
            lineOptions.color(getResources().getColor(R.color.selected_path_color));
            Polyline polyline = googleMap.addPolyline(lineOptions);
        }
//        DownloadTask downloadTask = new DownloadTask();
//        downloadTask.execute(getDirectionsUrl(latlongStart, latlongEnd));
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
        // url = "http://maps.googleapis.com/maps/api/directions/json?origin=46.839382,-100.771373&destination=46.791115,-100.763650&units=imperial&alternatives=true&sensor=false";
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
                                if (i == 0) {
                                    lineOptions.color(getResources().getColor(R.color.selected_path_color));
                                } else {
                                    lineOptions.color(getResources().getColor(R.color.deselected_path_color));
                                }
                                lineOptionsList.add(lineOptions);
                            }

                            // Drawing polyline in the Google Map for the i-th route.
                            if (googleMap != null) {
                                pathPolygonList.clear();
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
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        if (isChangeEnable) {
            isPathSelected = true;
            pathData = generateJsonForPath(polyline.getPoints());
//            pathList = generateJsonForPathNew(polyline.getPoints());
            int selectedPosition = pathPolygonList.indexOf(polyline);
            lineOptions = new PolylineOptions();
            lineOptions.addAll(polyline.getPoints());
            lineOptions.width(12);
            lineOptions.color(getResources().getColor(R.color.selected_path_color));

            polyline.remove();
            pathPolygonList.remove(selectedPosition);
            Polyline polyline1 = googleMap.addPolyline(lineOptions);
            polyline1.setClickable(true);

            pathPolygonList.add(selectedPosition, polyline1);
            for (int i = 0; i < pathPolygonList.size(); i++) {
                if (i != selectedPosition) {
                    pathPolygonList.get(i).setColor(activity.getResources().getColor(R.color.deselected_path_color));
                }
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

//    public Vector<Object> generateJsonForPathNew(List<LatLng> latLngList) {
//        Log.e("latLngList", latLngList.toString());
//        JsonArray jsonArray = new JsonArray();
//        Vector<Object> savedCardList = new Vector<>();
//        for (LatLng latLng : latLngList) {
//            JsonObject jsonObject = new JsonObject();
//            try {
//                jsonObject.addProperty(Const.LAT, latLng.latitude);
//                jsonObject.addProperty(Const.LONG, latLng.longitude);
//                savedCardList.add(jsonObject);
////                jsonArray.add(jsonObject);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return savedCardList;
//    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (this.googleMap != null) {
            this.googleMap.clear();
            this.googleMap.setOnPolylineClickListener(PendingOfferFragment.this);
            this.googleMap.setOnCameraChangeListener(PendingOfferFragment.this);
//            this.googleMap.setOnMarkerClickListener(PendingOfferFragment.this);
            updateStartOnMapReady();
            updateMap();
            adjustMap();
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
                source = address;
                latlongStart = place.getLatLng();
                updateMap();
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
                destination = address;
                latlongEnd = place.getLatLng();
                updateMap();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean validate() {
        jsonObject = new JsonObject();

        Calendar calendar = Calendar.getInstance();
        liftDate = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE);
        liftTime = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);

//        if (isChangeEnable && !isPathSelected) {
//            Helper.showSnackBar(linearParent, "Please select one path");
//            return false;
//        }
        source = startAutocompleteFragment.editSearch.getText().toString().trim();
        destination = endAutocompleteFragment.editSearch.getText().toString().trim();

        userId = Const.getUserId(activity);

        isPending = "No";
        jsonObject.addProperty(Const.LIFT_ID, liftId);
        jsonObject.addProperty(Const.VEHICLE_ID, vehicleId);
        jsonObject.addProperty(Const.USERID, userId);
        jsonObject.addProperty(Const.SOURCE, source);
        jsonObject.addProperty(Const.DESTINATION, destination);
        jsonObject.addProperty(Const.SOURCE_LAT, source_lat);
        jsonObject.addProperty(Const.SOURCE_LONG, source_long);
        jsonObject.addProperty(Const.DESTINATION_LAT_API_KEY, desti_lat);
        jsonObject.addProperty(Const.DESTINATION_LNG_API_KET, desti_long);
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
        new Thread(new Runnable() {
            public void run() {
                DbAdapter.getInstance(activity).setPath(pathData);
//                DbAdapter.getInstance(activity).setPath(pathList);
            }
        }).start();
        return true;
    }

    private void networkHit() {

        if (Helper.isConnected(activity)) {
            progress.show();
            Log.e("json", jsonObject.toString());
            futureIonHit = Ion.with(activity).load(API.API_ADD_LIFT).setTimeout(45 * 1000).setJsonObjectBody(jsonObject).asString().setCallback(new FutureCallback<String>() {

                                                                                                                                                    @Override
                                                                                                                                                    public void onCompleted(Exception e, String jsonString) {
                                                                                                                                                        progress.hide();
                                                                                                                                                        if (e == null) {
                                                                                                                                                            if (jsonString != null && !jsonString.isEmpty()) {
                                                                                                                                                                try {
                                                                                                                                                                    Log.e("json", jsonString);

                                                                                                                                                                    JSONObject jsonObject = new JSONObject(jsonString);
                                                                                                                                                                    if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                                                                                                                                                        HomeActivity.isShareLocation = true;
                                                                                                                                                                        sharedPreference.putBoolean("shareLocation", true);
                                                                                                                                                                        sharedPreference.putBoolean(Const.IS_RIDE_ACTIVE, true);
                                                                                                                                                                        sharedPreference.putString(Const.LIFT_ID, jsonObject.optString(Const.RESULT));
                                                                                                                                                                        sharedPreference.putString("LIFT_ID", jsonObject.optString(Const.RESULT));
                                                                                                                                                                        sharedPreference.putLong("autoCancelLocationUpdateTime", System.currentTimeMillis());
                                                                                                                                                                        sharedPreference.putString(Const.IS_OFFERER, "1");
                                                                                                                                                                        //((HomeActivity) activity).autoCancelLocationUpdateIfNoLiftRequest();
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
                                                                                                                                                                //Helper.showSnackBar(linearParent, Const.POOR_INTERNET);
                                                                                                                                                                networkHitRetry(Const.POOR_INTERNET);
                                                                                                                                                                isPending = "";
                                                                                                                                                            }
                                                                                                                                                        } else {
                                                                                                                                                            e.printStackTrace();
                                                                                                                                                            //Helper.showSnackBar(linearParent, /*e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
                                                                                                                                                            networkHitRetry(Const.POOR_INTERNET);
                                                                                                                                                        }
                                                                                                                                                    }
                                                                                                                                                }

            );
        } else {
            //Helper.showSnackBar(linearParent, Const.NO_INTERNET);
            networkHitRetry(Const.NO_INTERNET);
        }
    }

    private void networkHitpending() {

        if (Helper.isConnected(activity)) {
            progress.show();
            Log.e("json", jsonObject.toString());
            futureIonHit = Ion.with(activity).load(API.API_SEND_REQUEST_PENDING)
                    .setTimeout(45 * 1000).setJsonObjectBody(jsonObject).asString()
                    .setCallback(new FutureCallback<String>() {
                                     @Override
                                     public void onCompleted(Exception e, String jsonString) {
                                         progress.hide();
                                         if (e == null) {
                                             if (jsonString != null && !jsonString.isEmpty()) {
                                                 try {
                                                     Log.e("json", jsonString);

                                                     JSONObject jsonObject = new JSONObject(jsonString);
                                                     if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                                         HomeActivity.isShareLocation = true;
                                                         sharedPreference.putBoolean("shareLocation", true);
                                                         sharedPreference.putBoolean(Const.IS_RIDE_ACTIVE, true);
                                                         sharedPreference.putString(Const.LIFT_ID, jsonObject.optJSONObject("pushMessage").optString(Const.LIFT_ID));
                                                         sharedPreference.putString("LIFT_ID", jsonObject.optJSONObject("pushMessage").optString(Const.LIFT_ID));
                                                         sharedPreference.putLong("autoCancelLocationUpdateTime", System.currentTimeMillis());
                                                         sharedPreference.putString(Const.IS_OFFERER, "1");
                                                         //((HomeActivity) activity).autoCancelLocationUpdateIfNoLiftRequest();
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
                                                 //Helper.showSnackBar(linearParent, Const.POOR_INTERNET);
                                                 networkHitRetry(Const.POOR_INTERNET);
                                                 isPending = "";
                                             }
                                         } else {
                                             e.printStackTrace();
                                             //Helper.showSnackBar(linearParent, /*e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
                                             networkHitRetry(Const.POOR_INTERNET);
                                         }
                                     }
                                 }
                    );
        } else {
            //Helper.showSnackBar(linearParent, Const.NO_INTERNET);
            networkHitRetry(Const.NO_INTERNET);
        }
    }

    public void rideAlertDialog() {
        LayoutInflater inflater1 = getLayoutInflater(null);
        View alertLayout = inflater1.inflate(R.layout.ride_now, null);
        ImageView cancel = (ImageView) alertLayout.findViewById(R.id.cancel);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
        builder.setView(alertLayout);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ((HomeActivity) activity).gotoTrackerFragment();
            }
        });
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
    }*/

    private boolean validateRequest() {
        jsonObject = new JsonObject();
        String requesterId = "";
        DbAdapter dbAdapter = DbAdapter.getInstance(activity);
        Cursor cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_PROFILE);
        for (int i = 0; i < cursor.getCount(); i++) {
            requesterId = cursor.getString(cursor.getColumnIndex(Const.USERID));
            cursor.moveToNext();
        }
        SharedPreference sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
        if (requesterId.equalsIgnoreCase("")) {
            requesterId = sharedPreference.getString(Const.USERID, "");
        }
        jsonObject.addProperty(Const.LIFT_ID, liftId);
        jsonObject.addProperty(Const.OFFERER_ID, offererId);
        jsonObject.addProperty(Const.REQUESTER_ID, requesterId);
        jsonObject.addProperty(Const.ACTION, "");
        jsonObject.addProperty(Const.NUMBER_OF_SEATS, numberOfSeats);
        jsonObject.addProperty(Const.PICKUP_POINT, source_lat + "," + source_long);
        jsonObject.addProperty(Const.DROP_POINT, desti_lat + "," + desti_long);
        jsonObject.addProperty(Const.SOURCE, source);
        jsonObject.addProperty(Const.DESTINATION, destination);
        jsonObject.addProperty("payBy", SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_WALLET).getString("payBy", "0"));

        return true;
    }

    private void networkHitReRequest() {
        if (Helper.isConnected(activity)) {
            progress.show();
            Log.e("json", jsonObject.toString());
            futureIonHit = Ion.with(activity).load(API.ADD_LIFT_REQUESTED).setTimeout(45 * 1000).setJsonObjectBody(jsonObject).asString().setCallback(new FutureCallback<String>() {

                @Override
                public void onCompleted(Exception e, String jsonString) {
                    progress.hide();
                    if (e == null) {
                        if (jsonString != null && !jsonString.isEmpty()) {
                            try {
                                Log.e("json", jsonString);

                                JSONObject jsonObject = new JSONObject(jsonString);
                                if (jsonObject.optBoolean(Const.IS_SUCCESS)) {
                                    SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).putString(Const.IS_OFFERER, "");
                                    Helper.showSnackBar(linearParent, "Request sent successfully.");
                                } else {
                                    Helper.showSnackBar(linearParent, jsonObject.optString(Const.MESSAGE));
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                //Helper.showSnackBar(linearParent, Const.INTERNAL_ERROR);
                                networkHitReRequestRetry(Const.INTERNAL_ERROR);
                            }
                        } else {
                            //Helper.showSnackBar(linearParent, Const.POOR_INTERNET);
                            networkHitReRequestRetry(Const.POOR_INTERNET);
                        }
                    } else {
                        e.printStackTrace();
                        //Helper.showSnackBar(linearParent, /*e.getMessage() + "\n" +*/ Const.POOR_INTERNET);
                        networkHitReRequestRetry(Const.POOR_INTERNET);
                    }
                }
            });
        } else {
            //Helper.showSnackBar(linearParent, Const.NO_INTERNET);
            networkHitReRequestRetry(Const.NO_INTERNET);
        }
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
            if (iStream != null) {
                iStream.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
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
//                            pathList = generateJsonForPathNew(polyline.getPoints());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
//            if (result != null && getActivity() != null) {
//                ArrayList<LatLng> points = null;
//                ArrayList<PolylineOptions> lineOptionsList = new ArrayList<>();
//                // Traversing through all the routes
//                for (int i = 0; i < result.size(); i++) {
//                    points = new ArrayList<LatLng>();
//                    lineOptions = new PolylineOptions();
//                    // Fetching i-th route
//                    List<HashMap<String, String>> path = result.get(i);
//                    // Fetching all the points in i-th route
//                    for (int j = 0; j < path.size(); j++) {
//                        HashMap<String, String> point = path.get(j);
//                        double lat = Double.parseDouble(point.get("lat"));
//                        double lng = Double.parseDouble(point.get("lng"));
//                        LatLng position = new LatLng(lat, lng);
//                        points.add(position);
//                    }
//                    // Adding all the points in the route to LineOptions
//                    lineOptions.addAll(points);
//                    lineOptions.width(12);
//                    lineOptions.color(getResources().getColor(R.color.deselected_path_color));
////                    lineOptions.color(Color.parseColor("#21b5da"));
//                    lineOptionsList.add(lineOptions);
//                }
//                // Drawing polyline in the Google Map for the i-th route.
//                if (googleMap != null) {
//                    for (int i = 0; i < pathPolygonList.size(); i++) {
//                        pathPolygonList.get(i).remove();
//                    }
//                    pathPolygonList.clear();
//                    for (int i = 0; i < lineOptionsList.size(); i++) {
//                        Polyline polyline = googleMap.addPolyline(lineOptionsList.get(i));
//                        polyline.setClickable(true);
//                        pathPolygonList.add(polyline);
//                    }
//                    if(isFirstTime) {
//                        PolylineOptions option = new PolylineOptions();
//                        option.addAll(path);
//                        option.width(12);
//                        option.color(getResources().getColor(R.color.selected_path_color));
//                        Polyline polyline = googleMap.addPolyline(option);
//                        polyline.setClickable(true);
//                        pathPolygonList.add(polyline);
//
//                        pathData = generateJsonForPath(path);
//                        isPathSelected = true;
//                        isFirstTime = false;
//                    }
//                }
//            }
//        }
//    }
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
//                    networkHit();
                    networkHitpending();
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

    private void networkHitReRequestRetry(String message) {
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
                    networkHitReRequest();
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
