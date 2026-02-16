package com.tandoornightcafe.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tandoornightcafe.app.R;
import com.tandoornightcafe.app.model.MenuItem;

import java.util.List;
import java.util.Locale;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    private List<MenuItem> items;
    private OnMenuItemClickListener listener;

    public interface OnMenuItemClickListener {
        void onEditClick(MenuItem item);
        void onDeleteClick(MenuItem item);
    }

    public MenuAdapter(List<MenuItem> items, OnMenuItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuItem item = items.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateItems(List<MenuItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView descriptionText;
        TextView categoryText;
        TextView priceText;
        ImageButton editButton;
        ImageButton deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.text_name);
            descriptionText = itemView.findViewById(R.id.text_description);
            categoryText = itemView.findViewById(R.id.text_category);
            priceText = itemView.findViewById(R.id.text_price);
            editButton = itemView.findViewById(R.id.button_edit);
            deleteButton = itemView.findViewById(R.id.button_delete);
        }

        void bind(MenuItem item, OnMenuItemClickListener listener) {
            nameText.setText(item.getName());
            descriptionText.setText(item.getDescription());
            categoryText.setText(item.getCategory());
            priceText.setText(String.format(Locale.getDefault(), "₹%.2f", item.getPrice()));

            editButton.setOnClickListener(v -> listener.onEditClick(item));
            deleteButton.setOnClickListener(v -> listener.onDeleteClick(item));
        }
    }
}
