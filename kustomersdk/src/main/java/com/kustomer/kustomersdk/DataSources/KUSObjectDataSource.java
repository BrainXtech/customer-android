package com.kustomer.kustomersdk.DataSources;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Interfaces.KUSObjectDataSourceListener;
import com.kustomer.kustomersdk.Interfaces.KUSRequestCompletionListener;
import com.kustomer.kustomersdk.Models.KUSModel;
import com.kustomer.kustomersdk.Utils.JsonHelper;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSObjectDataSource {
    //region Properties
    private boolean fetching;
    private boolean fetched;
    @Nullable
    private Error error;
    @Nullable
    private KUSModel object;

    @NonNull
    private WeakReference<KUSUserSession> userSession;
    @Nullable
    private Object requestMarker;

    @NonNull
    private List<KUSObjectDataSourceListener> listeners;
    //endregion

    //region Initializer
    KUSObjectDataSource(@NonNull KUSUserSession userSession){
        this.userSession = new WeakReference<>(userSession);
        listeners = new CopyOnWriteArrayList<>();
    }
    //endregion

    //region public Methods
    public void fetch(){
        if(fetching){
            return;
        }

        fetching = true;
        error = null;

        final Object requestMarker = new Object();
        this.requestMarker = requestMarker;

        final WeakReference<KUSObjectDataSource> weakInstance = new WeakReference<>(this);
        performRequest(new KUSRequestCompletionListener() {
            @Override
            public void onCompletion(@Nullable Error errorObject, @Nullable JSONObject response) {
                if(weakInstance.get().requestMarker != requestMarker)
                    return;

                KUSObjectDataSource.this.requestMarker = null;

                KUSModel model = null;
                try {
                    if (response != null) {
                        model = objectFromJson(JsonHelper.jsonObjectFromKeyPath(response,"data"));
                        model.addIncludedWithJSON(JsonHelper.jsonArrayFromKeyPath(response,"included"));
                    }
                } catch (KUSInvalidJsonException ignore) { }

                fetching = false;
                if(errorObject != null || model == null){
                    if(error == null)
                        error = new Error();
                    else
                        error = errorObject;

                    notifyAnnouncersOnError(error);
                }else {
                    object = model;
                    fetched = true;
                    notifyAnnouncersOnLoad();
                }
            }
        });

    }

    public void cancel(){
        fetching = false;
        requestMarker = null;
    }

    public void addListener(@NonNull KUSObjectDataSourceListener listener){
        if(!listeners.contains(listener))
            listeners.add(listener);
    }

    public void removeListener(@NonNull KUSObjectDataSourceListener listener){
        listeners.remove(listener);
    }

    public void removeAllListeners(){
        listeners.clear();
    }
    //endregion

    //region Private Methods
    private void notifyAnnouncersOnError(@Nullable Error error){
        for(KUSObjectDataSourceListener listener : listeners){
            listener.objectDataSourceOnError(this,error);
        }
    }

    private void notifyAnnouncersOnLoad(){
        for(KUSObjectDataSourceListener listener : listeners){
            listener.objectDataSourceOnLoad(this);
        }
    }
    //endregion

    //region subclass methods
    @NonNull
    KUSModel objectFromJson(@Nullable JSONObject jsonObject) throws KUSInvalidJsonException {
        return new KUSModel(jsonObject);
    }

    void performRequest(@NonNull KUSRequestCompletionListener completionListener){}
    //endregion

    //region Accessors

    public boolean isFetching() {
        return fetching;
    }

    public boolean isFetched() {
        return fetched;
    }

    @Nullable
    public Error getError() {
        return error;
    }

    @Nullable
    public KUSModel getObject() {
        return object;
    }

    public KUSUserSession getUserSession() {
        return userSession.get();
    }

    @Nullable
    public Object getRequestMarker() {
        return requestMarker;
    }

    @NonNull
    public List<KUSObjectDataSourceListener> getListeners() {
        return listeners;
    }

    //endregion
}
