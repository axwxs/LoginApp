package com.example.loginapp;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ViewCredentialsActivity extends AppCompatActivity {
    private TextView tvUsername, tvPassword;
    private Button btnBack, btnLogout;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_credentials);

        tvUsername = findViewById(R.id.tvUsername);
        tvPassword = findViewById(R.id.tvPassword);
        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnLogout);

        sharedPreferences = getSharedPreferences("LoginAppPrefs", MODE_PRIVATE);
        displayCredentials();

        btnBack.setOnClickListener(v -> finish());
        btnLogout.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private void displayCredentials() {
        String currentUser = sharedPreferences.getString("current_user", null);
        if (currentUser == null) {
            tvUsername.setText("Username: Not logged in");
            tvPassword.setText("Password: N/A");
            return;
        }
        try {
            String usersJson = sharedPreferences.getString("users", "{}");
            org.json.JSONObject usersObj = new org.json.JSONObject(usersJson);
            if (!usersObj.has(currentUser)) {
                tvUsername.setText("Username: " + currentUser);
                tvPassword.setText("Password: Not set");
                return;
            }
            org.json.JSONObject credObj = usersObj.getJSONObject(currentUser);
            String hash = credObj.optString("hash", "Not set");
            tvUsername.setText("Username: " + currentUser);
            tvPassword.setText("Password Hash: " + hash);
        } catch (Exception e) {
            e.printStackTrace();
            tvUsername.setText("Username: Error");
            tvPassword.setText("Password: Error");
        }
    }
}
