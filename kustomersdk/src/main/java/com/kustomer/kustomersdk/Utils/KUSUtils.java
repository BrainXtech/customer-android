package com.kustomer.kustomersdk.Utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.kustomer.kustomersdk.Helpers.KUSLog;

import java.io.File;
import java.util.List;

/**
 * Created by Junaid on 1/26/2018.
 */

public class KUSUtils {

    public static final double MIN_TABLET_SIZE_IN_INCH = 6.5;
    private static final String AUTHORITY_SUFFIX = ".kustomersdk";
    private static final String HTTP_SCHEMA = "http://";

    @Nullable
    public static Drawable getDrawableForKey(@NonNull Context mContext, @NonNull String key) {
        String packageName = mContext.getPackageName();
        int resId = mContext.getResources().getIdentifier(key, "drawable", packageName);
        return resId == 0 ? null : mContext.getResources().getDrawable(resId);
    }

    public static void showShortToast(@NonNull Context context, @Nullable String message) {
        if (message != null)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static int getWindowHeight(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public static void openUrl(@NonNull Context context, @NonNull String url){
        try {
            Uri uri = Uri.parse(url.toLowerCase());

            if (uri.getScheme() == null)
                uri = Uri.parse(HTTP_SCHEMA + url.toLowerCase());

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(browserIntent);
        }catch (ActivityNotFoundException exception){
            KUSLog.KUSLogError(exception.getMessage());
        }
    }

    @Nullable
    public static String KUSUnescapeBackslashesFromString(@Nullable String string) {
        if (string == null)
            return null;

        String updatedString = "";

        int startingIndex = 0;
        for (int i = 0; i < string.length(); i++) {
            String character = string.substring(i, i + 1);
            if (character.equals("\\")) {
                String lastString = string.substring(startingIndex, i);
                updatedString = updatedString.concat(lastString);

                i++;
                startingIndex = i;
            }
        }

        String endingString = string.substring(startingIndex);
        updatedString = updatedString.concat(endingString);

        //remove zero_width_white_space from updatedString
        updatedString = updatedString.replaceAll("\u200B", "");

        return updatedString;
    }

    public static String removeNonASCIIChars(String original) {

        if (original != null)
            return original.replaceAll("[^\\p{ASCII}]", "");
        else
            return null;
    }

    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public static String listJoinedByString(List<String> list, String join) {
        StringBuilder joinedString = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {

            if (i < list.size() - 1)
                joinedString.append(list.get(i)).append(join);
            else
                joinedString.append(list.get(i));
        }

        return joinedString.toString();
    }

    public static boolean isPhone(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        float yInches = metrics.heightPixels / metrics.ydpi;
        float xInches = metrics.widthPixels / metrics.xdpi;
        double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);

        return diagonalInches < MIN_TABLET_SIZE_IN_INCH;
    }

    public static void showKeyboard(final View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }

    public static void hideKeyboard(final View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Nullable
    public static Uri getUriFromFile(Context context, File file) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                return Uri.fromFile(file);
            } else {
                return FileProvider.getUriForFile(context,
                        context.getPackageName() + AUTHORITY_SUFFIX,
                        file);
            }
        } catch (Exception e) {
            KUSLog.KUSLogError(e.getMessage());
            return null;
        }
    }
}
