package com.tandoornightcafe.app.model;

import java.util.Date;

public class Order {
    private long id;
    private String customerName;
    private String customerPhone;
    private double subtotal;
    private double tax;
    private double total;
    private String paymentMethod;
    private String status;
    private Date orderDate;
    private String invoiceNumber;

    public Order() {
    }

    public Order(long id, String customerName, String customerPhone, double subtotal, 
                 double tax, double total, String paymentMethod, String status, 
                 Date orderDate, String invoiceNumber) {
        this.id = id;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.subtotal = subtotal;
        this.tax = tax;
        this.total = total;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.orderDate = orderDate;
        this.invoiceNumber = invoiceNumber;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
}
