package com.example.smartpantrymanager.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.smartpantrymanager.data.ShelfRepository;
import com.example.smartpantrymanager.model.RecipeEntity;
import com.example.smartpantrymanager.model.ShelfItem;
import java.util.List;

public class ShelfViewModel extends AndroidViewModel {
    private final ShelfRepository repository;
    private final LiveData<List<ShelfItem>> allItems;
    private final LiveData<List<RecipeEntity>> allRecipes;

    public ShelfViewModel(@NonNull Application application) {
        super(application);
        repository = new ShelfRepository(application);
        allItems = repository.getAllItems();
        allRecipes = repository.getAllRecipes();
    }

    public LiveData<List<ShelfItem>> getAllItems() {
        return allItems;
    }

    public LiveData<List<ShelfItem>> searchItems(String query) {
        return repository.searchItems(query);
    }

    public void insert(ShelfItem item) {
        repository.insert(item);
    }

    public void update(ShelfItem item) {
        repository.update(item);
    }

    public void delete(ShelfItem item) {
        repository.delete(item);
    }

    public LiveData<List<RecipeEntity>> getAllRecipes() {
        return allRecipes;
    }

    public void insertRecipe(RecipeEntity recipe) {
        repository.insertRecipe(recipe);
    }

    public void updateRecipe(RecipeEntity recipe) {
        repository.updateRecipe(recipe);
    }

    public void deleteRecipe(RecipeEntity recipe) {
        repository.deleteRecipe(recipe);
    }
}
