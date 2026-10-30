package com.example.sensores.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.example.sensores.R;

public class GpsActivity extends AppCompatActivity implements LocationListener {
    private LocationManager locationManager;
    private TextView tvData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_base);
        tvData = findViewById(R.id.tv_sensor_data);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Pedimos permiso de ubicación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        iniciarGPS();
    }

    private void iniciarGPS() {
        try {
            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGpsEnabled && !isNetworkEnabled) {
                tvData.setText("Por favor, enciende el GPS en los ajustes de tu teléfono.");
                return;
            }

            tvData.setText("Buscando señal...\n(Si usas emulador, envía la ubicación manualmente en los 3 puntitos > Location)");

            // Usa la red Wi-Fi/Datos para ubicar rápido en interiores
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 1, this);
            }
            // Usa el GPS real para precisión en exteriores
            if (isGpsEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, this);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            iniciarGPS();
        } else {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        tvData.setText("Latitud: " + location.getLatitude() + "\nLongitud: " + location.getLongitude());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locationManager != null) locationManager.removeUpdates(this);
    }
}