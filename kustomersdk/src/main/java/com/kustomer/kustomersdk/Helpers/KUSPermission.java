package com.kustomer.kustomersdk.Helpers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

import static android.support.v4.content.ContextCompat.checkSelfPermission;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSPermission {

    //region Private Methods
    private static boolean hasSelfPermission(@NonNull Context context, String permission) {
        try {
            return checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
        } catch (RuntimeException t) {
            return false;
        }
    }
    private static boolean isPermissionDeclared(@NonNull Context context, String permission){
        PackageManager pm = context.getPackageManager();
        try
        {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] requestedPermissions = null;
            if (packageInfo != null) {
                requestedPermissions = packageInfo.requestedPermissions;
            }

            if (requestedPermissions != null && requestedPermissions.length > 0)
            {
                List<String> requestedPermissionsList = Arrays.asList(requestedPermissions);
                return requestedPermissionsList.contains(permission);
            }
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

        return false;
    }
    //endregion

    //region Public Methods
    public static boolean isCameraPermissionDeclared(@NonNull Context context){
        return isPermissionDeclared(context, Manifest.permission.CAMERA);
    }

    public static boolean isReadPermissionDeclared(@NonNull Context context){
        return isPermissionDeclared(context, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public static boolean isCameraPermissionAvailable(@NonNull Context context) {
        return hasSelfPermission(context, Manifest.permission.CAMERA);
    }

    public static boolean isStoragePermissionAvailable(@NonNull Context context) {
        return hasSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
    }
    public static boolean checkAudioPermission(@NonNull Context context) {
        return hasSelfPermission(context, Manifest.permission.RECORD_AUDIO);
    }
    //endregion

}
