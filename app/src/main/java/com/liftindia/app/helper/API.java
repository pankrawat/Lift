package com.liftindia.app.helper;

/**
 * Created by Abhishek Singh Arya on 11-04-2016.
 */

public class API {
    //    public static String LINKEDIN_CONSUMER_KEY = "75nqw3p7y13vgl";
//    public static String LINKEDIN_CONSUMER_KEY = "81a1au1wgplsof"; // main
    //    public static String LINKEDIN_CONSUMER_SECRET = "Zp6RjMQVTgShcles";
//    public static String LINKEDIN_CONSUMER_SECRET = "LeDJfA9gmVyPhQva";// main
    public static String LINKEDIN_CONSUMER_KEY = "81w9pkcqk003li";
    public static String LINKEDIN_CONSUMER_SECRET = "JIoY3OsbtBeTRa5m";

    public static String OAUTH_CALLBACK_URL = "http://www.appsquadz.com/accept";

    /*---------------LIFT INDIA API CONSTANTS---------------*/

    //Previous Working URL
    public static final String BASE = "http://liftindia.co/liftIndia/";

    //    public static final String BASE_URL = BASE + "liftindia/index.php/";
//    public static final String BASE_URL = BASE + "index.php/";
    public static final String BASE_URL = "http://dev.appsquadz.com/liftIndia/index.php/";

    public static final String API_LOGIN = BASE_URL + "login";

    public static final String API_USER_LOGIN_NEW = BASE_URL + "userLoginLatest1";//userLoginNewFlow

    public static final String API_PASSWORD_RESET = BASE_URL + "forgetPassword";

    public static final String API_CHANGE_PASSWORD = BASE_URL + "changePassword";

    public static final String API_CHECK_MOBILE = BASE_URL + "isPhoneExists";

    public static final String API_USER_PROFILE = BASE_URL + "userProfileNew";

    public static final String API_EDIT_PROFILE = BASE_URL + "editProfile";

    public static final String API_VERIFY = BASE_URL + "verifyOTP";

    public static final String API_ADD_VEHICLE = BASE_URL + "addVehicle";

    public static final String API_DELETE_VEHICLE = BASE_URL + "deleteVehicle";

    //    public static final String API_VEHICLE_DETAILS = BASE_URL + "getVehicleByMaster";
    public static final String API_VEHICLE_DETAILS = BASE_URL + "getVehicleByMasterNew";

    public static final String API_ADD_LIFT = BASE_URL + "addLiftNew";

    public static final String API_UPDATE_LATLNG = BASE_URL + "updateLatLong";

    public static final String API_SEARCH_LIFT = BASE_URL + "SearchLiftByRequest";

    public static final String ADD_LIFT_REQUESTED = BASE_URL + "addLiftRequested";

    public static final String API_SEND_REQUEST_PENDING = BASE_URL + "addLiftRequested_pending";

    public static final String API_UPDATE_DISTANCE = BASE_URL + "updateDistance";

    public static final String API_GET_DISTANCE = BASE_URL + "getDistancebyRequester";

    public static final String API_ACTION_ACCEPT = BASE_URL + "addLiftRequestedActionAccept";

    public static final String API_ALL_REQUESTED_LIFT = BASE_URL + "getAllRequestedYourLiftNew";

    public static final String DELETE_PENDING_LIFT = BASE_URL + "deletePendingLift";

    public static final String API_HISTORY = BASE_URL + "liftsHistory";

    public static final String API_PENDING_RIDE = BASE_URL + "getAllLiftsByUserId1";

    public static final String API_GET_FAV = BASE_URL + "getFavPlace";

    public static final String API_MAKE_FAV = BASE_URL + "addFavPlace";

    public static final String API_DELETE_FAV = BASE_URL + "deleteFavPlace";

    public static final String API_OFFERER_END_RIDE = BASE_URL + "endLiftByOfferer";

    public static final String API_GET_WALLET_DETAILS = BASE_URL + "getWalletDetailsByUserId";

    public static final String API_UPDATE_WALLET_DETAILS = BASE_URL + "updateWalletDetailsByUserId";

    public static final String API_RATE_USER = BASE_URL + "insertRating";

    public static final String API_HELP = BASE_URL + "help";

    public static final String API_REFER_AMOUNT = BASE_URL + "getReferringAmount";

    public static final String API_LOGOUT = BASE_URL + "logout";

    public static final String API_GET_CURRENT_BILLING_DETAILS = BASE_URL + "end_lift_billing_details";

    public static final String API_GET_PATH_BETWEEN_TWO_POINTS = BASE_URL + "get_intersect_path";

    /*---------------PAYTM API CONSTANTS---------------*/
    public static final String BASE_URL_PAYTM = BASE + "liftToIndia/PaytmKit/";

    public static final String API_GRATIFICATION = BASE + "liftIndia/PaytmKit/Gratification.php";

    public static final String API_GENERATE_CHECKSUM = BASE_URL_PAYTM + "generateChecksum.php";

    public static final String API_VERIFY_CHECKSUM = BASE_URL_PAYTM + "verifyChecksum.php";

    /*---------------MOBIKWIK API CONSTANTS---------------*/

    public static final String MOBIKWIK_MERCHANT_SECRET_KEY = "DAjAyQRMRWPuospQcS5hSec6rvx0";

    public static final String MOBIKWIK_MERCHANT_ID = "MBK4668";

    public static final String MOBIKWIK_MERCHANT_NAME = "TestMerchant";

    public static final String BASE_URL_MOBIKWIK = "https://walletapi.mobikwik.com/";

//    public static final String BASE_URL_MOBIKWIK = "https://test.mobikwik.com/";

    public static final String API_CHECK_CELL = BASE_URL_MOBIKWIK + "querywallet";

    public static final String API_GENERATE_OTP = BASE_URL_MOBIKWIK + "otpgenerate";

    public static final String API_GENERATE_TOKEN = BASE_URL_MOBIKWIK + "tokengenerate";

    public static final String TOPUP_MONEY_WALLET = BASE_URL_MOBIKWIK + "topupmoneytowallet";

    public static final String API_FETCH_CARD = BASE_URL_MOBIKWIK + "storedcarddata";

    public static final String ENCRYPTION_KEY_URL = "https://api.zaakpay.com/zaakpay.js";

    public static final String API_USER_BALANCE = BASE_URL_MOBIKWIK + "userbalance";

    public static final String ADD_MONEY_TO_WALLET = BASE_URL_MOBIKWIK + "addmoneytowallet";

}
