package com.example.smartpantrymanager.data;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.smartpantrymanager.model.RecipeEntity;
import com.example.smartpantrymanager.model.ShelfItem;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {ShelfItem.class, RecipeEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ShelfDao shelfDao();
    public abstract RecipeDao recipeDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "shelf_control_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final Callback sRoomDatabaseCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                ShelfDao dao = INSTANCE.shelfDao();
                dao.insert(new ShelfItem("Eggs", "Dairy", 12.0, "pcs", "A1", "Fresh farm eggs"));
                dao.insert(new ShelfItem("Butter", "Dairy", 250.0, "g", "A2", "Unsalted butter blocks"));
                dao.insert(new ShelfItem("Cheese", "Dairy", 500.0, "g", "A2", "Cheddar cheese block"));
                dao.insert(new ShelfItem("Milk", "Dairy", 1.5, "L", "A1", "Whole milk 1.5L"));
                dao.insert(new ShelfItem("Tomatoes", "Vegetables", 5.0, "pcs", "B1", "Ripe tomatoes"));
                dao.insert(new ShelfItem("Onions", "Vegetables", 3.0, "pcs", "B1", "Yellow onions"));
                dao.insert(new ShelfItem("Garlic", "Vegetables", 2.0, "pcs", "B1", "Garlic bulbs"));
                dao.insert(new ShelfItem("Salt", "Spices", 100.0, "g", "C1", "Sea salt"));
                dao.insert(new ShelfItem("Pasta", "Grains", 500.0, "g", "D1", "Spaghetti pasta"));
                dao.insert(new ShelfItem("Olive Oil", "Oils", 0.75, "L", "D2", "Extra virgin olive oil 750ml"));
                dao.insert(new ShelfItem("Broth", "Pantry", 500.0, "ml", "C2", "Chicken broth 500ml"));
                dao.insert(new ShelfItem("Chicken", "Meat", 1.0, "kg", "F1", "Chicken breasts"));
                dao.insert(new ShelfItem("Rice", "Grains", 2.0, "kg", "D2", "Basmati rice"));

                RecipeDao recipeDao = INSTANCE.recipeDao();
                recipeDao.insert(new RecipeEntity("Pantry Omelette", "10 mins", "2 eggs, 50 g butter, 100 g cheese, 5 g salt", "1. Whisk eggs in a bowl with salt.\n2. Melt butter in a skillet over medium heat.\n3. Pour in eggs and cook until set.\n4. Add cheese, fold, and serve warm."));
                recipeDao.insert(new RecipeEntity("Classic Garlic Tomato Pasta", "20 mins", "200 g pasta, 4 tomatoes, 2 cloves garlic, 30 ml olive oil, 5 g salt", "1. Boil pasta until al dente.\n2. Sauté minced garlic and diced tomatoes in olive oil.\n3. Toss pasta into the sauce and season with salt."));
                recipeDao.insert(new RecipeEntity("Cheesy Onion Soup", "25 mins", "2 onions, 500 ml broth, 100 g cheese, 30 g butter", "1. Caramelize sliced onions in butter.\n2. Add broth and simmer for 15 mins.\n3. Top with cheese and broil until melted."));
                recipeDao.insert(new RecipeEntity("Simple Scrambled Eggs", "5 mins", "3 eggs, 100 ml milk, 20 g butter, 5 g salt", "1. Beat eggs with milk and salt.\n2. Melt butter in pan, pour eggs, and stir gently until fluffy."));
                recipeDao.insert(new RecipeEntity("Garlic Butter Vegetables", "15 mins", "3 tomatoes, 2 onions, 2 cloves garlic, 40 g butter", "1. Sauté garlic and onions in melted butter.\n2. Add chopped tomatoes and simmer until tender."));
                recipeDao.insert(new RecipeEntity("Chicken & Rice Casserole", "45 mins", "500 g chicken, 200 g rice, 40 g butter, 5 g salt, 250 ml broth", "1. Layer chicken and rice in a baking dish.\n2. Pour broth and butter over top.\n3. Bake at 375°F for 40 minutes."));
                recipeDao.insert(new RecipeEntity("Tomato Basil Soup", "20 mins", "4 tomatoes, 2 cloves garlic, 200 ml milk, 30 g butter, 5 g salt", "1. Simmer tomatoes, garlic, and butter.\n2. Blend smooth, stir in milk, and season with salt."));
            });
        }
    };
}
