package com.kustomer.kustomersdk;

import com.kustomer.kustomersdk.API.KUSRequestManager;
import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Helpers.KUSDate;
import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Models.KUSChatSettings;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;

import static com.kustomer.kustomersdk.KustomerTestConstants.KUS_TEST_ORG_ID;
import static com.kustomer.kustomersdk.KustomerTestConstants.KUS_TEST_ORG_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class KUSDateTests {

    private static long KUSDateEpochTime = 1508694952000L;
    private static final String KUS_EXPECTED_DATE_STRING = "2017-10-22T17:55:52.000Z";

    @Test
    public void testAutoreplyWhitespaceTrim() {
        Date date = new Date(KUSDateEpochTime);
        String dateString = KUSDate.stringFromDate(date);
        assertEquals(dateString,KUS_EXPECTED_DATE_STRING);
    }

    @Test
    public void testStringToDateConversion() {
        Date expectedDate = new Date(KUSDateEpochTime);
        Date convertedDate = KUSDate.dateFromString(KUS_EXPECTED_DATE_STRING);
        assertEquals(expectedDate,convertedDate);
    }

    @Test
    public void testDateToStringAndBackConversion() {
        Date currentDate = new Date();
        String dateString = KUSDate.stringFromDate(currentDate);
        Date stringDate = KUSDate.dateFromString(dateString);
        assertEquals(currentDate.getTime(), stringDate != null ? stringDate.getTime() : 0);
    }

    @Test
    public void test100DateToStringPerformance(){
        Date date = new Date(KUSDateEpochTime);
        for(int i = 0; i<100; i++){
            String dateString = KUSDate.stringFromDate(date);
        }
    }

}