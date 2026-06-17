package com.example.dclassicsbooks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("GlobalVars", MODE_PRIVATE);
        if (sharedPreferences.contains("USERNAME")) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        // Inisialisasi komponen UI
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // Set action saat tombol Sign In diklik
        btnLogin.setOnClickListener(v -> processLogin());
    }

    private void processLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validasi 1: Username must be filled
        if (username.isEmpty()) {
            etUsername.setError("Username must be filled");
            etUsername.requestFocus();
            return;
        }

        // Validasi 2: Password must be filled
        if (password.isEmpty()) {
            etPassword.setError("Password must be filled");
            etPassword.requestFocus();
            return;
        }

        // Validasi 3: Password must be alphanumeric (kombinasi huruf dan angka)
        if (!password.matches("^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]+$")) {
            etPassword.setError("Password must be alphanumeric");
            etPassword.requestFocus();
            return;
        }

        // Jika validasi sukses:
        // 1. Store username to global variable (menggunakan SharedPreferences)
        SharedPreferences sharedPreferences = getSharedPreferences("GlobalVars", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USERNAME", username);
        editor.apply();

        // (BAGIAN YANG DIPERBAIKI)
        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

        // 2. Redirect to home page
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish(); // Menutup LoginActivity agar user tidak kembali saat menekan tombol back
    }
}
