package com.tandoornightcafe.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tandoornightcafe.app.model.MenuItem;
import com.tandoornightcafe.app.model.Order;
import com.tandoornightcafe.app.model.OrderItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "tandoor_cafe.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_MENU_ITEMS = "menu_items";
    private static final String TABLE_ORDERS = "orders";
    private static final String TABLE_ORDER_ITEMS = "order_items";
    private static final String TABLE_SETTINGS = "settings";

    private static final SimpleDateFormat dateFormat = 
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createMenuItemsTable = "CREATE TABLE " + TABLE_MENU_ITEMS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "description TEXT, " +
                "category TEXT NOT NULL, " +
                "price REAL NOT NULL)";
        db.execSQL(createMenuItemsTable);

        String createOrdersTable = "CREATE TABLE " + TABLE_ORDERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "customer_name TEXT NOT NULL, " +
                "customer_phone TEXT, " +
                "subtotal REAL NOT NULL, " +
                "tax REAL NOT NULL, " +
                "total REAL NOT NULL, " +
                "payment_method TEXT NOT NULL, " +
                "status TEXT NOT NULL, " +
                "order_date TEXT NOT NULL, " +
                "invoice_number TEXT UNIQUE NOT NULL)";
        db.execSQL(createOrdersTable);

        String createOrderItemsTable = "CREATE TABLE " + TABLE_ORDER_ITEMS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_id INTEGER NOT NULL, " +
                "menu_item_id INTEGER NOT NULL, " +
                "item_name TEXT NOT NULL, " +
                "quantity INTEGER NOT NULL, " +
                "price REAL NOT NULL, " +
                "subtotal REAL NOT NULL, " +
                "FOREIGN KEY(order_id) REFERENCES " + TABLE_ORDERS + "(id))";
        db.execSQL(createOrderItemsTable);

        String createSettingsTable = "CREATE TABLE " + TABLE_SETTINGS + " (" +
                "key TEXT PRIMARY KEY, " +
                "value TEXT NOT NULL)";
        db.execSQL(createSettingsTable);

        insertDefaultSettings(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENU_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        onCreate(db);
    }

    private void insertDefaultSettings(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        
        values.put("key", "language");
        values.put("value", "en");
        db.insert(TABLE_SETTINGS, null, values);

        values.clear();
        values.put("key", "tax_rate");
        values.put("value", "5.0");
        db.insert(TABLE_SETTINGS, null, values);

        values.clear();
        values.put("key", "currency_symbol");
        values.put("value", "₹");
        db.insert(TABLE_SETTINGS, null, values);

        values.clear();
        values.put("key", "restaurant_name");
        values.put("value", "Tandoor Night Cafe");
        db.insert(TABLE_SETTINGS, null, values);

        values.clear();
        values.put("key", "restaurant_address");
        values.put("value", "");
        db.insert(TABLE_SETTINGS, null, values);

        values.clear();
        values.put("key", "restaurant_phone");
        values.put("value", "");
        db.insert(TABLE_SETTINGS, null, values);
    }

    public long addMenuItem(MenuItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", item.getName());
        values.put("description", item.getDescription());
        values.put("category", item.getCategory());
        values.put("price", item.getPrice());
        long id = db.insert(TABLE_MENU_ITEMS, null, values);
        db.close();
        return id;
    }

    public int updateMenuItem(MenuItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", item.getName());
        values.put("description", item.getDescription());
        values.put("category", item.getCategory());
        values.put("price", item.getPrice());
        int rows = db.update(TABLE_MENU_ITEMS, values, "id = ?", 
                new String[]{String.valueOf(item.getId())});
        db.close();
        return rows;
    }

    public void deleteMenuItem(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MENU_ITEMS, "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public MenuItem getMenuItem(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MENU_ITEMS, null, "id = ?", 
                new String[]{String.valueOf(id)}, null, null, null);
        MenuItem item = null;
        if (cursor != null && cursor.moveToFirst()) {
            item = cursorToMenuItem(cursor);
            cursor.close();
        }
        db.close();
        return item;
    }

    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MENU_ITEMS, null, null, null, null, null, "name ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                items.add(cursorToMenuItem(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return items;
    }

    public List<MenuItem> getMenuItemsByCategory(String category) {
        List<MenuItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MENU_ITEMS, null, "category = ?", 
                new String[]{category}, null, null, "name ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                items.add(cursorToMenuItem(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return items;
    }

    private MenuItem cursorToMenuItem(Cursor cursor) {
        MenuItem item = new MenuItem();
        item.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
        item.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
        item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
        item.setCategory(cursor.getString(cursor.getColumnIndexOrThrow("category")));
        item.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
        return item;
    }

    public long createOrder(Order order, List<OrderItem> orderItems) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        long orderId = -1;
        try {
            ContentValues values = new ContentValues();
            values.put("customer_name", order.getCustomerName());
            values.put("customer_phone", order.getCustomerPhone());
            values.put("subtotal", order.getSubtotal());
            values.put("tax", order.getTax());
            values.put("total", order.getTotal());
            values.put("payment_method", order.getPaymentMethod());
            values.put("status", order.getStatus());
            values.put("order_date", dateFormat.format(order.getOrderDate()));
            values.put("invoice_number", order.getInvoiceNumber());
            
            orderId = db.insert(TABLE_ORDERS, null, values);

            if (orderId != -1) {
                for (OrderItem item : orderItems) {
                    ContentValues itemValues = new ContentValues();
                    itemValues.put("order_id", orderId);
                    itemValues.put("menu_item_id", item.getMenuItemId());
                    itemValues.put("item_name", item.getItemName());
                    itemValues.put("quantity", item.getQuantity());
                    itemValues.put("price", item.getPrice());
                    itemValues.put("subtotal", item.getSubtotal());
                    db.insert(TABLE_ORDER_ITEMS, null, itemValues);
                }
                db.setTransactionSuccessful();
            }
        } finally {
            db.endTransaction();
            db.close();
        }
        return orderId;
    }

    public Order getOrder(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ORDERS, null, "id = ?", 
                new String[]{String.valueOf(id)}, null, null, null);
        Order order = null;
        if (cursor != null && cursor.moveToFirst()) {
            order = cursorToOrder(cursor);
            cursor.close();
        }
        db.close();
        return order;
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ORDERS, null, null, null, null, null, "order_date DESC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                orders.add(cursorToOrder(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return orders;
    }

    public List<Order> getOrdersByDateRange(Date startDate, Date endDate) {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String start = dateFormat.format(startDate);
        String end = dateFormat.format(endDate);
        Cursor cursor = db.query(TABLE_ORDERS, null, 
                "order_date BETWEEN ? AND ?", 
                new String[]{start, end}, null, null, "order_date DESC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                orders.add(cursorToOrder(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return orders;
    }

    private Order cursorToOrder(Cursor cursor) {
        Order order = new Order();
        order.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
        order.setCustomerName(cursor.getString(cursor.getColumnIndexOrThrow("customer_name")));
        order.setCustomerPhone(cursor.getString(cursor.getColumnIndexOrThrow("customer_phone")));
        order.setSubtotal(cursor.getDouble(cursor.getColumnIndexOrThrow("subtotal")));
        order.setTax(cursor.getDouble(cursor.getColumnIndexOrThrow("tax")));
        order.setTotal(cursor.getDouble(cursor.getColumnIndexOrThrow("total")));
        order.setPaymentMethod(cursor.getString(cursor.getColumnIndexOrThrow("payment_method")));
        order.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
        order.setInvoiceNumber(cursor.getString(cursor.getColumnIndexOrThrow("invoice_number")));
        
        String dateStr = cursor.getString(cursor.getColumnIndexOrThrow("order_date"));
        try {
            order.setOrderDate(dateFormat.parse(dateStr));
        } catch (ParseException e) {
            order.setOrderDate(new Date());
        }
        
        return order;
    }

    public List<OrderItem> getOrderItems(long orderId) {
        List<OrderItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ORDER_ITEMS, null, "order_id = ?", 
                new String[]{String.valueOf(orderId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                items.add(cursorToOrderItem(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return items;
    }

    private OrderItem cursorToOrderItem(Cursor cursor) {
        OrderItem item = new OrderItem();
        item.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
        item.setOrderId(cursor.getLong(cursor.getColumnIndexOrThrow("order_id")));
        item.setMenuItemId(cursor.getLong(cursor.getColumnIndexOrThrow("menu_item_id")));
        item.setItemName(cursor.getString(cursor.getColumnIndexOrThrow("item_name")));
        item.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow("quantity")));
        item.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
        item.setSubtotal(cursor.getDouble(cursor.getColumnIndexOrThrow("subtotal")));
        return item;
    }

    public String getSetting(String key) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SETTINGS, new String[]{"value"}, 
                "key = ?", new String[]{key}, null, null, null);
        String value = null;
        if (cursor != null && cursor.moveToFirst()) {
            value = cursor.getString(0);
            cursor.close();
        }
        db.close();
        return value;
    }

    public void setSetting(String key, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("value", value);
        int rows = db.update(TABLE_SETTINGS, values, "key = ?", new String[]{key});
        if (rows == 0) {
            values.put("key", key);
            db.insert(TABLE_SETTINGS, null, values);
        }
        db.close();
    }

    public String generateInvoiceNumber() {
        String prefix = "INV";
        String date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_ORDERS + " WHERE invoice_number LIKE ?",
                new String[]{prefix + date + "%"});
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return String.format("%s%s%04d", prefix, date, count + 1);
    }
}
