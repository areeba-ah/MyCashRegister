package com.example.mycashregister;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ManagerLogin extends AppCompatActivity {

    ImageButton back;
    Button cancel, login;

    TextInputEditText  usernameInput, passwordInput;
    TextInputLayout usernameLayout, passwordLayout;


    String username = "manager2025", password = "jan2025";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manager_login);

        // Set up the toolbar
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);

        //Back Button
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Cancel Button
        cancel = findViewById(R.id.cancelBtn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //Login Button
        login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Username and Password
                usernameInput = findViewById(R.id.userName);
                passwordInput = findViewById(R.id.password);

                usernameLayout = findViewById(R.id.outlinedUsernameInput);
                passwordLayout = findViewById(R.id.outlinedPasswordInput);

                ValidateLogin(usernameInput, passwordInput, usernameLayout, passwordLayout);
            }
        });

    }


    private void ValidateLogin(TextInputEditText usernameInput, TextInputEditText passwordInput,
                               TextInputLayout usernameLayout, TextInputLayout passwordLayout) {
        // Retrieve the input text
        String enteredUsername = usernameInput.getText() != null ? usernameInput.getText().toString().trim() : "";
        String enteredPassword = passwordInput.getText() != null ? passwordInput.getText().toString().trim() : "";

        // Check conditions and show appropriate messages
        if (!enteredUsername.equals(username) && enteredPassword.equals(password)) {
            usernameLayout.setError("Invalid username");
            usernameLayout.setEndIconMode(TextInputLayout.END_ICON_NONE);
        } else if (!enteredPassword.equals(password) && enteredUsername.equals(username)) {
            passwordLayout.setError("Invalid password");
            passwordLayout.setEndIconMode(TextInputLayout.END_ICON_NONE);
        } else if (!enteredUsername.equals(username) && !enteredPassword.equals(password)) {
            usernameLayout.setError("Invalid username");
            usernameLayout.setEndIconMode(TextInputLayout.END_ICON_NONE);
            passwordLayout.setError("Invalid password");
            passwordLayout.setEndIconMode(TextInputLayout.END_ICON_NONE);
        } else {
            // Clear errors and reset end icon modes
            usernameLayout.setError(null);
            usernameLayout.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
            passwordLayout.setError(null);
            passwordLayout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);

            // Login successful
            Intent intent = new Intent(ManagerLogin.this, ManagerActivity.class);
            startActivity(intent);
        }

        // Add listeners to clear errors upon typing
        usernameInput.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                usernameLayout.setError(null);
                usernameLayout.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
            }
        });

        passwordInput.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordLayout.setError(null);
                passwordLayout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            }
        });
    }

    public abstract class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {}
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