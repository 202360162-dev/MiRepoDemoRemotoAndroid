package com.example.verthorizon;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "CicloDeVida";

    // 1. Creamos una constante (llave) para guardar y buscar nuestro texto en el Bundle
    private static final String TEXTO_GUARDADO = "texto_resultado";

    private TextView tvResultado;
    private EditText editInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Log.d(TAG, "onCreate: La actividad se ha creado o recreado.");
        Toast.makeText(this, "onCreate() ejecutado", Toast.LENGTH_SHORT).show();

        editInput = findViewById(R.id.edit_input);
        tvResultado = findViewById(R.id.tv_resultado);
        Button btnMostrar = findViewById(R.id.btn_mostrar);

        // 2. RECUPERAR EL ESTADO:
        // Verificamos si savedInstanceState no es nulo (significa que la app se recreó, por ejemplo, al girar)
        if (savedInstanceState != null) {
            // Obtenemos el texto usando nuestra llave
            String textoRecuperado = savedInstanceState.getString(TEXTO_GUARDADO);
            // Si el texto existe, se lo volvemos a poner al TextView
            if (textoRecuperado != null) {
                tvResultado.setText(textoRecuperado);
                Log.d(TAG, "onCreate: Texto restaurado exitosamente.");
            }
        }

        btnMostrar.setOnClickListener(v -> {
            String valor = editInput.getText().toString();
            tvResultado.setText(valor);
        });
    }

    // 3. GUARDAR EL ESTADO:
    // Este método se llama automáticamente ANTES de que la actividad se destruya al girar
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Guardamos el texto actual que tiene el TextView utilizando nuestra llave
        String textoActual = tvResultado.getText().toString();
        outState.putString(TEXTO_GUARDADO, textoActual);

        Log.d(TAG, "onSaveInstanceState: Guardando el dato antes de girar la pantalla.");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: La actividad se ha pausado.");
        Toast.makeText(this, "onPause() ejecutado", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: La actividad se está destruyendo.");
        Toast.makeText(this, "onDestroy() ejecutado", Toast.LENGTH_SHORT).show();
    }
}