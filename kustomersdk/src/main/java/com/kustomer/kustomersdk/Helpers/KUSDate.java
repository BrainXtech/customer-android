package com.kustomer.kustomersdk.Helpers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    @Nullable
    private static DateFormat shortDateFormat;
    @Nullable
    private static DateFormat shortTimeFormat;
    //endregion

    //region Static Methods
    @NonNull
    public static String humanReadableTextFromDate(@NonNull Context context, @Nullable Date date) {
        if (date == null)
            return "";

        long timeAgo = (Calendar.getInstance().getTimeInMillis() - date.getTime()) / 1000;
        if (timeAgo < SECONDS_PER_MINUTE)
            return context.getString(R.string.com_kustomer_just_now);

        return localizedHumanReadableTextFromDate(context, timeAgo);
    }

    @NonNull
    private static String localizedHumanReadableTextFromDate(@NonNull Context context, long timeAgo) {
        String unit;
        int count;

        if (timeAgo >= SECONDS_PER_MINUTE * MINUTES_PER_HOUR * HOURS_PER_DAY * DAYS_PER_WEEK) {
            count = (int) (timeAgo / (SECONDS_PER_MINUTE * MINUTES_PER_HOUR * HOURS_PER_DAY * DAYS_PER_WEEK));

            unit = count > 1 ? context.getString(R.string.com_kustomer_weeks) :
                    context.getString(R.string.com_kustomer_week);

        } else if (timeAgo >= SECONDS_PER_MINUTE * MINUTES_PER_HOUR * HOURS_PER_DAY) {
            count = (int) (timeAgo / (SECONDS_PER_MINUTE * MINUTES_PER_HOUR * HOURS_PER_DAY));

            unit = count > 1 ? context.getString(R.string.com_kustomer_days) :
                    context.getString(R.string.com_kustomer_day);

        } else if (timeAgo >= SECONDS_PER_MINUTE * MINUTES_PER_HOUR) {
            count = (int) (timeAgo / (SECONDS_PER_MINUTE * MINUTES_PER_HOUR));

            unit = count > 1 ? context.getString(R.string.com_kustomer_hours) :
                    context.getString(R.string.com_kustomer_hour);

        } else {
            count = (int) (timeAgo / (SECONDS_PER_MINUTE));

            unit = count > 1 ? context.getString(R.string.com_kustomer_minutes) :
                    context.getString(R.string.com_kustomer_minute);
        }

        return String.format(Locale.getDefault(), "%d %s %s", count, unit,
                context.getString(R.string.com_kustomer_ago));
    }

    @NonNull
    public static String humanReadableTextFromSeconds(@NonNull Context context, int seconds) {
        if (seconds < SECONDS_PER_MINUTE) {
            int stringId = seconds <= 1 ? R.string.com_kustomer_second : R.string.com_kustomer_seconds;
            return textWithCountAndUnit(context, seconds, stringId);
        } else if (seconds < SECONDS_PER_MINUTE * MINUTES_PER_HOUR) {
            int minutes = (int) Math.ceil(seconds / SECONDS_PER_MINUTE);
            int stringId = minutes <= 1 ? R.string.com_kustomer_minute : R.string.com_kustomer_minutes;
            return textWithCountAndUnit(context, minutes, stringId);
        } else if (seconds < SECONDS_PER_MINUTE * MINUTES_PER_HOUR * HOURS_PER_DAY) {
            int hours = (int) Math.ceil(seconds / (SECONDS_PER_MINUTE * MINUTES_PER_HOUR));
            int stringId = hours <= 1 ? R.string.com_kustomer_hour : R.string.com_kustomer_hours;
            return textWithCountAndUnit(context, hours, stringId);
        } else {
            return context.getString(R.string.com_kustomer_greater_than_one_day);
        }
    }

    @NonNull
    public static String humanReadableUpfrontVCWaitingTimeFromSeconds(@NonNull Context context,
                                                                      int seconds) {
        if (seconds == 0)
            return context.getString(R.string.com_kustomer_someone_should_be_with_you_momentarily);
        else {
            String waitTime = humanReadableTextFromSeconds(context, seconds);
            return String.format(Locale.getDefault(), "%s %s",
                    context.getString(R.string.com_kustomer_your_expected_wait_time_is), waitTime);
        }
    }

    @NonNull
    public static String messageTimeStampTextFromDate(@Nullable Date date) {

        if (date != null) {
            long now = System.currentTimeMillis();

            //2days
            if (now - date.getTime() <= TWO_DAYS_MILLIS) {
                return DateUtils.getRelativeTimeSpanString(date.getTime(),
                        now,
                        DateUtils.DAY_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE).toString() + shortTimeFormatter().format(date);
            } else
                return shortRelativeDateFormatter().format(date);
        } else {
            return "";
        }

    }

    @Nullable
    public static Date dateFromString(@Nullable String string) {
        if (string != null && string.length() > 0)
            try {
                return ISO8601DateFormatterFromString().parse(string);
            } catch (ParseException ignore) {
            }

        return null;
    }

    @NonNull
    public static String stringFromDate(@Nullable Date date) {
        if (date != null) {
            return ISO8601DateFormatterFromDate().format(date);
        } else
            return "";
    }
    //endregion

    //region Private Methods
    @NonNull
    private static String textWithCountAndUnit(@NonNull Context context, int unitCount, int unitString) {
        return String.format(Locale.getDefault(), "%d %s", unitCount,
                context.getString(unitString));
    }

    @NonNull
    private static DateFormat shortRelativeDateFormatter() {
        if (shortDateFormat == null) {
            shortDateFormat = new SimpleDateFormat("dd/MM/yyyy h:mm a", Locale.getDefault());
        }

        return shortDateFormat;
    }

    @NonNull
    private static DateFormat shortTimeFormatter() {
        if (shortTimeFormat == null) {
            shortTimeFormat = new SimpleDateFormat(", h:mm a", Locale.getDefault());
        }

        return shortTimeFormat;
    }

    @NonNull
    private static DateFormat ISO8601DateFormatterFromString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                new Locale("en_US_POSIX"));
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        return dateFormat;
    }

    @NonNull
    private static DateFormat ISO8601DateFormatterFromDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                new Locale("en_US_POSIX"));
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        return dateFormat;
    }
    //endregion

}
