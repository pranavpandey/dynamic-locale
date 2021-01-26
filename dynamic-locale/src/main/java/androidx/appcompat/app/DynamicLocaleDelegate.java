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

package androidx.appcompat.app;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StyleRes;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;

import com.pranavpandey.android.dynamic.locale.DynamicLocale;

/**
 * An {@link AppCompatDelegate} to properly apply the locale configurations.
 *
 * @see <a href="https://stackoverflow.com/a/58004553">More info</a>
 */
public class DynamicLocaleDelegate extends AppCompatDelegate {

    /**
     * The super delegate used by this delegate.
     */
    private final AppCompatDelegate mSuperDelegate;

    /**
     * The dynamic locale used by this delegate.
     */
    private final DynamicLocale mDynamicLocale;

    /**
     * Constructor to initialize an object of this class.
     *
     * @param superDelegate The super delegate for this instance.
     * @param dynamicLocale The dynamic locale for this instance.
     */
    public DynamicLocaleDelegate(@NonNull AppCompatDelegate superDelegate,
            @Nullable DynamicLocale dynamicLocale) {
        super();

        this.mSuperDelegate = superDelegate;
        this.mDynamicLocale = dynamicLocale;
    }

    /**
     * Get the super delegate used by this delegate.
     *
     * @return The super delegate used by this delegate.
     */
    public @NonNull AppCompatDelegate getSuperDelegate() {
        return mSuperDelegate;
    }

    /**
     * Get the dynamic locale used by this delegate.
     *
     * @return The dynamic locale used by this delegate.
     */
    public @Nullable DynamicLocale getDynamicLocale() {
        return mDynamicLocale;
    }

    /**
     * Wrap the base context if required.
     *
     * @param context The base context to be wrapped.
     *
     * @return The wrapped base context.
     */
    public @NonNull Context wrapBaseContext(@NonNull Context context) {
        if (getDynamicLocale() != null) {
            return getDynamicLocale().setLocale(context);
        }

        return context;
    }

    @Override
    public @Nullable ActionBar getSupportActionBar() {
        return getSuperDelegate().getSupportActionBar();
    }

    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        getSuperDelegate().setSupportActionBar(toolbar);
    }

    @Override
    public MenuInflater getMenuInflater() {
        return getSuperDelegate().getMenuInflater();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getSuperDelegate().onCreate(savedInstanceState);

        removeActivityDelegate(getSuperDelegate());
        addActiveDelegate(this);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        getSuperDelegate().onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        getSuperDelegate().onConfigurationChanged(newConfig);
    }

    @Override
    public void onStart() {
        getSuperDelegate().onStart();
    }

    @Override
    public void onStop() {
        getSuperDelegate().onStop();
    }

    @Override
    public void onPostResume() {
        getSuperDelegate().onPostResume();
    }

    @Override
    public void setTheme(@StyleRes int themeResId) {
        getSuperDelegate().setTheme(themeResId);
    }

    @Override
    public @Nullable <T extends View> T findViewById(@IdRes int id) {
        return getSuperDelegate().findViewById(id);
    }

    @Override
    public void setContentView(View v) {
        getSuperDelegate().setContentView(v);
    }

    @Override
    public void setContentView(@LayoutRes int resId) {
        getSuperDelegate().setContentView(resId);
    }

    @Override
    public void setContentView(View v, ViewGroup.LayoutParams lp) {
        getSuperDelegate().setContentView(v, lp);
    }

    @Override
    public void addContentView(View v, ViewGroup.LayoutParams lp) {
        getSuperDelegate().addContentView(v, lp);
    }

    @Override
    public @NonNull Context attachBaseContext2(@NonNull Context context) {
        return wrapBaseContext(getSuperDelegate().attachBaseContext2(
                super.attachBaseContext2(context)));
    }

    @Override
    public void setTitle(@Nullable CharSequence title) {
        getSuperDelegate().setTitle(title);
    }

    @Override
    public void invalidateOptionsMenu() {
        getSuperDelegate().invalidateOptionsMenu();
    }

    @Override
    public void onDestroy() {
        getSuperDelegate().onDestroy();
        removeActivityDelegate(this);
    }

    @Override
    public @Nullable ActionBarDrawerToggle.Delegate getDrawerToggleDelegate() {
        return getSuperDelegate().getDrawerToggleDelegate();
    }

    @Override
    public boolean requestWindowFeature(int featureId) {
        return getSuperDelegate().requestWindowFeature(featureId);
    }

    @Override
    public boolean hasWindowFeature(int featureId) {
        return getSuperDelegate().hasWindowFeature(featureId);
    }

    @Override
    public @Nullable ActionMode startSupportActionMode(@NonNull ActionMode.Callback callback) {
        return getSuperDelegate().startSupportActionMode(callback);
    }

    @Override
    public void installViewFactory() {
        getSuperDelegate().installViewFactory();
    }

    @Override
    public View createView(@Nullable View parent, String name,
            @NonNull Context context, @NonNull AttributeSet attrs) {
        return getSuperDelegate().createView(parent, name, context, attrs);
    }

    @Override
    public void setHandleNativeActionModesEnabled(boolean enabled) {
        getSuperDelegate().setHandleNativeActionModesEnabled(enabled);
    }

    @Override
    public boolean isHandleNativeActionModesEnabled() {
        return getSuperDelegate().isHandleNativeActionModesEnabled();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        getSuperDelegate().onSaveInstanceState(outState);
    }

    @Override
    public boolean applyDayNight() {
        return getSuperDelegate().applyDayNight();
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void setLocalNightMode(@NightMode int mode) {
        getSuperDelegate().setLocalNightMode(mode);
    }

    @Override
    public @NightMode int getLocalNightMode() {
        return getSuperDelegate().getLocalNightMode();
    }
}
