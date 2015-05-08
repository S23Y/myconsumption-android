package org.starfishrespect.myconsumption.android.tasks;

import android.os.AsyncTask;
import org.starfishrespect.myconsumption.android.data.SensorData;
import org.starfishrespect.myconsumption.android.data.UserData;
import org.starfishrespect.myconsumption.server.api.dto.SensorDTO;
import org.starfishrespect.myconsumption.server.api.dto.UserDTO;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.starfishrespect.myconsumption.android.SingleInstance;

/**
 * Task that retrieves all the data of an user from the server
 */
public class UserUpdater extends AsyncTask<Void, UserData, Void> {

    /**
     * Callback for the result
     */
    public interface GetUserCallback {
        public void userFound(UserData user);

        public void userRetrieveError(Exception e);
    }

    private GetUserCallback getUserCallback;
    private String username;

    public UserUpdater(String username) {
        this.username = username;
    }

    public UserUpdater setGetUserCallback(GetUserCallback getUserCallback) {
        this.getUserCallback = getUserCallback;
        return this;
    }

    @Override
    protected Void doInBackground(Void... params) {
        RestTemplate template = new RestTemplate();
        template.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
        try {
            UserDTO user = template.getForObject(SingleInstance.getServerUrl() + "users/" + username, UserDTO.class);
            UserData userData = new UserData(user);
            for (String sensor : user.getSensors()) {
                try {
                    SensorDTO SensorDTO = template.getForObject(SingleInstance.getServerUrl() + "sensors/" + sensor, SensorDTO.class);
                    userData.addSensor(new SensorData(SensorDTO));
                } catch (RestClientException e) {
                    e.printStackTrace();
                }
            }
            publishProgress(userData);
        } catch (RestClientException e) {
            e.printStackTrace();
            publishProgress(null);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(UserData... values) {

        if (getUserCallback != null) {
            if (values == null || values.length == 0 || values[0] == null) {
                getUserCallback.userRetrieveError(new NullPointerException());
                return;
            }
            getUserCallback.userFound(values[0]);
        }
    }


}
