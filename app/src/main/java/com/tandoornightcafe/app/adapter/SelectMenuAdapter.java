package com.tandoornightcafe.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tandoornightcafe.app.R;
import com.tandoornightcafe.app.model.MenuItem;

import java.util.List;
import java.util.Locale;

public class SelectMenuAdapter extends RecyclerView.Adapter<SelectMenuAdapter.ViewHolder> {
    private List<MenuItem> items;
    private OnMenuItemSelectListener listener;

    public interface OnMenuItemSelectListener {
        void onItemSelected(MenuItem item);
    }

    public SelectMenuAdapter(List<MenuItem> items, OnMenuItemSelectListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_select_menu, parent, false);
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
        TextView priceText;
        Button addButton;

        ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.text_name);
            descriptionText = itemView.findViewById(R.id.text_description);
            priceText = itemView.findViewById(R.id.text_price);
            addButton = itemView.findViewById(R.id.button_add);
        }

        void bind(MenuItem item, OnMenuItemSelectListener listener) {
            nameText.setText(item.getName());
            descriptionText.setText(item.getDescription());
            priceText.setText(String.format(Locale.getDefault(), "₹%.2f", item.getPrice()));
            addButton.setOnClickListener(v -> listener.onItemSelected(item));
        }
    }
}
