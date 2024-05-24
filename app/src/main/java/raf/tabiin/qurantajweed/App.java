package raf.tabiin.qurantajweed;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import raf.tabiin.qurantajweed.utils.SharedPreferencesUtils;

public class App extends Application {
    public static App instance = null;

    public static App getInstance() {
        if (instance == null)
            instance = new App();
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        setNightMode();
    }

    public void setNightMode() {
        int nightMode = SharedPreferencesUtils.getInteger(this, "nightMode", 1);
        int nightIcon = SharedPreferencesUtils.getInteger(this, "nightIcon", R.drawable.vectornightpress);
        int[] mode = {
                AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY,
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
                AppCompatDelegate.MODE_NIGHT_NO,
                AppCompatDelegate.MODE_NIGHT_YES};

        AppCompatDelegate.setDefaultNightMode(mode[nightMode]);
    }
}