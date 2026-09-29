package com.example.smartpantrymanager.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "shelf_items")
public class ShelfItem {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private String category;
    private double quantity;
    private String unit;
    private String shelfLocation;
    private String notes;

    public ShelfItem(String name, String category, double quantity, String unit, String shelfLocation, String notes) {
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.unit = unit != null ? unit : "";
        this.shelfLocation = shelfLocation;
        this.notes = notes;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getShelfLocation() { return shelfLocation; }
    public void setShelfLocation(String shelfLocation) { this.shelfLocation = shelfLocation; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
