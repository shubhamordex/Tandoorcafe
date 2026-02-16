package com.tandoornightcafe.app.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tandoornightcafe.app.R;
import com.tandoornightcafe.app.adapter.MenuAdapter;
import com.tandoornightcafe.app.db.DBHelper;
import com.tandoornightcafe.app.model.MenuItem;
import com.tandoornightcafe.app.util.LocaleHelper;

import java.util.List;

public class MenuActivity extends AppCompatActivity implements MenuAdapter.OnMenuItemClickListener {
    private DBHelper dbHelper;
    private RecyclerView recyclerView;
    private MenuAdapter adapter;
    private List<MenuItem> menuItems;

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
        setContentView(R.layout.activity_menu);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        dbHelper = new DBHelper(this);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> showAddEditDialog(null));

        loadMenuItems();
    }

    private void loadMenuItems() {
        menuItems = dbHelper.getAllMenuItems();
        if (adapter == null) {
            adapter = new MenuAdapter(menuItems, this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateItems(menuItems);
        }
    }

    private void showAddEditDialog(MenuItem item) {
        boolean isEdit = item != null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isEdit ? R.string.edit_item : R.string.add_item);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_menu_item, null);
        builder.setView(dialogView);

        EditText nameInput = dialogView.findViewById(R.id.input_name);
        EditText descriptionInput = dialogView.findViewById(R.id.input_description);
        Spinner categorySpinner = dialogView.findViewById(R.id.spinner_category);
        EditText priceInput = dialogView.findViewById(R.id.input_price);

        String[] categories = {
                getString(R.string.category_appetizer),
                getString(R.string.category_main_course),
                getString(R.string.category_bread),
                getString(R.string.category_beverage),
                getString(R.string.category_dessert)
        };
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, categories);
        categorySpinner.setAdapter(spinnerAdapter);

        if (isEdit) {
            nameInput.setText(item.getName());
            descriptionInput.setText(item.getDescription());
            priceInput.setText(String.valueOf(item.getPrice()));
            
            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equals(item.getCategory())) {
                    categorySpinner.setSelection(i);
                    break;
                }
            }
        }

        builder.setPositiveButton(R.string.save, null);
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();
            String category = categorySpinner.getSelectedItem().toString();
            String priceStr = priceInput.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                nameInput.setError(getString(R.string.error_empty_field));
                return;
            }

            if (TextUtils.isEmpty(priceStr)) {
                priceInput.setError(getString(R.string.error_empty_field));
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceStr);
                if (price <= 0) {
                    priceInput.setError(getString(R.string.error_invalid_price));
                    return;
                }
            } catch (NumberFormatException e) {
                priceInput.setError(getString(R.string.error_invalid_price));
                return;
            }

            if (isEdit) {
                item.setName(name);
                item.setDescription(description);
                item.setCategory(category);
                item.setPrice(price);
                dbHelper.updateMenuItem(item);
                Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show();
            } else {
                MenuItem newItem = new MenuItem(0, name, description, category, price);
                dbHelper.addMenuItem(newItem);
                Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show();
            }

            loadMenuItems();
            dialog.dismiss();
        });
    }

    @Override
    public void onEditClick(MenuItem item) {
        showAddEditDialog(item);
    }

    @Override
    public void onDeleteClick(MenuItem item) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_item)
                .setMessage(R.string.confirm_delete)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    dbHelper.deleteMenuItem(item.getId());
                    loadMenuItems();
                    Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
