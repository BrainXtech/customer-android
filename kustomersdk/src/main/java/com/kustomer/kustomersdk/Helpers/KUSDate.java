package com.kustomer.kustomersdk.Helpers;

import android.content.Context;
import android.text.format.DateUtils;

import com.kustomer.kustomersdk.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSDate {

    //region properties
    private static float TWO_DAYS_MILLIS = 48 * 60 * 60 * 1000f;
    private static float SECONDS_PER_MINUTE = 60f;
    private static float MINUTES_PER_HOUR = 60f;
    private static float HOURS_PER_DAY = 24f;
    private static int DAYS_PER_WEEK = 7;

    private static DateFormat shortDateFormat;
    private static DateFormat shortTimeFormat;
    //endregion

    //region Static Methods
    public static String humanReadableTextFromDate(Context context, Date date){
        if (date == null)
            return null;

        long timeAgo = (Calendar.getInstance().getTimeInMillis() - date.getTime()) / 1000;
        if (timeAgo < SECONDS_PER_MINUTE)
            return context.getString(R.string.com_kustomer_just_now);

        return (String) DateUtils.getRelativeTimeSpanString(date.getTime(), Calendar.getInstance().getTimeInMillis(), 0);
    }


    public static String humanReadableTextFromSeconds(Context context, int seconds){
        if(seconds < SECONDS_PER_MINUTE){
            int stringId = seconds <= 1 ? R.string.com_kustomer_second : R.string.com_kustomer_seconds;
            return textWithCountAndUnit(context, seconds,stringId);
        }else if(seconds < SECONDS_PER_MINUTE * MINUTES_PER_HOUR){
            int minutes = (int) Math.ceil(seconds/SECONDS_PER_MINUTE);
            int stringId = minutes <= 1 ? R.string.com_kustomer_minute : R.string.com_kustomer_minutes;
            return textWithCountAndUnit(context, minutes, stringId);
        }else if(seconds < SECONDS_PER_MINUTE * MINUTES_PER_HOUR * HOURS_PER_DAY){
            int hours = (int) Math.ceil(seconds/(SECONDS_PER_MINUTE * MINUTES_PER_HOUR));
            int stringId = hours <= 1 ? R.string.com_kustomer_hour : R.string.com_kustomer_hours;
            return textWithCountAndUnit(context, hours, stringId);
        }else{
            return context.getString(R.string.com_kustomer_greater_than_one_day);
        }
    }

    public static String humanReadableUpfrontVolumeControlWaitingTimeFromSeconds(Context context,
                                                                                 int seconds){
        if(seconds == 0)
            return context.getString(R.string.com_kustomer_someone_should_be_with_you_momentarily);
        else{
            String waitTime = humanReadableTextFromSeconds(context, seconds);
            return  String.format(Locale.getDefault(), "%s %s",
                    context.getString(R.string.com_kustomer_your_expected_wait_time_is), waitTime);
        }
    }

    public static String messageTimeStampTextFromDate(Date date){

        if(date != null){
            long now = System.currentTimeMillis();

            //2days
            if (now - date.getTime() <= TWO_DAYS_MILLIS) {
                return DateUtils.getRelativeTimeSpanString(date.getTime(),
                        now,
                        DateUtils.DAY_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE).toString() + shortTimeFormatter().format(date);
            } else
                return shortRelativeDateFormatter().format(date);
        }else {
            return "";
        }

    }

    public static Date dateFromString (String string){
        if(string != null && string.length() > 0)
            try {
                return ISO8601DateFormatterFromString().parse(string);
            } catch (ParseException ignore) { }

        return null;
    }

    public static String stringFromDate(Date date){
        if(date != null){
            return ISO8601DateFormatterFromDate().format(date);
        }else
            return null;
    }
    //endregion

    //region Private Methods
    private static String textWithCountAndUnit(Context context, int unitCount, int unitString){
        return String.format(Locale.getDefault(), "%d %s", unitCount,
                context.getString(unitString));
    }

    private static String agoWithTextCountAndUnit(long count, String unit){
        int mCount = (int) count;
        if(mCount > 1)
            return String.format(Locale.getDefault(),"%d %ss ago",mCount,unit);
        else
            return String.format(Locale.getDefault(),"%d %s ago",mCount,unit);
    }

    private static DateFormat shortRelativeDateFormatter(){
        if(shortDateFormat == null) {
            shortDateFormat = new SimpleDateFormat("dd/MM/yyyy h:mm a", Locale.getDefault());
        }

        return shortDateFormat;
    }

    private static DateFormat shortTimeFormatter(){
        if(shortTimeFormat == null) {
            shortTimeFormat = new SimpleDateFormat(", h:mm a", Locale.getDefault());
        }

        return shortTimeFormat;
    }

    private static DateFormat ISO8601DateFormatterFromString() throws ParseException{
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                new Locale("en_US_POSIX"));
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        return dateFormat;
    }

    private static DateFormat ISO8601DateFormatterFromDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                new Locale("en_US_POSIX"));
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        return dateFormat;
    }
    //endregion

}
