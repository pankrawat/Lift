package com.liftindia.app.helper;

/**
 * Created by appsquadz on 22/6/16.
 */
public class AppTheme {
    public String colorPrimary;
    public String colorPrimaryDark;
    public String colorAccent = "#ffffff";

    public static AppTheme appTheme;

    public static AppTheme getInstance(){
        if(appTheme == null){
            appTheme = new AppTheme();
        }
        return appTheme;
    }
    private void setAppTheme(int themeCode){
        switch (themeCode){
            case 1:
                colorPrimary = "#f44336";
                colorPrimaryDark = "#e03327";
                break;
            case 2:
                colorPrimary = "#e91363";
                colorPrimaryDark = "#bf1555";
                break;
            case 3:
                colorPrimary = "#9c27b0";
                colorPrimaryDark = "#7e0f91";
                break;
            case 4:
                colorPrimary = "#673ab7";
                colorPrimaryDark = "#52279e";
                break;
            case 5:
                colorPrimary = "#3f51b5";
                colorPrimaryDark = "#2336a0";
                break;
            case 6:
                colorPrimary = "#2196f3";
                colorPrimaryDark = "#197ac7";
                break;
            case 7:
                colorPrimary = "#03a9f4";
                colorPrimaryDark = "#008fd0";
                break;
            case 8:
                colorPrimary = "#00bcd4";
                colorPrimaryDark = "#0099ad";
                break;
            case 9:
                colorPrimary = "#009688";
                colorPrimaryDark = "#006a60";
                break;
            case 10:
                colorPrimary = "#4caf50";
                colorPrimaryDark = "#338d37";
                break;
            case 11:
                colorPrimary = "#ff9800";
                colorPrimaryDark = "#d37e00";
                break;
            case 12:
                colorPrimary = "#ff5722";
                colorPrimaryDark = "#e4420f";
                break;
            case 13:
                colorPrimary = "#795548";
                colorPrimaryDark = "#624236";
                break;
            case 14:
                colorPrimary = "#607d8b";
                colorPrimaryDark = "#4a6471";
                break;
            case 15:
                colorPrimary = "#252525";
                colorPrimaryDark = "#000000";
                break;
            case 16:
                colorPrimary = "#ffc107";
                colorPrimaryDark = "#d4a005";
                break;

        }
    }
}
