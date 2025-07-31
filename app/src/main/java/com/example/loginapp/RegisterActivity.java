package com.example.loginapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.loginapp.utils.PasswordUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    private EditText etRegisterUsername, etRegisterPassword, etRegisterPasswordConfirm;
    private Button btnRegisterSave;
    private SharedPreferences sharedPreferences;
    private TextView tvRegisterError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etRegisterUsername = findViewById(R.id.etRegisterUsername);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etRegisterPasswordConfirm = findViewById(R.id.etRegisterPasswordConfirm);
        btnRegisterSave = findViewById(R.id.btnRegisterSave);
        sharedPreferences = getSharedPreferences("LoginAppPrefs", MODE_PRIVATE);
        tvRegisterError = findViewById(R.id.tvRegisterError);

        btnRegisterSave.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String username = etRegisterUsername.getText().toString().trim();
        String password = etRegisterPassword.getText().toString().trim();
        String passwordConfirm = etRegisterPasswordConfirm.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username and password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            String usersJson = sharedPreferences.getString("users", "{}");
            JSONObject usersObj = new JSONObject(usersJson);
            String encUsername = com.example.loginapp.utils.SecureStorageHelper.encrypt(username);
            if (usersObj.has(username)) {
                Toast.makeText(this, "Username already exists!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Minimum password strength check
            if (password.length() < 8 || !password.matches(".*[A-Z].*") || !password.matches(".*[a-z].*") || !password.matches(".*[0-9].*") || !password.matches(".*[!@#$%^&*()_+=-].*")) {
                Toast.makeText(this, "Password must be at least 8 characters and include uppercase, lowercase, number, and special character.", Toast.LENGTH_LONG).show();
                tvRegisterError.setText(R.string.password_must_be_at_least_8_characters_and_include_uppercase_lowercase_number_and_special_character);
                tvRegisterError.setVisibility(View.VISIBLE);
                return;
            }

            // Confirm password match
            else if (!password.equals(passwordConfirm)) {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                tvRegisterError.setText(R.string.passwords_do_not_match);
                tvRegisterError.setVisibility(View.VISIBLE);
                return;
            }

            else {
                tvRegisterError.setVisibility(View.GONE);
            }

            String salt = PasswordUtils.generateSalt();
            String hash = PasswordUtils.hashPassword(password, salt);
            JSONObject credObj = new JSONObject();
            credObj.put("salt", salt);
            credObj.put("hash", hash);
            usersObj.put(username, credObj);
            sharedPreferences.edit().putString("users", usersObj.toString())
                .putString("current_user", encUsername)
                .apply();
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving credentials", Toast.LENGTH_SHORT).show();
        }
    }
}
