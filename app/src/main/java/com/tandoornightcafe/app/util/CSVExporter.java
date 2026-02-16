package com.tandoornightcafe.app.util;

import android.content.Context;
import android.os.Environment;

import com.tandoornightcafe.app.model.Order;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CSVExporter {
    
    public static File exportOrders(Context context, List<Order> orders) throws IOException {
        File directory = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Reports");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        SimpleDateFormat fileNameFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String fileName = "Sales_Report_" + fileNameFormat.format(new java.util.Date()) + ".csv";
        File file = new File(directory, fileName);

        FileWriter writer = new FileWriter(file);
        
        writer.append("Invoice Number,Date,Customer Name,Customer Phone,Subtotal,Tax,Total,Payment Method,Status\n");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        
        for (Order order : orders) {
            writer.append(escapeCSV(order.getInvoiceNumber())).append(",");
            writer.append(escapeCSV(dateFormat.format(order.getOrderDate()))).append(",");
            writer.append(escapeCSV(order.getCustomerName())).append(",");
            writer.append(escapeCSV(order.getCustomerPhone() != null ? order.getCustomerPhone() : "")).append(",");
            writer.append(String.format(Locale.US, "%.2f", order.getSubtotal())).append(",");
            writer.append(String.format(Locale.US, "%.2f", order.getTax())).append(",");
            writer.append(String.format(Locale.US, "%.2f", order.getTotal())).append(",");
            writer.append(escapeCSV(order.getPaymentMethod())).append(",");
            writer.append(escapeCSV(order.getStatus())).append("\n");
        }

        writer.flush();
        writer.close();

        return file;
    }

    private static String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
