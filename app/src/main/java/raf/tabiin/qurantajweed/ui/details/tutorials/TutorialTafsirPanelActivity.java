package raf.tabiin.qurantajweed.ui.details.tutorials;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import raf.tabiin.qurantajweed.MainActivity;
import raf.tabiin.qurantajweed.R;
import raf.tabiin.qurantajweed.databinding.ActivityTutorialTafsirPanelBinding;
import raf.tabiin.qurantajweed.ui.container.ConteinerActivity;

public class TutorialTafsirPanelActivity extends AppCompatActivity {
    ActivityTutorialTafsirPanelBinding b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        b = ActivityTutorialTafsirPanelBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.closeTutorial.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

    }
}