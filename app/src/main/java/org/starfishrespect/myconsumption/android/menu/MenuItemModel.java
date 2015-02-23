package org.starfishrespect.myconsumption.android.menu;

/**
 * Model per item menu.
 * Copyright 2013 Gabriele Mariotti (https://github.com/gabrielemariotti/androiddev/tree/master/NavigationDrawer)
 * Adapted by Thibaud Ledent (2015).
 */
public class MenuItemModel {
    private int mTitle;
    private int mIconRes;
    private int mCounter;
    private boolean mIsHeader;

    public MenuItemModel(int title, int iconRes, boolean header, int counter) {
        this.mTitle = title;
        this.mIconRes = iconRes;
        this.mIsHeader = header;
        this.mCounter = counter;
    }

    public MenuItemModel(int title, int iconRes, boolean header) {
        this(title, iconRes, header, 0);
    }

    public MenuItemModel(int title, int iconRes) {
        this(title, iconRes, false);
    }

    public int getTitle() {
        return mTitle;
    }

    public int getIconRes() {
        return mIconRes;
    }

    public int getCounter() {
        return mCounter;
    }

    public boolean isHeader() {
        return mIsHeader;
    }

    public void setCounter(int counter) {
        mCounter = counter;
    }
}
