package com.example.smartpantrymanager.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.smartpantrymanager.model.RecipeEntity;
import java.util.List;

@Dao
public interface RecipeDao {
    @Insert
    void insert(RecipeEntity recipe);

    @Update
    void update(RecipeEntity recipe);

    @Delete
    void delete(RecipeEntity recipe);

    @Query("SELECT * FROM recipes ORDER BY title ASC")
    LiveData<List<RecipeEntity>> getAllRecipes();
}
