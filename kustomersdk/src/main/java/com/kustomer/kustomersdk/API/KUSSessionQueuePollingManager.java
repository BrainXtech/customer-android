package com.kustomer.kustomersdk.API;

import com.kustomer.kustomersdk.DataSources.KUSObjectDataSource;
import com.kustomer.kustomersdk.DataSources.KUSSessionQueueDataSource;
import com.kustomer.kustomersdk.Interfaces.KUSObjectDataSourceListener;
import com.kustomer.kustomersdk.Interfaces.KUSSessionQueuePollingListener;
import com.kustomer.kustomersdk.Models.KUSSessionQueue;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class KUSSessionQueuePollingManager implements KUSObjectDataSourceListener {

    //region Properties
    private static final int ONE_MINUTE = 60;

    private Boolean isPollingStarted;
    private Boolean isPollingCanceled;

    private String sessionId;
    private List<KUSSessionQueuePollingListener> listeners;
    private WeakReference<KUSUserSession> userSession;
    private KUSSessionQueueDataSource sessionQueueDataSource;

    private Timer timer;
    //endregion

    //region Initializer
    public KUSSessionQueuePollingManager(KUSUserSession userSession, String sessionId){
        this.userSession = new WeakReference<>(userSession);
        this.sessionId = sessionId;
        isPollingStarted = false;
        isPollingCanceled = false;

        sessionQueueDataSource = new KUSSessionQueueDataSource(userSession,sessionId);
        sessionQueueDataSource.addListener(this);

        listeners = new ArrayList<>();
    }
    //endregion

    //region Private Methods
    private void endTimer(){
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void fetchQueueAfterInterval(long interval){

        endTimer();

        timer = new Timer();
        TimerTask doAsyncTask = new TimerTask() {
            @Override
            public void run() {
                if(!isPollingStarted){
                    isPollingStarted = true;
                    notifyAnnouncersOnPollingStarted();
                }

                sessionQueueDataSource.fetch();
            }
        };
        timer.schedule(doAsyncTask,interval);
    }

    private long getPollingIntervalFromEstimatedWaitTime(int estimatedWaitTimeSeconds){
        double delay;

        if(estimatedWaitTimeSeconds < ONE_MINUTE){
            delay = 0.1 * ONE_MINUTE;
        }else if(estimatedWaitTimeSeconds < 5 * ONE_MINUTE){
            delay = 0.5 * ONE_MINUTE;
        }else if(estimatedWaitTimeSeconds < 10 * ONE_MINUTE){
            delay = ONE_MINUTE;
        }else {
            delay = 0.1 * estimatedWaitTimeSeconds;
        }

        return (long) delay * 1000;
    }

    private void notifyAnnouncersOnPollingStarted() {
        for (KUSSessionQueuePollingListener listener : new ArrayList<>(listeners)) {
            if(listener != null)
                listener.onPollingStarted(this);
        }
    }

    private void notifyAnnouncersOnPollingEnd() {
        for (KUSSessionQueuePollingListener listener : new ArrayList<>(listeners)) {
            if(listener != null)
                listener.onPollingEnd(this);
        }
    }

    private void notifyAnnouncersOnPollingUpdated(KUSSessionQueue sessionQueue) {
        for (KUSSessionQueuePollingListener listener : new ArrayList<>(listeners)) {
            if(listener != null)
                listener.onSessionQueueUpdated(this,sessionQueue);
        }
    }

    private void notifyAnnouncersOnPollingCanceled() {
        for (KUSSessionQueuePollingListener listener : new ArrayList<>(listeners)) {
            if(listener != null)
                listener.onPollingCanceled(this);
        }
    }

    private void notifyAnnouncersOnFailure(Error error) {
        for (KUSSessionQueuePollingListener listener : new ArrayList<>(listeners)) {
            if(listener != null)
                listener.onFailure(error,this);
        }
    }
    //endregion

    //region Public Methods
    public void addListener(KUSSessionQueuePollingListener listener){
        if(!listeners.contains(listener))
            listeners.add(listener);
    }

    public void removeListener(KUSSessionQueuePollingListener listener){
        listeners.remove(listener);
    }

    public void startPolling(){
        // Starting Polling After 2 second to avoid race condition
        fetchQueueAfterInterval(2000);
    }

    public void cancelPolling(){
        if(isPollingStarted){
            isPollingCanceled = true;
            notifyAnnouncersOnPollingCanceled();

            endTimer();
        }
    }

    public KUSSessionQueue getSessionQueue(){
        return (KUSSessionQueue) sessionQueueDataSource.getObject();
    }
    //endregion

    //region Callback Listeners
    @Override
    public void objectDataSourceOnLoad(KUSObjectDataSource dataSource) {
        if(isPollingCanceled) return;

        KUSSessionQueue sessionQueue = (KUSSessionQueue) dataSource.getObject();

        if(sessionQueue == null) return;

        // Notify all announcers for the updated session queue object
        notifyAnnouncersOnPollingUpdated(sessionQueue);

        // Fetch queue object after specific delay if necessary
        if(sessionQueue.getEstimatedWaitTimeSeconds() == 0){
            notifyAnnouncersOnPollingEnd();
            endTimer();
            return;
        }

        long interval = getPollingIntervalFromEstimatedWaitTime(sessionQueue.getEstimatedWaitTimeSeconds());
        fetchQueueAfterInterval(interval);
    }

    @Override
    public void objectDataSourceOnError(KUSObjectDataSource dataSource, Error error) {
        notifyAnnouncersOnFailure(error);
    }
    //endregion

    //region Accessors

    public Boolean getPollingStarted() {
        return isPollingStarted;
    }

    public Boolean getPollingCanceled() {
        return isPollingCanceled;
    }

    //endregion
}
