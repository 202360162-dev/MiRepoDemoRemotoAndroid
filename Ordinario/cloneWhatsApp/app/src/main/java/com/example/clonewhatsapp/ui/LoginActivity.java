package com.example.clonewhatsapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.clonewhatsapp.MainActivity;
import com.example.clonewhatsapp.R;
import com.example.clonewhatsapp.auth.AutenticacionManager;
import com.example.clonewhatsapp.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegistro, tvRecuperar;
    private AutenticacionManager authManager;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        if (sessionManager.fetchAuthToken() != null && !sessionManager.fetchAuthToken().equals("registro_exitoso_requiere_confirmacion")) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        authManager = new AutenticacionManager();
        etEmail = findViewById(R.id.etEmailLogin);
        etPassword = findViewById(R.id.etPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegistro = findViewById(R.id.tvIrRegistro);
        tvRecuperar = findViewById(R.id.tvRecuperarPass);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            if(email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Llena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            authManager.login(email, password, new AutenticacionManager.AuthCallback() {
                @Override
                public void onSuccess(String token) {
                    sessionManager.saveAuthToken(token);
                    Toast.makeText(LoginActivity.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
        });

        tvRegistro.setOnClickListener(v -> {
            startActivity(new Intent(this, RegistroActivity.class));
        });

        tvRecuperar.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            if(email.isEmpty()) {
                Toast.makeText(this, "Ingresa tu correo primero", Toast.LENGTH_SHORT).show();
                return;
            }
            authManager.recuperarContrasena(email, new AutenticacionManager.AuthCallback() {
                @Override
                public void onSuccess(String msg) {
                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onError(String error) {
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}