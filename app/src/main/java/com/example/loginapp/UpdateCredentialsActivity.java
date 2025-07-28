package com.example.loginapp;
import com.example.loginapp.utils.SecureFileHelper;
import com.example.loginapp.utils.SecureStorageHelper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;

public class UpdateCredentialsActivity extends AppCompatActivity {
    private EditText etNewUsername, etNewPassword, etData;
    private Button btnSave, btnViewData, btnLogout, btnSaveData, btnViewCredentials;
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
        btnSaveData = findViewById(R.id.btnSaveData);
        btnViewCredentials = findViewById(R.id.btnViewCredentials);

        sharedPreferences = getSharedPreferences("LoginAppPrefs", MODE_PRIVATE);

        btnSave.setOnClickListener(v -> saveCredentials());
        btnSaveData.setOnClickListener(v -> saveData());
        btnViewData.setOnClickListener(v -> viewData());
        btnLogout.setOnClickListener(v -> logout());
        btnViewCredentials.setOnClickListener(v -> viewCredentials());
    }

    private void saveCredentials() {
        String newUsername = etNewUsername.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        if (!newUsername.isEmpty() && !newPassword.isEmpty()) {
            try {
                String currentUser = sharedPreferences.getString("current_user", null);
                if (currentUser == null) {
                    Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
                    return;
                }
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
                usersObj.put(newUsername, SecureStorageHelper.encrypt(newPassword));
                sharedPreferences.edit()
                    .putString("users", usersObj.toString())
                    .putString("current_user", newUsername)
                    .apply();
                Toast.makeText(this, "Credentials saved!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error encrypting credentials", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Toast.makeText(this, "Username and password cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveData() {
        String data = etData.getText().toString().trim();
        String currentUser = sharedPreferences.getString("current_user", null);
        if (currentUser == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            SecureFileHelper.writeEncryptedFile(this, "user_data_" + currentUser + ".txt", data);
            Toast.makeText(this, "Data saved!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewData() {
        startActivity(new Intent(this, ViewDataActivity.class));
    }

    private void viewCredentials() {
        startActivity(new Intent(this, ViewCredentialsActivity.class));
    }

    private void logout() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}