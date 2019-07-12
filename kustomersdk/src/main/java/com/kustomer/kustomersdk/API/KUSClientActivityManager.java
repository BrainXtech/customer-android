package com.kustomer.kustomersdk.API;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kustomer.kustomersdk.DataSources.KUSClientActivityDataSource;
import com.kustomer.kustomersdk.DataSources.KUSObjectDataSource;
import com.kustomer.kustomersdk.Interfaces.KUSObjectDataSourceListener;
import com.kustomer.kustomersdk.Models.KUSChatSettings;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class KUSClientActivityManager implements KUSObjectDataSourceListener {

    //region Properties
    @Nullable
    private String currentPageName;
    @NonNull
    private WeakReference<KUSUserSession> userSession;
    @Nullable
    private String previousPageName;
    @Nullable
    private List<Timer> timers = new ArrayList<>();
    @Nullable
    private KUSClientActivityDataSource activityDataSource;
    //endregion

    //region LifeCycle
    public KUSClientActivityManager(@NonNull KUSUserSession userSession) {
        this.userSession = new WeakReference<>(userSession);
    }
    //endregion

    //region Internal Methods
    private void cancelTimers() {

        if (timers == null || timers.size() == 0) {
            return;
        }

        List<Timer> tempTimers = new ArrayList<>(timers);
        timers = null;

        for (Timer timer : tempTimers) {
            timer.cancel();
        }

    }

    private void updateTimers() {
        if (activityDataSource != null && activityDataSource.getObject() == null) {
            cancelTimers();
            return;
        }

        List<Timer> timers = new ArrayList<>();

        //Interval value is in seconds rather than milliseconds
        for (final Double interval : activityDataSource.getIntervals()) {
            final Handler handler = new Handler();
            Timer timer = new Timer();
            TimerTask doAsynchronousTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            onActivityTimer(interval);
                        }
                    });
                }
            };
            timer.schedule(doAsynchronousTask, interval.longValue() > 0 ? interval.longValue() * 1000 : 0);

            timers.add(timer);
        }
        this.timers = timers;
    }

    private void requestClientActivityWithCurrentPageSeconds(double currentPageSeconds) {

        activityDataSource = new KUSClientActivityDataSource(userSession.get(),
                previousPageName,
                currentPageName,
                currentPageSeconds);

        activityDataSource.addListener(this);
        activityDataSource.fetch();
    }

    private void onActivityTimer(double interval) {
        requestClientActivityWithCurrentPageSeconds(interval);
    }
    //endregion

    //region Public Methods
    public void setCurrentPageName(@Nullable String currentPageName) {

        if (this.currentPageName != null && this.currentPageName.equals(currentPageName))
            return;

        previousPageName = this.currentPageName;
        this.currentPageName = currentPageName;

        cancelTimers();

        if (activityDataSource != null)
            activityDataSource.cancel();

        activityDataSource = null;

        //If we don't have a current page name, stop here.
        if (this.currentPageName == null)
            return;

        if (userSession.get() == null)
            return;

        // Check that settings is fetched and no history is not enabled
        if (userSession.get().getChatSettingsDataSource().isFetched()) {
            KUSChatSettings settings = (KUSChatSettings) userSession.get()
                    .getChatSettingsDataSource()
                    .getObject();

            if (settings == null || !settings.getNoHistory()) {
                requestClientActivityWithCurrentPageSeconds(0.0);
            }
        } else {
            userSession.get().getChatSettingsDataSource().addListener(this);
            userSession.get().getChatSettingsDataSource().fetch();
        }
    }

    @Nullable
    public String getCurrentPageName() {
        return currentPageName;
    }

    //endregion

    //region Callbacks
    @Override
    public void objectDataSourceOnLoad(@NonNull final KUSObjectDataSource dataSource) {

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (userSession.get() == null)
                    return;

                if (dataSource == userSession.get().getChatSettingsDataSource()) {
                    userSession.get().getChatSettingsDataSource().removeListener(KUSClientActivityManager.this);

                    KUSChatSettings settings = (KUSChatSettings) userSession.get()
                            .getChatSettingsDataSource()
                            .getObject();
                    if (settings == null || !settings.getNoHistory()) {
                        requestClientActivityWithCurrentPageSeconds(0.0);
                    }
                    return;
                }
                if (dataSource == activityDataSource) {
                    if (activityDataSource.getCurrentPageSeconds() > 0) {
                        // Tell the push client to perform a sessions list pull to check for automated messages
                        // We delay a bit here to avoid a race in message creation delay

                        Handler handler = new Handler();
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                if (userSession.get() == null)
                                    return;
                                userSession.get().getPushClient().onClientActivityTick();
                            }
                        };
                        handler.postDelayed(runnable, 1000);
                    }
                }

                updateTimers();
            }
        };
        handler.post(runnable);
    }

    @Override
    public void objectDataSourceOnError(@NonNull KUSObjectDataSource dataSource, @Nullable Error error) {

    }
    //endregion
}
