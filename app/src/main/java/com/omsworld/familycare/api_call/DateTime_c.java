package com.omsworld.familycare.api_call;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by omprakash.m on 3/9/2018.
 */

public class DateTime_c {

    public static DateTime_c instance;
    SimpleDateFormat df;

    private DateTime_c() {
    }

    public static DateTime_c getInstance() {
        if (instance == null) {
            instance = new DateTime_c();
        }
        return instance;
    }

    public String formateDateTime(String parseDateTime) {
        String returnDtm = "";

        try {
           //for current date
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            final String C_Year = String.valueOf(calendar.get(Calendar.YEAR));
            final String C_Day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            final String C_Month = new SimpleDateFormat("MMM").format(calendar.getTime());

            if (parseDateTime.equals("currentTime")) {
                returnDtm = new SimpleDateFormat("hh:mm a").format(calendar.getTime());
            }else if (parseDateTime.equals("currentDateTime")) {
                returnDtm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
            } else {
                //---------------------------------------------
                df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = df.parse(parseDateTime);
                //for server date
                calendar.setTime(date);

                final String Year = String.valueOf(calendar.get(Calendar.YEAR));
                final String Day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                final String Month = new SimpleDateFormat("MMM").format(calendar.getTime());
                final String Time = new SimpleDateFormat("hh:mm a").format(date);

                if (C_Year.equals(Year)) {
                    if (C_Month.equals(Month) && C_Day.equals(Day)) {
                        returnDtm = Time;
                    } else {
                        returnDtm = Day + " " + Month;
                    }
                } else {
                    returnDtm = parseDateTime;
                }

                //---------------------------------------------
            }
        } catch (Exception ex) {
        }

        return returnDtm;
    }

    public String changeFormate(String parseDateTime) {
        String returnDtm = "";
        try {
            df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = df.parse(parseDateTime);

            returnDtm = new SimpleDateFormat("d MMM yyyy  h:mm a").format(date);

        } catch (Exception ex) {
        }
        return returnDtm;
    }

    public boolean dateExistInFromToDate(String from, String to, String compare) {
        boolean status = false;
        try {
            df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            final Date fromD = df.parse(from);
            final Date toD = df.parse(to);
            final Date compareDate = df.parse(compare);

            if (compareDate.after(fromD) && compareDate.before(toD)) {
                //date >0 will exist in from date  AND
                Log.d("---after-->", "date >0 will exist in from date" + compare);
                status = true;
            }
           /* if (compareDate.before(toD)) {
                //date <0 will exist in to date
                Log.d("---before-->", "date <0 will exist in to date "+compare);
            }*/
        } catch (Exception Ex) {
        }
        return status;
    }
}
