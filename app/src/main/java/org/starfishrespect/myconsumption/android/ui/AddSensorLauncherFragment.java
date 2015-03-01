package org.starfishrespect.myconsumption.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.starfishrespect.myconsumption.android.AddSensorActivity;

/**
 * Created by thibaud on 25.02.15.
 */
public class AddSensorLauncherFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Intent intent = new Intent(getActivity(), AddSensorActivity.class);
        startActivity(intent);
    }

}
