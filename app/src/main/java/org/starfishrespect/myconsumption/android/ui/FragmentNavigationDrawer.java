package org.starfishrespect.myconsumption.android.ui;


/*
** FragmentNavigationDrawer object for use with support-v7 library
** using compatibility fragments and support actionbar
*/

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.starfishrespect.myconsumption.android.NavDrawerItem;
import org.starfishrespect.myconsumption.android.NavDrawerListAdapter;
import org.starfishrespect.myconsumption.android.R;

import java.util.ArrayList;

public class FragmentNavigationDrawer extends DrawerLayout {
    private ActionBarDrawerToggle drawerToggle;
    private ListView lvDrawer;
    private Toolbar toolbar;
    private NavDrawerListAdapter drawerAdapter;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private ArrayList<FragmentNavItem> drawerNavItems;
    private int drawerContainerRes;
    private LinearLayout llDrawer;

    public FragmentNavigationDrawer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public FragmentNavigationDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FragmentNavigationDrawer(Context context) {
        super(context);
    }

    public void setupDrawerConfiguration(ListView drawerListView, Toolbar drawerToolbar,
                                         LinearLayout linearLDrawer, int drawerContainerResId) {
        // Setup navigation items array
        drawerNavItems = new ArrayList<FragmentNavigationDrawer.FragmentNavItem>();
        navDrawerItems = new ArrayList<NavDrawerItem>();
        drawerContainerRes = drawerContainerResId;
        // Setup drawer list view
        lvDrawer = drawerListView;
        llDrawer = linearLDrawer;
        toolbar = drawerToolbar;
        // Setup item listener
        lvDrawer.setOnItemClickListener(new FragmentDrawerItemListener());
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        drawerToggle = setupDrawerToggle();
        setDrawerListener(drawerToggle);
        // Setup action buttons
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    // addNavItem("First", R.drawable.ic_one, "First Fragment", FirstFragment.class)
    public void addNavItem(String navTitle, int icon, String windowTitle, Class<? extends Fragment> fragmentClass) {
        // adding nav drawer items to array
        navDrawerItems.add(new NavDrawerItem(navTitle, icon));
        // Set the adapter for the list view
        drawerAdapter = new NavDrawerListAdapter(getActivity(), navDrawerItems);
        lvDrawer.setAdapter(drawerAdapter);
        drawerNavItems.add(new FragmentNavItem(windowTitle, fragmentClass));
    }

    /**
     * Swaps fragments in the main content view
     */
    public void selectDrawerItem(int position) {
        // Create a new fragment and specify the one to show based on position
        FragmentNavItem navItem = drawerNavItems.get(position);
        Fragment fragment = null;
        Class<? extends Fragment> fragmentClass = navItem.getFragmentClass();
        try {
            fragment = fragmentClass.newInstance();
            Bundle args = navItem.getFragmentArgs();
            if (args != null) {
                fragment.setArguments(args);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(drawerContainerRes, fragment, fragmentClass.toString()).commit();

        // Highlight the selected item, update the title, and close the drawer
        lvDrawer.setItemChecked(position, true);
        setTitle(navItem.getTitle());
        closeDrawer(llDrawer);
    }


    public ActionBarDrawerToggle getDrawerToggle() {
        return drawerToggle;
    }


    private FragmentActivity getActivity() {
        return (FragmentActivity) getContext();
    }

    private ActionBar getSupportActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    private void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    private class FragmentDrawerItemListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectDrawerItem(position);
        }
    }

    private class FragmentNavItem {
        private Class<? extends Fragment> fragmentClass;
        private String title;
        private Bundle fragmentArgs;

        public FragmentNavItem(String title, Class<? extends Fragment> fragmentClass) {
            this(title, fragmentClass, null);
        }

        public FragmentNavItem(String title, Class<? extends Fragment> fragmentClass, Bundle args) {
            this.fragmentClass = fragmentClass;
            this.fragmentArgs = args;
            this.title = title;
        }

        public Class<? extends Fragment> getFragmentClass() {
            return fragmentClass;
        }

        public String getTitle() {
            return title;
        }

        public Bundle getFragmentArgs() {
            return fragmentArgs;
        }
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(getActivity(), this, toolbar, R.string.app_name, R.string.app_name);
    }

    public boolean isDrawerOpen() {
        return isDrawerOpen(llDrawer);
    }
}