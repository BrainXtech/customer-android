package com.kustomer.kustomersdk;

import com.kustomer.kustomersdk.API.KUSUserSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static com.kustomer.kustomersdk.KustomerTestConstants.KUS_TEST_API_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class KustomerTests {

    @Before
    public void setUp(){
        Kustomer.setLogOptions(0);
    }

    @Test
    public void testExpectsValidAPIKey(){

        try {
            Kustomer.init(RuntimeEnvironment.application.getApplicationContext(),null);
            fail("Error should have been thrown");
        }catch (AssertionError | Exception ignore){}

        try {
            Kustomer.init(RuntimeEnvironment.application.getApplicationContext(),"");
            fail("Error should have been thrown");
        }catch (AssertionError | Exception ignore){}

        try {
            Kustomer.init(RuntimeEnvironment.application.getApplicationContext(),"key");
            fail("Error should have been thrown");
        }catch (AssertionError | Exception ignore){}
    }

    @Test
    public void testValidAPIKeySuccess() {
        Kustomer.init(RuntimeEnvironment.application.getApplicationContext(),KUS_TEST_API_KEY);
    }

    @Test
    public void testWasProperlySetup() {
        Kustomer.init(RuntimeEnvironment.application.getApplicationContext(),KUS_TEST_API_KEY);
        assertNotNull(Kustomer.getSharedInstance());
        assertNotNull(Kustomer.getSharedInstance().getUserSession());
    }

    @Test
    public void testUserSessionHasExpectedProperties() {
        Kustomer.init(RuntimeEnvironment.application.getApplicationContext(),KUS_TEST_API_KEY);
        KUSUserSession userSession = Kustomer.getSharedInstance().getUserSession();
        assertEquals(userSession.getOrgId(),"testOrgId");
        assertEquals(userSession.getOrgName(),"testOrgName");
        assertEquals(userSession.getOrganizationName(),"TestOrgName");
    }

    @Test
    public void test100InitializeWithAPIKeyPerformance(){
        for(int i = 0; i<100; i++){
            Kustomer.init(RuntimeEnvironment.application.getApplicationContext(),KUS_TEST_API_KEY);
        }
    }

}