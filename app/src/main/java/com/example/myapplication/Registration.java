package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class Registration extends AppCompatActivity {

    private EditText usernameField, passwordField, fullNameField;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize views
        usernameField = findViewById(R.id.username);
        passwordField = findViewById(R.id.password);
        fullNameField = findViewById(R.id.fullname);
        registerButton = findViewById(R.id.registerButton);

        // Set register button click listener
        registerButton.setOnClickListener(v -> {
            String username = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String fullName = fullNameField.getText().toString().trim();

            // Validate form fields
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(fullName)) {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            } else {
                // Register user in database
                registerUser(username, password, fullName);
            }
        });
    }

    private void registerUser(String username, String password, String fullName) {
        new Thread(() -> {
            try {
                // Database connection
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(
                        "jdbc:mysql://192.168.1.1:3306/musicapp", // Replace with your XAMPP server address
                        "root", // Replace with your MySQL username
                        ""      // Replace with your MySQL password
                );

                // Insert query
                String query = "INSERT INTO users (username, password, fullname) VALUES (?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, fullName);

                // Execute query
                int rowsInserted = preparedStatement.executeUpdate();
                connection.close();

                // Show result on UI thread
                runOnUiThread(() -> {
                    if (rowsInserted > 0) {
                        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Registration failed.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}
