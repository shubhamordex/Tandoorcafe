package com.tandoornightcafe.app.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.tandoornightcafe.app.R;
import com.tandoornightcafe.app.db.DBHelper;
import com.tandoornightcafe.app.util.LocaleHelper;

public class SettingsActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    private RadioGroup languageGroup;
    private RadioButton englishRadio;
    private RadioButton hindiRadio;
    private EditText taxRateInput;
    private EditText restaurantNameInput;
    private EditText restaurantAddressInput;
    private EditText restaurantPhoneInput;
    private Button saveButton;

    private String currentLanguage;

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
        setContentView(R.layout.activity_settings);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        dbHelper = new DBHelper(this);

        languageGroup = findViewById(R.id.radio_group_language);
        englishRadio = findViewById(R.id.radio_english);
        hindiRadio = findViewById(R.id.radio_hindi);
        taxRateInput = findViewById(R.id.input_tax_rate);
        restaurantNameInput = findViewById(R.id.input_restaurant_name);
        restaurantAddressInput = findViewById(R.id.input_restaurant_address);
        restaurantPhoneInput = findViewById(R.id.input_restaurant_phone);
        saveButton = findViewById(R.id.button_save);

        loadSettings();

        saveButton.setOnClickListener(v -> saveSettings());
    }

    private void loadSettings() {
        currentLanguage = dbHelper.getSetting("language");
        if (currentLanguage == null) currentLanguage = "en";
        
        if (currentLanguage.equals("hi")) {
            hindiRadio.setChecked(true);
        } else {
            englishRadio.setChecked(true);
        }

        String taxRate = dbHelper.getSetting("tax_rate");
        if (taxRate != null) {
            taxRateInput.setText(taxRate);
        }

        String restaurantName = dbHelper.getSetting("restaurant_name");
        if (restaurantName != null) {
            restaurantNameInput.setText(restaurantName);
        }

        String restaurantAddress = dbHelper.getSetting("restaurant_address");
        if (restaurantAddress != null) {
            restaurantAddressInput.setText(restaurantAddress);
        }

        String restaurantPhone = dbHelper.getSetting("restaurant_phone");
        if (restaurantPhone != null) {
            restaurantPhoneInput.setText(restaurantPhone);
        }
    }

    private void saveSettings() {
        String selectedLanguage = englishRadio.isChecked() ? "en" : "hi";
        String taxRate = taxRateInput.getText().toString().trim();
        String restaurantName = restaurantNameInput.getText().toString().trim();
        String restaurantAddress = restaurantAddressInput.getText().toString().trim();
        String restaurantPhone = restaurantPhoneInput.getText().toString().trim();

        if (TextUtils.isEmpty(taxRate)) {
            taxRateInput.setError(getString(R.string.error_empty_field));
            return;
        }

        double taxRateValue;
        try {
            taxRateValue = Double.parseDouble(taxRate);
            if (taxRateValue < 0 || taxRateValue > 100) {
                taxRateInput.setError(getString(R.string.error_invalid_tax));
                return;
            }
        } catch (NumberFormatException e) {
            taxRateInput.setError(getString(R.string.error_invalid_tax));
            return;
        }

        if (TextUtils.isEmpty(restaurantName)) {
            restaurantNameInput.setError(getString(R.string.error_empty_field));
            return;
        }

        dbHelper.setSetting("language", selectedLanguage);
        dbHelper.setSetting("tax_rate", taxRate);
        dbHelper.setSetting("restaurant_name", restaurantName);
        dbHelper.setSetting("restaurant_address", restaurantAddress);
        dbHelper.setSetting("restaurant_phone", restaurantPhone);

        Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show();

        if (!selectedLanguage.equals(currentLanguage)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.language)
                    .setMessage("Restart app to apply language change / भाषा परिवर्तन लागू करने के लिए ऐप पुनः प्रारंभ करें")
                    .setPositiveButton(R.string.ok, (dialog, which) -> {
                        Intent intent = new Intent(SettingsActivity.this, SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
