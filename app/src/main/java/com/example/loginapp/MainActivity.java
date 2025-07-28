package com.example.loginapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import org.json.JSONObject;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnRegister;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // Initialize biometric authentication
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(MainActivity.this, "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                startActivity(new Intent(MainActivity.this, UpdateCredentialsActivity.class));
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up biometric prompt info
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login")
                .setSubtitle("Log in using your fingerprint")
                .setNegativeButtonText("Use account password")
                .build();

        // Set click listener for login button
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() && password.isEmpty()) {
                // If both fields are empty, launch biometric prompt
                checkBiometricAndAuthenticate();
            } else if (validateCredentials(username, password)) {
                // If credentials are valid, save current user and launch biometric prompt
                getSharedPreferences("LoginAppPrefs", MODE_PRIVATE)
                    .edit().putString("current_user", username).apply();
                checkBiometricAndAuthenticate();
            } else {
                // Log what was entered for debugging
                 Log.d("MainActivity", "Entered Username: " + username + ", Entered Password: " + password);

                // If credentials are invalid, show error
                Toast.makeText(this, getString(R.string.invalid_credentials), Toast.LENGTH_SHORT).show();
            }
        });

        // Set click listener for register button
        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        });
    }

    private void checkBiometricAndAuthenticate() {
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                biometricPrompt.authenticate(promptInfo);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "No fingerprints enrolled!", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "No fingerprint sensor detected!", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this, "Biometric sensor is unavailable", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private boolean validateCredentials(String username, String password) {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("LoginAppPrefs", MODE_PRIVATE);
            String usersJson = sharedPreferences.getString("users", "{}");
            JSONObject usersObj = new JSONObject(usersJson);
            if (!usersObj.has(username)) return false;
            String savedPasswordEnc = usersObj.getString(username);
            String savedPassword = com.example.loginapp.utils.SecureStorageHelper.decrypt(savedPasswordEnc);
            return password.equals(savedPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}