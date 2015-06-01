package org.starfishrespect.myconsumption.android;

/**
 * Global configuration of the app
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Author: Thibaud Ledent
 */
public class Config {
    // General configuration

    // Is this an internal build? (debug mode for the log in LogUtils)
    public static final boolean IS_INTERNAL_BUILD = false;

    public static final String EXTRA_FIRST_LAUNCH = "firstLaunch";

    public static final String SENDER_ID = "225408614844";

    // IP address of the server
    public static final String serverAddress = "myconsumption.s23y.com";
    //public static final String serverAddress = "212.166.22.110"; // public vm address
    //public static final String serverAddress = "172.20.1.75"; // @manex (bridged)
    //public static final String serverAddress = "192.168.1.9"; // @ans (bridged ethernet)
    //public static final String serverAddress = "192.168.1.32";   // @lw (bridged)

    public static final int port = 8080;
    public static final String protocol = "http://";
    public static final String serverDir = ""; // "myconsumption"
}