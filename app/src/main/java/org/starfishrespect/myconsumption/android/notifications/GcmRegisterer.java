/*
package org.starfishrespect.myconsumption.android.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import org.starfishrespect.myconsumption.android.MainActivity;
import org.starfishrespect.myconsumption.android.dao.SingleInstance;
import org.starfishrespect.myconsumption.android.misc.Constants;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

*/
/**
 * Utility class that registers the application to Google Cloud Messaging (GCM)
 * to allow push notifications from the server
 *//*

public class GcmRegisterer {

    private static final String TAG = "GcmRegisterer";
    private GoogleCloudMessaging gcm;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PROPERTY_SERVER_HAS_REG_ID = "server_has_reg_id";
    private String regid;

    private Context context;
    private String username;

    public GcmRegisterer(Context context, String username) {
        this.context = context;
        this.username = username;
    }

    */
/**
     * Tries to get an id from the GCM service if no id is available, and send it
     * to the server
     *//*


    public void register() {
        gcm = GoogleCloudMessaging.getInstance(context);
        regid = getRegistrationId(context);
        Log.d(TAG, "regid is : " + regid);
        if (regid.equals("")) {
            registerInBackground();
        } else {
            sendRegistrationIdToBackend();
        }
    }

    */
/**
     * Tries to remove the id of the application from the user, if it has been sent to the server
     *//*


    public void unregister() {
        gcm = GoogleCloudMessaging.getInstance(context);
        regid = getRegistrationId(context);
        if (!regid.equals("")) {
            removeRegistrationIdOfBackend();
        }
    }

    */
/*
     * GCM Registration code
     * Source inspired from http://developer.android.com/google/gcm/client.html
     *//*


    */
/**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param regId registration ID
     *//*

    private void storeRegistrationId(String regId) {
        final SharedPreferences prefs = getGcmPreferences();
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    */
/**
     * @return Application's version code from the {@code PackageManager}.
     *//*

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

    */
/**
     * @return Application's {@code SharedPreferences}.
     *//*

    private SharedPreferences getGcmPreferences() {
        return context.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(Constants.GCM_SENDER_ID);
                    sendRegistrationIdToBackend();
                    storeRegistrationId(regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
            }
        }.execute(null, null, null);
    }

    private void sendRegistrationIdToBackend() {
        final SharedPreferences prefs = getGcmPreferences();
        if (prefs.getBoolean(PROPERTY_SERVER_HAS_REG_ID, false)) {
            return;
        }
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                RestTemplate template = new RestTemplate();
                template.getMessageConverters().add(new FormHttpMessageConverter());
                template.getMessageConverters().add(new StringHttpMessageConverter());
                MultiValueMap<String, String> postParams = new LinkedMultiValueMap<>();
                postParams.add("device_type", "android");
                postParams.add("token", regid);
                try {
                    String result = template.postForObject(SingleInstance.getServerUrl() + "user/" + username + "/token", postParams, String.class);
                    Log.d(TAG, result);
                    prefs.edit().putBoolean(PROPERTY_SERVER_HAS_REG_ID, true);
                } catch (ResourceAccessException | HttpClientErrorException | HttpServerErrorException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private void removeRegistrationIdOfBackend() {
        final SharedPreferences prefs = getGcmPreferences();
        if (!prefs.getBoolean(PROPERTY_SERVER_HAS_REG_ID, false)) {
            return;
        }
        // TODO no API available on the server yet
    }


    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences();
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.equals("")) {
            registerInBackground();
        }
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            registerInBackground();
        }
        return registrationId;
    }

    */
/*
     * End GCM Registration
     *//*

}
*/
