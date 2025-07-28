package com.example.loginapp;
import com.example.loginapp.utils.SecureFileHelper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ViewDataActivity extends AppCompatActivity {
    private TextView tvData;
    private Button btnBack, btnLogout;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data);

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

    private void displayData(){
        String currentUser = sharedPreferences.getString("current_user", null);
        if (currentUser == null) {
            tvData.setText("Saved Data: N/A");
            return;
        }
        try {
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