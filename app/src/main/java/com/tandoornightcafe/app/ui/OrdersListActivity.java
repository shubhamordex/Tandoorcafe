package com.tandoornightcafe.app.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.tandoornightcafe.app.R;
import com.tandoornightcafe.app.adapter.OrderAdapter;
import com.tandoornightcafe.app.db.DBHelper;
import com.tandoornightcafe.app.model.Order;
import com.tandoornightcafe.app.model.OrderItem;
import com.tandoornightcafe.app.util.LocaleHelper;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrdersListActivity extends AppCompatActivity implements OrderAdapter.OnOrderClickListener {
    private DBHelper dbHelper;
    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<Order> orders;
    private TextView emptyView;

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
        setContentView(R.layout.activity_orders_list);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        dbHelper = new DBHelper(this);

        recyclerView = findViewById(R.id.recycler_view);
        emptyView = findViewById(R.id.text_empty);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadOrders();
    }

    private void loadOrders() {
        orders = dbHelper.getAllOrders();
        if (orders.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            if (adapter == null) {
                adapter = new OrderAdapter(orders, this);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.updateOrders(orders);
            }
        }
    }

    @Override
    public void onOrderClick(Order order) {
        showOrderDetails(order);
    }

    private void showOrderDetails(Order order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.view_details));

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_order_details, null);
        builder.setView(dialogView);

        TextView invoiceText = dialogView.findViewById(R.id.text_invoice);
        TextView dateText = dialogView.findViewById(R.id.text_date);
        TextView customerText = dialogView.findViewById(R.id.text_customer);
        TextView phoneText = dialogView.findViewById(R.id.text_phone);
        TextView paymentText = dialogView.findViewById(R.id.text_payment);
        TextView itemsText = dialogView.findViewById(R.id.text_items);
        TextView subtotalText = dialogView.findViewById(R.id.text_subtotal);
        TextView taxText = dialogView.findViewById(R.id.text_tax);
        TextView totalText = dialogView.findViewById(R.id.text_total);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        
        invoiceText.setText(order.getInvoiceNumber());
        dateText.setText(dateFormat.format(order.getOrderDate()));
        customerText.setText(order.getCustomerName());
        phoneText.setText(order.getCustomerPhone() != null ? order.getCustomerPhone() : "-");
        paymentText.setText(order.getPaymentMethod());
        subtotalText.setText(String.format(Locale.getDefault(), "₹%.2f", order.getSubtotal()));
        taxText.setText(String.format(Locale.getDefault(), "₹%.2f", order.getTax()));
        totalText.setText(String.format(Locale.getDefault(), "₹%.2f", order.getTotal()));

        List<OrderItem> orderItems = dbHelper.getOrderItems(order.getId());
        StringBuilder itemsBuilder = new StringBuilder();
        for (OrderItem item : orderItems) {
            itemsBuilder.append(item.getItemName())
                    .append(" x").append(item.getQuantity())
                    .append(" - ₹").append(String.format(Locale.getDefault(), "%.2f", item.getSubtotal()))
                    .append("\n");
        }
        itemsText.setText(itemsBuilder.toString());

        builder.setPositiveButton(R.string.ok, null);
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
