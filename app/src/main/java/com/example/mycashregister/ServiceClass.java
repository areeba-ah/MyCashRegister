package com.example.mycashregister;

import android.content.SharedPreferences;

import java.lang.reflect.Type;
import java.util.ArrayList;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
public class ServiceClass {
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private List<Items> items;
    private static final String ITEMS_KEY = "items_list";

    public ServiceClass(Context context) {
        this.sharedPreferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        this.gson = new Gson();
        this.items = loadItems();

        // Initialize with empty list if null
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
    }

    public void addNewItem(Items newItem) {
        items.add(newItem); // Add to list

        // Save the updated list to SharedPreferences
        saveItems(items);
    }

    public List<Items> loadItems() {
        String json = sharedPreferences.getString(ITEMS_KEY, "[]"); // Default to empty list
        Type type = new TypeToken<List<Items>>() {}.getType();

        List<Items> items = gson.fromJson(json, type);
        return items != null ? items : new ArrayList<>();
    }

    // Method for updating an existing item
    // Update an existing item at a specific position
    public void updateItem(int position, Items updatedItem) {
        List<Items> items = loadItems(); // Load existing items

        // Update the item at the specified position
        if (position >= 0 && position < items.size()) {
            items.set(position, updatedItem);
        }

        // Save the updated list back to SharedPreferences
        saveItems(items);
    }

    // Helper method to save the list of items back to SharedPreferences
    void saveItems(List<Items> items) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(items); // Serialize the list to JSON
        editor.putString(ITEMS_KEY, json); // Save JSON string to SharedPreferences
        editor.apply(); // Commit changes
    }

}
