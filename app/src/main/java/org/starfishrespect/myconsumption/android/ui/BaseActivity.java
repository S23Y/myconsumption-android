package org.starfishrespect.myconsumption.android.ui;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.starfishrespect.myconsumption.android.R;
import org.starfishrespect.myconsumption.android.SingleInstance;
import org.starfishrespect.myconsumption.android.events.BuildAlertEvent;
import org.starfishrespect.myconsumption.android.tasks.GCMRegister;
import org.starfishrespect.myconsumption.android.tasks.UserUpdater;
import org.starfishrespect.myconsumption.android.tasks.ConfigUpdater;
import org.starfishrespect.myconsumption.android.dao.SensorValuesDao;
import org.starfishrespect.myconsumption.android.tasks.SensorValuesUpdater;
import org.starfishrespect.myconsumption.android.tasks.StatValuesUpdater;
import org.starfishrespect.myconsumption.android.data.UserData;
import org.starfishrespect.myconsumption.android.events.ReloadConfigEvent;
import org.starfishrespect.myconsumption.android.events.ReloadStatEvent;
import org.starfishrespect.myconsumption.android.events.ReloadUserEvent;
import org.starfishrespect.myconsumption.android.ui.widget.ScrimInsetsScrollView;
import org.starfishrespect.myconsumption.android.util.LUtils;
import org.starfishrespect.myconsumption.android.util.MiscFunctions;
import org.starfishrespect.myconsumption.android.util.PlayServicesUtils;
import org.starfishrespect.myconsumption.android.util.PrefUtils;
import org.starfishrespect.myconsumption.android.util.UIUtils;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

import static org.starfishrespect.myconsumption.android.util.LogUtils.LOGD;
import static org.starfishrespect.myconsumption.android.util.LogUtils.LOGE;
import static org.starfishrespect.myconsumption.android.util.LogUtils.LOGI;
import static org.starfishrespect.myconsumption.android.util.LogUtils.LOGW;
import static org.starfishrespect.myconsumption.android.util.LogUtils.makeLogTag;

public abstract class BaseActivity extends ActionBarActivity implements SensorValuesUpdater.UpdateFinishedCallback,
        UserUpdater.GetUserCallback, StatValuesUpdater.StatUpdateFinishedCallback,
        ConfigUpdater.ConfigUpdateFinishedCallback{
    private static final String TAG = makeLogTag(BaseActivity.class);

    private ObjectAnimator mStatusBarColorAnimator;
    private Handler mHandler;

    // Helper methods for L APIs
    private LUtils mLUtils;

    // Primary toolbar and drawer toggle
    private Toolbar mActionBarToolbar;

    // Navigation drawer:
    private DrawerLayout mDrawerLayout;

    // A Runnable that we should execute when the navigation drawer finishes its closing animation
    private Runnable mDeferredOnDrawerClosedRunnable;

    private boolean mAccountBoxExpanded = false;

    // variables that control the Action Bar auto hide behavior (aka "quick recall")
    private boolean mActionBarAutoHideEnabled = false;
    private boolean mActionBarShown = true;

    // views that correspond to each navdrawer item, null if not yet created
    private View[] mNavDrawerItemViews = null;

    // list of navdrawer items that were actually added to the navdrawer, in order
    private ArrayList<Integer> mNavDrawerItems = new ArrayList<Integer>();

    private ViewGroup mDrawerItemsListContainer;

    private int mNormalStatusBarColor;
    private int mThemedStatusBarColor;
    private static final TypeEvaluator ARGB_EVALUATOR = new ArgbEvaluator();

    // When set, these components will be shown/hidden in sync with the action bar
    // to implement the "quick recall" effect (the Action Bar and the header views disappear
    // when you scroll down a list, and reappear quickly when you scroll up).
    private ArrayList<View> mHideableHeaderViews = new ArrayList<View>();

    // Durations for certain animations we use:
    private static final int HEADER_HIDE_ANIM_DURATION = 300;
    private static final int ACCOUNT_BOX_EXPAND_ANIM_DURATION = 200;

    // symbols for navdrawer items (indices must correspond to array below). This is
    // not a list of items that are necessarily *present* in the Nav Drawer; rather,
    // it's a list of all possible items.
    protected static final int NAVDRAWER_ITEM_CHART = 0;
    protected static final int NAVDRAWER_ITEM_STATS = 1;
    protected static final int NAVDRAWER_ITEM_COMPARISON = 2;
    protected static final int NAVDRAWER_ITEM_SIGN_IN = 3;
    protected static final int NAVDRAWER_ITEM_ADD_SENSOR = 4;
    protected static final int NAVDRAWER_ITEM_SETTINGS = 5;
//    protected static final int NAVDRAWER_ITEM_EXPERTS_DIRECTORY = 7;
//    protected static final int NAVDRAWER_ITEM_PEOPLE_IVE_MET = 8;
    protected static final int NAVDRAWER_ITEM_INVALID = -1;
    protected static final int NAVDRAWER_ITEM_SEPARATOR = -2;
    protected static final int NAVDRAWER_ITEM_SEPARATOR_SPECIAL = -3;

    // titles for navdrawer items (indices must correspond to the above)
    private static final int[] NAVDRAWER_TITLE_RES_ID = new int[]{
            R.string.navdrawer_item_chart,
            R.string.navdrawer_item_stat,
            R.string.navdrawer_item_comparison,
            R.string.navdrawer_item_sign_in,
            R.string.navdrawer_item_add_sensor,
            R.string.navdrawer_item_settings
    };

    // icons for navdrawer items (indices must correspond to above array)
    private static final int[] NAVDRAWER_ICON_RES_ID = new int[] {
            R.drawable.ic_drawer_chart,  // Chart
            R.drawable.ic_drawer_stat, // Stat
            R.drawable.ic_comparison,   // Comparison
            0, // Sign in
            R.drawable.ic_add,   // Add sensor
            R.drawable.ic_drawer_settings
    };

    // delay to launch nav drawer item, to allow close animation to play
    private static final int NAVDRAWER_LAUNCH_DELAY = 250;

    // fade in and fade out durations for the main content when switching between
    // different Activities of the app through the Nav Drawer
    private static final int MAIN_CONTENT_FADEOUT_DURATION = 150;
    private static final int MAIN_CONTENT_FADEIN_DURATION = 250;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();

        // Enable or disable each Activity depending on the form factor. This is necessary
        // because this app uses many implicit intents where we don't name the exact Activity
        // in the Intent, so there should only be one enabled Activity that handles each
        // Intent in the app.
        UIUtils.enableDisableActivitiesByFormFactor(this);

        // Initialize context, database helper, user and so on...
        SingleInstance.init(this);

        if (savedInstanceState == null) {
            registerGCMClient();
        }

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        mLUtils = LUtils.getInstance(this);
        mThemedStatusBarColor = getResources().getColor(R.color.theme_primary_dark);
        mNormalStatusBarColor = mThemedStatusBarColor;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupNavDrawer();
        setupAccountBox();

        trySetupSwipeRefresh();
        updateSwipeRefreshProgressBarTop();

        View mainContent = findViewById(R.id.main_content);
        if (mainContent != null) {
            mainContent.setAlpha(0);
            mainContent.animate().alpha(1).setDuration(MAIN_CONTENT_FADEIN_DURATION);
        } else {
            LOGW(TAG, "No view with ID main_content to fade in.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Verifies the proper version of Google Play Services exists on the device.
        PlayServicesUtils.checkGooglePlaySevices(this);
    }

    private void trySetupSwipeRefresh() {
        // TODO
    }

    /**
     * Sets up the account box. The account box is the area at the top of the nav drawer that
     * shows which account the user is logged in as, and lets them switch accounts. It also
     * shows the user's Google+ cover photo as background.
     */
    private void setupAccountBox() {
        // TODO
    }

    public LUtils getLUtils() {
        return mLUtils;
    }

    public int getThemedStatusBarColor() {
        return mThemedStatusBarColor;
    }

    public void setNormalStatusBarColor(int color) {
        mNormalStatusBarColor = color;
        if (mDrawerLayout != null) {
            mDrawerLayout.setStatusBarBackgroundColor(mNormalStatusBarColor);
        }
    }

    /**
     * Sets up the navigation drawer as appropriate. Note that the nav drawer will be
     * different depending on whether the attendee indicated that they are attending the
     * event on-site vs. attending remotely.
     */
    private void setupNavDrawer() {
        // What nav drawer item should be selected?
        int selfItem = getSelfNavDrawerItem();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout == null) {
            return;
        }
        mDrawerLayout.setStatusBarBackgroundColor(
                getResources().getColor(R.color.theme_primary_dark));
        ScrimInsetsScrollView navDrawer = (ScrimInsetsScrollView)
                mDrawerLayout.findViewById(R.id.navdrawer);
        if (selfItem == NAVDRAWER_ITEM_INVALID) {
            // do not show a nav drawer
            if (navDrawer != null) {
                ((ViewGroup) navDrawer.getParent()).removeView(navDrawer);
            }
            mDrawerLayout = null;
            return;
        }

        if (navDrawer != null) {
            final View chosenAccountContentView = findViewById(R.id.chosen_account_content_view);
            final View chosenAccountView = findViewById(R.id.chosen_account_view);
            final int navDrawerChosenAccountHeight = getResources().getDimensionPixelSize(
                    R.dimen.navdrawer_chosen_account_height);
            navDrawer.setOnInsetsCallback(new ScrimInsetsScrollView.OnInsetsCallback() {
                @Override
                public void onInsetsChanged(Rect insets) {
                    ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)
                            chosenAccountContentView.getLayoutParams();
                    lp.topMargin = insets.top;
                    chosenAccountContentView.setLayoutParams(lp);

                    ViewGroup.LayoutParams lp2 = chosenAccountView.getLayoutParams();
                    lp2.height = navDrawerChosenAccountHeight + insets.top;
                    chosenAccountView.setLayoutParams(lp2);
                }
            });
        }

        if (mActionBarToolbar != null) {
            mActionBarToolbar.setNavigationIcon(R.drawable.ic_drawer);
            mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawerLayout.openDrawer(Gravity.START);
                }
            });
        }

        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                // run deferred action, if we have one
                if (mDeferredOnDrawerClosedRunnable != null) {
                    mDeferredOnDrawerClosedRunnable.run();
                    mDeferredOnDrawerClosedRunnable = null;
                }
                if (mAccountBoxExpanded) {
                    mAccountBoxExpanded = false;
                    setupAccountBoxToggle();
                }
                onNavDrawerStateChanged(false, false);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                onNavDrawerStateChanged(true, false);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                onNavDrawerStateChanged(isNavDrawerOpen(), newState != DrawerLayout.STATE_IDLE);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                onNavDrawerSlide(slideOffset);
            }
        });

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);

        // populate the nav drawer with the correct items
        populateNavDrawer();

        // TODO
//        // When the user runs the app for the first time, we want to land them with the
//        // navigation drawer open. But just the first time.
//        if (!PrefUtils.isWelcomeDone(this)) {
//            // first run of the app starts with the nav drawer open
//            PrefUtils.markWelcomeDone(this);
//            mDrawerLayout.openDrawer(Gravity.START);
//        }
    }

    protected void autoShowOrHideActionBar(boolean show) {
        if (show == mActionBarShown) {
            return;
        }

        mActionBarShown = show;
        onActionBarAutoShowOrHide(show);
    }

    // Subclasses can override this for custom behavior
    protected void onNavDrawerStateChanged(boolean isOpen, boolean isAnimating) {
        if (mActionBarAutoHideEnabled && isOpen) {
            autoShowOrHideActionBar(true);
        }
    }

    protected void onNavDrawerSlide(float offset) {}

    protected boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(Gravity.START);
    }

    private void setupAccountBoxToggle() {
        // TODO
    }

    /** Populates the navigation drawer with the appropriate items. */
    private void populateNavDrawer() {
        //boolean attendeeAtVenue = PrefUtils.isAttendeeAtVenue(this);
        mNavDrawerItems.clear();

        // decide which items will appear in the nav drawer

        // TODO
//        if (AccountUtils.hasActiveAccount(this)) {
//            // Only logged-in users can save sessions, so if there is no active account,
//            // there is no My Schedule
//            mNavDrawerItems.add(NAVDRAWER_ITEM_MY_SCHEDULE);
//        } else {
//            // If no active account, show Sign In
//            mNavDrawerItems.add(NAVDRAWER_ITEM_SIGN_IN);
//        }

        // Chart is always shown
        mNavDrawerItems.add(NAVDRAWER_ITEM_CHART);
        mNavDrawerItems.add(NAVDRAWER_ITEM_STATS);
        mNavDrawerItems.add(NAVDRAWER_ITEM_COMPARISON);

//        // If the attendee is on-site, show Map on the nav drawer
//        if (attendeeAtVenue) {
//            mNavDrawerItems.add(NAVDRAWER_ITEM_STATS);
//        }
        mNavDrawerItems.add(NAVDRAWER_ITEM_SEPARATOR);
        mNavDrawerItems.add(NAVDRAWER_ITEM_ADD_SENSOR);

//        // If attendee is on-site, show the People I've Met item
//        if (attendeeAtVenue) {
//            mNavDrawerItems.add(NAVDRAWER_ITEM_PEOPLE_IVE_MET);
//        }

//        // If the experts directory hasn't expired, show it
//        if (!Config.hasExpertsDirectoryExpired()) {
//            mNavDrawerItems.add(NAVDRAWER_ITEM_EXPERTS_DIRECTORY);
//        }

//        // Other items that are always in the nav drawer irrespective of whether the
//        // attendee is on-site or remote:
//        mNavDrawerItems.add(NAVDRAWER_ITEM_SOCIAL);
//        mNavDrawerItems.add(NAVDRAWER_ITEM_VIDEO_LIBRARY);
        mNavDrawerItems.add(NAVDRAWER_ITEM_SEPARATOR_SPECIAL);
        mNavDrawerItems.add(NAVDRAWER_ITEM_SETTINGS);

        createNavDrawerItems();
    }

    private void createNavDrawerItems() {
        mDrawerItemsListContainer = (ViewGroup) findViewById(R.id.navdrawer_items_list);
        if (mDrawerItemsListContainer == null) {
            return;
        }

        mNavDrawerItemViews = new View[mNavDrawerItems.size()];
        mDrawerItemsListContainer.removeAllViews();
        int i = 0;
        for (int itemId : mNavDrawerItems) {
            mNavDrawerItemViews[i] = makeNavDrawerItem(itemId, mDrawerItemsListContainer);
            mDrawerItemsListContainer.addView(mNavDrawerItemViews[i]);
            ++i;
        }
    }

    private View makeNavDrawerItem(final int itemId, ViewGroup container) {
        boolean selected = getSelfNavDrawerItem() == itemId;
        int layoutToInflate = 0;
        if (itemId == NAVDRAWER_ITEM_SEPARATOR) {
            layoutToInflate = R.layout.navdrawer_separator;
        } else if (itemId == NAVDRAWER_ITEM_SEPARATOR_SPECIAL) {
            layoutToInflate = R.layout.navdrawer_separator;
        } else {
            layoutToInflate = R.layout.navdrawer_item;
        }
        View view = getLayoutInflater().inflate(layoutToInflate, container, false);

        if (isSeparator(itemId)) {
            // we are done
            UIUtils.setAccessibilityIgnore(view);
            return view;
        }

        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        TextView titleView = (TextView) view.findViewById(R.id.title);
        int iconId = itemId >= 0 && itemId < NAVDRAWER_ICON_RES_ID.length ?
                NAVDRAWER_ICON_RES_ID[itemId] : 0;
        int titleId = itemId >= 0 && itemId < NAVDRAWER_TITLE_RES_ID.length ?
                NAVDRAWER_TITLE_RES_ID[itemId] : 0;

        // set icon and text
        iconView.setVisibility(iconId > 0 ? View.VISIBLE : View.GONE);
        if (iconId > 0) {
            iconView.setImageResource(iconId);
        }
        titleView.setText(getString(titleId));

        formatNavDrawerItem(view, itemId, selected);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavDrawerItemClicked(itemId);
            }
        });

        return view;
    }

    private void onNavDrawerItemClicked(final int itemId) {
        if (itemId == getSelfNavDrawerItem()) {
            mDrawerLayout.closeDrawer(Gravity.START);
            return;
        }

        if (isSpecialItem(itemId)) {
            goToNavDrawerItem(itemId);
        } else {
            // launch the target Activity after a short delay, to allow the close animation to play
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToNavDrawerItem(itemId);
                }
            }, NAVDRAWER_LAUNCH_DELAY);

            // change the active item on the list so the user can see the item changed
            setSelectedNavDrawerItem(itemId);
            // fade out the main content
            View mainContent = findViewById(R.id.main_content);
            if (mainContent != null) {
                mainContent.animate().alpha(0).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
            }
        }

        mDrawerLayout.closeDrawer(Gravity.START);
    }

    /**
     * Sets up the given navdrawer item's appearance to the selected state. Note: this could
     * also be accomplished (perhaps more cleanly) with state-based layouts.
     */
    private void setSelectedNavDrawerItem(int itemId) {
        if (mNavDrawerItemViews != null) {
            for (int i = 0; i < mNavDrawerItemViews.length; i++) {
                if (i < mNavDrawerItems.size()) {
                    int thisItemId = mNavDrawerItems.get(i);
                    formatNavDrawerItem(mNavDrawerItemViews[i], thisItemId, itemId == thisItemId);
                }
            }
        }
    }

    private void goToNavDrawerItem(int item) {
        Intent intent;
        switch (item) {
//            case NAVDRAWER_ITEM_MY_SCHEDULE:
//                intent = new Intent(this, HelloWorldActivity.class);
//                startActivity(intent);
//                finish();
//                break;
            case NAVDRAWER_ITEM_CHART:
                intent = new Intent(this, ChartActivity.class);
                startActivity(intent);
                finish();
                break;
            case NAVDRAWER_ITEM_STATS:
                intent = new Intent(this, StatActivity.class);
                startActivity(intent);
                finish();
                break;
            case NAVDRAWER_ITEM_COMPARISON:
                intent = new Intent(this, ComparisonActivity.class);
                startActivity(intent);
                finish();
                break;
            case NAVDRAWER_ITEM_ADD_SENSOR:
                intent = new Intent(this, AddSensorActivity.class);
                startActivity(intent);
                finish();
                break;
//            case NAVDRAWER_ITEM_EXPERTS_DIRECTORY:
//                intent = new Intent(this, HelloWorldActivity.class);
//                startActivity(intent);
//                finish();
//                break;
//            case NAVDRAWER_ITEM_PEOPLE_IVE_MET:
//                intent = new Intent(this, HelloWorldActivity.class);
//                startActivity(intent);
//                finish();
//                break;
            case NAVDRAWER_ITEM_SIGN_IN:
                signInOrCreateAnAccount();
                break;
            case NAVDRAWER_ITEM_SETTINGS:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
//            case NAVDRAWER_ITEM_VIDEO_LIBRARY:
//                intent = new Intent(this, HelloWorldActivity.class);
//                startActivity(intent);
//                finish();
//                break;
        }
    }

    private void signInOrCreateAnAccount() {
        // TODO
    }

    private boolean isSpecialItem(int itemId) {
        return itemId == NAVDRAWER_ITEM_SETTINGS;
    }

    private void formatNavDrawerItem(View view, int itemId, boolean selected) {
        if (isSeparator(itemId)) {
            // not applicable
            return;
        }

        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        TextView titleView = (TextView) view.findViewById(R.id.title);

        if (selected) {
            view.setBackgroundResource(R.drawable.selected_navdrawer_item_background);
        }

        // configure its appearance according to whether or not it's selected
        titleView.setTextColor(selected ?
                getResources().getColor(R.color.navdrawer_text_color_selected) :
                getResources().getColor(R.color.navdrawer_text_color));
        iconView.setColorFilter(selected ?
                getResources().getColor(R.color.navdrawer_icon_tint_selected) :
                getResources().getColor(R.color.navdrawer_icon_tint));
    }

    /** Registers device on the GCM server, if necessary. */
    private void registerGCMClient() {
        // Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
        if (PlayServicesUtils.checkGooglePlaySevices(this)) {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
            String regid = PrefUtils.getRegistrationId(this);

            if (regid.isEmpty()) {
                GCMRegister task = new GCMRegister();
                task.registerInBackground(this);
            }
        } else {
            LOGI(TAG, "No valid Google Play Services APK found.");
        }




// TODO if login with google account ?
//            // Get the correct GCM key for the user. GCM key is a somewhat non-standard
//            // approach we use in this app. For more about this, check GCM.TXT.
//            final String gcmKey = AccountUtils.hasActiveAccount(this) ?
//                    AccountUtils.getGcmKey(this, AccountUtils.getActiveAccountName(this)) : null;
//            // Device is already registered on GCM, needs to check if it is
//            // registered on our server as well.
//            if (ServerUtilities.isRegisteredOnServer(this, gcmKey)) {
//                // Skips registration.
//                LOGI(TAG, "Already registered on the GCM server with right GCM key.");
//            } else {
//                // Try to register again, but not in the UI thread.
//                // It's also necessary to cancel the thread onDestroy(),
//                // hence the use of AsyncTask instead of a raw thread.
//                mGCMRegisterTask = new AsyncTask<Void, Void, Void>() {
//                    @Override
//                    protected Void doInBackground(Void... params) {
//                        LOGI(TAG, "Registering on the GCM server with GCM key: "
//                                + AccountUtils.sanitizeGcmKey(gcmKey));
//                        boolean registered = ServerUtilities.register(BaseActivity.this,
//                                regId, gcmKey);
//                        // At this point all attempts to register with the app
//                        // server failed, so we need to unregister the device
//                        // from GCM - the app will try to register again when
//                        // it is restarted. Note that GCM will send an
//                        // unregistered callback upon completion, but
//                        // GCMIntentService.onUnregistered() will ignore it.
//                        if (!registered) {
//                            LOGI(TAG, "GCM registration failed.");
//                            GCMRegistrar.unregister(BaseActivity.this);
//                        } else {
//                            LOGI(TAG, "GCM registration successful.");
//                        }
//                        return null;
//                    }
//
//                    @Override
//                    protected void onPostExecute(Void result) {
//                        mGCMRegisterTask = null;
//                    }
//                };
//                mGCMRegisterTask.execute(null, null, null);
//            }
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // TODO if login with google account ? related to registerGCMClient
//        if (mGCMRegisterTask != null) {
//            LOGD(TAG, "Cancelling GCM registration task.");
//            mGCMRegisterTask.cancel(true);
//        }
//
//        try {
//            GCMRegistrar.onDestroy(this);
//        } catch (Exception e) {
//            LOGW(TAG, "C2DM unregistration error", e);
//        }

//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
//        sp.unregisterOnSharedPreferenceChangeListener(this);
    }

    private boolean isSeparator(int itemId) {
        return itemId == NAVDRAWER_ITEM_SEPARATOR || itemId == NAVDRAWER_ITEM_SEPARATOR_SPECIAL;
    }

    protected Toolbar getActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
            if (mActionBarToolbar != null) {
                setSupportActionBar(mActionBarToolbar);
            }
        }
        return mActionBarToolbar;
    }

    protected void onActionBarAutoShowOrHide(boolean shown) {
        if (mStatusBarColorAnimator != null) {
            mStatusBarColorAnimator.cancel();
        }
        mStatusBarColorAnimator = ObjectAnimator.ofInt(
                (mDrawerLayout != null) ? mDrawerLayout : mLUtils,
                (mDrawerLayout != null) ? "statusBarBackgroundColor" : "statusBarColor",
                shown ? Color.BLACK : mNormalStatusBarColor,
                shown ? mNormalStatusBarColor : Color.BLACK)
                .setDuration(250);
        if (mDrawerLayout != null) {
            mStatusBarColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ViewCompat.postInvalidateOnAnimation(mDrawerLayout);
                }
            });
        }
        mStatusBarColorAnimator.setEvaluator(ARGB_EVALUATOR);
        mStatusBarColorAnimator.start();

        updateSwipeRefreshProgressBarTop();

        for (View view : mHideableHeaderViews) {
            if (shown) {
                view.animate()
                        .translationY(0)
                        .alpha(1)
                        .setDuration(HEADER_HIDE_ANIM_DURATION)
                        .setInterpolator(new DecelerateInterpolator());
            } else {
                view.animate()
                        .translationY(-view.getBottom())
                        .alpha(0)
                        .setDuration(HEADER_HIDE_ANIM_DURATION)
                        .setInterpolator(new DecelerateInterpolator());
            }
        }
    }

    private void updateSwipeRefreshProgressBarTop() {
        // TODO
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main, menu);

        configureStandardMenuItems(menu);

        return true;
    }

    protected void configureStandardMenuItems(Menu menu) {
        MenuItem refreshItem = menu.findItem(R.id.menu_refresh);
        if (refreshItem != null) {
            refreshItem.setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_refresh:
                refreshData();
                LOGD(TAG, "menu refresh clicked");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Returns the navigation drawer item that corresponds to this Activity. Subclasses
     * of BaseActivity override this to indicate what nav drawer item corresponds to them
     * Return NAVDRAWER_ITEM_INVALID to mean that this Activity should not have a Nav Drawer.
     */
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_INVALID;
    }

    /**
     * Refresh data from server to the local Android db.
     */
    public void refreshData() {
        if (!MiscFunctions.isOnline(this)) {
            MiscFunctions.makeOfflineDialog(this).show();
            return;
        }

        showReloadLayout(true);

        // Reload the user from server to see if new sensors have been added
        UserUpdater userUpdater = new UserUpdater(SingleInstance.getUserController().getUser().getName(),
                SingleInstance.getUserController().getUser().getPassword());
        userUpdater.setGetUserCallback(this);
        userUpdater.execute();
    }

    /**
     * Show or hide a reload layout while loading data from server.
     * @param visible
     */
    private void showReloadLayout(boolean visible) {
        if (visible) {
            findViewById(R.id.layoutGlobalReloading).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.layoutGlobalReloading).setVisibility(View.GONE);
        }
    }

    // from callback of GetUserAsyncTask
    @Override
    public void userFound(UserData user) {
        new SensorValuesDao(SingleInstance.getDatabaseHelper()).updateSensorList(user.getSensors());

        // Fetch sensor values from server
        SensorValuesUpdater updater = new SensorValuesUpdater();
        updater.setUpdateFinishedCallback(this);
        updater.refreshDB();

        // Fetch the stats from the server
        StatValuesUpdater statUpdater = new StatValuesUpdater();
        statUpdater.setUpdateFinishedCallback(this);
        statUpdater.refreshDB();

        // Fetch the config from the server
        ConfigUpdater configUpdater = new ConfigUpdater();
        configUpdater.setUpdateFinishedCallback(this);
        configUpdater.refreshDB();
    }

    // from callback of GetUserAsyncTask
    @Override
    public void userRetrieveError(Exception e) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title_error)
                .setMessage(getString(R.string.dialog_error_update_data_error))
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
        showReloadLayout(false);
    }

    @Override
    public void onUpdateFinished() {
        SingleInstance.getUserController().loadUser();
        showReloadLayout(false);
        //SingleInstance.getUserController().loadUser(false);
        //SingleInstance.getUserController().reloadUser(false);
        EventBus.getDefault().post(new ReloadUserEvent(false));
    }

    @Override
    public void onStatUpdateFinished() {
        EventBus.getDefault().post(new ReloadStatEvent(true));
    }

    @Override
    public void onConfigUpdateFinished() {
        EventBus.getDefault().post(new ReloadConfigEvent(true));
    }

    public void onEvent(BuildAlertEvent event) {
        if (!event.buildAlert())
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title_error)
                .setMessage(this.getString(R.string.dialog_message_error_when_loading_please_reconnect))
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SingleInstance.disconnect();
                    }
                });
        builder.show();
    }
}
