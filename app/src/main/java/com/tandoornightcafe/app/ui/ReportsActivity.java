package com.tandoornightcafe.app.ui;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.tandoornightcafe.app.R;
import com.tandoornightcafe.app.db.DBHelper;
import com.tandoornightcafe.app.model.Order;
import com.tandoornightcafe.app.util.CSVExporter;
import com.tandoornightcafe.app.util.LocaleHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportsActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;

    private DBHelper dbHelper;
    private EditText fromDateInput;
    private EditText toDateInput;
    private TextView totalSalesText;
    private TextView totalOrdersText;
    private TextView averageOrderText;
    private Button generateButton;
    private Button exportButton;

    private Calendar fromDate;
    private Calendar toDate;
    private SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private List<Order> currentOrders;

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
        setContentView(R.layout.activity_reports);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        dbHelper = new DBHelper(this);

        fromDateInput = findViewById(R.id.input_from_date);
        toDateInput = findViewById(R.id.input_to_date);
        totalSalesText = findViewById(R.id.text_total_sales);
        totalOrdersText = findViewById(R.id.text_total_orders);
        averageOrderText = findViewById(R.id.text_average_order);
        generateButton = findViewById(R.id.button_generate);
        exportButton = findViewById(R.id.button_export);

        fromDate = Calendar.getInstance();
        fromDate.add(Calendar.MONTH, -1);
        toDate = Calendar.getInstance();

        fromDateInput.setText(displayFormat.format(fromDate.getTime()));
        toDateInput.setText(displayFormat.format(toDate.getTime()));

        fromDateInput.setOnClickListener(v -> showDatePicker(true));
        toDateInput.setOnClickListener(v -> showDatePicker(false));

        generateButton.setOnClickListener(v -> generateReport());
        exportButton.setOnClickListener(v -> exportReport());
    }

    private void showDatePicker(boolean isFromDate) {
        Calendar calendar = isFromDate ? fromDate : toDate;
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    if (isFromDate) {
                        fromDateInput.setText(displayFormat.format(calendar.getTime()));
                    } else {
                        toDateInput.setText(displayFormat.format(calendar.getTime()));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void generateReport() {
        Date startDate = fromDate.getTime();
        Date endDate = toDate.getTime();

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);
        endCal.set(Calendar.HOUR_OF_DAY, 23);
        endCal.set(Calendar.MINUTE, 59);
        endCal.set(Calendar.SECOND, 59);
        endDate = endCal.getTime();

        currentOrders = dbHelper.getOrdersByDateRange(startDate, endDate);

        if (currentOrders.isEmpty()) {
            Toast.makeText(this, R.string.no_orders, Toast.LENGTH_SHORT).show();
            totalSalesText.setText("₹0.00");
            totalOrdersText.setText("0");
            averageOrderText.setText("₹0.00");
            return;
        }

        double totalSales = 0;
        for (Order order : currentOrders) {
            totalSales += order.getTotal();
        }

        int totalOrders = currentOrders.size();
        double averageOrder = totalSales / totalOrders;

        totalSalesText.setText(String.format(Locale.getDefault(), "₹%.2f", totalSales));
        totalOrdersText.setText(String.valueOf(totalOrders));
        averageOrderText.setText(String.format(Locale.getDefault(), "₹%.2f", averageOrder));

        Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show();
    }

    private void exportReport() {
        if (currentOrders == null || currentOrders.isEmpty()) {
            Toast.makeText(this, R.string.generate_report, Toast.LENGTH_SHORT).show();
            return;
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
                return;
            }
        }

        try {
            File csvFile = CSVExporter.exportOrders(this, currentOrders);
            Toast.makeText(this, getString(R.string.report_exported) + "\n" + csvFile.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error) + ": " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportReport();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
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
