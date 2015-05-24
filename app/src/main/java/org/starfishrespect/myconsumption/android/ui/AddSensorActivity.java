package org.starfishrespect.myconsumption.android.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.util.Log;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import org.starfishrespect.myconsumption.android.Config;
import org.starfishrespect.myconsumption.android.R;
import org.starfishrespect.myconsumption.android.SingleInstance;
import org.starfishrespect.myconsumption.android.dao.DatabaseHelper;
import org.starfishrespect.myconsumption.android.data.KeyValueData;
import org.starfishrespect.myconsumption.android.data.SensorData;
import org.starfishrespect.myconsumption.android.data.UserData;
import org.starfishrespect.myconsumption.android.util.CryptoUtils;
import org.starfishrespect.myconsumption.android.util.MiscFunctions;
import org.starfishrespect.myconsumption.android.sensorviews.AbstractSensorView;
import org.starfishrespect.myconsumption.android.sensorviews.SensorViewFactory;
import org.starfishrespect.myconsumption.server.api.dto.SimpleResponseDTO;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Add sensor Activity
 */
public class AddSensorActivity extends BaseActivity {

    private static final String TAG = "AddSensorActivity";

    private Spinner spinnerSensorType;
    private EditText editTextSensorName;
    private Button buttonCreateSensor;
    private LinearLayout layoutSensorSpecificSettings;
    private String sensorTypes[] = {"Flukso"};
    private String selectedSensorType = "flukso";
    private AbstractSensorView sensorView;
    private boolean edit = false;
    private SensorData editSensor;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sensor);

        Toolbar toolbar = getActionBarToolbar();
        getSupportActionBar().setTitle(getString(R.string.title_add_sensor));
        toolbar.setNavigationIcon(R.drawable.ic_up);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateUpToFromChild(AddSensorActivity.this,
                        IntentCompat.makeMainActivity(new ComponentName(AddSensorActivity.this,
                                ChartActivity.class)));
            }
        });

        spinnerSensorType = (Spinner) findViewById(R.id.spinnerSensorType);
        editTextSensorName = (EditText) findViewById(R.id.editTextSensorName);
        layoutSensorSpecificSettings = (LinearLayout) findViewById(R.id.layoutSensorSpecificSettings);

        buttonCreateSensor = (Button) findViewById(R.id.buttonCreateSensor);

        if (getIntent().getExtras() != null) {
            Bundle b = getIntent().getExtras();
            if (b.containsKey("edit")) {
                try {
                    editSensor = SingleInstance.getDatabaseHelper().getSensorDao().queryForId(b.getString("edit"));
                    edit = true;
                    editTextSensorName.setText(editSensor.getName());
                    sensorTypes = new String[1];
                    sensorTypes[0] = editSensor.getType();
                    layoutSensorSpecificSettings.removeAllViews();
                    selectedSensorType = sensorTypes[0].toLowerCase();
                    sensorView = SensorViewFactory.makeView(AddSensorActivity.this, selectedSensorType);
                    sensorView.setEditMode(true);
                    layoutSensorSpecificSettings.addView(sensorView);
                    buttonCreateSensor.setText(R.string.button_edit_sensor);
                } catch (SQLException e) {
                    finish();
                }
            }
        }

        spinnerSensorType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sensorTypes));

        if (!edit) {
            spinnerSensorType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    layoutSensorSpecificSettings.removeAllViews();
                    selectedSensorType = sensorTypes[position].toLowerCase();
                    sensorView = SensorViewFactory.makeView(AddSensorActivity.this, selectedSensorType);
                    layoutSensorSpecificSettings.addView(sensorView);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            buttonCreateSensor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!MiscFunctions.isOnline(AddSensorActivity.this)) {
                        MiscFunctions.makeOfflineDialog(AddSensorActivity.this).show();
                        return;
                    }
                    if (editTextSensorName.getText().toString().equals("")
                            || !sensorView.areSettingsValid()) {
                        new AlertDialog.Builder(AddSensorActivity.this)
                                .setTitle(R.string.dialog_title_error)
                                .setMessage("You must fill all the fields in order to add a sensor !")
                                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        return;
                    }


                    new AsyncTask<Void, Boolean, Void>() {
                        private ProgressDialog waitingDialog;

                        @Override
                        protected void onPreExecute() {
                            waitingDialog = new ProgressDialog(AddSensorActivity.this);
                            waitingDialog.setTitle(R.string.dialog_title_loading);
                            waitingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            waitingDialog.setMessage(getResources().getString(R.string.dialog_message_loading_creating_sensor));
                            waitingDialog.show();
                        }

                        @Override
                        protected Void doInBackground(Void... params) {
                            publishProgress(create());
                            return null;
                        }

                        @Override
                        protected void onProgressUpdate(Boolean... values) {
                            for (boolean b : values) {
                                if (b) {
                                    new AlertDialog.Builder(AddSensorActivity.this)
                                            .setTitle(R.string.dialog_title_information)
                                            .setMessage(R.string.dialog_message_information_sensor_added)
                                            .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    setResult(RESULT_OK);
                                                    Intent intent = new Intent(AddSensorActivity.this, ChartActivity.class);
                                                    intent.putExtra(Config.EXTRA_FIRST_LAUNCH, true);
                                                    startActivity(intent);
                                                }
                                            }).show();
                                } else {
                                    new AlertDialog.Builder(AddSensorActivity.this)
                                            .setTitle(R.string.dialog_title_error)
                                            .setMessage(R.string.dialog_message_error_sensor_not_added)
                                            .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).show();
                                }
                            }
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            waitingDialog.dismiss();
                        }
                    }.execute();
                }
            });
        } else {
            buttonCreateSensor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AsyncTask<Void, Boolean, Void>() {
                        private ProgressDialog waitingDialog;

                        @Override
                        protected void onPreExecute() {
                            waitingDialog = new ProgressDialog(AddSensorActivity.this);
                            waitingDialog.setTitle(R.string.dialog_title_loading);
                            waitingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            waitingDialog.setMessage(getResources().getString(R.string.dialog_message_loading_editing_sensor));
                            waitingDialog.show();
                        }

                        @Override
                        protected Void doInBackground(Void... params) {
                            publishProgress(edit());
                            return null;
                        }

                        @Override
                        protected void onProgressUpdate(Boolean... values) {
                            for (boolean b : values) {
                                if (b) {
                                    new AlertDialog.Builder(AddSensorActivity.this)
                                            .setTitle(R.string.dialog_title_information)
                                            .setMessage(R.string.dialog_message_information_sensor_edited)
                                            .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    setResult(Activity.RESULT_OK);
                                                    finish();
                                                }
                                            }).show();
                                } else {
                                    new AlertDialog.Builder(AddSensorActivity.this)
                                            .setTitle(R.string.dialog_title_error)
                                            .setMessage(R.string.dialog_message_error_sensor_not_added)
                                            .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).show();
                                }
                            }
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            waitingDialog.dismiss();
                        }
                    }.execute();
                }
            });

        }
    }

    private boolean create() {
        DatabaseHelper db = new DatabaseHelper(this);
        String user = null;
        KeyValueData userJson = db.getValueForKey("user");
        ObjectMapper mapper = new ObjectMapper();
        if (userJson != null) {

            try {
                user = mapper.readValue(userJson.getValue(), UserData.class).getName();
            } catch (IOException e) {
                return false;
            }
        }
        if (user == null) {
            return false;
        }
        RestTemplate template = new RestTemplate();
        HttpHeaders httpHeaders = CryptoUtils.createHeadersCurrentUser();
        ResponseEntity<String> responseEnt;
        template.getMessageConverters().add(new FormHttpMessageConverter());
        template.getMessageConverters().add(new StringHttpMessageConverter());

        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(SingleInstance.getServerUrl() + "sensors/")
                    .queryParam("name", editTextSensorName.getText().toString())
                    .queryParam("type", selectedSensorType)
                    .queryParam("user", user)
                    .queryParam("settings", mapper.writeValueAsString(sensorView.getSensorSettings()));

            responseEnt = template.exchange(builder.build().encode().toUri(),
                    HttpMethod.POST, new HttpEntity<>(httpHeaders), String.class);

            String result = responseEnt.getBody();
            Log.d(TAG, result);

            SimpleResponseDTO response = mapper.readValue(result, SimpleResponseDTO.class);
            if (response.getStatus() == 0) {
                return true;
            }
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private boolean edit() {
        ObjectMapper mapper = new ObjectMapper();
        RestTemplate template = new RestTemplate();
        HttpHeaders httpHeaders = CryptoUtils.createHeadersCurrentUser();
        ResponseEntity<String> responseEnt;
        template.getMessageConverters().add(new FormHttpMessageConverter());
        template.getMessageConverters().add(new StringHttpMessageConverter());

        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(SingleInstance.getServerUrl() + "sensors/" + editSensor.getSensorId())
                    .queryParam("name", editTextSensorName.getText().toString())
                    .queryParam("type", selectedSensorType)
                    .queryParam("settings", mapper.writeValueAsString(sensorView.getSensorSettings()));

            responseEnt = template.exchange(builder.build().encode().toUri(),
                    HttpMethod.POST, new HttpEntity<>(httpHeaders), String.class);

            String result = responseEnt.getBody();

            Log.d(TAG, result);

            SimpleResponseDTO response = mapper.readValue(result, SimpleResponseDTO.class);
            if (response.getStatus() == 0) {
                return true;
            }
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }


}