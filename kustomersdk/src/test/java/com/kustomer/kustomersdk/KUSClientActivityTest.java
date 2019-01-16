package com.kustomer.kustomersdk;

import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Models.KUSClientActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

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
public class KUSClientActivityTest {

    @Test
    public void testClientActivityParsingOne() {

        JSONArray intervalsArray = new JSONArray();
        try {
            intervalsArray.put(new JSONObject(){{ put("seconds",35); }});
            intervalsArray.put(new JSONObject(){{ put("seconds",70); }});
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray languages = new JSONArray();
        languages.put("en-US");
        languages.put("en");

        JSONObject attributesObject = new JSONObject();
        try {
            attributesObject.put("trackingId","5a7a3d7d2d8dbf00100c4d55");
            attributesObject.put("intervals",intervalsArray);
            attributesObject.put("ip","216.139.145.141");
            attributesObject.put("languages",languages);
            attributesObject.put("currentPage","pricing");
            attributesObject.put("currentPageSeconds",0);
            attributesObject.put("previousPage","home");
            attributesObject.put("createdAt","2018-02-07T13:10:50.096Z");
            attributesObject.put("userAgent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject json = new JSONObject();
        try {
            json.put("type","client_activity");
            json.put("id","5a7afadacb1dc9001169e97e");
            json.put("attributes",attributesObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        KUSClientActivity clientActivity = null;
        try {
            clientActivity = new KUSClientActivity(json);
        } catch (KUSInvalidJsonException ignore) { }

        assertNotNull(clientActivity);
        List<Double> expectedIntervals = new ArrayList<Double>(){{add(35.0); add(70.0);}};
        assertEquals(clientActivity.getIntervals(), expectedIntervals);
        assertEquals(clientActivity.getCurrentPage(),"pricing");
        assertEquals(clientActivity.getPreviousPage(),"home");
        assertEquals(clientActivity.getCurrentPageSeconds(),0.0,0.0);
    }

    @Test
    public void testClientActivityParsingTwo() {

        JSONArray intervalsArray = new JSONArray();
        try {
            intervalsArray.put(new JSONObject(){{ put("seconds",10); }});
            intervalsArray.put(new JSONObject(){{ put("seconds",20); }});
            intervalsArray.put(new JSONObject(){{ put("seconds",30); }});
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject attributesObject = new JSONObject();
        try {
            attributesObject.put("trackingId","5a7a3d7d2d8dbf00100c4d55");
            attributesObject.put("intervals",intervalsArray);
            attributesObject.put("currentPage","profile");
            attributesObject.put("currentPageSeconds",20);
            attributesObject.put("previousPage","settings");
            attributesObject.put("createdAt","2018-02-07T13:10:50.096Z");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject json = new JSONObject();
        try {
            json.put("type","client_activity");
            json.put("id","5a7afadacb1dc9001169e97e");
            json.put("attributes",attributesObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        KUSClientActivity clientActivity = null;
        try {
            clientActivity = new KUSClientActivity(json);
        } catch (KUSInvalidJsonException ignore) { }

        assertNotNull(clientActivity);
        List<Double> expectedIntervals = new ArrayList<Double>(){{add(10.0); add(20.0); add(30.0);}};
        assertEquals(clientActivity.getIntervals(), expectedIntervals);
        assertEquals(clientActivity.getCurrentPage(),"profile");
        assertEquals(clientActivity.getPreviousPage(),"settings");
        assertEquals(clientActivity.getCurrentPageSeconds(),20.0,0.0);
    }

    @Test
    public void testClientActivityParsingMissingType() {

        JSONArray intervalsArray = new JSONArray();
        try {
            intervalsArray.put(new JSONObject(){{ put("seconds",10); }});
            intervalsArray.put(new JSONObject(){{ put("seconds",20); }});
            intervalsArray.put(new JSONObject(){{ put("seconds",30); }});
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject attributesObject = new JSONObject();
        try {
            attributesObject.put("trackingId","5a7a3d7d2d8dbf00100c4d55");
            attributesObject.put("intervals",intervalsArray);
            attributesObject.put("currentPage","profile");
            attributesObject.put("currentPageSeconds",20);
            attributesObject.put("previousPage","settings");
            attributesObject.put("createdAt","2018-02-07T13:10:50.096Z");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject json = new JSONObject();
        try {
            json.put("id","5a7afadacb1dc9001169e97e");
            json.put("attributes",attributesObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        KUSClientActivity clientActivity = null;
        try {
            clientActivity = new KUSClientActivity(json);
        } catch (KUSInvalidJsonException ignore) { }

        assertNull(clientActivity);
    }
}