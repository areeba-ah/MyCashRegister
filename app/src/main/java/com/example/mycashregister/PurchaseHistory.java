package com.example.mycashregister;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class PurchaseHistory extends AppCompatActivity {

    TextView noText;
    ListView history;

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_purchase_history);

        // Set up the toolbar
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);

        history = findViewById(R.id.itemListView);

        //No Item in List
        noText = findViewById(R.id.noItemText);

        // Initialize and load items
        loadAndDisplayItems();

    }

    private void loadAndDisplayItems() {
        ServiceClass_History service = new ServiceClass_History(this); // Pass the context
        List<History> loadHistory = service.loadItems(); // Load items

        // Convert items to string list for the adapter
        List<String> itemsStringList = new ArrayList<>();

        if (loadHistory != null) {
            for (History item : loadHistory) {
                noText.setText("");
                itemsStringList.add(item.toString());
                Log.d("UpdatedItems", "Name: " + item.productName + ", Price: " + item.productPrice + ", Qty: " + item.quantityPurchased);
            }
        } else {
            noText.setText(R.string.no_purchase_history_available);
            Log.e("Update", "Items list is null");
            return;
        }

        // Set up ArrayAdapter
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                itemsStringList
        );
        history.setAdapter(adapter);

        adapter.notifyDataSetChanged(); // Notify the adapter of changes

        // Set up the long press listener to delete items
        history.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                History clickedItem = loadHistory.get(position); // Get clicked item

                // Create an AlertDialog for confirming the deletion
                new AlertDialog.Builder(PurchaseHistory.this)
                        .setTitle("Delete Item")
                        .setMessage("Are you sure you want to delete " + clickedItem.productName + "?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Remove the item from the list
                            loadHistory.remove(position);

                            // Use the saveItems method in ServiceClass to save the updated list to SharedPreferences
                            service.saveHistory(loadHistory); // Save updated list

                            // Notify the adapter about the change
                            adapter.notifyDataSetChanged();

                            // Show a toast message
                            Toast.makeText(PurchaseHistory.this, "History deleted: " + clickedItem.productName, Toast.LENGTH_SHORT).show();

                            // Log the removed item
                            Log.d("ManagerActivity", "Removed item: " + clickedItem.productName);

                            // Reload the list or refresh the view (optional, already done with notifyDataSetChanged)
                            loadAndDisplayItems(); // You can call the method to reload the items if needed
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            // Cancel the deletion
                            dialog.dismiss();
                        })
                        .create()
                        .show();

                // Return true to indicate the event was handled
                return true;
            }
        });
    }


    /**
     * Reload items when returning from Add/Update Activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ManagerActivity", "onActivityResult called. RequestCode: " + requestCode + ", ResultCode: " + resultCode);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            Log.d("ManagerActivity", "Reloading items...");
            loadAndDisplayItems(); // Reload the ListView
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAndDisplayItems(); // Reload the items when the activity is resumed
    }

    private boolean shouldShowIcon() {
        return false;  // Hide icon in SomeActivity
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.top_app_bar_menu, menu);

        // Get the menu item by its ID
        MenuItem actionButton = menu.findItem(R.id.action_button);

        // Hide or show the icon based on the activity
        if (shouldShowIcon()) {
            actionButton.setVisible(true);  // Show the icon
        } else {
            actionButton.setVisible(false);  // Hide the icon
        }

        return true;
    }
}