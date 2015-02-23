package org.starfishrespect.myconsumption.android;

import android.app.Fragment;

/**
 * Created by thibaud on 16.02.15.
 */
public abstract class MainFragment extends Fragment {
    public abstract void reloadUser(boolean refreshData);
    public abstract void destroyFragment();
}
