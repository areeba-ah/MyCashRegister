package com.example.mycashregister;

import android.annotation.SuppressLint;
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

import java.util.Date;

public class CashRegister extends AppCompatActivity {

    private int position = -1;
    int productQtyCheck;
    Double calcTotal = 0.00;

    private String currentNumber = "";

    String productName, productPrice, productQty;

    boolean newCalculation = true;

    TextView name, price, qty, quantityToBuy, total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cash_register);

        // Set up the toolbar here if you haven't done so already
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);

        name = findViewById(R.id.productName);
        price = findViewById(R.id.productPrice);
        qty = findViewById(R.id.availableProductQuantity);
        quantityToBuy = findViewById(R.id.productQuantity);
        total = findViewById(R.id.totalPrice);


        // Set up button click listeners
        setNumberButtonListeners();
        setOperatorButtonListeners();

        // Get the Intent that started this activity
        Intent intent = getIntent();

        // Check if data was passed through the Intent
        if (intent != null) {
            // Retrieve the values passed through Intent
            productName = intent.getStringExtra("itemName");
            productPrice = intent.getStringExtra("itemPrice");
            productQty =  intent.getStringExtra("itemQuantity");

            productQtyCheck = Integer.parseInt(productQty);

            // Retrieve the position if passed for editing
            position = intent.getIntExtra("position", -1); // Default to -1 if not provided

            // If position is valid (i.e., editing an item), populate the fields with the existing item data
            if (position != -1) {
                name.setText(productName);
                price.setText(productPrice);
                qty.setText(productQty);
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
                quantityToBuy.setText("");
            }
            newCalculation = false;

            if(quantityToBuy.length() <= 4){
                quantityToBuy.setText(currentNumber);

                calculateTotal();
            }

        };

        for (int id : numberButtonIds) {
            findViewById(id).setOnClickListener(listener);
        }
    }

    @SuppressLint("SetTextI18n")
    private void calculateTotal(){

        String quantityInput = quantityToBuy.getText().toString().trim();

        if(currentNumber != ""){
            Double unit_price = Double.parseDouble(productPrice);
            int qty = Integer.parseInt(quantityInput);

            calcTotal = unit_price*qty;
            total.setText("$"+ calcTotal);
        }else{
            total.setText("$0");
        }
    }

    private void setOperatorButtonListeners() {
        int[] operatorButtonIds = {
                R.id.clear, R.id.delete, R.id.buyBtn, R.id.cancelBtn
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
                quantityToBuy.setText("");
                newCalculation = true;
                calculateTotal();
                break;

            case "Del":
                // Get and trim the text from the quantity field
                String input = quantityToBuy.getText().toString().trim();

                // Check if there is any text to delete
                if (!input.isEmpty()) {
                    // Check if there's only one character left
                    if (input.length() == 1) {
                        // Handle the case where only one character is left and will be deleted
                        input = "";  // Clear the input
                        currentNumber = ""; // Reset the current number
                        quantityToBuy.setText(input); // Update the EditText
                        newCalculation = true;  // Set to true, since the input will be empty now
                        calculateTotal();
                        Log.d("Quantity", "Cleared the last character");
                    } else {
                        // Handle the case for deleting the last character when more than one character is present
                        input = input.substring(0, input.length() - 1);  // Remove the last character
                        currentNumber = input; // Synchronize currentNumber with the updated input
                        quantityToBuy.setText(input);  // Update the EditText
                        newCalculation = false;  // Allow further editing
                        calculateTotal();
                        Log.d("Quantity", "Deleted one character");
                    }
                } else {
                    // Case where there's no text to delete
                    Log.d("DelButton", "No characters to delete.");
                    newCalculation = true;  // Set to true, as there's nothing to edit
                }
                break;

            case "Buy":
                Buy();
                break;

            default:
                finish();
                break;
        }
    }


    private void Buy() {
        // Trimmed input values
        String nameInput = name.getText().toString().trim();
        String priceInput = price.getText().toString().trim();
        String quantityInput = quantityToBuy.getText().toString().trim();

        int qtyInput = Integer.parseInt(quantityInput);
        int purchase = productQtyCheck - qtyInput; //Remaining quantity

        String quantityRemainingString = String.valueOf(purchase); // Convert to String

        // Check if the values are not empty
        if (!quantityInput.isEmpty()) {
            // Create a new Items object with updated details

            if(qtyInput <= productQtyCheck && qtyInput != 0){
                makePurchase(nameInput, priceInput, quantityInput, quantityRemainingString);
            }
            else{
                Toast.makeText(CashRegister.this, "Not Enough Quantity Available!", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(CashRegister.this, "All fields are required!", Toast.LENGTH_SHORT).show();
        }
    }

    private void makePurchase(String nameInput, String priceInput, String quantityInput, String quantityRemaining) {

        Items updatedItem = new Items(nameInput, priceInput, quantityRemaining);

        // Send the updated item back to ManagerActivity (or wherever it's needed)
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedItem", updatedItem);
        resultIntent.putExtra("position", position);  // Pass the position for updating existing item

        // Use ServiceClass to save the updated item (or add if it's a new item)
        ServiceClass service = new ServiceClass(this); // Pass the context

        // If position is valid, we are updating an existing item
        service.updateItem(position, updatedItem);  // Update the existing item at the given position


        addHistory(nameInput, priceInput, quantityInput);


        // Set the result and finish the activity
        setResult(RESULT_OK, resultIntent);
        Toast.makeText(this, position == -1 ? "Product Added!" : "Product Updated!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void addHistory(String productName, String productPrice, String productQty) {
        // Use ServiceClass to save the updated item (or add if it's a new item)
        ServiceClass_History serviceClassHistory = new ServiceClass_History(this); // Pass the context

        Date date = new Date(); // Get current date and time
        String dateString = date.toString(); // Convert to string representation

        // Remove non-numeric characters except for the decimal point
        String replaceDollar = productPrice.replaceAll("\\$", "");

        double total =  Double.parseDouble(replaceDollar) * Integer.parseInt(productQty);

        History historyItem = new History(productName, replaceDollar, productQty, dateString, total);
        serviceClassHistory.addHistoryItem(historyItem);
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