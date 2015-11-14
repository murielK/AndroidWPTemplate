# How to build WPAndroidTemplate

This is a Gradle-based project that works best with [Android Studio].


## Building the app

1. Install the following software:
       - Android SDK:
         http://developer.android.com/sdk/index.html
       - Gradle:
         http://www.gradle.org/downloads
       - Android Studio:
         http://developer.android.com/sdk/installing/studio.html

2. Run the Android SDK Manager by pressing the SDK Manager toolbar button
   in Android Studio or by running the `android` command in a terminal
   window.

3. In the Android SDK Manager, ensure that the following are installed,
   and are updated to the latest available version:
       - Tools > Android SDK Platform-tools (rev 21 or above)
       - Tools > Android SDK Tools (rev 23.0.5 or above)
       - Tools > Android SDK Build-tools version 20
       - Tools > Android SDK Build-tools version 21 (rev 21.1.2 or above)
       - Android 4.0.3 > SDK Platform (API 15)
       - Extras > Android Support Library
       - Extras > Google Play services

4. Create a file in your working directory called local.properties,
   containing the path to your Android SDK. Use local.properties.example as a
   model. _(On Windows, use a double-backslash (`\\`) as a path separator!)_

5. Import the project in Android Studio:

    1. Press File > Import Project
    2. Navigate to and choose the settings.gradle file in this project
    3. Press OK


6. Choose Build > Make Project in Android Studio or run the following
    command in the project root directory:  
   ```sh
    ./gradlew clean assembleDebug
   ```  

7. To install on your test device:  
   ```sh
    ./gradlew installDebug
   ```  

[Android Studio]:http://developer.android.com/sdk/installing/studio.html
[App Signing]:http://developer.android.com/tools/publishing/app-signing.html#studio
