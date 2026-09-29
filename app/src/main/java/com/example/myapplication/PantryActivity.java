package com.example.myapplication;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PantryActivity extends AppCompatActivity {

    private List<PantryItem> pantryList = new ArrayList<>();
    private List<PantryItem> displayedList = new ArrayList<>();
    private PantryAdapter adapter;
    private DatabaseHelper dbHelper;
    private TextView txtItemCount;
    private LinearLayout tvEmptyPantry;
    private TextView tvEmptyPantryTitle, tvEmptyPantrySubtitle;
    private EditText etSearchPantry;
    private RecyclerView rvPantryList;

    private static final String[] STANDARD_UNITS = new String[]{
            "pcs", "kg", "g", "litre", "ml", "pack", "can", "jar", "bottle", "loaf", "bulb", "pc", "block", "tbsp", "tsp"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set layout for pantry screen
        setContentView(R.layout.activity_pantry);

        // setup bottom navigation bar
        NavigationHelper.setupNavBar(this, R.id.navPantry);

        // init database and views
        dbHelper = new DatabaseHelper(this);

        txtItemCount = findViewById(R.id.txtItemCount);
        tvEmptyPantry = findViewById(R.id.tvEmptyPantry);
        tvEmptyPantryTitle = findViewById(R.id.tvEmptyPantryTitle);
        tvEmptyPantrySubtitle = findViewById(R.id.tvEmptyPantrySubtitle);
        etSearchPantry = findViewById(R.id.etSearchPantry);
        rvPantryList = findViewById(R.id.rvPantryList);

        rvPantryList.setLayoutManager(new LinearLayoutManager(this));

        // handle edit and delete actions
        adapter = new PantryAdapter(displayedList, new PantryAdapter.OnItemClickListener() {
            @Override
            public void onEdit(int position) {
                if (position >= 0 && position < displayedList.size()) {
                    PantryItem selected = displayedList.get(position);
                    showAddOrEditDialog(selected);
                }
            }

            @Override
            public void onDelete(int position) {
                if (position >= 0 && position < displayedList.size()) {
                    PantryItem itemToDelete = displayedList.get(position);
                    confirmDeleteItem(itemToDelete, position);
                }
            }
        });
        rvPantryList.setAdapter(adapter);

        // setup real-time search filter
        if (etSearchPantry != null) {
            etSearchPantry.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterPantry(s != null ? s.toString() : "");
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        // add ingredient button
        Button btnAddItem = findViewById(R.id.btnAddIngredient);
        if (btnAddItem != null) {
            btnAddItem.setOnClickListener(v -> showAddOrEditDialog(null));
        }

        // sort button
        Button btnSort = findViewById(R.id.btnSort);
        if (btnSort != null) {
            btnSort.setOnClickListener(v -> showSortDialog());
        }

        loadPantryData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantryData();
    }

    // reload items from SQLite database and update list view
    private void loadPantryData() {
        if (dbHelper != null) {
            pantryList.clear();
            pantryList.addAll(dbHelper.getAllPantryItems());
            String currentSearch = etSearchPantry != null ? etSearchPantry.getText().toString() : "";
            filterPantry(currentSearch);
        }
    }

    private void filterPantry(String query) {
        displayedList.clear();
        String q = query != null ? query.trim().toLowerCase() : "";
        if (q.isEmpty()) {
            displayedList.addAll(pantryList);
        } else {
            for (PantryItem item : pantryList) {
                if ((item.getName() != null && item.getName().toLowerCase().contains(q)) ||
                        (item.getUnit() != null && item.getUnit().toLowerCase().contains(q)) ||
                        (item.getExpiryDate() != null && item.getExpiryDate().toLowerCase().contains(q))) {
                    displayedList.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
        updateUIState(q);
    }

    // update item count display and empty state
    private void updateUIState(String currentQuery) {
        if (txtItemCount != null && pantryList != null) {
            txtItemCount.setText(String.valueOf(pantryList.size()));
        }

        if (tvEmptyPantry != null && rvPantryList != null) {
            if (displayedList.isEmpty()) {
                rvPantryList.setVisibility(View.GONE);
                tvEmptyPantry.setVisibility(View.VISIBLE);

                if (pantryList.isEmpty()) {
                    if (tvEmptyPantryTitle != null) tvEmptyPantryTitle.setText("Your pantry is empty");
                    if (tvEmptyPantrySubtitle != null) tvEmptyPantrySubtitle.setText("Tap + Add to start stocking your pantry");
                } else {
                    if (tvEmptyPantryTitle != null) tvEmptyPantryTitle.setText("No ingredients found");
                    if (tvEmptyPantrySubtitle != null) {
                        tvEmptyPantrySubtitle.setText("No results matching \"" + currentQuery + "\"");
                    }
                }
            } else {
                rvPantryList.setVisibility(View.VISIBLE);
                tvEmptyPantry.setVisibility(View.GONE);
            }
        }
    }

    // show sort options dialog
    private void showSortDialog() {
        String[] options = {"Name (A-Z)", "Expiry Date (Soonest first)", "Quantity (Highest first)"};
        new MaterialAlertDialogBuilder(this)
                .setTitle("Sort Pantry By")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Collections.sort(pantryList, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
                        Toast.makeText(this, "Sorted by Name", Toast.LENGTH_SHORT).show();
                    } else if (which == 1) {
                        Collections.sort(pantryList, (a, b) -> {
                            String d1 = TextUtils.isEmpty(a.getExpiryDate()) ? "9999-99-99" : a.getExpiryDate();
                            String d2 = TextUtils.isEmpty(b.getExpiryDate()) ? "9999-99-99" : b.getExpiryDate();
                            return d1.compareTo(d2);
                        });
                        Toast.makeText(this, "Sorted by Expiry Date", Toast.LENGTH_SHORT).show();
                    } else if (which == 2) {
                        Collections.sort(pantryList, (a, b) -> {
                            double q1 = parseQty(a.getQuantity());
                            double q2 = parseQty(b.getQuantity());
                            return Double.compare(q2, q1); // highest first
                        });
                        Toast.makeText(this, "Sorted by Quantity", Toast.LENGTH_SHORT).show();
                    }

                    // reapply active search filter
                    String currentSearch = etSearchPantry != null ? etSearchPantry.getText().toString() : "";
                    filterPantry(currentSearch);
                })
                .show();
    }

    // helper to parse quantity
    private double parseQty(String qty) {
        try {
            return Double.parseDouble(qty != null ? qty.trim() : "0");
        } catch (Exception e) {
            return 0.0;
        }
    }

    // confirmation dialog before deleting an ingredient
    private void confirmDeleteItem(PantryItem item, int position) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Ingredient")
                .setMessage("Are you sure you want to remove \"" + item.getName() + "\" from your pantry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deletePantryItem(item.getId());
                    pantryList.remove(item);
                    displayedList.remove(position);
                    adapter.notifyDataSetChanged();
                    String currentSearch = etSearchPantry != null ? etSearchPantry.getText().toString() : "";
                    updateUIState(currentSearch);
                    Toast.makeText(PantryActivity.this, "Deleted " + item.getName(), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // unified modern dialog for adding or editing an ingredient
    private void showAddOrEditDialog(PantryItem currentItem) {
        boolean isEdit = (currentItem != null);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_pantry, null);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextView tvSubtitle = dialogView.findViewById(R.id.tvDialogSubtitle);
        ImageView ivIcon = dialogView.findViewById(R.id.ivDialogIcon);
        TextInputLayout tilName = dialogView.findViewById(R.id.tilIngredientName);
        TextInputEditText etName = dialogView.findViewById(R.id.etIngredientName);
        TextInputLayout tilQty = dialogView.findViewById(R.id.tilIngredientQty);
        TextInputEditText etQty = dialogView.findViewById(R.id.etIngredientQty);
        Spinner spUnit = dialogView.findViewById(R.id.spIngredientUnit);
        TextInputLayout tilExpiry = dialogView.findViewById(R.id.tilIngredientExpiry);
        TextInputEditText etExpiry = dialogView.findViewById(R.id.etIngredientExpiry);
        TextView tvClearExpiry = dialogView.findViewById(R.id.tvClearExpiry);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancelDialog);
        MaterialButton btnSave = dialogView.findViewById(R.id.btnSaveDialog);

        // setup spinner
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, STANDARD_UNITS);
        spUnit.setAdapter(unitAdapter);

        if (isEdit) {
            tvTitle.setText("Edit Ingredient");
            tvSubtitle.setText("Update quantities, units or expiration");
            ivIcon.setImageResource(R.drawable.ic_edit_outline);
            btnSave.setText("Save Changes");

            etName.setText(currentItem.getName());
            etQty.setText(currentItem.getQuantity());

            String currentUnit = currentItem.getUnit();
            for (int i = 0; i < STANDARD_UNITS.length; i++) {
                if (STANDARD_UNITS[i].equalsIgnoreCase(currentUnit)) {
                    spUnit.setSelection(i);
                    break;
                }
            }

            if (!TextUtils.isEmpty(currentItem.getExpiryDate())) {
                etExpiry.setText(currentItem.getExpiryDate());
                tvClearExpiry.setVisibility(View.VISIBLE);
            }
        } else {
            tvTitle.setText("Add Ingredient");
            tvSubtitle.setText("Track a new item in your pantry");
            ivIcon.setImageResource(R.drawable.ic_add);
            btnSave.setText("Add to Pantry");
            etQty.setText("1");
        }

        // date picker on expiry field tap or icon click
        View.OnClickListener dateClick = v -> showDatePicker(etExpiry, tvClearExpiry);
        etExpiry.setOnClickListener(dateClick);
        tilExpiry.setEndIconOnClickListener(dateClick);

        tvClearExpiry.setOnClickListener(v -> {
            etExpiry.setText("");
            tvClearExpiry.setVisibility(View.GONE);
        });

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String name = etName.getText() != null ? etName.getText().toString().trim() : "";
            String rawQty = etQty.getText() != null ? etQty.getText().toString().trim() : "";
            String unit = spUnit.getSelectedItem() != null ? spUnit.getSelectedItem().toString() : "pcs";
            String expiry = etExpiry.getText() != null ? etExpiry.getText().toString().trim() : "";

            // validate name
            if (TextUtils.isEmpty(name)) {
                tilName.setError("Please enter an ingredient name");
                etName.requestFocus();
                return;
            } else {
                tilName.setError(null);
            }

            // validate quantity
            double parsedQty;
            try {
                parsedQty = Double.parseDouble(rawQty);
                if (parsedQty <= 0) {
                    tilQty.setError("Quantity must be greater than zero");
                    etQty.requestFocus();
                    return;
                }
                tilQty.setError(null);
            } catch (Exception e) {
                tilQty.setError("Please enter a valid number");
                etQty.requestFocus();
                return;
            }

            String formattedQty = dbHelper.formatQuantity(parsedQty);

            if (isEdit) {
                currentItem.setName(name);
                currentItem.setQuantity(formattedQty);
                currentItem.setUnit(unit);
                currentItem.setExpiryDate(expiry);
                dbHelper.updatePantryItem(currentItem);
                Toast.makeText(this, "Updated " + name, Toast.LENGTH_SHORT).show();
            } else {
                PantryItem newItem = new PantryItem(name, formattedQty, unit, expiry);
                long id = dbHelper.addPantryItem(newItem);
                newItem.setId(id);
                pantryList.add(newItem);
                Toast.makeText(this, "Added " + name + " (" + formattedQty + " " + unit + ")", Toast.LENGTH_SHORT).show();
            }

            dialog.dismiss();

            String currentSearch = etSearchPantry != null ? etSearchPantry.getText().toString() : "";
            filterPantry(currentSearch);
        });

        dialog.show();
    }

    // show date picker dialog pre-selecting current or existing expiry date
    private void showDatePicker(EditText targetEditText, TextView clearButton) {
        Calendar cal = Calendar.getInstance();
        String currentText = targetEditText.getText() != null ? targetEditText.getText().toString().trim() : "";

        if (!TextUtils.isEmpty(currentText)) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date parsedDate = sdf.parse(currentText);
                if (parsedDate != null) {
                    cal.setTime(parsedDate);
                }
            } catch (Exception ignored) {}
        }

        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    targetEditText.setText(selectedDate);
                    if (clearButton != null) {
                        clearButton.setVisibility(View.VISIBLE);
                    }
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }
}
