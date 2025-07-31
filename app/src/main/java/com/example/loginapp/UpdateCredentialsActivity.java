package com.example.loginapp;
import com.example.loginapp.utils.PasswordUtils;
import com.example.loginapp.utils.SecureStorageHelper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import org.json.JSONArray;

public class UpdateCredentialsActivity extends AppCompatActivity {
    private EditText etAddUsername, etAddPassword, etAddPin, etAddNote, etNewUsername, etNewPassword, etNewPasswordConfirm;
    private Button btnAddUsername, btnAddPassword, btnAddPin, btnAddNote, btnLogout, btnSave;
    private SharedPreferences sharedPreferences;
    private JSONArray usernamesArr, passwordsArr, pinsArr, notesArr;
    private TextView tvPasswordError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        sharedPreferences = getSharedPreferences("LoginAppPrefs", MODE_PRIVATE);
        if (!isSessionValid()) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        etAddUsername = findViewById(R.id.etAddUsername);
        etAddPassword = findViewById(R.id.etAddPassword);
        etAddPin = findViewById(R.id.etAddPin);
        etAddNote = findViewById(R.id.etAddNote);
        btnAddUsername = findViewById(R.id.btnAddUsername);
        btnAddPassword = findViewById(R.id.btnAddPassword);
        etNewPasswordConfirm = findViewById(R.id.etConfirmPassword);
        btnAddPin = findViewById(R.id.btnAddPin);
        btnAddNote = findViewById(R.id.btnAddNote);
        btnSave = findViewById(R.id.btnSave);
        btnLogout = findViewById(R.id.btnLogout);
        tvPasswordError = findViewById(R.id.tvPasswordError);

        Button btnViewVault = findViewById(R.id.btnViewVault);
        loadVaultData();

        btnAddUsername.setOnClickListener(v -> addEntry(usernamesArr, etAddUsername, "vault_usernames"));
        btnAddPassword.setOnClickListener(v -> addEntry(passwordsArr, etAddPassword, "vault_passwords"));
        btnAddPin.setOnClickListener(v -> addEntry(pinsArr, etAddPin, "vault_pins"));
        btnAddNote.setOnClickListener(v -> addEntry(notesArr, etAddNote, "vault_notes"));
        btnLogout.setOnClickListener(v -> logout());
        btnViewVault.setOnClickListener(v -> {
            startActivity(new Intent(this, VaultViewActivity.class));
        });

        btnSave.setOnClickListener(v -> saveCredentials());
    }

    private void saveCredentials() {
        etNewUsername = findViewById(R.id.etNewUsername);
        etNewPassword = findViewById(R.id.etNewPassword);
        etNewPasswordConfirm = findViewById(R.id.etConfirmPassword);
        String newUsername = etNewUsername.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String passwordConfirm = etNewPasswordConfirm.getText().toString().trim();
        if (newUsername.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this, "Username and password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Minimum password strength check
        if (newPassword.length() < 8 || !newPassword.matches(".*[A-Z].*") || !newPassword.matches(".*[a-z].*") || !newPassword.matches(".*[0-9].*") || !newPassword.matches(".*[!@#$%^&*()_+=-].*")) {
            Toast.makeText(this, "Password must be at least 8 characters and include uppercase, lowercase, number, and special character.", Toast.LENGTH_LONG).show();
            tvPasswordError.setText(R.string.password_must_be_at_least_8_characters_and_include_uppercase_lowercase_number_and_special_character);
            tvPasswordError.setVisibility(View.VISIBLE);
            return;
        }

        // Confirm password match
        else if (!newPassword.equals(passwordConfirm)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            tvPasswordError.setText(R.string.passwords_do_not_match);
            tvPasswordError.setVisibility(View.VISIBLE);
            return;
        }

        else {
            tvPasswordError.setVisibility(View.GONE);
        }


        try {
            String encCurrentUser = sharedPreferences.getString("current_user", null);
            if (encCurrentUser == null) {
                Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
                return;
            }
            String currentUser = com.example.loginapp.utils.SecureStorageHelper.decrypt(encCurrentUser);
            String usersJson = sharedPreferences.getString("users", "{}");
            JSONObject usersObj = new JSONObject(usersJson);
            if (!currentUser.equals(newUsername) && usersObj.has(newUsername)) {
                Toast.makeText(this, "Username already exists!", Toast.LENGTH_SHORT).show();
                return;
            }
            // Remove old username if changed
            if (!currentUser.equals(newUsername)) {
                usersObj.remove(currentUser);
            }
            String salt = PasswordUtils.generateSalt();
            String hash = PasswordUtils.hashPassword(newPassword, salt);
            JSONObject credObj = new JSONObject();
            credObj.put("salt", salt);
            credObj.put("hash", hash);
            usersObj.put(newUsername, credObj);
            sharedPreferences.edit()
                .putString("users", usersObj.toString())
                .putString("current_user", SecureStorageHelper.encrypt(newUsername))
                .apply();
            Toast.makeText(this, "Credentials updated!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error updating credentials", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isSessionValid() {
        try {
            long now = System.currentTimeMillis();
            String encExpiry = sharedPreferences.getString("session_expiry", null);
            String encUser = sharedPreferences.getString("current_user", null);
            if (encExpiry == null || encUser == null) return false;
            String decUser = com.example.loginapp.utils.SecureStorageHelper.decrypt(encUser);
            long expiry = Long.parseLong(com.example.loginapp.utils.SecureStorageHelper.decrypt(encExpiry));
            return decUser != null && now < expiry;
        } catch (Exception e) {
            return false;
        }
    }

    private void loadVaultData() {
        usernamesArr = loadCategory("vault_usernames");
        passwordsArr = loadCategory("vault_passwords");
        pinsArr = loadCategory("vault_pins");
        notesArr = loadCategory("vault_notes");
    }

    private JSONArray loadCategory(String key) {
        try {
            String enc = sharedPreferences.getString(key, null);
            if (enc == null) return new JSONArray();
            String dec = SecureStorageHelper.decrypt(enc);
            return new JSONArray(dec);
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    private void addEntry(JSONArray arr, EditText et, String key) {
        String value = et.getText().toString().trim();
        if (value.isEmpty()) return;
        arr.put(value);
        et.setText("");
        saveCategory(arr, key);
    }

    private void saveCategory(JSONArray arr, String key) {
        try {
            String enc = SecureStorageHelper.encrypt(arr.toString());
            sharedPreferences.edit().putString(key, enc).apply();
        } catch (Exception e) {
            Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        sharedPreferences.edit().remove("current_user")
            .remove("session_start")
            .remove("session_expiry")
            .apply();

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}