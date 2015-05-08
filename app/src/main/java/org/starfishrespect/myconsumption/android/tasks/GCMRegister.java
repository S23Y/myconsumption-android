package org.starfishrespect.myconsumption.android.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.starfishrespect.myconsumption.android.Config;
import org.starfishrespect.myconsumption.android.SingleInstance;
import org.starfishrespect.myconsumption.android.util.PrefUtils;
import org.starfishrespect.myconsumption.server.api.dto.SimpleResponseDTO;

import java.io.IOException;
import java.util.List;

import static org.starfishrespect.myconsumption.android.util.LogUtils.LOGD;
import static org.starfishrespect.myconsumption.android.util.LogUtils.LOGI;
import static org.starfishrespect.myconsumption.android.util.LogUtils.makeLogTag;

/**
 * Created by thibaud on 08.05.15.
 */
public class GCMRegister {
    private static final String TAG = makeLogTag(GCMRegister.class);
    private GoogleCloudMessaging gcm;

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    public void registerInBackground(final Context context) {
        AsyncTask<Void, List, String> task = new AsyncTask<Void, List, String>() {
            @Override
            protected String doInBackground(Void... params) {
                RestTemplate template = new RestTemplate();
                template.getMessageConverters().add(new FormHttpMessageConverter());
                template.getMessageConverters().add(new StringHttpMessageConverter());
                String msg = "";

                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    String regid = gcm.register(Config.SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // Send the registration ID to the server
                    String url = SingleInstance.getServerUrl() + "notifs/"
                            + SingleInstance.getUserController().getUser().getName() + "/id/" + regid;
                    String result = template.postForObject(url, null, String.class);
                    LOGD(TAG, result);

                    SimpleResponseDTO response = new ObjectMapper().readValue(result, SimpleResponseDTO.class);

                    if (response.getStatus() != SimpleResponseDTO.STATUS_SUCCESS) {
                        msg = "Error: " + response.getStatus() + " Cannot post register id on server side.";
                    }

                    // Persist the registration ID - no need to register again.
                    PrefUtils.setRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error:" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                LOGI(TAG, msg);
            }
        };

        task.execute();
    }
}
