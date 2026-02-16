package com.tandoornightcafe.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tandoornightcafe.app.R;
import com.tandoornightcafe.app.model.CartItem;

import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<CartItem> items;
    private OnCartItemChangeListener listener;

    public interface OnCartItemChangeListener {
        void onQuantityChanged();
        void onRemoveItem(CartItem item);
    }

    public CartAdapter(List<CartItem> items, OnCartItemChangeListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = items.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateItems(List<CartItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView priceText;
        TextView quantityText;
        TextView subtotalText;
        ImageButton decreaseButton;
        ImageButton increaseButton;
        ImageButton removeButton;

        ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.text_name);
            priceText = itemView.findViewById(R.id.text_price);
            quantityText = itemView.findViewById(R.id.text_quantity);
            subtotalText = itemView.findViewById(R.id.text_subtotal);
            decreaseButton = itemView.findViewById(R.id.button_decrease);
            increaseButton = itemView.findViewById(R.id.button_increase);
            removeButton = itemView.findViewById(R.id.button_remove);
        }

        void bind(CartItem item, OnCartItemChangeListener listener) {
            nameText.setText(item.getMenuItem().getName());
            priceText.setText(String.format(Locale.getDefault(), "₹%.2f", item.getMenuItem().getPrice()));
            quantityText.setText(String.valueOf(item.getQuantity()));
            subtotalText.setText(String.format(Locale.getDefault(), "₹%.2f", item.getSubtotal()));

            decreaseButton.setOnClickListener(v -> {
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                    quantityText.setText(String.valueOf(item.getQuantity()));
                    subtotalText.setText(String.format(Locale.getDefault(), "₹%.2f", item.getSubtotal()));
                    listener.onQuantityChanged();
                }
            });

            increaseButton.setOnClickListener(v -> {
                item.setQuantity(item.getQuantity() + 1);
                quantityText.setText(String.valueOf(item.getQuantity()));
                subtotalText.setText(String.format(Locale.getDefault(), "₹%.2f", item.getSubtotal()));
                listener.onQuantityChanged();
            });

            removeButton.setOnClickListener(v -> listener.onRemoveItem(item));
        }
    }
}
