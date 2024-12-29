package com.pacman.MentAlly.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.pacman.MentAlly.R;
import com.pacman.MentAlly.ui.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_SCREEN_DURATION = 3000; // Duration for the splash screen

    Animation zoomIn;
    ImageView image;
    TextView t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startlogin);

        image = findViewById(R.id.splash_logo); // Ensure this matches your ImageView ID
        t = findViewById(R.id.sub); // Ensure this matches your TextView ID

        // Load the zoom-in animation
        zoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in);

        // Start the zoom-in animation on the image
        image.startAnimation(zoomIn);

        // Delay for a few seconds before transitioning to the LoginActivity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            // Apply the transition animation
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish(); // Finish SplashActivity
        }, SPLASH_SCREEN_DURATION); // Keep the splash screen visible for 5 seconds
    }
}