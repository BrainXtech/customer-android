package com.kustomer.kustomersdk.DataSources;

import android.support.annotation.NonNull;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Enums.KUSBusinessHoursAvailability;
import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Interfaces.KUSRequestCompletionListener;
import com.kustomer.kustomersdk.Models.KUSChatSettings;
import com.kustomer.kustomersdk.Models.KUSHoliday;
import com.kustomer.kustomersdk.Models.KUSModel;
import com.kustomer.kustomersdk.Models.KUSSchedule;
import com.kustomer.kustomersdk.Utils.JsonHelper;
import com.kustomer.kustomersdk.Utils.KUSConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;


public class KUSScheduleDataSource extends KUSObjectDataSource {
    //region Properties
    private boolean isActiveBusinessHours;

    private String scheduleId;
    private String lastFetchedScheduleId;
    private boolean isFetched;
    //endregion

    //region Initializer
    public KUSScheduleDataSource(KUSUserSession userSession) {
        super(userSession);
    }
    //endregion

    //region subclass methods
    @Override
    KUSModel objectFromJson(JSONObject jsonObject) throws KUSInvalidJsonException {
        return new KUSSchedule(jsonObject);
    }

    @Override
    void performRequest(@NonNull final KUSRequestCompletionListener completionListener) {
        if (getUserSession() == null) {
            completionListener.onCompletion(new Error(), null);
            return;
        }

        final String scheduleIdToFetch = scheduleIdToFetch();
        String endPoint = String.format(KUSConstants.URL.BUSINESS_SCHEDULE_ENDPOINT_WITH_ID, scheduleIdToFetch);

        getUserSession().getRequestManager().getEndpoint(
                endPoint,
                true,
                new KUSRequestCompletionListener() {
                    @Override
                    public void onCompletion(Error error, JSONObject response) {
                        if (error == null) {
                            lastFetchedScheduleId = scheduleIdToFetch;
                            isFetched = true;
                        } else {
                            isFetched = false;
                        }
                        scheduleId = null;
                        completionListener.onCompletion(error, response);
                    }
                }
        );
    }

    @Override
    public void fetch() {
        boolean shouldFetch = !isFetched() || !lastFetchedScheduleId.equals(scheduleIdToFetch());
        if (shouldFetch) {
            isFetched = false;
            super.fetch();
        } else {
            scheduleId = null;
        }
    }

    @Override
    public boolean isFetched() {
        if (super.isFetched()) {
            return isFetched;
        }
        return false;
    }

    private String scheduleIdToFetch() {
        return scheduleId != null ? scheduleId : "default";
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public boolean isActiveBusinessHours() {
        KUSChatSettings chatSettings = null;

        if (getUserSession() != null)
            chatSettings = (KUSChatSettings) getUserSession().getChatSettingsDataSource().getObject();

        if (chatSettings == null || chatSettings.getAvailability() ==
                KUSBusinessHoursAvailability.KUS_BUSINESS_HOURS_AVAILABILITY_ONLINE) {
            return true;
        }

        KUSSchedule businessHours = (KUSSchedule) getObject();

        if (businessHours == null)
            return true;

        // Check that current date is not in holiday date and time
        Date now = Calendar.getInstance().getTime();
        for (KUSHoliday holiday : businessHours.getHolidays()) {
            if (holiday.getEnabled()) {

                boolean todayIsDuringOrAfterHolidayStartDate = now.equals(holiday.getStartDate())
                        || now.after(holiday.getStartDate());

                boolean todayIsDuringOrBeforeHolidayEndDate = now.equals(holiday.getEndDate())
                        || now.before(holiday.getEndDate());

                boolean todayIsHoliday = todayIsDuringOrAfterHolidayStartDate
                        && todayIsDuringOrBeforeHolidayEndDate;

                if (todayIsHoliday) {
                    return false;
                }
            }
        }

        // Get Week Day
        Calendar calendar = Calendar.getInstance();
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int minutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);

        JSONArray businessHoursOfCurrentDay = JsonHelper.arrayFromKeyPath(businessHours.getHours(),
                String.valueOf(weekDay));
        if (businessHoursOfCurrentDay == null)
            return false;

        for (int i = 0; i < businessHoursOfCurrentDay.length(); i++) {
            try {
                JSONArray businessHoursRange = businessHoursOfCurrentDay.getJSONArray(i);
                if (businessHoursRange != null && businessHoursRange.length() == 2
                        && businessHoursRange.getInt(0) <= minutes
                        && businessHoursRange.getInt(1) >= minutes) {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
    //endregion
}
