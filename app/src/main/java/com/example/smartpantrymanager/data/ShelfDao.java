package com.example.smartpantrymanager.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.smartpantrymanager.model.ShelfItem;
import java.util.List;

@Dao
public interface ShelfDao {
    @Insert
    void insert(ShelfItem item);

    @Update
    void update(ShelfItem item);

    @Delete
    void delete(ShelfItem item);

    @Query("SELECT * FROM shelf_items ORDER BY name ASC")
    LiveData<List<ShelfItem>> getAllItems();

    @Query("SELECT * FROM shelf_items WHERE name LIKE :query OR category LIKE :query OR shelfLocation LIKE :query ORDER BY name ASC")
    LiveData<List<ShelfItem>> searchItems(String query);
}
