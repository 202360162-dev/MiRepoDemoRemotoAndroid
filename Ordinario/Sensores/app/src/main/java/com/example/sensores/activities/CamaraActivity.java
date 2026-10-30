package com.example.sensores.activities;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.sensores.R;
import java.io.OutputStream;

public class CamaraActivity extends AppCompatActivity {
    private ImageView ivFoto;
    private Button btnGuardarFoto;
    private ActivityResultLauncher<Intent> launcher;
    private Bitmap fotoBitmap; // Variable para guardar la foto en memoria

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camara);

        Button btnTomarFoto = findViewById(R.id.btn_tomar_foto);
        btnGuardarFoto = findViewById(R.id.btn_guardar_foto);
        ivFoto = findViewById(R.id.iv_foto);

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        fotoBitmap = (Bitmap) extras.get("data"); // Obtenemos el mapa de bits
                        ivFoto.setImageBitmap(fotoBitmap);
                        btnGuardarFoto.setEnabled(true); // Activamos el botón de guardar
                    }
                }
        );

        btnTomarFoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
            } else {
                abrirCamara();
            }
        });

        btnGuardarFoto.setOnClickListener(v -> {
            if (fotoBitmap != null) {
                guardarFotoEnGaleria(fotoBitmap);
            }
        });
    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        launcher.launch(intent);
    }

    private void guardarFotoEnGaleria(Bitmap bitmap) {
        // Configuramos los metadatos de la imagen (Nombre, formato, ubicación)
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "Foto_Sensor_" + System.currentTimeMillis() + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        // Se guardará en la carpeta Pictures/SensoresApp
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/SensoresApp");

        // Insertamos la imagen usando el ContentResolver (Forma moderna compatible con Android 10+)
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try {
            if (uri != null) {
                OutputStream outputStream = getContentResolver().openOutputStream(uri);
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream); // 100% de calidad
                    outputStream.close();
                    Toast.makeText(this, "¡Foto guardada en la galería!", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al guardar la foto", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            abrirCamara();
        } else {
            Toast.makeText(this, "Se requiere permiso para usar la cámara", Toast.LENGTH_SHORT).show();
        }
    }
}