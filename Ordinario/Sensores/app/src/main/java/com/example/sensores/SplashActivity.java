package com.example.sensores;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Referenciar el contenedor de texto
        LinearLayout llLogo = findViewById(R.id.ll_logo);

        // Cargar animación de aparición hacia arriba
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        llLogo.startAnimation(slideUp);

        // Esperar 2.5 segundos para mostrar la animación antes de avanzar
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            // Transición suave entre activities
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 2500);
    }
}