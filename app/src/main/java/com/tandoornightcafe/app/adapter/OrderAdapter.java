package com.tandoornightcafe.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tandoornightcafe.app.R;
import com.tandoornightcafe.app.model.Order;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private List<Order> orders;
    private OnOrderClickListener listener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public OrderAdapter(List<Order> orders, OnOrderClickListener listener) {
        this.orders = orders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order, listener, dateFormat);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void updateOrders(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView invoiceText;
        TextView dateText;
        TextView customerText;
        TextView totalText;
        TextView statusText;

        ViewHolder(View itemView) {
            super(itemView);
            invoiceText = itemView.findViewById(R.id.text_invoice);
            dateText = itemView.findViewById(R.id.text_date);
            customerText = itemView.findViewById(R.id.text_customer);
            totalText = itemView.findViewById(R.id.text_total);
            statusText = itemView.findViewById(R.id.text_status);
        }

        void bind(Order order, OnOrderClickListener listener, SimpleDateFormat dateFormat) {
            invoiceText.setText(order.getInvoiceNumber());
            dateText.setText(dateFormat.format(order.getOrderDate()));
            customerText.setText(order.getCustomerName());
            totalText.setText(String.format(Locale.getDefault(), "₹%.2f", order.getTotal()));
            statusText.setText(order.getStatus());

            itemView.setOnClickListener(v -> listener.onOrderClick(order));
        }
    }
}
