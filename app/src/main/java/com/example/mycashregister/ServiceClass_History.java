package com.example.mycashregister;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ServiceClass_History {
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private List<History> historyList;
    private static final String HISTORY = "history_list";

    public ServiceClass_History(Context context) {
        this.sharedPreferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        this.gson = new Gson();
        this.historyList = loadItems();

        // Initialize with empty list if null
        if (this.historyList == null) {
            this.historyList = new ArrayList<>();
        }
    }

    public void addHistoryItem(History newItem) {
        historyList.add(newItem); // Add to list

        saveHistory(historyList);
    }


    // Method for updating an existing item
    // Update an existing item at a specific position
    public void updateHistory(int position, History updatedHistory) {
        List<History> history = loadItems(); // Load existing items

        // Update the item at the specified position
        if (position >= 0 && position < history.size()) {
            history.set(position, updatedHistory);
        }

        // Save the updated list back to SharedPreferences
        saveHistory(history);
    }

    public List<History> loadItems() {
        String json = sharedPreferences.getString(HISTORY, "[]"); // Default to empty list
        Type type = new TypeToken<List<History>>() {}.getType();

        // Deserialize JSON into a list of History objects
        List<History> items = gson.fromJson(json, type);

        // Return the list or an empty list if null
        return items != null ? items : new ArrayList<>();
    }

    void saveHistory(List<History> history) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(history); // Serialize the list to JSON
        editor.putString(HISTORY, json); // Save JSON string to SharedPreferences
        editor.apply(); // Commit changes
    }

}
