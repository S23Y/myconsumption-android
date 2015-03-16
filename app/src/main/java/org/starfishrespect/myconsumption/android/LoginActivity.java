package org.starfishrespect.myconsumption.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.starfishrespect.myconsumption.android.asynctasks.GetUserAsyncTask;
import org.starfishrespect.myconsumption.android.dao.DatabaseHelper;
import org.starfishrespect.myconsumption.android.dao.SensorValuesDao;
import org.starfishrespect.myconsumption.android.data.KeyValueData;
import org.starfishrespect.myconsumption.android.data.UserData;
import org.starfishrespect.myconsumption.android.misc.MiscFunctions;
/*import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;*/
import org.codehaus.jackson.map.ObjectMapper;
import org.starfishrespect.myconsumption.android.ui.MainActivity;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Login Activity
 */
public class LoginActivity extends Activity implements View.OnClickListener, GetUserAsyncTask.GetUserCallback {
    private static final int REQUEST_CREATE_ACCOUNT = 43;
    private static final String TAG = "LoginActivity";

    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin, buttonCreateAccount;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // todo: not sure that this code belongs here with the MVC and DAO stuffs...
        DatabaseHelper db = new DatabaseHelper(this);
        KeyValueData userJson = db.getValueForKey("user");
        if (userJson != null) {
            startMainActivity(false);
            finish();
            return;
        }

        setContentView(R.layout.activity_login);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);

        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(this);

        buttonCreateAccount = (Button) findViewById(R.id.buttonCreateAccount);
        buttonCreateAccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonLogin:
                if (!MiscFunctions.isOnline(this)) {
                    MiscFunctions.makeOfflineDialog(this).show();
                    return;
                }
                if (editTextUsername.getText().toString().equals("")) {
                    Toast.makeText(this, "Username is mandatory !", Toast.LENGTH_LONG).show();
                    return;
                }
                new GetUserAsyncTask(editTextUsername.getText().toString()).setGetUserCallback(this).execute();
                break;
            case R.id.buttonCreateAccount:
                if (!MiscFunctions.isOnline(this)) {
                    MiscFunctions.makeOfflineDialog(this).show();
                    return;
                }
                startActivityForResult(new Intent(this, CreateAccountActivity.class), REQUEST_CREATE_ACCOUNT);
                return;
        }
    }

    @Override
    public void userFound(UserData user) {
        DatabaseHelper db = new DatabaseHelper(this);
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(user);
            Log.d(TAG, json);
            db.getKeyValueDao().createOrUpdate(new KeyValueData("user", json));
            new SensorValuesDao(db).updateSensorList(user.getSensors());
        } catch (IOException | SQLException e) {
            Log.d(TAG, "Cannot create user " + user.getName(), e);
            return;
        }
        startMainActivity(true);
    }

    private void startMainActivity(boolean firstLaunch) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_FIRST_LAUNCH, firstLaunch);
        startActivity(intent);
        finish();
    }

    @Override
    public void userRetrieveError(Exception e) {
        Toast.makeText(this, "Cannot retrieve this user", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CREATE_ACCOUNT && resultCode == RESULT_OK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.dialog_title_information).setMessage(R.string.dialog_message_information_user_added)
                    .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //@TODO: uncomment (caused the app to crash)
/*        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getApplicationContext());

        if (resultCode == ConnectionResult.SUCCESS) {
            Log.d(TAG, "Google play services OK");
        } else {
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, 1).show();
        }*/
    }
}