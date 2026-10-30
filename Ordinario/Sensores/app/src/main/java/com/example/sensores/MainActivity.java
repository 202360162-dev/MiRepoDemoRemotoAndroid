package com.example.sensores;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.sensores.activities.*;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_acelerometro) {
                startActivity(new Intent(MainActivity.this, AcelerometroActivity.class));
            } else if (id == R.id.nav_podometro) {
                startActivity(new Intent(MainActivity.this, PodometroActivity.class));
            } else if (id == R.id.nav_giroscopio) {
                startActivity(new Intent(MainActivity.this, GiroscopioActivity.class));
            } else if (id == R.id.nav_camara) {
                startActivity(new Intent(MainActivity.this, CamaraActivity.class));
            } else if (id == R.id.nav_gps) {
                startActivity(new Intent(MainActivity.this, GpsActivity.class));
            } else if (id == R.id.nav_magnetometro) {
                startActivity(new Intent(MainActivity.this, MagnetometroActivity.class));
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }
}