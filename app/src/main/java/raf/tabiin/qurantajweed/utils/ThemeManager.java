package raf.tabiin.qurantajweed.utils;

import android.content.res.Resources;

import androidx.appcompat.app.AppCompatDelegate;

import java.util.HashSet;
import java.util.Set;

public class ThemeManager {

private Set<ThemeChangedListener> listeners = new HashSet<>();
    int theme = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;

    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
    }
interface ThemeChangedListener {
    void onThemeChanged(Resources.Theme theme);
}

    public void addListener(ThemeChangedListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ThemeChangedListener listener) {
        listeners.remove(listener);
    }

    // ...
}
