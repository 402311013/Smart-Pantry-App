package com.example.myapplication;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PantryActivity extends AppCompatActivity {

    private List<PantryItem> pantryList;
    private PantryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set layout for pantry screen
        setContentView(R.layout.activity_pantry);

        // load up initial ingredients list
        initSampleData();

        // set up recycler view
        RecyclerView rvPantryList = findViewById(R.id.rvPantryList);
        rvPantryList.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PantryAdapter(pantryList, new PantryAdapter.OnItemClickListener() {
            @Override
            public void onEdit(int position) {
                showEditDialog(position);
            }

            @Override
            public void onDelete(int position) {
                pantryList.remove(position);
                adapter.notifyItemRemoved(position);
                Toast.makeText(PantryActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });
        rvPantryList.setAdapter(adapter);

        // add item button
        Button btnAddItem = findViewById(R.id.btnAddItem);
        btnAddItem.setOnClickListener(v -> showAddDialog());

        // back button to return home
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    // initial ingredients list
    private void initSampleData() {
        pantryList = new ArrayList<>();
        pantryList.add(new PantryItem("Basmati Rice", "1", "kg", "2025-12-31"));
        pantryList.add(new PantryItem("Turmeric Powder", "1", "pack", "2026-06-30"));
        pantryList.add(new PantryItem("Chilli Powder", "1", "pack", "2026-06-30"));
        pantryList.add(new PantryItem("Ghee", "1", "jar", "2025-09-15"));
        pantryList.add(new PantryItem("Curry Powder", "1", "pack", "2026-01-20"));
    }

    // dialog to add new item
    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Ingredient");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        final EditText etName = new EditText(this);
        etName.setHint("Ingredient Name");
        layout.addView(etName);

        final EditText etQuantity = new EditText(this);
        etQuantity.setHint("Quantity (e.g. 1)");
        layout.addView(etQuantity);

        final EditText etUnit = new EditText(this);
        etUnit.setHint("Unit (e.g. kg, pack, jar)");
        layout.addView(etUnit);

        final EditText etExpiry = new EditText(this);
        etExpiry.setHint("Expiry Date (Optional - tap to pick)");
        etExpiry.setFocusable(false);
        etExpiry.setOnClickListener(v -> showDatePicker(etExpiry));
        layout.addView(etExpiry);

        builder.setView(layout);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String rawQty = etQuantity.getText().toString().trim();
            String qty = TextUtils.isEmpty(rawQty) ? "1" : rawQty;
            String unit = etUnit.getText().toString().trim();
            String expiry = etExpiry.getText().toString().trim();

            if (!TextUtils.isEmpty(name)) {
                pantryList.add(new PantryItem(name, qty, unit, expiry));
                adapter.notifyItemInserted(pantryList.size() - 1);
                Toast.makeText(this, "Added " + name, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please enter ingredient name", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // dialog to edit item
    private void showEditDialog(int position) {
        PantryItem currentItem = pantryList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Ingredient");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        final EditText etName = new EditText(this);
        etName.setText(currentItem.getName());
        etName.setHint("Ingredient Name");
        layout.addView(etName);

        final EditText etQuantity = new EditText(this);
        etQuantity.setText(currentItem.getQuantity());
        etQuantity.setHint("Quantity");
        layout.addView(etQuantity);

        final EditText etUnit = new EditText(this);
        etUnit.setText(currentItem.getUnit());
        etUnit.setHint("Unit");
        layout.addView(etUnit);

        final EditText etExpiry = new EditText(this);
        etExpiry.setText(currentItem.getExpiryDate());
        etExpiry.setHint("Expiry Date (Optional - tap to pick)");
        etExpiry.setFocusable(false);
        etExpiry.setOnClickListener(v -> showDatePicker(etExpiry));
        layout.addView(etExpiry);

        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String qty = etQuantity.getText().toString().trim();
            String unit = etUnit.getText().toString().trim();
            String expiry = etExpiry.getText().toString().trim();

            if (!TextUtils.isEmpty(name)) {
                currentItem.setName(name);
                currentItem.setQuantity(qty);
                currentItem.setUnit(unit);
                currentItem.setExpiryDate(expiry);
                adapter.notifyItemChanged(position);
                Toast.makeText(this, "Updated " + name, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // helper to show date picker dialog
    private void showDatePicker(EditText targetEditText) {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    targetEditText.setText(selectedDate);
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }
}
