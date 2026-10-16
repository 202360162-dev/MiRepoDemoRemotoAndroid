package com.example.fragmentado;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Vinculamos los nuevos IDs de los botones
        Button btnInicio = findViewById(R.id.btn_inicio);
        Button btnPerfil = findViewById(R.id.btn_perfil);
        Button btnAjustes = findViewById(R.id.btn_ajustes);

        // Mostrar el fragmento de Inicio por defecto al abrir la app
        if (savedInstanceState == null) {
            replaceFragment(new Fragment1());
        }

        // Asignamos qué fragmento abre cada botón
        btnInicio.setOnClickListener(v -> replaceFragment(new Fragment1()));
        btnPerfil.setOnClickListener(v -> replaceFragment(new Fragment2()));
        btnAjustes.setOnClickListener(v -> replaceFragment(new Fragment3()));
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}