package com.kustomer.kustomersdk.DataSources;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Enums.KUSRequestType;
import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Helpers.KUSLog;
import com.kustomer.kustomersdk.Interfaces.KUSPaginatedDataSourceListener;
import com.kustomer.kustomersdk.Interfaces.KUSRequestCompletionListener;
import com.kustomer.kustomersdk.Models.KUSModel;
import com.kustomer.kustomersdk.Models.KUSPaginatedResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Junaid on 1/20/2018.
 */


public class KUSPaginatedDataSource {

    //region Properties
    @NonNull
    private List<KUSModel> fetchedModels;

    @NonNull
    private HashMap<String, KUSModel> fetchedModelsById;

    @Nullable
    private KUSPaginatedResponse mostRecentPaginatedResponse;
    @Nullable
    private KUSPaginatedResponse lastPaginatedResponse;

    @NonNull
    private WeakReference<KUSUserSession> userSession;
    @NonNull
    protected List<KUSPaginatedDataSourceListener> listeners;

    private boolean fetching;
    private boolean fetched;
    private boolean fetchedAll;

    @Nullable
    private Error error;

    @Nullable
    private Object requestMarker;
    //endregion

    //region LifeCycle
    public KUSPaginatedDataSource(@NonNull KUSUserSession userSession) {
        this.userSession = new WeakReference<>(userSession);
        listeners = new CopyOnWriteArrayList<>();
        fetchedModels = new CopyOnWriteArrayList<>();
        fetchedModelsById = new HashMap<>();
    }
    //endregion

    //region Methods
    public synchronized int getSize() {
        return fetchedModels.size();
    }

    @NonNull
    public synchronized List<KUSModel> getList() {
        return fetchedModels;
    }

    @Nullable
    public KUSModel findById(@Nullable String oid) {
        return fetchedModelsById.get(oid);
    }

    @Nullable
    public synchronized KUSModel get(int index) {
        if (index < getSize() && index >= 0)
            return fetchedModels.get(index);
        else
            return null;
    }

    private int indexOf(@Nullable KUSModel obj) {
        if (obj != null) {
            return indexOfObjectId(obj.getId());
        }
        return -1;
    }

    @Nullable
    synchronized KUSModel getFirst() {
        if (getSize() > 0) {
            return fetchedModels.get(0);
        }
        return null;
    }

    private int indexOfObjectId(@Nullable String objectId) {
        if (objectId == null) {
            return -1;
        }

        KUSModel internalObj = findById(objectId);
        if (internalObj == null)
            return -1;

        int i = 0;
        for (KUSModel model : fetchedModels) {
            if (model.getId().equals(internalObj.getId())) {
                return i;
            }

            i++;
        }
        return -1;
    }

    public void addListener(@NonNull KUSPaginatedDataSourceListener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    public void removeListener(@NonNull KUSPaginatedDataSourceListener listener) {
        listeners.remove(listener);
    }

    public void removeAllListeners() {
        listeners.clear();
    }

    public void fetchLatest() {
        URL url = getFirstUrl();

        if (getUserSession() == null)
            return;

        if (mostRecentPaginatedResponse != null && mostRecentPaginatedResponse.getFirstPath() != null) {
            url = getUserSession().getRequestManager().urlForEndpoint(mostRecentPaginatedResponse.getFirstPath());
        }

        if (url == null || fetching)
            return;

        fetching = true;
        error = null;

        final Object requestMarker = new Object();
        this.requestMarker = requestMarker;

        final KUSPaginatedDataSource dataSource = this;

        getUserSession().getRequestManager().performRequestType(
                KUSRequestType.KUS_REQUEST_TYPE_GET,
                url,
                null,
                true,
                new KUSRequestCompletionListener() {
                    @Override
                    public void onCompletion(final Error error, final JSONObject response) {

                        try {
                            final KUSPaginatedResponse pageResponse = new KUSPaginatedResponse(response, dataSource);

                            if (requestMarker != KUSPaginatedDataSource.this.requestMarker)
                                return;

                            KUSPaginatedDataSource.this.requestMarker = null;
                            KUSPaginatedDataSource.this.prependResponse(pageResponse, error);
                        } catch (JSONException | KUSInvalidJsonException ignore) {
                        }
                    }
                });

    }

    public void fetchNext() {
        URL url = null;
        if (getUserSession() == null)
            return;

        if (lastPaginatedResponse != null) {
            url = getUserSession().getRequestManager().urlForEndpoint(lastPaginatedResponse.getNextPath());

        } else if (mostRecentPaginatedResponse != null) {
            url = getUserSession().getRequestManager().urlForEndpoint(mostRecentPaginatedResponse.getNextPath());
        }

        if (url == null || fetching)
            return;

        fetching = true;
        error = null;

        final Object requestMarker = new Object();
        this.requestMarker = requestMarker;

        final KUSPaginatedDataSource dataSource = this;

        userSession.get().getRequestManager().performRequestType(
                KUSRequestType.KUS_REQUEST_TYPE_GET,
                url,
                null,
                true,
                new KUSRequestCompletionListener() {
                    @Override
                    public void onCompletion(final Error error, JSONObject json) {
                        try {
                            final KUSPaginatedResponse response = new KUSPaginatedResponse(json, dataSource);
                            if (requestMarker != KUSPaginatedDataSource.this.requestMarker)
                                return;

                            KUSPaginatedDataSource.this.requestMarker = null;
                            KUSPaginatedDataSource.this.appendResponse(response, error);

                        } catch (JSONException | KUSInvalidJsonException e) {
                            KUSLog.kusLogError(e.getMessage());
                        }
                    }
                });

    }

    public void cancel() {
        fetching = false;
        requestMarker = null;
    }

    @Nullable
    public URL getFirstUrl() {
        return null;
    }

    private void appendResponse(@Nullable KUSPaginatedResponse response,@Nullable Error error) {
        if (error != null || response == null) {
            fetching = false;
            this.error = error != null ? error : new Error();
            notifyAnnouncersOnError(error);
            return;
        }

        lastPaginatedResponse = response;
        mostRecentPaginatedResponse = response;

        fetching = false;
        fetched = true;
        fetchedAll = (fetchedAll || response.getNextPath() == null || response.getNextPath().equals("null"));

        upsertAll(response.getObjects());
        notifyAnnouncersOnLoad();
    }

    private void prependResponse(@Nullable KUSPaginatedResponse response,@Nullable Error error) {
        if (error != null || response == null) {
            fetching = false;
            this.error = error != null ? error : new Error();
            notifyAnnouncersOnError(error);
            return;
        }

        mostRecentPaginatedResponse = response;

        fetching = false;
        fetched = true;
        fetchedAll = (fetchedAll || response.getNextPath() == null || response.getNextPath().equals("null"));

        upsertAll(response.getObjects());
        notifyAnnouncersOnLoad();
    }

    synchronized void sort() {
        ArrayList<KUSModel> temp = new ArrayList<>(fetchedModels);
        Collections.sort(temp);

        fetchedModels.clear();
        fetchedModels.addAll(temp);
    }

    public boolean isFetched() {
        return fetched;
    }

    public boolean isFetchedAll() {
        return fetchedAll;
    }

    public boolean isFetching() {
        return fetching;
    }

    @Nullable
    public Error getError() {
        return error;
    }

    public synchronized void removeAll(@Nullable List<KUSModel> objects) {
        if (objects == null || objects.size() == 0) {
            return;
        }

        boolean didChange = false;
        for (KUSModel obj : new ArrayList<>(objects)) {
            int index = indexOf(obj);
            if (index != -1) {
                didChange = true;

                try {
                    fetchedModels.remove(index);
                } catch (IndexOutOfBoundsException ignore) {
                }
                fetchedModelsById.remove(obj.getId());
            }
        }

        if (didChange) {
            notifyAnnouncersOnContentChange();
        }
    }

    public synchronized void upsertAll(@Nullable List<KUSModel> objects) {
        if (objects == null || objects.size() == 0) {
            return;
        }

        boolean didChange = false;
        for (KUSModel obj : objects) {
            int index = indexOf(obj);
            
            if (index != -1) {
                KUSModel curObj = findById(obj.getId());
                if (!obj.equals(curObj)) {
                    didChange = true;
                }

                try {
                    int existingIndex = indexOf(curObj);
                    if (existingIndex != -1) {
                        fetchedModels.remove(existingIndex);
                    }
                } catch (IndexOutOfBoundsException ignore) {
                }

                fetchedModels.add(obj);
                fetchedModelsById.put(obj.getId(), obj);
            
            } else {
                didChange = true;
                fetchedModels.add(obj);
                fetchedModelsById.put(obj.getId(), obj);
            }
        }

        sort();

        if (didChange) {
            notifyAnnouncersOnContentChange();
        }
    }

    @Nullable
    public List<KUSModel> objectsFromJSON(@Nullable JSONObject jsonObject) {

        ArrayList<KUSModel> arrayList = null;

        KUSModel model = null;
        try {
            model = new KUSModel(jsonObject);
        } catch (KUSInvalidJsonException e) {
            KUSLog.kusLogError(e.getMessage());
        }

        if (model != null) {
            arrayList = new ArrayList<>();
            arrayList.add(model);
        }

        return arrayList;
    }

    @Nullable
    public List<KUSModel> objectsFromJSONArray(@Nullable JSONArray jsonArray) {
        if (jsonArray == null)
            return null;

        ArrayList<KUSModel> objects = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                List<KUSModel> models = objectsFromJSON(jsonObject);
                if (models != null) {
                    for (int j = models.size() - 1; j >= 0; j--) {
                        objects.add(models.get(j));
                    }
                }
            } catch (JSONException e) {
                KUSLog.kusLogError(e.getMessage());
            }
        }
        return objects;
    }

    //endregion

    //region Accessors

    @Nullable
    public KUSUserSession getUserSession() {
        return userSession.get();
    }

    public void setUserSession(@NonNull KUSUserSession userSession) {
        this.userSession = new WeakReference<>(userSession);
    }

    //endregion

    // region Notifier
    void notifyAnnouncersOnContentChange() {
        for (KUSPaginatedDataSourceListener listener : listeners) {
            if (listener != null)
                listener.onContentChange(this);
        }
    }

    void notifyAnnouncersOnError(@Nullable Error error) {
        for (KUSPaginatedDataSourceListener listener : listeners) {
            if (listener != null)
                listener.onError(this, error);
        }
    }

    private void notifyAnnouncersOnLoad() {
        for (KUSPaginatedDataSourceListener listener : listeners) {
            if (listener != null)
                listener.onLoad(this);
        }
    }
    //endregion

}
