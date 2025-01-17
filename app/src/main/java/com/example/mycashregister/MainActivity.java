package com.example.mycashregister;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView noText;
    ListView items;

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        items = findViewById(R.id.itemListView);
        noText = findViewById(R.id.noItemText);

        // Initialize and load items
        loadAndDisplayItems();

        // Initialize the MaterialToolbar
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        // Handle menu item clicks
        topAppBar.setOnMenuItemClickListener(new MaterialToolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_button) {

                    Intent intent = new Intent(MainActivity.this, ManagerLogin.class);
                    startActivity(intent);

                    return true;
                }
                return false;
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAndDisplayItems(); // Reload the items when the activity is resumed
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ManagerActivity", "onActivityResult called. RequestCode: " + requestCode + ", ResultCode: " + resultCode);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            Log.d("ManagerActivity", "Reloading items...");
            loadAndDisplayItems(); // Reload the ListView
        }
    }

    private void loadAndDisplayItems() {
        ServiceClass service = new ServiceClass(this); // Pass the context
        List<Items> updatedItems = service.loadItems(); // Load items

        // Convert items to string list for the adapter
        List<String> itemsStringList = new ArrayList<>();

        if (updatedItems != null && !updatedItems.isEmpty()) {
            noText.setVisibility(View.GONE); // Hide "No items" text
            for (Items item : updatedItems) {
                itemsStringList.add(item.toStringUser());
                Log.d("UpdatedItems", "Name: " + item.productName + ", Price: " + item.productPrice + ", Qty: " + item.productQty);
            }
        } else {
            noText.setVisibility(View.VISIBLE); // Show "No items" text
            noText.setText(R.string.no_item_available);
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
            Intent intent = new Intent(MainActivity.this, CashRegister.class);
            intent.putExtra("itemName", clickedItem.productName);
            intent.putExtra("itemPrice", clickedItem.productPrice);
            intent.putExtra("itemQuantity", clickedItem.productQty);
            intent.putExtra("position", position);
            startActivity(intent);
        });
    }

}