package com.example.clonewhatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.clonewhatsapp.ui.LoginActivity;
import com.example.clonewhatsapp.utils.SessionManager;

public class MainActivity extends AppCompatActivity {
    private Button btnCerrarSesion;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        btnCerrarSesion.setOnClickListener(v -> {
            sessionManager.clearSession();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });
    }
}