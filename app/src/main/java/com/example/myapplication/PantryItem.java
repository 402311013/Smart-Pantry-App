package com.example.myapplication;

// model class to hold ingredient details
public class PantryItem {
    private long id;
    private String name;
    private String quantity;
    private String unit;
    private String expiryDate;

    // constructor with db id
    public PantryItem(long id, String name, String quantity, String unit, String expiryDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    // constructor for new items before getting db id
    public PantryItem(String name, String quantity, String unit, String expiryDate) {
        this(-1, name, quantity, unit, expiryDate);
    }

    public PantryItem(String name, String quantity, String unit) {
        this(-1, name, quantity, unit, "");
    }

    // getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}
