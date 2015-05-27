package org.starfishrespect.myconsumption.android.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

import org.starfishrespect.myconsumption.android.R;

import static org.starfishrespect.myconsumption.android.util.LogUtils.LOGI;
import static org.starfishrespect.myconsumption.android.util.LogUtils.makeLogTag;


/**
 * Utilities and constants related to app preferences.
 */
public class PrefUtils  {
    private static final String TAG = makeLogTag("PrefUtils");

    public static final String PREF_PROFILE_PRO = "pref_profile_professional";
    public static final String PREF_PROFILE_ANNUAL = "pref_profile_annual_consumption";
    public static final String PREF_PROFILE_HOUSE = "pref_profile_house";
    public static final String PREF_SYNC_REFRESH = "pref_sync_refresh";

    // For registration id (google play services)
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

    public static int getSyncRefreshIndex(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.valueOf(sp.getString(PREF_SYNC_REFRESH, "1"));
    }

    public static int getSyncRefresh(final Context context) {
        int position = getSyncRefreshIndex(context);
        if (position < 0)
            return -1;
        else
            return context.getResources().getIntArray(R.array.pref_sync_refresh_minutes_values)[position];
    }

    public static int getProfileIndex(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.valueOf(sp.getString(PREF_PROFILE_HOUSE, "1"));
    }

    public static String getProfileTextDescription(final Context context) {
        int position = getProfileIndex(context);
        if (position < 0)
            return "";
        else
            return context.getResources().getStringArray(R.array.pref_house_entries)[position];
    }

    public static int getProfileConsumption(final Context context) {
        int position = getProfileIndex(context);
        if (position < 0)
            return -1;
        else
            return context.getResources().getIntArray(R.array.pref_house_kwh_values)[position];
    }

    public static void registerOnSharedPreferenceChangeListener(final Context context,
                                                                SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unregisterOnSharedPreferenceChangeListener(final Context context,
                                                                  SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.unregisterOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Gets the current registration ID for application on GCM service
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    public static String getRegistrationId(Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            LOGI(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    public static void setRegistrationId(Context context, String id) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, id);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
}
