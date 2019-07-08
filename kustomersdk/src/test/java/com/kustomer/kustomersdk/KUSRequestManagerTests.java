package com.kustomer.kustomersdk;

import com.kustomer.kustomersdk.API.KUSRequestManager;
import com.kustomer.kustomersdk.API.KUSUserSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.net.URL;
import java.util.Objects;

import static com.kustomer.kustomersdk.KustomerTestConstants.KUS_TEST_ORG_ID;
import static com.kustomer.kustomersdk.KustomerTestConstants.KUS_TEST_ORG_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class KUSRequestManagerTests {

    @Test
    public void testUserSessionCreatesDefaultRequestManager(){
        KUSUserSession userSession = new KUSUserSession(KUS_TEST_ORG_NAME,KUS_TEST_ORG_ID);
        assertNotNull(userSession.getRequestManager());
    }

    @Test
    public void testRequestManagerBaseUrl(){
        KUSUserSession userSession = new KUSUserSession(KUS_TEST_ORG_NAME,KUS_TEST_ORG_ID);
        KUSRequestManager requestManager = new KUSRequestManager(userSession);
        URL baseURL = requestManager.urlForEndpoint("");
        assert baseURL != null;
        assertEquals(baseURL.toString(),"https://testOrgName.api.kustomerapp.com");
    }

    @Test
    public void testRequestManagerEndpointURLs(){
        KUSUserSession userSession = new KUSUserSession(KUS_TEST_ORG_NAME,KUS_TEST_ORG_ID);
        KUSRequestManager requestManager = new KUSRequestManager(userSession);
        assertEquals(Objects.requireNonNull(requestManager.urlForEndpoint("/c/v1/customers/current")).toString()
                ,"https://testOrgName.api.kustomerapp.com/c/v1/customers/current");
        assertEquals(Objects.requireNonNull(requestManager.urlForEndpoint("/c/v1/identity")).toString()
                ,"https://testOrgName.api.kustomerapp.com/c/v1/identity");
        assertEquals(Objects.requireNonNull(requestManager.urlForEndpoint("/c/v1/pusher/auth")).toString()
                ,"https://testOrgName.api.kustomerapp.com/c/v1/pusher/auth");
        assertEquals(Objects.requireNonNull(requestManager.urlForEndpoint("/c/v1/chat/messages")).toString()
                ,"https://testOrgName.api.kustomerapp.com/c/v1/chat/messages");
        assertEquals(Objects.requireNonNull(requestManager.urlForEndpoint("/c/v1/chat/sessions")).toString()
                ,"https://testOrgName.api.kustomerapp.com/c/v1/chat/sessions");
        assertEquals(Objects.requireNonNull(requestManager.urlForEndpoint("/c/v1/chat/settings")).toString()
                ,"https://testOrgName.api.kustomerapp.com/c/v1/chat/settings");
        assertEquals(Objects.requireNonNull(requestManager.urlForEndpoint("/c/v1/tracking/tokens/current")).toString()
                ,"https://testOrgName.api.kustomerapp.com/c/v1/tracking/tokens/current");
    }

    @Test
    public void test100RequestManagerInitPerformance(){
        KUSUserSession userSession = new KUSUserSession(KUS_TEST_ORG_NAME,KUS_TEST_ORG_ID);
        for(int i = 0; i<100; i++){
            KUSRequestManager requestManager = new KUSRequestManager(userSession);
        }
    }


}