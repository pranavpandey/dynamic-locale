<img src="./graphics/icon.png" width="160" height="160" align="right" hspace="20">

# Dynamic Locale

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Build Status](https://travis-ci.org/pranavpandey/dynamic-locale.svg?branch=master)](https://travis-ci.org/pranavpandey/dynamic-locale)
[![Release](https://img.shields.io/maven-central/v/com.pranavpandey.android/dynamic-locale)](https://search.maven.org/artifact/com.pranavpandey.android/dynamic-locale)

A library to perform runtime locale changes on Android 4.1 (API 16) and above.

> It uses [AndroidX][androidx] so, first [migrate][androidx-migrate] your project to AndroidX.
<br/>Since v2.1.0, it is dependent on Java 8 due to the dependency on
[Dynamic Utils](https://github.com/pranavpandey/dynamic-utils).

---

## Contents

- [Installation](#installation)
- [Usage](#usage)
    - [Application](#application)
    - [Activity](#activity)
    - [Dependency](#dependency)
- [License](#license)

---

## Installation

It can be installed by adding the following dependency to your `build.gradle` file:

```groovy
dependencies {
    // For AndroidX enabled projects.
    implementation 'com.pranavpandey.android:dynamic-locale:2.3.1'
}
```

---

## Usage

It provides an [interface][dynamic-locale] that can be implemented in the 
[application][dynamic-application] or [activity][dynamic-activity] class to provide the modified
base `context`. It has optional feature to provide `font scale` to make the text smaller or larger. 
It would be beneficial for some specific locales and theme styles.

> For a complete reference, please read the [documentation][documentation].

### Application

Implement the `DynamicLocale` interface for the `application` class by using the 
[helper][dynamic-locale-utils] class and register it in the `AndroidManifest` to apply 
the locale at runtime.

> While using it for the application class, you should update the `context` when user 
> configuration changes as Android caches the application in the memory and the 
> `attachBaseContext(Context)` might not be called each time. 
>
> Please check an example [here][dynamic-application-example].

```java
public class DynamicApp extends Application implements DynamicLocale {

    @Override
    public void attachBaseContext(@NonNull Context base) {
        // Set the dynamic locale.
        super.attachBaseContext(setLocale(base));
    }    

    @Override
    public @Nullable String[] getSupportedLocales() {
        // Returns an array of supported locales.
        return new String[] {
            Locale.ENGLISH.toString(),
                Locale.GERMAN.toString(),
                new Locale(DynamicLocaleUtils.ADS_LOCALE_SPANISH, "").toString(),
                new Locale(DynamicLocaleUtils.ADS_LOCALE_INDONESIA, "").toString(),
                Locale.ITALIAN.toString(),
                new Locale(DynamicLocaleUtils.ADS_LOCALE_TURKISH, "").toString(),
                Locale.CHINESE.toString() };
    }

    @Override
    public @NonNull Locale getDefaultLocale(@NonNull Context context) {
        // Returns the default locale to be used if no dynamic locale support is provided.
        return DynamicLocaleUtils.getDefaultLocale(context, getSupportedLocales());
    }
    
    @Override
    public @Nullable Locale getLocale() {
        // Returns the locale to be applied.
        return DynamicLocaleUtils.toLocale(DynamicLocaleUtils.ADS_LOCALE_HINDI);
    }

    @Override
    public @NonNull Context setLocale(@NonNull Context context) {
        // Apply the locale according to the configuration.
        return DynamicLocaleUtils.setLocale(context,
                DynamicLocaleUtils.getLocale(getLocale(),
                    getDefaultLocale(context)), getFontScale());
    }

    @Override
    public float getFontScale() {
        // Returns the font scale to be applied.
        // Use the default font scale.
        return 1f;
    }
}
```

### Activity

Similarly, it can be used for the `activity` to apply locale at runtime.

> You should `recreate()` the activity when user configuration changes.

```java
public class DynamicActivity extends Activity implements DynamicLocale {

    @Override
    public void attachBaseContext(@NonNull Context base) {
        // Set the dynamic locale.
        super.attachBaseContext(setLocale(base));
    }    

    ...

    @Override
    public @NonNull Context setLocale(@NonNull Context context) {
        // Apply the locale according to the configuration.
        return DynamicLocaleUtils.setLocale(context,
                DynamicLocaleUtils.getLocale(getLocale(),
                    getDefaultLocale(context)), getFontScale());
    }

    ...
}
```

### Dependency

It depends on the [dynamic-utils][dynamic-utils] to perform various internal operations. 
So, its functions can also be used to perform other useful operations.

---

## Author

Pranav Pandey

[![GitHub](https://img.shields.io/github/followers/pranavpandey?label=GitHub&style=social)](https://github.com/pranavpandey)
[![Follow on Twitter](https://img.shields.io/twitter/follow/pranavpandeydev?label=Follow&style=social)](https://twitter.com/intent/follow?screen_name=pranavpandeydev)
[![Donate via PayPal](https://img.shields.io/static/v1?label=Donate&message=PayPal&color=blue)](https://paypal.me/pranavpandeydev)

---

## License

    Copyright 2019-2023 Pranav Pandey

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


[androidx]: https://developer.android.com/jetpack/androidx
[androidx-migrate]: https://developer.android.com/jetpack/androidx/migrate
[documentation]: https://pranavpandey.github.io/dynamic-locale
[dynamic-locale]: https://github.com/pranavpandey/dynamic-locale/blob/master/dynamic-locale/src/main/java/com/pranavpandey/android/dynamic/locale/DynamicLocale.java
[dynamic-application]: https://github.com/pranavpandey/dynamic-support/blob/master/dynamic-support/src/main/java/com/pranavpandey/android/dynamic/support/DynamicApplication.java
[dynamic-application-example]: https://github.com/pranavpandey/dynamic-support/blob/5d94b3e700e49b55008069f42763965f6d3bf033/dynamic-support/src/main/java/com/pranavpandey/android/dynamic/support/DynamicApplication.java#L206
[dynamic-activity]: https://github.com/pranavpandey/dynamic-support/blob/master/dynamic-support/src/main/java/com/pranavpandey/android/dynamic/support/activity/DynamicSystemActivity.java
[dynamic-locale-utils]: https://github.com/pranavpandey/dynamic-locale/blob/master/dynamic-locale/src/main/java/com/pranavpandey/android/dynamic/locale/DynamicLocaleUtils.java
[dynamic-utils]: https://github.com/pranavpandey/dynamic-utils
