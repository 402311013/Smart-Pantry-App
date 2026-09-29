package com.example.myapplication;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// adapter to handle displaying items in recycler view with expiry highlighting
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    // click interface for edit and delete actions
    public interface OnItemClickListener {
        void onEdit(int position);
        void onDelete(int position);
    }

    private List<PantryItem> itemList;
    private final OnItemClickListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public PantryAdapter(List<PantryItem> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    public void updateList(List<PantryItem> newList) {
        this.itemList = newList;
        notifyDataSetChanged();
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
        String qtyText = item.getQuantity();
        if (!TextUtils.isEmpty(item.getUnit())) {
            qtyText += " " + item.getUnit();
        }
        holder.tvQuantityUnit.setText(qtyText);

        // evaluate expiry date status and apply visual badges/colors
        String expiry = item.getExpiryDate();
        if (!TextUtils.isEmpty(expiry)) {
            holder.tvExpiry.setVisibility(View.VISIBLE);
            int expiryStatus = checkExpiryStatus(expiry);
            if (expiryStatus < 0) {
                // expired: mark item prominently in red
                holder.tvExpiry.setText("Expired: " + expiry);
                holder.tvExpiry.setBackgroundResource(R.drawable.bg_badge_danger);
                holder.tvExpiry.setTextColor(Color.parseColor("#DC2626"));
                holder.tvName.setTextColor(Color.parseColor("#DC2626"));
                holder.cardView.setStrokeColor(Color.parseColor("#FCA5A5"));
                holder.cardView.setCardBackgroundColor(Color.parseColor("#FEF2F2"));
            } else if (expiryStatus <= 3) {
                // expiring within 3 days: show orange warning
                holder.tvExpiry.setText("Expiring: " + expiry);
                holder.tvExpiry.setBackgroundResource(R.drawable.bg_badge_warning);
                holder.tvExpiry.setTextColor(Color.parseColor("#92400E"));
                holder.tvName.setTextColor(Color.parseColor("#0F172A"));
                holder.cardView.setStrokeColor(Color.parseColor("#E2E8F0"));
                holder.cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            } else {
                // safe expiry
                holder.tvExpiry.setText("Exp: " + expiry);
                holder.tvExpiry.setBackgroundResource(R.drawable.bg_badge_success);
                holder.tvExpiry.setTextColor(Color.parseColor("#065F46"));
                holder.tvName.setTextColor(Color.parseColor("#0F172A"));
                holder.cardView.setStrokeColor(Color.parseColor("#E2E8F0"));
                holder.cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        } else {
            holder.tvExpiry.setVisibility(View.GONE);
            holder.tvName.setTextColor(Color.parseColor("#0F172A"));
            holder.cardView.setStrokeColor(Color.parseColor("#E2E8F0"));
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        // edit item
        holder.btnEdit.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && listener != null) {
                listener.onEdit(pos);
            }
        });

        // delete item
        holder.btnDelete.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && listener != null) {
                listener.onDelete(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // helper to determine days until expiry
    // returns negative if expired, 0..3 if expiring soon, > 3 if fine
    private int checkExpiryStatus(String expiryDateStr) {
        try {
            Date expiryDate = dateFormat.parse(expiryDateStr.trim());
            if (expiryDate == null) return 999;

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            Calendar expCal = Calendar.getInstance();
            expCal.setTime(expiryDate);
            expCal.set(Calendar.HOUR_OF_DAY, 0);
            expCal.set(Calendar.MINUTE, 0);
            expCal.set(Calendar.SECOND, 0);
            expCal.set(Calendar.MILLISECOND, 0);

            long diffMillis = expCal.getTimeInMillis() - today.getTimeInMillis();
            long diffDays = diffMillis / (1000 * 60 * 60 * 24);
            return (int) diffDays;
        } catch (Exception e) {
            return 999;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView tvName, tvQuantityUnit, tvExpiry;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            tvName = itemView.findViewById(R.id.tvItemName);
            tvQuantityUnit = itemView.findViewById(R.id.tvItemQuantityUnit);
            tvExpiry = itemView.findViewById(R.id.tvItemExpiry);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
