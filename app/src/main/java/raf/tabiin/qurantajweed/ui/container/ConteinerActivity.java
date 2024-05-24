package raf.tabiin.qurantajweed.ui.container;

import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.color.DynamicColors;

import raf.tabiin.qurantajweed.App;
import raf.tabiin.qurantajweed.R;
import raf.tabiin.qurantajweed.databinding.ActivityConteinerBinding;
import raf.tabiin.qurantajweed.databinding.ActivityMainBinding;
import raf.tabiin.qurantajweed.ui.container.modules.PortfolioFragment;
import raf.tabiin.qurantajweed.ui.container.modules.QuranHatmsFragment;
import raf.tabiin.qurantajweed.ui.container.modules.TajweedFragment;
import raf.tabiin.qurantajweed.ui.container.modules.about_app.AppAboutFragment;
import raf.tabiin.qurantajweed.utils.SharedPreferencesUtils;

public class ConteinerActivity extends AppCompatActivity {
    ActivityConteinerBinding b;
    TajweedFragment tajweedFragment;
    PortfolioFragment portfolioFragment;
    QuranHatmsFragment quranHatmsFragment;
    AppAboutFragment appAboutFragment;
    Boolean flag = false;
    View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        int nightIcon = SharedPreferencesUtils.getInteger(this, "nightIcon", R.drawable.vectornightpress);

        App.instance.setNightMode();

        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        view = findViewById(R.id.view);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.containerFragment, new AppAboutFragment())
                    .commit();
        }

        if (SharedPreferencesUtils.getBoolean(this, "useDynamicColors"))
            DynamicColors.applyToActivityIfAvailable(this);

        if (SharedPreferencesUtils.getBoolean(this, "addFollowSystemIcon"))
            flag = true;

        b = ActivityConteinerBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        appAboutFragment = new AppAboutFragment();
        tajweedFragment = new TajweedFragment();
        portfolioFragment = new PortfolioFragment();
        quranHatmsFragment = new QuranHatmsFragment();

        b.navView.setSelectedItemId(R.id.about_app);

        b.navView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {

                case R.id.tajweed:

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.containerFragment, new TajweedFragment())
                            .commit();

                    return true;

                case R.id.hatm_quran:

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.containerFragment, new QuranHatmsFragment())
                            .commit();

                    return true;

                case R.id.our_projects:

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.containerFragment, new PortfolioFragment())
                            .commit();

                    return true;

                case R.id.about_app:

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.containerFragment, new AppAboutFragment())
                            .commit();
                    return true;
            }
            return false;
        });


    }
}