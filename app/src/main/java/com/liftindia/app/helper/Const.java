package com.liftindia.app.helper;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by Abhishek Singh Arya on 11-09-2015.
 */

public class Const {
//    public static String LINKEDIN_CONSUMER_KEY = "75nqw3p7y13vgl";
//    public static String LINKEDIN_CONSUMER_SECRET = "Zp6RjMQVTgShcles";
//    public static String OAUTH_CALLBACK_URL = "http://www.appsquadz.com/accept";
//
//    /*---------------API CONSTANTS---------------*/
//    public static final String BASE = "http://ec2-52-205-20-98.compute-1.amazonaws.com/";
//
//    public static final String BASE_URL = BASE + "liftIndia/index.php/";
//
//    public static final String API_LOGIN = BASE_URL + "login";
//
//    public static final String API_USER_LOGIN_NEW = BASE_URL + "userLoginLatest1";//userLoginNewFlow
//
//    public static final String API_PASSWORD_RESET = BASE_URL + "forgetPassword";
//
//    public static final String API_CHANGE_PASSWORD = BASE_URL + "changePassword";
//
//    public static final String API_USER_PROFILE = BASE_URL + "userProfile";
//
//    public static final String API_EDIT_PROFILE = BASE_URL + "editProfile";
//
//    public static final String API_VERIFY = BASE_URL + "verifyOTP";
//
//    public static final String API_ADD_VEHICLE = BASE_URL + "addVehicle";
//
//    public static final String API_DELETE_VEHICLE = BASE_URL + "deleteVehicle";
//
//    public static final String API_VEHICLE_DETAILS = BASE_URL + "getVehicleByMaster";
//
//    public static final String API_ADD_LIFT = BASE_URL + "addLift";
//
//    public static final String API_UPDATE_LATLNG = BASE_URL + "updateLatLong";
//
//    public static final String API_SEARCH_LIFT = BASE_URL + "SearchLiftByRequest";
//
//    public static final String ADD_LIFT_REQUESTED = BASE_URL + "addLiftRequested";
//
//    public static final String API_UPDATE_DISTANCE = BASE_URL + "updateDistance";
//
//    public static final String API_GET_DISTANCE = BASE_URL + "getDistancebyRequester";
//
//    public static final String API_ACTION_ACCEPT = BASE_URL + "addLiftRequestedActionAccept";
//
//    public static final String API_ALL_REQUESTED_LIFT = BASE_URL + "getAllRequestedYourLiftNew";
//
//    public static final String DELETE_PENDING_LIFT = BASE_URL + "deletePendingLift";
//
//    public static final String API_HISTORY = BASE_URL + "liftsHistory";
//
//    public static final String API_PENDING_RIDE = BASE_URL + "getAllLiftsByUserId";
//
//    public static final String API_GET_FAV = BASE_URL + "getFavPlace";
//
//    public static final String API_MAKE_FAV = BASE_URL + "addFavPlace";
//
//    public static final String API_DELETE_FAV = BASE_URL + "deleteFavPlace";
//
//    public static final String API_OFFERER_END_RIDE = BASE_URL + "endLiftByOfferer";
//
//    public static final String API_GET_PAYTM_DETAILS = BASE_URL + "getPaytmDetailsByUserId";
//
//    public static final String API_UPDATE_PAYTM_DETAILS = BASE_URL + "updatePaytmDetailsByUserId";
//
//    public static final String API_RATE_USER = BASE_URL + "insertRating";
//
//    public static final String API_HELP = BASE_URL + "help";
//
//    public static final String BASE_URL_PAYTM = BASE + "liftToIndia/PaytmKit/";
//
//    public static final String API_GRATIFICATION = BASE + "liftIndia/PaytmKit/Gratification.php";
//
//    public static final String API_GENERATE_CHECKSUM = BASE_URL_PAYTM + "generateChecksum.php";
//
//    public static final String API_VERIFY_CHECKSUM = BASE_URL_PAYTM + "verifyChecksum.php";


    /*---------------API CONSTANTS---------------*/
    public static final String EMAIL = "email";
    public static final String NAME = "name";
    public static final String FB_ID = "fbId";
    public static final String LN_ID = "lnId";
    public static final String G_ID = "gId";
    public static final String IMAGE = "image";
    public static final String GENDER = "gender";
    public static final String AGE = "age";
    public static final String LOGIN_TYPE = "loginType";
    public static final String BALANCE = "balance";

    public static final String PHONE = "phone";
    public static final String PHONE_EM = "phoneEm";
    public static final String USERID = "userId";
    public static final String FK_USERID = "fk_userId";
    public static final String VEHICLE_ID = "vehicleId";
    public static final String MOBILE = "mobile";
    public static final String MOBILE_EM = "mobileEm";
    public static final String PASSWORD = "password";
    public static final String ID_TYPE = "idType";
    public static final String ID_NUMBER = "idNumber";
    public static final String DOB = "dob";
    public static final String PROFILE_IMAGE = "profileImage";
    public static final String ID_IMAGE = "idImage";
    public static final String IMAGE_TYPE = "imageType";
    public static final String COMPANY = "company";
    public static final String POST = "post";
    public static final String DESIGNATION = "designation";
    public static final String CAR_NAME = "carName";
    public static final String CAR_NUMBER = "carNumber";
    public static final String CAR_ID = "carId";
    public static final String CAR_DETAILS = "carDetails";
    public static final String SMOKING = "smoking";
    public static final String MUSIC = "music";
    public static final String CONNECTIONS = "connections";
    public static final String FB_FRIENDS = "fbFriends";
    public static final String REVIEWS = "reviews";
    public static final String ETA = "eta";
    public static final String FRND_REFERRAL_CODE = "frndReferCode";
    public static final String REFERRAL_CODE = "referCode";

    public static final String TYPE = "type";
    public static final String OWNERSHIP_TYPE = "ownershipType";
    public static final String PERMIT = "permit";
    public static final String VEHICLE_TYPE = "vehicleType";
    public static final String VEHICLE_STATUS = "carStatus";
    public static final String VEHICLE_NUMBER = "vehicleNo";
    public static final String BRAND = "brand";
    public static final String MODEL = "model";
    public static final String RC_NUMBER = "rcNo";
    public static final String DL_NUMBER = "dlNo";
    public static final String VEHICLE_IMAGE = "vehicleImage";
    public static final String PERMIT_IMAGE = "permitImage";
    public static final String RC_IMAGE = "rcImage";
    public static final String DL_IMAGE = "dlImage";
    public static final String Image_Name = "uploadNgetImageName";

    public static final String DEVICE_TOKEN = "deviceToken";
    public static final String DEVICE_TYPE = "deviceType";

    public static final String OTP = "otp";

    public static final String SOURCE = "source";
    public static final String DESTINATION = "destination";
    public static final String LIFT_DATE = "liftDate";
    public static final String LIFT_TIME = "liftTime";
    public static final String PRICE = "price";
    public static final String NUMBER_OF_SEATS = "numberOfSeats";
    public static final String PAY_BY = "payBy";
    public static final String RECURRING_DAYS = "recurringDays";
    public static final String LOCATION_SEARCH_PARAMETER = "location_search_parameter_lift_india";
    public static final String IS_PENDING = "isPending";
    public static final String PATH = "path";
    public static final String REQUESTER_PATH = "requestPath";

    public static final String SOURCE_LAT = "sourceLat";
    public static final String SOURCE_LONG = "sourceLong";
    /*public static final String DESTINATION_LATI = "destinationLat";
    public static final String DESTINATION_LNG = "destinationLong";*/
    public static final String DESTINATION_LAT_API_KEY = "destinationLat";
        public static final String DESTINATION_LNG_API_KET = "destinationLong";
    public static final String ACTION_PUSH = "action_push";


    public static final String REQUEST_DIALOG_SHOW = "r2o_dialog";
    public static final String CONFIRM_DIALOG_SHOW = "o2r_dialog";

    public static final String LIFT_ID = "liftId";
    public static final String OFFERER_ID = "offererId";
    public static final String REQUESTER_ID = "requesterId";
    public static final String ACTION = "action";
    public static final String PICKUP_POINT = "pickupPoint";
    public static final String PICK_POINTS = "pickPoints";
    public static final String DROP_POINT = "dropPoint";

    public static final String REVIEW = "review";
    public static final String RATING = "rating";
    public static final String RATE = "rate";
    public static final String TIME = "time";

    public static final String TO_ID = "toId";
    public static final String FROM_ID = "fromId";
    public static final String RATING_COUNT = "ratingCount";
    public static final String REVIEW_COMMENTS = "reviewComments";
    public static final String COMMENTS = "comments";

    public static final String LIFT_STATUS = "liftStatus";
    public static final String START_DATE = "startDate";
    public static final String START_TIME = "startTime";
    public static final String END_DATE = "endDate";
    public static final String END_TIME = "endTime";
    public static final String AMOUNT = "amount";
    public static final String DISTANCE = "distance";
    public static final String TIME_TAKEN = "timeTaken";
    public static final String TOTAL_REVIEWS = "totalReviews";
    public static final String COMMON_ROUTE = "commonRoute";
    public static final String EXPERIENCE = "experience";
    public static final String LAST_RIDE = "lastRide";
    public static final String IMAGE_URL = "imageUrl";
    public static final String MESSAGE = "message";
    public static final String LIFT_REQUEST_ID = "lift_request_id";


    public static final String OFFERED_CAR_NAME = "offered_car_name";
    public static final String OFFERED_CAR_NUMBER = "offered_car_number";
    public static final String SOURCE_NAME = "source_name";
    public static final String DESTINATION_NAME = "destination_name";
    public static final String DESTINATION_LATI = "destination_lat";
    public static final String DESTINATION_LNG = "destination_lng";

    public static final String IS_VEHICLE_ADDED = "isVehicleAdded";
    public static final String IS_RIDE_ACTIVE = "isRideActive";
    public static final String IS_RATING_PENDING = "isRatingPending";
    public static final String IS_RATING_PENDING_PROGILE = "is_rating_pending";

    public static final String IS_REQUESTER = "isRequester";

    public static final String IS_OFFERER = "isOfferer";
    public static final String IS_RECURRING = "isRecurring";
    public static final String IS_SUCCESS = "isSuccess";
    public static final String IS_WALLET = "isWallet";
    public static final String IS_MOBIKWIK = "isPaytm";
    public static final String IS_ENDED = "isEnded";
    public static final String IS_RATED = "isRated";

    public static final String IS_PHONE_VERIFIED = "isPhoneVerified";
    public static final String IS_USER_VERIFIED = "userStatus";
    public static final String IS_ONLY_REQUESTER = "isOnlyRequester";
    public static final String TO_SHOW_SPLASH = "toShowSplash";

    public static final String PRIVATE = "private";
    public static final String COMMERCIAL = "Commercial";
    public static final String RESULT = "Result";

    public static final String LAT = "lat";

    public static final String LONG = "long";


    public static final String WHEELER4 = "4-Wheeler";
    public static final String WHEELER3 = "3-Wheeler";
    public static final String WHEELER2 = "2-Wheeler";

    public static final String NO_INTERNET = "Please check your internet connection.";
    public static final String POOR_INTERNET = "Something went wrong";
    public static final String RETRY = "Retry";

   /* public static final String POOR_INTERNET = "Some network error occurs.";*/

    public static final String TIME_OUT = "Connection Timeout.";

    public static final String INTERNAL_ERROR = /*Internal Server Error*/"Something went wrong";

    public static final String CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";

    public static final String INTERNET = "internet";

    public static final String LATITUDE = "latitude";

    public static final String LONGITUDE = "longitude";

    public static final String REFERRAL_AMOUNT = "referral_amount";

    public static final String ENABLE_INTERNET = "Internet is disabled in your device, Do you want to enable it?";

    public static final String ENABLE_GPS = "GPS is disabled in your device, Do you want to enable it?";

    public static final String ENABLE_HIGH_ACCURACY = "Please On your GPS in high accuracy mode.";

    public static final String SELECT_LOCATION_MARKER_CLICK = "Click on marker to choose Location.";

    public static final String SETTING = "Settings";

    public static final String EXIT = "Exit";

    public static final String GOTO = "goto";

    public static final int TYPE1 = 1;

    public static final int TYPE2 = 2;

    public static final int TYPE3 = 3;
    public static final int TYPE4 = 4;
    public static final int TYPE5 = 5;
    public static final int TYPE6 = 6;
    public static final int TYPE7 = 7;
    public static final int END_LIFT = 8;
    public static final int PAYMENT_DUE = 10;

    /*---------------JAWBONE CONSTANTS---------------*/
    public static final String CORRECT_OTP = "Please enter correct OTP.";

    public static final String OTP_SENT = "OTP sent on SMS successfully.";


    public static final String CORRECT_MOBILE = "Please check your mobile number.";

    public static final String SNACKBAR_TEXT_COLOR = "#FFFFFF";

    public static final int SNACKBAR_ACTION_COLOR = Color.parseColor("#017060");
    public static final int SNACKBAR_ACTION_TEXT_COLOR = Color.parseColor("#ffffff");

    public static final int SNACKBAR_ACTION_MARGIN = 50;

    public static final String ENTER_CAR_NUMBER = "Please enter your car number.";

    public static final String VALID_CAR_NUMBER = "Car number must be of 4 digits.";

    public static final String ENTER_MOBILE_NUMBER = "Please enter your mobile number.";

    public static final String VALID_MOBILE_NUMBER = "Mobile number must be of 10 digits.";

    public static final String ENTER_OTP = "Please enter OTP.";


    public static final String NO_LOCATION = "Unable to find your current location.";

    public static final String VEHICLE_NOT_VERIFIED = "Thanks for the details.\n" +
            "We will approve your vehicle after validation within 24 hours.\n" +
            "Will notify you by message as well notification on your registered Number.";

    public static final String VEHICLE_NOT_VERIFIED_MESSAGE = "Your Vehicles are not approved. We will approve your vehicle after validation within 24 hours.\n" +
            "Will notify you by message as well notification on your registered Number.";

    public static final String PROFILE_NOT_VERIFIED = "Thanks for the details.\n" +
            "We will approve your profile after validation within 24 hours.\n" +
            "Will notify you by message as well notification on your registered Number.";
    public static final String PROFILE_NOT_VERIFIED_MESSAGE = "Your profile is pending for approval.\n" +
            "We will approve your profile after validation within 24 hours.\n" +
            "Will notify you by message as well notification on your registered Number.";
    public static final String PROFILE_BLOCKED = "Your Account has been blocked by the Admin.\n" +
            "We will approve your profile after validation within 24 hours.\n" +
            "Will notify you by message as well notification on your registered Number.";
    public static final String PROFILE_REJECTED = "Your Account has been Rejected by the Admin. kindly update your details\n" +
            "We will approve your profile after validation within 24 hours.\n" +
            "Will notify you by message as well notification on your registered Number.";


    public static Bitmap bitmap;

    /*-------------REQUESTER SCREEN PATH CONSTANTS-------------*/
    public static final String PATH_OFFERER = "path_offerer";
    //public static final String PATH_OFFERER_LNG = "path_offerer_lng";
    public static final String PATH_REQUESTER = "path_requester";
    //public static final String PATH_REQUESTER_LNG ="path_requester_lng";
    public static final String PATH_MATCHED = "path_matched";
    //public static final String PATH_MATCHED_LNG = "path_matched_lng";


    /*---------------APP CONSTANTS---------------*/
    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final String PAN_PATTERN = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
    public static final String VOTER_ID_PATTERN = "[A-Z]{3}[0-9]{7}";
    public static final String ADHAAR_PATTERN = "[0-9]{12}";


    public static final String RC_NO_PATTERN = "^[A-Z]{2}[0-9]{1,2}(?:[A-Z])?(?:[A-Z]*)?[0-9]{4}$";
    //previous pattern
    // public static final String RC_NO_PATTERN = "[a-zA-Z]{2}[0-9]{2}[a-zA-Z]{1,2}[0-9]{4}";


    public static final String PROFILE_COMPLETED = "profile_completed";
    public static final String VEHICLE_COMPLETED = "vehicle_completed";
    public static final boolean COMPLETE = true;
    public static final boolean INCOMPLETE = false;


    public static final int PICK_GALLERY = 901;
    public static final int PICK_CAMERA = 902;
    public static final int CROP_PICTURE = 903;
    public static final int IMAGE_CAPTURE = 904;


    public static String userId = "";
    public static final String isRequesterPathSaved = "isRequesterPathSaved";
    public static final String isRequsterOffererPathSaved = "isRequesterOffererPathSaved";
    public static final String isRequsterMatchedPathSaved = "isRequsterMatchedPathSaved";

    public static final String CONFIRM_DIALOG_LIFT_ID = "confirm_dialog_lift_id";
    public static final String CONFIRM_DIALOG_OFFERER_ID = "confirm_dialog_offerer_id";
    public static final String CONFIRM_DIALOG_REQUESTER_ID = "confirm_dialog_requester_id";
    public static final String CONFIRM_DIALOG_ACTION = "confirm_dialog_action";
    public static final String CONFIRM_DIALOG_NUM_OF_SEATS = "confirm_dialog_num_of_seats";
    public static final String CONFIRM_DIALOG_LATITUDE = "confirm_dialog_latitude";
    public static final String CONFIRM_DIALOG_LONGITUDE = "confirm_dialog_longitude";

    public static final String NOTIFICATION_COUNTER = "0";
    public static final String NOTIFICATION_MSG="";
    public static final String NOTIFICATION_TYPE = "";


    public static String getUserId(Context activity) {
        if (userId.equalsIgnoreCase("")) {
            DbAdapter dbAdapter = DbAdapter.getInstance(activity);
            Cursor cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_PROFILE);
            for (int i = 0; i < cursor.getCount(); i++) {
                userId = cursor.getString(cursor.getColumnIndex(Const.USERID));
                cursor.moveToNext();
            }
            if (userId.equalsIgnoreCase("")) {
                SharedPreference sharedPreference = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL);
                userId = sharedPreference.getString(Const.USERID, "");
            }
        }
        return userId;
    }


}
