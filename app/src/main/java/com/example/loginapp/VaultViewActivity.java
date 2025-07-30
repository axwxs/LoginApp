package com.example.loginapp;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.loginapp.utils.SecureStorageHelper;
import org.json.JSONArray;

public class VaultViewActivity extends AppCompatActivity {
    private ListView listUsernames, listPasswords, listPins, listNotes;
    private ArrayAdapter<String> adapterUsernames, adapterPasswords, adapterPins, adapterNotes;
    private JSONArray usernamesArr, passwordsArr, pinsArr, notesArr;
    private SharedPreferences sharedPreferences;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vault_view);
        sharedPreferences = getSharedPreferences("LoginAppPrefs", MODE_PRIVATE);
        listUsernames = findViewById(R.id.listUsernames);
        listPasswords = findViewById(R.id.listPasswords);
        listPins = findViewById(R.id.listPins);
        listNotes = findViewById(R.id.listNotes);
        btnBack = findViewById(R.id.btnBack);
        loadVaultData();
        setupAdapters();
        listUsernames.setOnItemClickListener((parent, view, position, id) -> deleteEntry(usernamesArr, adapterUsernames, position, "vault_usernames"));
        listPasswords.setOnItemClickListener((parent, view, position, id) -> deleteEntry(passwordsArr, adapterPasswords, position, "vault_passwords"));
        listPins.setOnItemClickListener((parent, view, position, id) -> deleteEntry(pinsArr, adapterPins, position, "vault_pins"));
        listNotes.setOnItemClickListener((parent, view, position, id) -> deleteEntry(notesArr, adapterNotes, position, "vault_notes"));
        btnBack.setOnClickListener(v -> finish());
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

    private void setupAdapters() {
        adapterUsernames = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, jsonArrayToList(usernamesArr));
        adapterPasswords = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, jsonArrayToList(passwordsArr));
        adapterPins = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, jsonArrayToList(pinsArr));
        adapterNotes = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, jsonArrayToList(notesArr));
        listUsernames.setAdapter(adapterUsernames);
        listPasswords.setAdapter(adapterPasswords);
        listPins.setAdapter(adapterPins);
        listNotes.setAdapter(adapterNotes);
    }

    private void deleteEntry(JSONArray arr, ArrayAdapter<String> adapter, int position, String key) {
        JSONArray newArr = new JSONArray();
        for (int i = 0; i < arr.length(); i++) {
            if (i != position) newArr.put(arr.optString(i));
        }
        saveCategory(newArr, key);
        arr = newArr;
        adapter.clear();
        adapter.addAll(jsonArrayToList(arr));
        adapter.notifyDataSetChanged();
        if (key.equals("vault_usernames")) usernamesArr = arr;
        else if (key.equals("vault_passwords")) passwordsArr = arr;
        else if (key.equals("vault_pins")) pinsArr = arr;
        else if (key.equals("vault_notes")) notesArr = arr;
    }

    private void saveCategory(JSONArray arr, String key) {
        try {
            String enc = SecureStorageHelper.encrypt(arr.toString());
            sharedPreferences.edit().putString(key, enc).apply();
        } catch (Exception e) {
            Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show();
        }
    }

    private java.util.List<String> jsonArrayToList(JSONArray arr) {
        java.util.List<String> list = new java.util.ArrayList<>();
        for (int i = 0; i < arr.length(); i++) list.add(arr.optString(i));
        return list;
    }
}

