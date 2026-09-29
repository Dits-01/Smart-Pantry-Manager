package com.example.smartpantrymanager.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.smartpantrymanager.model.RecipeEntity;
import com.example.smartpantrymanager.model.ShelfItem;
import java.util.List;

public class ShelfRepository {
    private final ShelfDao shelfDao;
    private final RecipeDao recipeDao;
    private final LiveData<List<ShelfItem>> allItems;
    private final LiveData<List<RecipeEntity>> allRecipes;

    public ShelfRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        shelfDao = db.shelfDao();
        recipeDao = db.recipeDao();
        allItems = shelfDao.getAllItems();
        allRecipes = recipeDao.getAllRecipes();
    }

    public LiveData<List<ShelfItem>> getAllItems() {
        return allItems;
    }

    public LiveData<List<ShelfItem>> searchItems(String query) {
        return shelfDao.searchItems("%" + query + "%");
    }

    public void insert(ShelfItem item) {
        AppDatabase.databaseWriteExecutor.execute(() -> shelfDao.insert(item));
    }

    public void update(ShelfItem item) {
        AppDatabase.databaseWriteExecutor.execute(() -> shelfDao.update(item));
    }

    public void delete(ShelfItem item) {
        AppDatabase.databaseWriteExecutor.execute(() -> shelfDao.delete(item));
    }

    public LiveData<List<RecipeEntity>> getAllRecipes() {
        return allRecipes;
    }

    public void insertRecipe(RecipeEntity recipe) {
        AppDatabase.databaseWriteExecutor.execute(() -> recipeDao.insert(recipe));
    }

    public void updateRecipe(RecipeEntity recipe) {
        AppDatabase.databaseWriteExecutor.execute(() -> recipeDao.update(recipe));
    }

    public void deleteRecipe(RecipeEntity recipe) {
        AppDatabase.databaseWriteExecutor.execute(() -> recipeDao.delete(recipe));
    }
}
