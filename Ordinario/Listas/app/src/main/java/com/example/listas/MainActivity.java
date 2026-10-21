package com.example.listas;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Inicializar el RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        // 2. Definir el posicionamiento (LayoutManager) - Linear, Vertical por defecto
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 3. Preparar los datos de prueba
        List<ItemModel> listaDatos = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            listaDatos.add(new ItemModel("Elemento de la lista #" + i));
        }

        // 4. Configurar e inyectar el Adapter
        MyAdapter adapter = new MyAdapter(listaDatos);
        recyclerView.setAdapter(adapter);
    }
}