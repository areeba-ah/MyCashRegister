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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ManagerActivity extends AppCompatActivity {

    ImageButton history;
    FloatingActionButton fab;

    TextView noText;

    ListView items;

    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manager);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        items = findViewById(R.id.listView);
        noText = findViewById(R.id.managerViewText);
        fab = findViewById(R.id.fab_add);
        history = findViewById(R.id.history);

        // Initialize and load items
        loadAndDisplayItems();

        // Set up History Button
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerActivity.this, PurchaseHistory.class);
                startActivity(intent);
            }
        });

        // Set up Floating Action Button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerActivity.this, Add_Update_Items.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Method to load and display items in the ListView.
     */
    private void loadAndDisplayItems() {
        ServiceClass service = new ServiceClass(this); // Pass the context
        List<Items> updatedItems = service.loadItems(); // Load items

        // Convert items to string list for the adapter
        List<String> itemsStringList = new ArrayList<>();

        if (updatedItems != null) {
            noText.setText(""); // Clear "No items" text
            for (Items item : updatedItems) {
                itemsStringList.add(item.toString());
                Log.d("UpdatedItems", "Name: " + item.productName + ", Price: " + item.productPrice + ", Qty: " + item.productQty);
            }
        } else {
            noText.setText(R.string.no_item_available); // Set "No items" text
            Log.e("Update", "Items list is null");
        }

        // Set up ArrayAdapter
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                itemsStringList
        );
        items.setAdapter(adapter);

        adapter.notifyDataSetChanged(); // Notify the adapter of changes

        // Set OnItemClickListener
        items.setOnItemClickListener((parent, view, position, id) -> {
            Items clickedItem = updatedItems.get(position); // Get clicked item
            Intent intent = new Intent(ManagerActivity.this, Add_Update_Items.class);
            intent.putExtra("itemName", clickedItem.productName);
            intent.putExtra("itemPrice", clickedItem.productPrice);
            intent.putExtra("itemQuantity", clickedItem.productQty);
            intent.putExtra("position", position);
            startActivity(intent);
        });

        // Set up the long press listener to delete items
        items.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Items clickedItem = updatedItems.get(position); // Get clicked item

                // Create an AlertDialog for confirming the deletion
                new AlertDialog.Builder(ManagerActivity.this)
                        .setTitle("Delete Item")
                        .setMessage("Are you sure you want to delete " + clickedItem.productName + "?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Remove the item from the list
                            updatedItems.remove(position);

                            // Use the saveItems method in ServiceClass to save the updated list to SharedPreferences
                            service.saveItems(updatedItems); // Save updated list

                            // Notify the adapter about the change
                            adapter.notifyDataSetChanged();

                            // Show a toast message
                            Toast.makeText(ManagerActivity.this, "Item deleted: " + clickedItem.productName, Toast.LENGTH_SHORT).show();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.top_app_bar_menu, menu);

        // Get the menu item by its ID
        MenuItem actionButton = menu.findItem(R.id.action_button);

        // Change the icon dynamically
        actionButton.setIcon(R.drawable.baseline_logout_24);

        // Handle the item click
        actionButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                // Perform the action you want when the icon is clicked
                Toast.makeText(ManagerActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ManagerActivity.this, MainActivity.class);
                startActivity(intent);

                // Return true to consume the click event
                return true;
            }
        });

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAndDisplayItems(); // Reload the items when the activity is resumed
    }

    // Determine if the icon should be shown or hidden (based on some condition)
    private boolean shouldShowIcon() {
        // Implement your logic to decide whether to show the icon
        return true; // For now, always show the icon
    }
}