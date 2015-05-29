package org.starfishrespect.myconsumption.android.ui;

import android.os.Bundle;

import org.starfishrespect.myconsumption.android.R;

/**
 * Template class to use if  you want to add an activity that extends BaseActivity.
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Author: Thibaud Ledent
 */
public class HelloWorldActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helloworld);

        getActionBarToolbar();
        getSupportActionBar().setTitle("HelloWorld");

        overridePendingTransition(0, 0);
    }

    @Override
    protected int getSelfNavDrawerItem() {
        // set this to have a nav drawer associated with this activity
        // TODO: change this when you use this template to create a new activity
        return NAVDRAWER_ITEM_CHART;
    }

}
