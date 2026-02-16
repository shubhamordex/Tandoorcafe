package com.tandoornightcafe.app.util;

import android.content.Context;
import android.os.Environment;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.tandoornightcafe.app.model.Order;
import com.tandoornightcafe.app.model.OrderItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PDFGenerator {
    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

    public static File generateInvoice(Context context, Order order, List<OrderItem> items,
                                      String restaurantName, String restaurantAddress, 
                                      String restaurantPhone) throws IOException, DocumentException {
        File directory = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Invoices");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = "Invoice_" + order.getInvoiceNumber() + ".pdf";
        File file = new File(directory, fileName);

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        Paragraph title = new Paragraph(restaurantName, TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        if (restaurantAddress != null && !restaurantAddress.isEmpty()) {
            Paragraph address = new Paragraph(restaurantAddress, NORMAL_FONT);
            address.setAlignment(Element.ALIGN_CENTER);
            document.add(address);
        }

        if (restaurantPhone != null && !restaurantPhone.isEmpty()) {
            Paragraph phone = new Paragraph("Phone: " + restaurantPhone, NORMAL_FONT);
            phone.setAlignment(Element.ALIGN_CENTER);
            document.add(phone);
        }

        document.add(new Paragraph("\n"));

        Paragraph invoiceHeader = new Paragraph("INVOICE", HEADER_FONT);
        invoiceHeader.setAlignment(Element.ALIGN_CENTER);
        document.add(invoiceHeader);

        document.add(new Paragraph("\n"));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        document.add(new Paragraph("Invoice Number: " + order.getInvoiceNumber(), NORMAL_FONT));
        document.add(new Paragraph("Date: " + dateFormat.format(order.getOrderDate()), NORMAL_FONT));
        document.add(new Paragraph("Customer: " + order.getCustomerName(), NORMAL_FONT));
        if (order.getCustomerPhone() != null && !order.getCustomerPhone().isEmpty()) {
            document.add(new Paragraph("Phone: " + order.getCustomerPhone(), NORMAL_FONT));
        }
        document.add(new Paragraph("Payment Method: " + order.getPaymentMethod(), NORMAL_FONT));

        document.add(new Paragraph("\n"));

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 1, 2, 2});

        addTableHeader(table, "Item");
        addTableHeader(table, "Qty");
        addTableHeader(table, "Price");
        addTableHeader(table, "Total");

        for (OrderItem item : items) {
            addTableCell(table, item.getItemName());
            addTableCell(table, String.valueOf(item.getQuantity()));
            addTableCell(table, String.format(Locale.getDefault(), "₹%.2f", item.getPrice()));
            addTableCell(table, String.format(Locale.getDefault(), "₹%.2f", item.getSubtotal()));
        }

        document.add(table);

        document.add(new Paragraph("\n"));

        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(100);
        summaryTable.setWidths(new float[]{3, 1});

        addSummaryRow(summaryTable, "Subtotal:", String.format(Locale.getDefault(), "₹%.2f", order.getSubtotal()));
        addSummaryRow(summaryTable, "Tax:", String.format(Locale.getDefault(), "₹%.2f", order.getTax()));
        addSummaryRow(summaryTable, "Total:", String.format(Locale.getDefault(), "₹%.2f", order.getTotal()));

        document.add(summaryTable);

        document.add(new Paragraph("\n\n"));
        Paragraph thanks = new Paragraph("Thank you for your order!", NORMAL_FONT);
        thanks.setAlignment(Element.ALIGN_CENTER);
        document.add(thanks);

        document.close();

        return file;
    }

    private static void addTableHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, HEADER_FONT));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private static void addTableCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, NORMAL_FONT));
        cell.setPadding(5);
        table.addCell(cell);
    }

    private static void addSummaryRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, HEADER_FONT));
        labelCell.setBorder(PdfPCell.NO_BORDER);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        labelCell.setPadding(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, NORMAL_FONT));
        valueCell.setBorder(PdfPCell.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setPadding(5);
        table.addCell(valueCell);
    }
}
