package com.example.dclassicsbooks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword;
    private TextView tvUsernameError, tvPasswordError;
    private View usernameErrorGroup, passwordErrorGroup;
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
        usernameErrorGroup = findViewById(R.id.usernameErrorGroup);
        passwordErrorGroup = findViewById(R.id.passwordErrorGroup);
        tvUsernameError = findViewById(R.id.tvUsernameError);
        tvPasswordError = findViewById(R.id.tvPasswordError);
        btnLogin = findViewById(R.id.btnLogin);

        // Set action saat tombol Sign In diklik
        btnLogin.setOnClickListener(v -> processLogin());
    }

    private void processLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        clearErrors();

        // Validasi 1: Username must be filled
        if (username.isEmpty()) {
            showUsernameError("Please enter your username");
            etUsername.requestFocus();
            return;
        }

        // Validasi 2: Password must be filled
        if (password.isEmpty()) {
            showPasswordError("Please enter your password");
            etPassword.requestFocus();
            return;
        }

        // Validasi 3: Password must be alphanumeric (kombinasi huruf dan angka)
        if (!password.matches("^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]+$")) {
            showPasswordError("Password must be alphanumeric");
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

    private void clearErrors() {
        usernameErrorGroup.setVisibility(View.GONE);
        passwordErrorGroup.setVisibility(View.GONE);
        etUsername.setBackgroundResource(R.drawable.bg_rounded_input);
        etPassword.setBackgroundResource(R.drawable.bg_rounded_input);
    }

    private void showUsernameError(String message) {
        tvUsernameError.setText(message);
        usernameErrorGroup.setVisibility(View.VISIBLE);
        etUsername.setBackgroundResource(R.drawable.bg_rounded_input_error);
    }

    private void showPasswordError(String message) {
        tvPasswordError.setText(message);
        passwordErrorGroup.setVisibility(View.VISIBLE);
        etPassword.setBackgroundResource(R.drawable.bg_rounded_input_error);
    }
}
