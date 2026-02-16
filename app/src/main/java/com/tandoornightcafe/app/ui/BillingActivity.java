package com.tandoornightcafe.app.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.tandoornightcafe.app.R;
import com.tandoornightcafe.app.db.DBHelper;
import com.tandoornightcafe.app.model.MenuItem;
import com.tandoornightcafe.app.model.Order;
import com.tandoornightcafe.app.model.OrderItem;
import com.tandoornightcafe.app.util.LocaleHelper;
import com.tandoornightcafe.app.util.PDFGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BillingActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;

    private DBHelper dbHelper;
    private EditText customerNameInput;
    private EditText customerPhoneInput;
    private Spinner paymentMethodSpinner;
    private TextView subtotalText;
    private TextView taxText;
    private TextView totalText;
    private Button generateInvoiceButton;

    private double subtotal;
    private double tax;
    private double total;
    private ArrayList<Long> itemIds;
    private ArrayList<Integer> quantities;

    private Order savedOrder;
    private List<OrderItem> savedOrderItems;

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
        setContentView(R.layout.activity_billing);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        dbHelper = new DBHelper(this);

        customerNameInput = findViewById(R.id.input_customer_name);
        customerPhoneInput = findViewById(R.id.input_customer_phone);
        paymentMethodSpinner = findViewById(R.id.spinner_payment_method);
        subtotalText = findViewById(R.id.text_subtotal);
        taxText = findViewById(R.id.text_tax);
        totalText = findViewById(R.id.text_total);
        generateInvoiceButton = findViewById(R.id.button_generate_invoice);
        Button savePdfButton = findViewById(R.id.button_save_pdf);
        Button shareButton = findViewById(R.id.button_share);

        String[] paymentMethods = {
                getString(R.string.payment_cash),
                getString(R.string.payment_card),
                getString(R.string.payment_upi)
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, paymentMethods);
        paymentMethodSpinner.setAdapter(adapter);

        Intent intent = getIntent();
        subtotal = intent.getDoubleExtra("subtotal", 0);
        tax = intent.getDoubleExtra("tax", 0);
        total = intent.getDoubleExtra("total", 0);
        itemIds = (ArrayList<Long>) intent.getSerializableExtra("item_ids");
        quantities = (ArrayList<Integer>) intent.getSerializableExtra("quantities");

        subtotalText.setText(String.format(Locale.getDefault(), "₹%.2f", subtotal));
        taxText.setText(String.format(Locale.getDefault(), "₹%.2f", tax));
        totalText.setText(String.format(Locale.getDefault(), "₹%.2f", total));

        generateInvoiceButton.setOnClickListener(v -> generateInvoice());

        savePdfButton.setOnClickListener(v -> {
            if (savedOrder != null) {
                savePDF();
            } else {
                Toast.makeText(this, R.string.generate_invoice, Toast.LENGTH_SHORT).show();
            }
        });

        shareButton.setOnClickListener(v -> {
            if (savedOrder != null) {
                shareInvoice();
            } else {
                Toast.makeText(this, R.string.generate_invoice, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateInvoice() {
        String customerName = customerNameInput.getText().toString().trim();
        String customerPhone = customerPhoneInput.getText().toString().trim();
        String paymentMethod = paymentMethodSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(customerName)) {
            customerNameInput.setError(getString(R.string.error_empty_field));
            return;
        }

        Order order = new Order();
        order.setCustomerName(customerName);
        order.setCustomerPhone(customerPhone);
        order.setSubtotal(subtotal);
        order.setTax(tax);
        order.setTotal(total);
        order.setPaymentMethod(paymentMethod);
        order.setStatus(getString(R.string.status_completed));
        order.setOrderDate(new Date());
        order.setInvoiceNumber(dbHelper.generateInvoiceNumber());

        List<OrderItem> orderItems = new ArrayList<>();
        for (int i = 0; i < itemIds.size(); i++) {
            MenuItem menuItem = dbHelper.getMenuItem(itemIds.get(i));
            int quantity = quantities.get(i);
            
            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItemId(menuItem.getId());
            orderItem.setItemName(menuItem.getName());
            orderItem.setQuantity(quantity);
            orderItem.setPrice(menuItem.getPrice());
            orderItem.setSubtotal(menuItem.getPrice() * quantity);
            orderItems.add(orderItem);
        }

        long orderId = dbHelper.createOrder(order, orderItems);
        order.setId(orderId);

        savedOrder = order;
        savedOrderItems = orderItems;

        Toast.makeText(this, R.string.order_placed, Toast.LENGTH_SHORT).show();
        generateInvoiceButton.setEnabled(false);
    }

    private void savePDF() {
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
            String restaurantName = dbHelper.getSetting("restaurant_name");
            String restaurantAddress = dbHelper.getSetting("restaurant_address");
            String restaurantPhone = dbHelper.getSetting("restaurant_phone");

            File pdfFile = PDFGenerator.generateInvoice(this, savedOrder, savedOrderItems,
                    restaurantName, restaurantAddress, restaurantPhone);

            Toast.makeText(this, getString(R.string.report_exported) + "\n" + pdfFile.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error) + ": " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void shareInvoice() {
        try {
            String restaurantName = dbHelper.getSetting("restaurant_name");
            String restaurantAddress = dbHelper.getSetting("restaurant_address");
            String restaurantPhone = dbHelper.getSetting("restaurant_phone");

            File pdfFile = PDFGenerator.generateInvoice(this, savedOrder, savedOrderItems,
                    restaurantName, restaurantAddress, restaurantPhone);

            Uri uri = FileProvider.getUriForFile(this,
                    getApplicationContext().getPackageName() + ".provider", pdfFile);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_invoice)));
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
                savePDF();
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
