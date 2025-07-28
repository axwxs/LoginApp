package com.example.loginapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.loginapp.utils.SecureStorageHelper;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    private EditText etRegisterUsername, etRegisterPassword;
    private Button btnRegisterSave;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etRegisterUsername = findViewById(R.id.etRegisterUsername);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        btnRegisterSave = findViewById(R.id.btnRegisterSave);
        sharedPreferences = getSharedPreferences("LoginAppPrefs", MODE_PRIVATE);

        btnRegisterSave.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String username = etRegisterUsername.getText().toString().trim();
        String password = etRegisterPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username and password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            String usersJson = sharedPreferences.getString("users", "{}");
            JSONObject usersObj = new JSONObject(usersJson);
            if (usersObj.has(username)) {
                Toast.makeText(this, "Username already exists!", Toast.LENGTH_SHORT).show();
                return;
            }
            usersObj.put(username, SecureStorageHelper.encrypt(password));
            sharedPreferences.edit().putString("users", usersObj.toString()).apply();
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving credentials", Toast.LENGTH_SHORT).show();
        }
    }
}
