package com.tandoornightcafe.app.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.tandoornightcafe.app.R;
import com.tandoornightcafe.app.adapter.CartAdapter;
import com.tandoornightcafe.app.adapter.SelectMenuAdapter;
import com.tandoornightcafe.app.db.DBHelper;
import com.tandoornightcafe.app.model.CartItem;
import com.tandoornightcafe.app.model.MenuItem;
import com.tandoornightcafe.app.util.LocaleHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderActivity extends AppCompatActivity 
        implements SelectMenuAdapter.OnMenuItemSelectListener, CartAdapter.OnCartItemChangeListener {
    
    private DBHelper dbHelper;
    private RecyclerView menuRecyclerView;
    private RecyclerView cartRecyclerView;
    private SelectMenuAdapter menuAdapter;
    private CartAdapter cartAdapter;
    private List<MenuItem> menuItems;
    private List<CartItem> cartItems;
    private TextView subtotalText;
    private TextView taxText;
    private TextView totalText;
    private Button proceedButton;
    private TabLayout categoryTabs;
    private double taxRate = 5.0;

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
        setContentView(R.layout.activity_order);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        dbHelper = new DBHelper(this);
        String taxRateStr = dbHelper.getSetting("tax_rate");
        if (taxRateStr != null) {
            try {
                taxRate = Double.parseDouble(taxRateStr);
            } catch (NumberFormatException e) {
                taxRate = 5.0;
            }
        }

        categoryTabs = findViewById(R.id.category_tabs);
        menuRecyclerView = findViewById(R.id.recycler_menu);
        cartRecyclerView = findViewById(R.id.recycler_cart);
        subtotalText = findViewById(R.id.text_subtotal);
        taxText = findViewById(R.id.text_tax);
        totalText = findViewById(R.id.text_total);
        proceedButton = findViewById(R.id.button_proceed);

        menuRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItems, this);
        cartRecyclerView.setAdapter(cartAdapter);

        setupCategoryTabs();
        loadMenuItems(null);

        proceedButton.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, R.string.error_select_items, Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(OrderActivity.this, BillingActivity.class);
            intent.putExtra("subtotal", calculateSubtotal());
            intent.putExtra("tax", calculateTax());
            intent.putExtra("total", calculateTotal());
            
            ArrayList<Long> itemIds = new ArrayList<>();
            ArrayList<Integer> quantities = new ArrayList<>();
            for (CartItem item : cartItems) {
                itemIds.add(item.getMenuItem().getId());
                quantities.add(item.getQuantity());
            }
            intent.putExtra("item_ids", itemIds);
            intent.putExtra("quantities", quantities);
            
            startActivity(intent);
        });

        updateTotals();
    }

    private void setupCategoryTabs() {
        categoryTabs.addTab(categoryTabs.newTab().setText(R.string.all));
        categoryTabs.addTab(categoryTabs.newTab().setText(R.string.category_appetizer));
        categoryTabs.addTab(categoryTabs.newTab().setText(R.string.category_main_course));
        categoryTabs.addTab(categoryTabs.newTab().setText(R.string.category_bread));
        categoryTabs.addTab(categoryTabs.newTab().setText(R.string.category_beverage));
        categoryTabs.addTab(categoryTabs.newTab().setText(R.string.category_dessert));

        categoryTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String category = null;
                if (tab.getPosition() > 0) {
                    category = tab.getText().toString();
                }
                loadMenuItems(category);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadMenuItems(String category) {
        if (category == null) {
            menuItems = dbHelper.getAllMenuItems();
        } else {
            menuItems = dbHelper.getMenuItemsByCategory(category);
        }
        
        if (menuAdapter == null) {
            menuAdapter = new SelectMenuAdapter(menuItems, this);
            menuRecyclerView.setAdapter(menuAdapter);
        } else {
            menuAdapter.updateItems(menuItems);
        }
    }

    @Override
    public void onItemSelected(MenuItem item) {
        for (CartItem cartItem : cartItems) {
            if (cartItem.getMenuItem().getId() == item.getId()) {
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                cartAdapter.updateItems(cartItems);
                updateTotals();
                return;
            }
        }
        
        cartItems.add(new CartItem(item, 1));
        cartAdapter.updateItems(cartItems);
        updateTotals();
    }

    @Override
    public void onQuantityChanged() {
        updateTotals();
    }

    @Override
    public void onRemoveItem(CartItem item) {
        cartItems.remove(item);
        cartAdapter.updateItems(cartItems);
        updateTotals();
    }

    private double calculateSubtotal() {
        double subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += item.getSubtotal();
        }
        return subtotal;
    }

    private double calculateTax() {
        return calculateSubtotal() * taxRate / 100;
    }

    private double calculateTotal() {
        return calculateSubtotal() + calculateTax();
    }

    private void updateTotals() {
        subtotalText.setText(String.format(Locale.getDefault(), "₹%.2f", calculateSubtotal()));
        taxText.setText(String.format(Locale.getDefault(), "₹%.2f (%.1f%%)", calculateTax(), taxRate));
        totalText.setText(String.format(Locale.getDefault(), "₹%.2f", calculateTotal()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
