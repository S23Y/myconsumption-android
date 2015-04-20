package org.starfishrespect.myconsumption.android.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.starfishrespect.myconsumption.android.R;

import static org.starfishrespect.myconsumption.android.util.LogUtils.makeLogTag;


/**
 * Utilities and constants related to app preferences.
 */
public class PrefUtils  {
    private static final String TAG = makeLogTag("PrefUtils");

    public static final String PREF_PROFILE_PRO = "pref_profile_professional";
    public static final String PREF_PROFILE_ANNUAL = "pref_profile_annual_consumption";
    public static final String PREF_PROFILE_HOUSE = "pref_profile_house";

    public static int getProfileIndex(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.valueOf(sp.getString(PREF_PROFILE_HOUSE, ""));
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
}
