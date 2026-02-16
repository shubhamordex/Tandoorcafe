package com.tandoornightcafe.app.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.GridLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.appbar.MaterialToolbar;
import com.tandoornightcafe.app.R;
import com.tandoornightcafe.app.db.DBHelper;
import com.tandoornightcafe.app.util.LocaleHelper;

public class HomeActivity extends AppCompatActivity {
    private DBHelper dbHelper;

    @Override
    protected void attachBaseContext(Context newBase) {
        DBHelper tempHelper = new DBHelper(newBase);
        String language = tempHelper.getSetting("language");
        tempHelper.close();
        super.attachBaseContext(LocaleHelper.applyLocale(newBase, language != null ? language : "en"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new DBHelper(this);

        CardView menuCard = findViewById(R.id.card_menu);
        CardView orderCard = findViewById(R.id.card_order);
        CardView ordersCard = findViewById(R.id.card_orders);
        CardView reportsCard = findViewById(R.id.card_reports);
        CardView settingsCard = findViewById(R.id.card_settings);

        menuCard.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MenuActivity.class);
            startActivity(intent);
        });

        orderCard.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, OrderActivity.class);
            startActivity(intent);
        });

        ordersCard.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, OrdersListActivity.class);
            startActivity(intent);
        });

        reportsCard.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ReportsActivity.class);
            startActivity(intent);
        });

        settingsCard.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
