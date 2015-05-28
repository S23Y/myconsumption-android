package org.starfishrespect.myconsumption.android.tasks;

import android.os.AsyncTask;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.starfishrespect.myconsumption.android.data.SensorData;
import org.starfishrespect.myconsumption.android.data.UserData;
import org.starfishrespect.myconsumption.android.util.CryptoUtils;
import org.starfishrespect.myconsumption.server.api.dto.SensorDTO;
import org.starfishrespect.myconsumption.server.api.dto.UserDTO;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.starfishrespect.myconsumption.android.SingleInstance;

/**
 * Task that retrieves all the data of an user from the server
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
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
    private String password;

    public UserUpdater(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public UserUpdater setGetUserCallback(GetUserCallback getUserCallback) {
        this.getUserCallback = getUserCallback;
        return this;
    }

    @Override
    protected Void doInBackground(Void... params) {
        RestTemplate template = new RestTemplate();
        HttpHeaders httpHeaders = CryptoUtils.createHeaders(username, password);
        template.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
        try {
            ResponseEntity<UserDTO> response = template.exchange(SingleInstance.getServerUrl() + "users/" + username,
                    HttpMethod.GET, new HttpEntity<>(httpHeaders), UserDTO.class);
            UserDTO user = response.getBody();
            UserData userData = new UserData(user);
            for (String sensor : user.getSensors()) {
                try {
                    ResponseEntity<SensorDTO> responseSensor = template.exchange(SingleInstance.getServerUrl() + "sensors/" + sensor,
                            HttpMethod.GET, new HttpEntity<>(httpHeaders), SensorDTO.class);
                    SensorDTO SensorDTO = responseSensor.getBody();
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
