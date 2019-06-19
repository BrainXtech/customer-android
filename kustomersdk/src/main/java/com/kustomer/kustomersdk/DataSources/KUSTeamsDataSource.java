package com.kustomer.kustomersdk.DataSources;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Helpers.KUSLog;
import com.kustomer.kustomersdk.Models.KUSModel;
import com.kustomer.kustomersdk.Models.KUSTeam;
import com.kustomer.kustomersdk.Utils.KUSConstants;
import com.kustomer.kustomersdk.Utils.KUSUtils;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSTeamsDataSource extends KUSPaginatedDataSource {

    //region Properties
    @NonNull
    private List<String> teamIds;
    //endregion

    //region Initializer
    public KUSTeamsDataSource(@NonNull KUSUserSession userSession,@NonNull List<String> teamIds) {
        super(userSession);
        this.teamIds = new ArrayList<>(teamIds);
    }

    @NonNull
    public List<String> getTeamIds() {
        return teamIds;
    }
    //endregion

    //region subclass methods
    @Nullable
    public URL getFirstUrl() {
        if (getUserSession() == null)
            return null;

        String endPoint = String.format(KUSConstants.URL.TEAMS_ENDPOINT,
                KUSUtils.listJoinedByString(teamIds, ","));

        return getUserSession().getRequestManager().urlForEndpoint(endPoint);
    }

    @Nullable
    @Override
    public List<KUSModel> objectsFromJSON(@Nullable JSONObject jsonObject) {
        ArrayList<KUSModel> arrayList = null;

        KUSModel model = null;
        try {
            model = new KUSTeam(jsonObject);
        } catch (KUSInvalidJsonException e) {
            KUSLog.kusLogError(e.getMessage());
        }

        if (model != null) {
            arrayList = new ArrayList<>();
            arrayList.add(model);
        }

        return arrayList;
    }
    //endregion
}
