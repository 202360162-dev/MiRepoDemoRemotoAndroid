package com.example.clonewhatsapp.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.clonewhatsapp.R;
import com.example.clonewhatsapp.auth.AutenticacionManager;

public class RegistroActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnRegistrar;
    private AutenticacionManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        authManager = new AutenticacionManager();
        etEmail = findViewById(R.id.etEmailRegistro);
        etPassword = findViewById(R.id.etPasswordRegistro);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            if(email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Llena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            authManager.registro(email, password, new AutenticacionManager.AuthCallback() {
                @Override
                public void onSuccess(String token) {
                    Toast.makeText(RegistroActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(RegistroActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}