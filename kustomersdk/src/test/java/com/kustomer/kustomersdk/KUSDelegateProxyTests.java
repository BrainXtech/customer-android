package com.kustomer.kustomersdk;

import android.app.PendingIntent;
import android.content.Context;

import com.kustomer.kustomersdk.DataSources.KUSDelegateProxy;
import com.kustomer.kustomersdk.Interfaces.KUSKustomerListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class KUSDelegateProxyTests implements KUSKustomerListener {
    private KUSDelegateProxy delegateProxy;

    private boolean shouldDisplayInAppNotification;
    private boolean pendingIntentRequested;

    @Before
    public void setUp(){
        delegateProxy = new KUSDelegateProxy();
        shouldDisplayInAppNotification = false;
        pendingIntentRequested = false;
    }

    @Test
    public void testDefaultBehavior() {
        assertTrue(delegateProxy.shouldDisplayInAppNotification());

        delegateProxy.getPendingIntent(RuntimeEnvironment.application.getApplicationContext());
        assertFalse(pendingIntentRequested);
    }

    @Test
    public void testOverriddenBehavior() {
        delegateProxy.setListener(this);

        shouldDisplayInAppNotification = true;
        assertTrue(delegateProxy.shouldDisplayInAppNotification());

        shouldDisplayInAppNotification = false;
        assertFalse(delegateProxy.shouldDisplayInAppNotification());

        shouldDisplayInAppNotification = true;
        assertTrue(delegateProxy.shouldDisplayInAppNotification());

        delegateProxy.getPendingIntent(null);
        assertTrue(pendingIntentRequested);
    }

    //region Kustomer Delegate methods
    @Override
    public boolean kustomerShouldDisplayInAppNotification() {
        return shouldDisplayInAppNotification;
    }

    @Override
    public PendingIntent getPendingIntent(Context context) {
        pendingIntentRequested = true;
        return null;
    }
    //endregion


}