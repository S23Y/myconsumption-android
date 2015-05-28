# MyConsumption Android app

[![MyConsumption Android](https://dl.dropboxusercontent.com/u/22987083/banner-myconsumption-android%20v2.png)](http://s23y.org)

This repository contains the source code for the S23Y MyConsumption Android app.

## License

* [Apache Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

## Building

### With Gradle and Android Studio

The easiest way to build is to install [Android Studio](https://developer.android.com/sdk/index.html) v1.+
with [Gradle](https://www.gradle.org/) v2.+. You will need the Android SDK API 21 and `my-consumption-api` available at [Github MyConsumption Server](https://github.com/S23Y/myconsumption-server/) (run `mvn clean install` from the root directory of the server to install the API on your machine).

Once installed, you can import the project into Android Studio:

1. Open `File`
2. Import Project
3. Select `build.gradle` under the project directory
4. Click `OK`

Then, Gradle will do everything for you.

## Acknowledgements

This project uses many other open source libraries such as:

* [Ormlite](https://github.com/j256/ormlite-android)
* [Spring](https://github.com/spring-projects/spring-framework)
* [Achartengine](https://code.google.com/p/achartengine/)

The entire list of dependencies
is listed in the [app's Gradle file](https://github.com/S23Y/myconsumption-android/blob/master/app/build.gradle).

## Contributing

Please fork this repository and contribute back using
[pull requests](https://github.com/S23Y/myconsumption-android/pulls).

Any contributions, large or small, major features, bug fixes, language translations, 
unit/integration tests are welcomed and appreciated
but will be reviewed and discussed.
