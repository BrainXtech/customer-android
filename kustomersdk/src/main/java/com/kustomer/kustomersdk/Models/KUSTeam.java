package com.kustomer.kustomersdk.Models;

import android.support.annotation.Nullable;

import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Helpers.KUSLog;
import com.kustomer.kustomersdk.Utils.JsonHelper;

import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSTeam extends KUSModel {

    //region Properties
    @Nullable
    private String emoji;
    @Nullable
    private String displayName;
    @Nullable
    private String icon;
    //endregion

    //region LifeCycleMethods
    public KUSTeam(@Nullable JSONObject json) throws KUSInvalidJsonException {
        super(json);

        displayName = JsonHelper.stringFromKeyPath(json,"attributes.displayName");
        icon = JsonHelper.stringFromKeyPath(json, "attributes.icon");

        try {

            if(icon != null) {

                String [] unicodes = icon.split("-");
                StringBuilder text = new StringBuilder();
                byte [] bytes = null;

                long emojiInt = Long.parseLong(unicodes[0], 16);
                ByteBuffer b = ByteBuffer.allocate(8);
                b.order(ByteOrder.LITTLE_ENDIAN);
                b.putLong(emojiInt);

                bytes = b.array();

                if(unicodes.length > 1){
                    long emojiInt2 = Long.parseLong(unicodes[1], 16);
                    ByteBuffer b2 = ByteBuffer.allocate(8);
                    b2.order(ByteOrder.LITTLE_ENDIAN);
                    b2.putLong(emojiInt2);

                    bytes[4] = b2.array()[0];
                    bytes[5] = b2.array()[1];
                    bytes[6] = b2.array()[2];
                    bytes[7] = b2.array()[3];
                }

                text.append(new String(bytes,"UTF-32LE"));

                emoji = text.toString();
            }

        } catch (Exception e) {
            KUSLog.kusLogError(e.getMessage());
        }
    }

    @Nullable
    public String fullDisplay(){
        if(emoji != null){
            return String.format("%s %s",emoji,displayName);
        }

        return displayName;
    }
    //endregion

    //region Class methods
    @Nullable
    public String modelType(){
        return null;
    }
    public boolean enforcesModelType(){
        return false;
    }
    //endregion

    //region Accessors

    @Nullable
    public String getDisplayName() {
        return displayName;
    }

    //endregion
}
