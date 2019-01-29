package com.kustomer.kustomersdk.Models;

import android.support.annotation.Nullable;

import com.kustomer.kustomersdk.DataSources.KUSPaginatedDataSource;
import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Utils.JsonHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSPaginatedResponse {

    //region Properties
    private List<KUSModel> objects;
    private int page;
    private int pageSize;

    @Nullable
    private String selfPath;
    @Nullable
    private String firstPath;
    @Nullable
    private String prevPath;
    @Nullable
    private String nextPath;
    //endregion

    //region LifeCycle
    public KUSPaginatedResponse() {

    }

    public KUSPaginatedResponse(JSONObject json, KUSPaginatedDataSource dataSource)
            throws JSONException, KUSInvalidJsonException {
        if (json == null)
            return;

        Object data = json.get("data");
        boolean dataIsArray = data.getClass().equals(JSONArray.class);
        boolean dataIsJsonObject = data.getClass().equals(JSONObject.class);

        if (!dataIsArray && !dataIsJsonObject)
            throw new KUSInvalidJsonException("Json Format for \"data\" is invalid.");

        ArrayList<KUSModel> objects = new ArrayList<>();

        if (dataIsArray) {
            JSONArray array = (JSONArray) json.get("data");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);

                List<KUSModel> models = dataSource.objectsFromJSON(jsonObject);
                if (models != null) {
                    for (int j = models.size() - 1; j >= 0; j--) {
                        objects.add(models.get(j));
                    }
                }
            }
        } else {
            List<KUSModel> models = dataSource.objectsFromJSON(json);

            if (models != null) {
                for (int j = models.size() - 1; j >= 0; j--) {
                    objects.add(models.get(j));
                }
            }
        }

        this.objects = objects;

        page = JsonHelper.integerFromKeyPath(json, "meta.page");

        int a = JsonHelper.integerFromKeyPath(json, "meta.pageSize");
        pageSize = Math.max(a, objects.size());

        selfPath = JsonHelper.stringFromKeyPath(json, "links.self");
        firstPath = JsonHelper.stringFromKeyPath(json, "links.first");
        prevPath = JsonHelper.stringFromKeyPath(json, "links.prev");
        nextPath = JsonHelper.stringFromKeyPath(json, "links.next");
    }

    //region Accessors

    public List<KUSModel> getObjects() {
        return objects;
    }

    public void setObjects(List<KUSModel> objects) {
        this.objects = objects;
    }

    public int getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Nullable
    public String getSelfPath() {
        return selfPath;
    }

    public void setSelfPath(@Nullable String selfPath) {
        this.selfPath = selfPath;
    }

    @Nullable
    public String getFirstPath() {
        return firstPath;
    }

    public void setFirstPath(@Nullable String firstPath) {
        this.firstPath = firstPath;
    }

    @Nullable
    public String getPrevPath() {
        return prevPath;
    }

    public void setPrevPath(@Nullable String prevPath) {
        this.prevPath = prevPath;
    }

    @Nullable
    public String getNextPath() {
        return nextPath;
    }

    public void setNextPath(@Nullable String nextPath) {
        this.nextPath = nextPath;
    }

    //endregion
}
