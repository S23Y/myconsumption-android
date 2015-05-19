package org.starfishrespect.myconsumption.android;
/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class Config {
    // General configuration

    // Is this an internal dogfood build?
    public static final boolean IS_DOGFOOD_BUILD = false;

    public static final String EXTRA_FIRST_LAUNCH = "firstLaunch";

    public static final String SENDER_ID = "225408614844";

    // Warning messages for dogfood build
    public static final String DOGFOOD_BUILD_WARNING_TITLE = "Test build";
    public static final String DOGFOOD_BUILD_WARNING_TEXT = "This is a test build.";

    //public static String serverAddress = "212.166.22.110"; // public vm address // TODO
    public static String serverAddress = "172.20.1.75"; // @manex (bridged)
    //public static String serverAddress = "192.168.242.129";  // @ans (nat)
    //public static String serverAddress = "192.168.1.8"; // @ans bridged ethernet
    //public static String serverAddress = "192.168.1.32";   // @lw (bridged)
    public static int port = 8080;
    public static String protocol = "http://";
    public static String serverDir = ""; // "myconsumption"
}