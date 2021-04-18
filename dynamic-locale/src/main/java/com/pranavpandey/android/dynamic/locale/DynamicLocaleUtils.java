/*
 * Copyright 2019-2021 Pranav Pandey
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
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.ConfigurationCompat;
import androidx.core.text.TextUtilsCompat;
import androidx.core.view.ViewCompat;

import com.pranavpandey.android.dynamic.utils.DynamicSdkUtils;

import java.util.Locale;

/**
 * Helper class to perform various locale operations.
 */
public class DynamicLocaleUtils {

    /**
     * Returns the layout direction for the selected locale.
     *
     * @return The layout direction for the selected locale.
     *
     * @see ViewCompat#LAYOUT_DIRECTION_LTR
     * @see ViewCompat#LAYOUT_DIRECTION_RTL
     * @see ViewCompat#LAYOUT_DIRECTION_INHERIT
     * @see ViewCompat#LAYOUT_DIRECTION_LOCALE
     */
    public static int getLayoutDirection() {
        return TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault());
    }

    /**
     * Checks whether the layout is RTL (right-to-left).
     *
     * @return {@code true} if the layout is RTL (right-to-left).
     *
     * @see ViewCompat#LAYOUT_DIRECTION_RTL
     */
    public static boolean isLayoutRtl() {
        return DynamicSdkUtils.is17() && getLayoutDirection() == ViewCompat.LAYOUT_DIRECTION_RTL;
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
    public static @NonNull Locale getDefaultLocale(
            @NonNull Context context, @Nullable String[] supportedLocales) {
        if (supportedLocales == null) {
            return ConfigurationCompat.getLocales(
                    Resources.getSystem().getConfiguration()).get(0);
        } else {
            Locale defaultLocale = ConfigurationCompat.getLocales(
                    Resources.getSystem().getConfiguration()).getFirstMatch(supportedLocales);
            return defaultLocale != null ? defaultLocale : Locale.getDefault();
        }
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
     *
     * @return The modified context after applying the locale.
     */
    public static @NonNull Context setLocale(@NonNull Context context,
            boolean activity, @Nullable Locale locale, float fontScale) {
        if (locale == null) {
            return context;
        }

        if (DynamicSdkUtils.is17()) {
            return updateResources(context, activity, locale, fontScale);
        }

        return updateResourcesLegacy(context, locale, fontScale);
    }

    /**
     * Set the locale for a given context.
     *
     * @param context The context to set the locale.
     * @param locale The locale to be used for the context resources.
     * @param fontScale The font scale to be used for the context resources.
     *
     * @return The modified context after applying the locale.
     */
    public static @NonNull Context setLocale(@NonNull Context context,
            @Nullable Locale locale, float fontScale) {
        return setLocale(context, true, locale, fontScale);
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
     * Update resources for a given context after setting the locale on API 17 and above devices.
     *
     * @param context The context to set the updated resources.
     * @param activity {@code true} if the context an instance of {@link Activity}.
     * @param locale The locale to be used for the context resources.
     * @param fontScale The font scale to be used for the context resources.
     *
     * @return The modified context after applying the locale.
     */
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
     * Update resources for a given context after setting the locale on
     * {@link Build.VERSION_CODES#JELLY_BEAN} or below devices.
     *
     * @param context The context to set the updated resources.
     * @param locale The locale to be used for the context resources.
     * @param fontScale The font scale to be used for the context resources.
     *
     * @return The modified context after applying the locale.
     */
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
    public static @NonNull Locale getCurrentLocale(@NonNull Context context) {
        return ConfigurationCompat.getLocales(context.getResources().getConfiguration()).get(0);
    }
}
