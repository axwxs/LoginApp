package com.example.loginapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class UpdateCredentialsActivity extends AppCompatActivity {
    private EditText etNewUsername, etNewPassword, etData;
    private Button btnSave, btnViewData, btnLogout;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        etNewUsername = findViewById(R.id.etNewUsername);
        etNewPassword = findViewById(R.id.etNewPassword);
        etData = findViewById(R.id.etData);
        btnSave = findViewById(R.id.btnSave);
        btnViewData = findViewById(R.id.btnViewData);
        btnLogout = findViewById(R.id.btnLogout);

        sharedPreferences = getSharedPreferences("LoginAppPrefs", MODE_PRIVATE);

        btnSave.setOnClickListener(v -> saveCredentials());
        btnViewData.setOnClickListener(v -> viewData());
        btnLogout.setOnClickListener(v -> logout());
    }

    private void saveCredentials() {
        String newUsername = etNewUsername.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String data = etData.getText().toString().trim();

        if (!newUsername.isEmpty() && !newPassword.isEmpty()) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", newUsername);
            editor.putString("password", newPassword);
            editor.putString("user_data", data);
            editor.apply();
            Toast.makeText(this, "Credentials and data saved!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Username and password cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewData() {
        startActivity(new Intent(this, ViewDataActivity.class));
    }

    private void logout() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}