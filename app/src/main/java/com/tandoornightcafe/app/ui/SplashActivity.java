package com.tandoornightcafe.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.tandoornightcafe.app.R;
import com.tandoornightcafe.app.db.DBHelper;
import com.tandoornightcafe.app.util.LocaleHelper;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DURATION = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        DBHelper dbHelper = new DBHelper(this);
        String language = dbHelper.getSetting("language");
        if (language != null) {
            LocaleHelper.applyLocale(this, language);
        }
        dbHelper.close();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }
}
