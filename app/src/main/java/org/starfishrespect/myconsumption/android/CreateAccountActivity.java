package org.starfishrespect.myconsumption.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.starfishrespect.myconsumption.android.dao.SingleInstance;
import org.starfishrespect.myconsumption.android.misc.MiscFunctions;
import org.starfishrespect.myconsumption.server.api.dto.SimpleResponseDTO;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * Create account activity
 */
public class CreateAccountActivity extends Activity {

    private static final String TAG = "createAccountActivity";

    public EditText editTextUsername, editTextPassword, editTextPassword2;
    public Button buttonCreateAccount;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextPassword2 = (EditText) findViewById(R.id.editTextPassword2);
        buttonCreateAccount = (Button) findViewById(R.id.buttonCreateAccount);
        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MiscFunctions.isOnline(CreateAccountActivity.this)) {
                    MiscFunctions.makeOfflineDialog(CreateAccountActivity.this).show();
                    return;
                }
                if (editTextUsername.getText().toString().equals("") || editTextPassword.getText().toString().equals("")
                        || editTextPassword2.getText().toString().equals("")) {
                    Toast.makeText(CreateAccountActivity.this, getString(R.string.dialog_message_error_must_fill_all_fields), Toast.LENGTH_LONG).show();
                    return;
                }

                if (!editTextPassword.getText().toString().equals(editTextPassword2.getText().toString())) {
                    Toast.makeText(CreateAccountActivity.this, R.string.dialog_message_error_password_must_match, Toast.LENGTH_LONG).show();
                    return;
                }

                new AsyncTask<Void, Void, Integer>() {

                    @Override
                    protected Integer doInBackground(Void... params) {
                        return createAccount();
                    }

                    @Override
                    protected void onPostExecute(Integer result) {
                        if (result == 0) {
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccountActivity.this);
                            builder.setTitle(R.string.dialog_title_error);
                            builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            switch (result) {
                                case SimpleResponseDTO.STATUS_ALREADY_EXISTS:
                                    builder.setMessage(R.string.dialog_message_error_user_already_exists);
                                    break;
                                default:
                                    builder.setMessage("Unknown error");
                                    break;

                            }
                            builder.show();
                        }
                    }
                }.execute();
            }
        });
    }

    private int createAccount() {
        RestTemplate template = new RestTemplate();
        template.getMessageConverters().add(new FormHttpMessageConverter());
        template.getMessageConverters().add(new StringHttpMessageConverter());
        MultiValueMap<String, String> postParams = new LinkedMultiValueMap<>();
        postParams.add("password", editTextPassword.getText().toString());
        try {
            String result = template.postForObject(SingleInstance.getServerUrl() + "user/" + editTextUsername.getText().toString(),
                    postParams, String.class);
            Log.d(TAG, result);

            SimpleResponseDTO response = new ObjectMapper().readValue(result, SimpleResponseDTO.class);
            return response.getStatus();

        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            return -1;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
}