package com.example.myapplication;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// adapter to handle displaying items in recycler view
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    // click interface for edit and delete actions
    public interface OnItemClickListener {
        void onEdit(int position);
        void onDelete(int position);
    }

    private final List<PantryItem> itemList;
    private final OnItemClickListener listener;

    public PantryAdapter(List<PantryItem> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PantryItem item = itemList.get(position);
        holder.tvName.setText(item.getName());

        // show quantity and unit
        String qtyText = "Quantity: " + item.getQuantity();
        if (!TextUtils.isEmpty(item.getUnit())) {
            qtyText += " " + item.getUnit();
        }
        holder.tvQuantityUnit.setText(qtyText);

        // show expiry date if set
        if (!TextUtils.isEmpty(item.getExpiryDate())) {
            holder.tvExpiry.setText("Expires: " + item.getExpiryDate());
            holder.tvExpiry.setVisibility(View.VISIBLE);
        } else {
            holder.tvExpiry.setVisibility(View.GONE);
        }

        // edit item
        holder.btnEdit.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                listener.onEdit(pos);
            }
        });

        // delete item
        holder.btnDelete.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                listener.onDelete(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQuantityUnit, tvExpiry;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvQuantityUnit = itemView.findViewById(R.id.tvItemQuantityUnit);
            tvExpiry = itemView.findViewById(R.id.tvItemExpiry);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
