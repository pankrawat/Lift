package com.liftindia.app.caldroid;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.liftindia.app.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;


/**
 * The CaldroidGridAdapter provides customized view for the dates gridview
 *
 * @author thomasdao
 */
public class CaldroidGridAdapter extends BaseAdapter {
    protected ArrayList<DateTime> datetimeList;
    protected int month;
    protected int year;
    protected Context context;
    protected ArrayList<DateTime> disableDates;
    protected ArrayList<DateTime> selectedDates;

    // Use internally, to make the search for date faster instead of using
    // indexOf methods on ArrayList
    protected HashMap<DateTime, Integer> disableDatesMap = new HashMap<DateTime, Integer>();
    protected HashMap<DateTime, Integer> selectedDatesMap = new HashMap<DateTime, Integer>();

    protected DateTime minDateTime;
    protected DateTime maxDateTime;
    protected DateTime today;
    protected int startDayOfWeek;
    protected boolean sixWeeksInCalendar;
    protected Resources resources;
    private int disableTextColr;
    private int notAvailbleTextColr;
    private boolean isSelectionEnable = true;

    /**
     * caldroidData belongs to Caldroid
     */
    protected HashMap<String, Object> caldroidData;
    /**
     * extraData belongs to client
     */
    protected HashMap<String, Object> extraData;

    public void setAdapterDateTime(DateTime dateTime) {
        this.month = dateTime.getMonth();
        this.year = dateTime.getYear();
        this.datetimeList = CalendarHelper.getFullWeeks(this.month, this.year,
                startDayOfWeek, sixWeeksInCalendar);
    }

    // GETTERS AND SETTERS
    public ArrayList<DateTime> getDatetimeList() {
        return datetimeList;
    }

    public DateTime getMinDateTime() {
        return minDateTime;
    }

    public void setMinDateTime(DateTime minDateTime) {
        this.minDateTime = minDateTime;
    }

    public DateTime getMaxDateTime() {
        return maxDateTime;
    }

    public void setMaxDateTime(DateTime maxDateTime) {
        this.maxDateTime = maxDateTime;
    }

    public ArrayList<DateTime> getDisableDates() {
        return disableDates;
    }

    public void setDisableDates(ArrayList<DateTime> disableDates) {
        this.disableDates = disableDates;
    }

    public ArrayList<DateTime> getSelectedDates() {
        return selectedDates;
    }

    public void setSelectedDates(ArrayList<DateTime> selectedDates) {
        this.selectedDates = selectedDates;
    }

    public HashMap<String, Object> getCaldroidData() {
        return caldroidData;
    }

    public void setCaldroidData(HashMap<String, Object> caldroidData, DataMap datamap) {
        this.caldroidData = caldroidData;

        // Reset parameters

        populateFromCaldroidData();
        populateHashMap(datamap);
    }

    public HashMap<String, Object> getExtraData() {
        return extraData;
    }

    public void setExtraData(HashMap<String, Object> extraData) {
        this.extraData = extraData;
    }

    /**
     * Constructor
     *
     * @param context
     * @param month
     * @param year
     * @param caldroidData
     * @paramfget extraData
     */
    public CaldroidGridAdapter(Context context, int month, int year,
                               HashMap<String, Object> caldroidData,
                               HashMap<String, Object> extraData, DataMap datamap, boolean isSelectionEnable) {
        super();
        this.month = month;
        this.year = year;
        this.context = context;
        this.caldroidData = caldroidData;
        this.extraData = extraData;
        this.resources = context.getResources();
        this.isSelectionEnable = isSelectionEnable;
        // Get data from caldroidData

        disableTextColr = Color.parseColor("#ababab");
        notAvailbleTextColr = Color.parseColor("#000000");
        selected_color = context.getResources().getColor(R.color.white);
        populateFromCaldroidData();
        populateHashMap(datamap);

    }

   /* @Override
    public boolean areAllItemsEnabled() {
        return isSelectionEnable;
    }*/

    HashMap<Integer, Integer> currentMonthMap;
    private int selected_color;

    private boolean calender_not_null = false;

    private void populateHashMap(DataMap data) {

        if (data != null)
            if (data.getYearMap() != null)
                if (data.getYearMap().get(year) != null)
                    if (data.getYearMap().get(year).getMonthMap() != null)
                        if (data.getYearMap().get(year).getMonthMap().get(month) != null)
                            if (data.getYearMap().get(year).getMonthMap().get(month).getDateMap() != null) {
                                currentMonthMap = data.getYearMap().get(year).getMonthMap().get(month).getDateMap();
                                calender_not_null = true;
                            }
    }

    /**
     * Retrieve internal parameters from caldroid data
     */
    @SuppressWarnings("unchecked")
    private void populateFromCaldroidData() {
        disableDates = (ArrayList<DateTime>) caldroidData
                .get(CaldroidFragment.DISABLE_DATES);
        if (disableDates != null) {
            disableDatesMap.clear();
            for (DateTime dateTime : disableDates) {
                disableDatesMap.put(dateTime, 1);
            }
        }

        selectedDates = (ArrayList<DateTime>) caldroidData
                .get(CaldroidFragment.SELECTED_DATES);
        if (selectedDates != null) {
            selectedDatesMap.clear();
            for (DateTime dateTime : selectedDates) {
                selectedDatesMap.put(dateTime, 1);
            }
        }

        minDateTime = (DateTime) caldroidData
                .get(CaldroidFragment._MIN_DATE_TIME);
        maxDateTime = (DateTime) caldroidData
                .get(CaldroidFragment._MAX_DATE_TIME);
        startDayOfWeek = (Integer) caldroidData
                .get(CaldroidFragment.START_DAY_OF_WEEK);
        sixWeeksInCalendar = (Boolean) caldroidData
                .get(CaldroidFragment.SIX_WEEKS_IN_CALENDAR);

        this.datetimeList = CalendarHelper.getFullWeeks(this.month, this.year,
                startDayOfWeek, sixWeeksInCalendar);
    }

    public void updateToday() {
        today = CalendarHelper.convertDateToDateTime(new Date());
    }

    protected DateTime getToday() {
        if (today == null) {
            today = CalendarHelper.convertDateToDateTime(new Date());
        }
        return today;
    }

    @SuppressWarnings("unchecked")
    protected void setCustomResources(DateTime dateTime, View backgroundView,
                                      TextView textView) {
        // Set custom background resource
        HashMap<DateTime, Integer> backgroundForDateTimeMap = (HashMap<DateTime, Integer>) caldroidData
                .get(CaldroidFragment._BACKGROUND_FOR_DATETIME_MAP);
        if (backgroundForDateTimeMap != null) {
            // Get background resource for the dateTime
            Integer backgroundResource = backgroundForDateTimeMap.get(dateTime);

            // Set it
            if (backgroundResource != null) {
                backgroundView.setBackgroundResource(backgroundResource
                        .intValue());
            }
        }

        // Set custom text color
        HashMap<DateTime, Integer> textColorForDateTimeMap = (HashMap<DateTime, Integer>) caldroidData
                .get(CaldroidFragment._TEXT_COLOR_FOR_DATETIME_MAP);
        if (textColorForDateTimeMap != null) {
            // Get textColor for the dateTime
            Integer textColorResource = textColorForDateTimeMap.get(dateTime);

            // Set it
            if (textColorResource != null) {
                textView.setTextColor(resources.getColor(textColorResource
                        .intValue()));
            }
        }


    }

    /**
     * Customize colors of text and background based on states of the cell
     * (disabled, active, selected, etc)
     * <p/>
     * To be used only in getView method
     *
     * @param position
     * @param cellView
     */
    protected void customizeTextView(int position, TextView cellView) {

        // Get dateTime of this cell
        DateTime dateTime = this.datetimeList.get(position);
        Calendar calendar = Calendar.getInstance();


        // Set color of the dates in previous / next month
        if (dateTime.getMonth() != month) {
            cellView.setTextColor(resources
                    .getColor(R.color.caldroid_darker_gray));
            cellView.setVisibility(View.GONE);
            return;
        }

        if (calendar.get(Calendar.YEAR) == dateTime.getYear() && dateTime.getMonth() == calendar.get(Calendar.MONTH) + 1) {
            if (dateTime.getDay() < calendar.get(Calendar.DAY_OF_MONTH)) {
                cellView.setTextColor(resources
                        .getColor(R.color.caldroid_darker_gray));

            }
        } else if (calendar.get(Calendar.YEAR) == dateTime.getYear() && dateTime.getMonth() + 1 == calendar.get(Calendar.MONTH) + 1 + 4) {
            if (dateTime.getDay() > calendar.get(Calendar.DAY_OF_MONTH)) {
                Log.e("not selectable", "not 2");
                cellView.setTextColor(resources
                        .getColor(R.color.caldroid_darker_gray));

            } else {
                Log.e("good selectable", "good 2");


            }


        }
        boolean shouldResetDiabledView = false;
        boolean shouldResetSelectedView = false;

        // Customize for disabled dates and date outside min/max dates
        if ((minDateTime != null && dateTime.lt(minDateTime))
                || (maxDateTime != null && dateTime.gt(maxDateTime))
                || (disableDates != null && disableDatesMap.get(dateTime) != null
        )) {

            cellView.setTextColor(disableTextColr);
            /*if (CaldroidFragment.disabledBackgroundDrawable == -1) {
                cellView.setTextColor(disableTextColr);

			} else {
			//	cellView.setBackgroundResource(CaldroidFragment.disabledBackgroundDrawable);
			}*/
///udated by sagar
//            if (dateTime.equals(getToday())) {
//                cellView.setTextColor(Color.parseColor("#ffffff"));
//                //cellView.setBackgroundResource(R.drawable.red_border_gray_bg);
//            } else {
//
//
//            }
        } else {
            shouldResetDiabledView = true;
        }

        // Customize for selected dates
        if (selectedDates != null && selectedDatesMap.get(dateTime) != null) {
            cellView.setTextColor(selected_color);
        } else {
            shouldResetSelectedView = true;
            cellView.setTextColor(notAvailbleTextColr);
        }
///udated by sagar
//        if (dateTime.equals(getToday())) {
//            cellView.setBackgroundResource(R.drawable.today_circle_bg);
//            cellView.setTextColor(selected_color);
//        }

        cellView.setText("" + dateTime.getDay());

        //	// Set custom color if required
        //	setCustomResources(dateTime, cellView, cellView);

        if (calender_not_null) {
            if (currentMonthMap.get(dateTime.getDay()) != null) {
                cellView.setTextColor(selected_color);
                cellView.setBackgroundResource(R.drawable.selected_circle_bg);
            }
        }
        if (!isEnabled(position))
            cellView.setTextColor(disableTextColr);


    }

    private long nintyDaysMillis = 1000l * 60l * 60l * 24l * 90l;

    @Override
    public boolean isEnabled(int position) {

        boolean isEnable = true;
        DateTime dateTime = this.datetimeList.get(position);

        long currentmillis = dateTime.getMilliseconds(TimeZone.getDefault());
        long todaymillis = today.getMilliseconds(TimeZone.getDefault());
        //disable dates
        if (currentmillis < todaymillis) {
            isEnable = false;
        } else if (dateTime.getMonth() - today.getMonth() == 3) {
            if (dateTime.getDay() > today.getDay()) {
                isEnable = false;
            }
        }

        return isEnable;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return this.datetimeList.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView cellView = (TextView) convertView;

        // For reuse
        if (convertView == null) {
            cellView = (TextView) inflater.inflate(R.layout.date_cell, null);
        }

        customizeTextView(position, cellView);

        return cellView;
    }


}

