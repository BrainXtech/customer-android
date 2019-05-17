package com.kustomer.kustomersdk.Helpers;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.os.LocaleListCompat;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;

import com.kustomer.kustomersdk.R;

import java.util.Locale;

public class KUSLocalization {
    //region properties

    private static KUSLocalization localizationManager;
    private Locale dLocale;
    private Locale uLocale;

    //endregion

    //region LifeCycle
    private KUSLocalization() {
    }

    public static KUSLocalization getSharedInstance() {
        if (localizationManager == null) {
            localizationManager = new KUSLocalization();
        }
        return localizationManager;
    }
    //endregion

    //region Private Methods

    private boolean isLocaleResourceAvailable(@NonNull Context mContext,@NonNull Locale locale) {

        Configuration config = new Configuration(mContext.getResources().getConfiguration());

        config.setLocale(locale);
        String result = mContext.createConfigurationContext(config).getResources().getString(R.string.com_kustomer_chat_with);

        config.setLocale(Locale.ENGLISH);
        String englishString = mContext.createConfigurationContext(config).getResources().getString(R.string.com_kustomer_chat_with);

        if (result.equals(englishString)) {
            return locale.getLanguage().equals(Locale.ENGLISH.getLanguage());
        }

        return true;

    }

    private Locale getAvailableLocale(@NonNull Context mContext) {
        LocaleListCompat localeList = LocaleListCompat.getDefault();
        for (int i = 0; i < localeList.size(); i++) {
            if (isLocaleResourceAvailable(mContext, localeList.get(i))) {
                return localeList.get(i);
            } else {
                Locale lanLocale = new Locale(localeList.get(i).getLanguage());
                if (isLocaleResourceAvailable(mContext, lanLocale))
                    return localeList.get(i);
            }
        }
        return Locale.ENGLISH;
    }

    //endregion

    //region Public Methods

    public Locale getUserLocale() {
        return uLocale;
    }

    public void setUserLocale(@Nullable Locale locale) {
        uLocale = locale;
    }

    public void updateKustomerLocaleWithFallback(@NonNull Context mContext) {
        if (uLocale == null)
            uLocale = Locale.getDefault();

        Locale[] locales = Locale.getAvailableLocales();

        for (Locale loc : locales) {
            if (loc.equals(uLocale)) {
                dLocale = uLocale;
                break;
            }
        }

        if (dLocale != null) {
            if (isLocaleResourceAvailable(mContext, dLocale))
                return;
        }

        dLocale = getAvailableLocale(mContext);

    }

    public String localizedString(@NonNull Context mContext,@Nullable String key) {
        String packageName = mContext.getPackageName();
        int resId = mContext.getResources().getIdentifier(key, "string", packageName);
        if (resId == 0)
            return key;
        else {
            return mContext.getString(resId);
        }
    }

    public void updateConfig(@NonNull ContextThemeWrapper wrapper) {
        if (dLocale != null) {
            Locale.setDefault(dLocale);
            Configuration configuration = new Configuration();
            configuration.setLocale(dLocale);
            wrapper.applyOverrideConfiguration(configuration);
        }
    }

    public void updateConfig(@NonNull Context mContext) {
        if (dLocale != null) {
            Locale.setDefault(dLocale);
            Configuration configuration = mContext.getResources().getConfiguration();
            configuration.setLocale(dLocale);
            mContext.getResources().updateConfiguration(configuration, mContext.getResources().getDisplayMetrics());
        }
    }

    public boolean isLTR() {
        return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == View.LAYOUT_DIRECTION_LTR;
    }

    //endregion
}
