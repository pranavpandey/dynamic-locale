/*
 * Copyright 2019-2022 Pranav Pandey
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

package com.pranavpandey.android.dynamic.locale;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LocaleManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.os.ConfigurationCompat;
import androidx.core.text.TextUtilsCompat;
import androidx.core.view.ViewCompat;

import com.pranavpandey.android.dynamic.util.DynamicSdkUtils;

import java.util.Locale;

/**
 * Helper class to perform various locale operations.
 */
public class DynamicLocaleUtils {

    /**
     * Default value to set application locale on API 33 and above.
     */
    private static final boolean ADL_SYSTEM = false;

    /**
     * Default value to update activity resources on legacy devices.
     */
    private static final boolean ADL_ACTIVITY = true;

    /**
     * Returns the locale manager on API 33.
     *
     * @param context The context to be used.
     *
     * @return The locale manager on API 33.
     */
    @TargetApi(Build.VERSION_CODES.TIRAMISU)
    public static @Nullable LocaleManager getLocaleManager(@Nullable Context context) {
        if (!DynamicSdkUtils.is33() || context == null) {
            return null;
        }

        return ContextCompat.getSystemService(context, LocaleManager.class);
    }

    /**
     * Returns the layout direction for the selected locale.
     *
     * @param context The context to be used.
     *
     * @return The layout direction for the selected locale.
     *
     * @see ViewCompat#LAYOUT_DIRECTION_LTR
     * @see ViewCompat#LAYOUT_DIRECTION_RTL
     * @see ViewCompat#LAYOUT_DIRECTION_INHERIT
     * @see ViewCompat#LAYOUT_DIRECTION_LOCALE
     */
    public static int getLayoutDirection(@Nullable Context context) {
        return TextUtilsCompat.getLayoutDirectionFromLocale(context != null
                ? getCurrentLocale(context) : Locale.getDefault());
    }

    /**
     * Checks whether the layout direction is RTL (right-to-left).
     *
     * @param context The context to be used.
     *
     * @return {@code true} if the layout direction is RTL (right-to-left).
     *
     * @see #getLayoutDirection(Context)
     * @see ViewCompat#LAYOUT_DIRECTION_RTL
     */
    public static boolean isLayoutRtl(@Nullable Context context) {
        return DynamicSdkUtils.is17() && getLayoutDirection(context)
                == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    /**
     * Checks whether the layout direction is RTL (right-to-left).
     *
     * @return {@code true} if the layout direction is RTL (right-to-left).
     *
     * @see #isLayoutRtl(Context)
     */
    public static boolean isLayoutRtl() {
        return isLayoutRtl(null);
    }

    /**
     * Get locale from the locale string in the format: {@code language,region}.
     *
     * @param locale The locale string to be converted.
     *
     * @return The converted locale from the locale string.
     *         <p>Return {@code null} for the default locale value.
     *
     * @see DynamicLocale#SYSTEM
     */
    public static @Nullable Locale toLocale(@Nullable String locale) {
        Locale localeWithRegion;
        if (locale == null || locale.equals(DynamicLocale.SYSTEM)) {
            localeWithRegion = null;
        } else {
            String[] localeFormat = locale.split(DynamicLocale.SPLIT);
            localeWithRegion = new Locale(localeFormat[0]);
            if (localeFormat.length > 1) {
                localeWithRegion = new Locale(localeFormat[0], localeFormat[1]);
            }
        }

        return localeWithRegion;
    }

    /**
     * Get the default locale language from the supported locales.
     *
     * @param context The context to get the configuration.
     * @param supportedLocales The supported locales.
     *
     * @return The default locale according to the supported locales.
     */
    @TargetApi(Build.VERSION_CODES.TIRAMISU)
    public static @NonNull Locale getDefaultLocale(
            @Nullable Context context, @Nullable String[] supportedLocales) {
        if (DynamicSdkUtils.is33()) {
            LocaleManager localeManager;
            if ((localeManager = getLocaleManager(context)) != null
                    && !localeManager.getApplicationLocales().isEmpty()) {
                return supportedLocales != null
                        ? localeManager.getApplicationLocales().getFirstMatch(supportedLocales)
                        : localeManager.getApplicationLocales().get(0);
            }
        }

        Locale defaultLocale = supportedLocales != null ? ConfigurationCompat.getLocales(
                Resources.getSystem().getConfiguration()).getFirstMatch(supportedLocales)
                : ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0);

        return defaultLocale != null ? defaultLocale : Locale.getDefault();
    }

    /**
     * Returns the locale after performing safety checks.
     *
     * @param locale The locale to be checked.
     * @param defaultLocale The default locale if current locale does not passes checks.
     *
     * @return The locale after performing safety checks.
     */
    public static @NonNull Locale getLocale(@Nullable Locale locale,
            @NonNull Locale defaultLocale) {
        return locale == null ? defaultLocale : locale;
    }

    /**
     * Set the locale for a given context.
     *
     * @param context The context to set the locale.
     * @param activity {@code true} if the context an instance of {@link Activity}.
     * @param locale The locale to be used for the context resources.
     * @param fontScale The font scale to be used for the context resources.
     * @param system {@code true} to set application locales on API 33 and above.
     *
     * @return The modified context after applying the locale.
     *
     * @see LocaleManager#setApplicationLocales(LocaleList)
     */
    @TargetApi(Build.VERSION_CODES.TIRAMISU)
    public static @NonNull Context setLocale(@NonNull Context context,
            boolean activity, @Nullable Locale locale, float fontScale, boolean system) {
        if (locale == null) {
            return context;
        }

        if (DynamicSdkUtils.is17()) {
            if (system && DynamicSdkUtils.is33()) {
                LocaleManager localeManager;
                if ((localeManager = getLocaleManager(context)) != null) {
                    localeManager.setApplicationLocales(new LocaleList(locale));
                }
            }

            return updateResources(context, activity, locale, fontScale);
        }

        return updateResourcesLegacy(context, locale, fontScale);
    }

    /**
     * Set the locale for a given context.
     *
     * @param context The context to set the locale.
     * @param activity {@code true} if the context an instance of {@link Activity}.
     * @param locale The locale to be used for the context resources.
     * @param fontScale The font scale to be used for the context resources.
     *
     * @return The modified context after applying the locale.
     *
     * @see #setLocale(Context, boolean, Locale, float, boolean)
     */
    @TargetApi(Build.VERSION_CODES.TIRAMISU)
    public static @NonNull Context setLocale(@NonNull Context context,
            boolean activity, @Nullable Locale locale, float fontScale) {
        return setLocale(context, activity, locale, fontScale, ADL_SYSTEM);
    }

    /**
     * Set the locale for a given context.
     *
     * @param context The context to set the locale.
     * @param locale The locale to be used for the context resources.
     * @param fontScale The font scale to be used for the context resources.
     * @param system {@code true} to set application locales on API 33 and above.
     *
     * @return The modified context after applying the locale.
     *
     * @see #setLocale(Context, boolean, Locale, float, boolean)
     */
    public static @NonNull Context setLocale(@NonNull Context context,
            @Nullable Locale locale, float fontScale, boolean system) {
        return setLocale(context, ADL_ACTIVITY, locale, fontScale, system);
    }

    /**
     * Set the locale for a given context.
     *
     * @param context The context to set the locale.
     * @param locale The locale to be used for the context resources.
     * @param fontScale The font scale to be used for the context resources.
     *
     * @return The modified context after applying the locale.
     *
     * @see #setLocale(Context, Locale, float, boolean)
     */
    public static @NonNull Context setLocale(@NonNull Context context,
            @Nullable Locale locale, float fontScale) {
        return setLocale(context, locale, fontScale, ADL_SYSTEM);
    }

    /**
     * Set the locale for a given configuration.
     *
     * @param config The config to set the locale.
     * @param locale The locale to be used for the context resources.
     * @param fontScale The font scale to be used for the context resources.
     *
     * @return The modified context after applying the locale.
     */
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.N)
    public static @NonNull Configuration setLocale(@NonNull Configuration config,
            @Nullable Locale locale, float fontScale) {
        config.fontScale = fontScale;

        if (locale != null) {
            if (DynamicSdkUtils.is17()) {
                config.setLocale(locale);
            } else {
                config.locale = locale;
            }
        }

        return config;
    }

    /**
     * Update resources for a given context after setting the locale on API 17 and above.
     *
     * @param context The context to set the updated resources.
     * @param activity {@code true} if the context an instance of {@link Activity}.
     * @param locale The locale to be used for the context resources.
     * @param fontScale The font scale to be used for the context resources.
     *
     * @return The modified context after applying the locale.
     */
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static @NonNull Context updateResources(@NonNull Context context,
            boolean activity, @NonNull Locale locale, float fontScale) {
        Configuration configuration = new Configuration(
                context.getResources().getConfiguration());
        configuration.fontScale = fontScale;
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        if (activity) {
            // Fix for app compat 1.2.0.
            return context.createConfigurationContext(configuration);
        } else {
            // Fix for application font scale.
            context.createConfigurationContext(configuration);
            context.getResources().updateConfiguration(configuration,
                    context.getResources().getDisplayMetrics());

            return context;
        }
    }

    /**
     * Update resources for a given context after setting the locale on API 16 and below.
     *
     * @param context The context to set the updated resources.
     * @param locale The locale to be used for the context resources.
     * @param fontScale The font scale to be used for the context resources.
     *
     * @return The modified context after applying the locale.
     */
    @SuppressWarnings("deprecation")
    private static @NonNull Context updateResourcesLegacy(@NonNull Context context,
            @NonNull Locale locale, float fontScale) {
        Resources res = context.getResources();
        Configuration configuration = new Configuration(res.getConfiguration());
        configuration.fontScale = fontScale;
        configuration.locale = locale;

        context.getResources().updateConfiguration(configuration, res.getDisplayMetrics());
        return context;
    }

    /**
     * Returns the current locale for the supplied context.
     *
     * @param context The context to get the resources.
     *
     * @return The current locale for the supplied context.
     */
    public static @NonNull Locale getCurrentLocale(@Nullable Context context) {
        Locale currentLocale;
        if ((currentLocale = ConfigurationCompat.getLocales(context != null
                ? context.getResources().getConfiguration()
                : Resources.getSystem().getConfiguration()).get(0)) != null) {
            return currentLocale;
        }

        return Locale.getDefault();
    }
}
