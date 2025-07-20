package com.example.loginapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ViewDataActivity extends AppCompatActivity {
    private TextView tvUsername, tvPassword, tvData;
    private Button btnBack, btnLogout;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data);

        tvUsername = findViewById(R.id.tvUsername);
        tvPassword = findViewById(R.id.tvPassword);
        tvData = findViewById(R.id.tvData);
        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnLogout);

        sharedPreferences = getSharedPreferences("LoginAppPrefs", MODE_PRIVATE);
        displayData();

        btnBack.setOnClickListener(v -> finish());
        btnLogout.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private void displayData() {
        String username = sharedPreferences.getString("username", "Not set");
        String password = sharedPreferences.getString("password", "Not set");
        String data = sharedPreferences.getString("user_data", "No data saved");

        tvUsername.setText("Username: " + username);
        tvPassword.setText("Password: " + password);
        tvData.setText("Saved Data: " + data);
    }
}