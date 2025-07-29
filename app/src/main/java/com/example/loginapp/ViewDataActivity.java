package com.example.loginapp;
import com.example.loginapp.utils.SecureFileHelper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ViewDataActivity extends AppCompatActivity {
    private TextView tvData;
    private Button btnBack, btnLogout;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data);

        sharedPreferences = getSharedPreferences("LoginAppPrefs", MODE_PRIVATE);
        if (!isSessionValid()) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        tvData = findViewById(R.id.tvData);
        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnLogout);
        displayData();
        btnBack.setOnClickListener(v -> finish());
        btnLogout.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
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

    private void displayData(){
        String encCurrentUser = sharedPreferences.getString("current_user", null);
        if (encCurrentUser == null) {
            tvData.setText("Saved Data: N/A");
            return;
        }
        try {
            String currentUser = com.example.loginapp.utils.SecureStorageHelper.decrypt(encCurrentUser);
            String data;
            try {
                data = com.example.loginapp.utils.SecureFileHelper.readEncryptedFile(this, "user_data_" + currentUser + ".txt");
            } catch (Exception e) {
                data = "No data saved";
            }
            tvData.setText("Saved Data: " + data);
        } catch (Exception e) {
            e.printStackTrace();
            tvData.setText("Saved Data: Error");
        }
    }

}