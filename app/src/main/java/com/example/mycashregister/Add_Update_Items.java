package com.example.mycashregister;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.io.Serializable;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

public class Add_Update_Items extends AppCompatActivity {
    TextInputEditText name, price;
    TextView quantity;

    private String currentNumber = "";

    boolean newCalculation = true;

    private int position = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_update_items);

        // Set up the toolbar
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);

        name = findViewById(R.id.productName);
        price = findViewById(R.id.productPrice);
        quantity = findViewById(R.id.availableProductQuantity);

        // Set up button click listeners
        setNumberButtonListeners();
        setOperatorButtonListeners();


        // Get the Intent that started this activity
        Intent intent = getIntent();

        // Check if data was passed through the Intent
        if (intent != null) {
            // Retrieve the values passed through Intent
            String productName = intent.getStringExtra("itemName");
            String productPrice = intent.getStringExtra("itemPrice");
            String productQty = intent.getStringExtra("itemQuantity");

            // Retrieve the position if passed for editing
            position = intent.getIntExtra("position", -1); // Default to -1 if not provided

            // If position is valid (i.e., editing an item), populate the fields with the existing item data
            if (position != -1) {
                name.setText(productName);
                price.setText(productPrice);
                quantity.setText(productQty);
            }
        }
    }

    private void setNumberButtonListeners() {
        int[] numberButtonIds = {
                R.id.zero, R.id.one, R.id.two, R.id.three,
                R.id.four, R.id.five, R.id.six, R.id.seven, R.id.eight, R.id.nine
        };

        View.OnClickListener listener = view -> {

            Button button = (Button) view;
            currentNumber += button.getText().toString();

            if(newCalculation){
                quantity.setText("");
            }
            newCalculation = false;

            if(quantity.length() <= 4){
                quantity.setText(currentNumber);
                Log.d("current number at add", String.valueOf(currentNumber));
                Log.d("Quantity at add", quantity.getText().toString());
                Log.d("Expression length at add", String.valueOf(quantity.length()));
            }

        };

        for (int id : numberButtonIds) {
            findViewById(id).setOnClickListener(listener);
        }
    }

    private void setOperatorButtonListeners() {
        int[] operatorButtonIds = {
                R.id.clear, R.id.delete, R.id.updateBtn, R.id.cancelBtn
        };

        for (int id : operatorButtonIds) {
            findViewById(id).setOnClickListener(this::onFunctionBtnClick);
        }
    }

    private void onFunctionBtnClick(View view) {
        Button button = (Button) view;
        String clickedOperator = button.getText().toString();

        switch (clickedOperator) {
            case "AC":
                currentNumber = "";
                quantity.setText("");
                newCalculation = true;
                break;

            case "Del":
                // Get and trim the text from the quantity field
                String input = quantity.getText().toString().trim();

                // Check if there is any text to delete
                if (!input.isEmpty()) {
                    // Check if there's only one character left
                    if (input.length() == 1) {
                        // Handle the case where only one character is left and will be deleted
                        input = "";  // Clear the input
                        currentNumber = ""; // Reset the current number
                        quantity.setText(input); // Update the EditText
                        newCalculation = true;  // Set to true, since the input will be empty now

                        Log.d("Quantity", "Cleared the last character");
                    } else {
                        // Handle the case for deleting the last character when more than one character is present
                        input = input.substring(0, input.length() - 1);  // Remove the last character
                        currentNumber = input; // Synchronize currentNumber with the updated input
                        quantity.setText(input);  // Update the EditText
                        newCalculation = false;  // Allow further editing

                        Log.d("Quantity", "Deleted one character");
                    }
                } else {
                    // Case where there's no text to delete
                    Log.d("DelButton", "No characters to delete.");
                    newCalculation = true;  // Set to true, as there's nothing to edit
                }
                break;



            case "Update":
                Update();
                break;


            default:
                finish();
                break;
        }
    }


    private void Update() {
        // Trimmed input values
        String nameInput = name.getText().toString().trim();
        String priceInput = price.getText().toString().trim();
        String quantityInput = quantity.getText().toString().trim();

        // Check if the values are not empty
        if (!nameInput.isEmpty() && !priceInput.isEmpty() && !quantityInput.isEmpty()) {
            // Create a new Items object with updated details
            Items updatedItem = new Items(nameInput, priceInput, quantityInput);

            // Send the updated item back to ManagerActivity (or wherever it's needed)
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updatedItem", updatedItem);
            resultIntent.putExtra("position", position);  // Pass the position for updating existing item

            // Use ServiceClass to save the updated item (or add if it's a new item)
            ServiceClass service = new ServiceClass(this); // Pass the context
            if (position == -1) {
                // If position is -1, we are adding a new item
                service.addNewItem(updatedItem);  // Add the new item
            } else {
                // If position is valid, we are updating an existing item
                service.updateItem(position, updatedItem);  // Update the existing item at the given position
            }

            // Set the result and finish the activity
            setResult(RESULT_OK, resultIntent);
            Toast.makeText(this, position == -1 ? "Product Added!" : "Product Updated!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(Add_Update_Items.this, "All fields are required!", Toast.LENGTH_SHORT).show();
        }
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