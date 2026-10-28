package com.example.persistencia;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private EditText etProducto, etDescripcion, etPrecio;
    private Button btnGuardar;
    private RecyclerView recyclerView;
    private Integer editId = null;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etProducto = findViewById(R.id.etProducto);
        etDescripcion = findViewById(R.id.etDescripcion);
        etPrecio = findViewById(R.id.etPrecio);
        btnGuardar = findViewById(R.id.btnGuardar);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnGuardar.setOnClickListener(v -> guardarRegistro());
        consultarRegistros();
    }

    private void consultarRegistros() {
        executor.execute(() -> {
            try {
                URL url = new URL(SupabaseConfig.URL + "productos?select=*");
                HttpURLConnection conn = configurarConexion(url, "GET");

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);

                JSONArray jsonArray = new JSONArray(sb.toString());
                List<Producto> lista = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    lista.add(new Producto(obj.getInt("id"), obj.getString("producto"), obj.getString("descripcion"), obj.getInt("precio")));
                }

                handler.post(() -> {
                    ProductoAdapter adapter = new ProductoAdapter(lista, new ProductoAdapter.OnItemClickListener() {
                        @Override public void onEdit(Producto p) { prepararEdicion(p); }
                        @Override public void onDelete(int id) { eliminarRegistro(id); }
                    });
                    recyclerView.setAdapter(adapter);
                });
            } catch (Exception e) {
                mostrarError(e.getMessage());
            }
        });
    }

    private void guardarRegistro() {
        String prod = etProducto.getText().toString();
        String desc = etDescripcion.getText().toString();
        String precioStr = etPrecio.getText().toString();

        if (prod.isEmpty() || desc.isEmpty() || precioStr.isEmpty()) {
            Toast.makeText(this, "Llena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        executor.execute(() -> {
            try {
                URL url = new URL(SupabaseConfig.URL + "productos" + (editId != null ? "?id=eq." + editId : ""));
                HttpURLConnection conn = configurarConexion(url, editId != null ? "PATCH" : "POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("producto", prod);
                jsonParam.put("descripcion", desc);
                jsonParam.put("precio", Integer.parseInt(precioStr));

                OutputStream os = conn.getOutputStream();
                os.write(jsonParam.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == 201 || responseCode == 204 || responseCode == 200) {
                    handler.post(() -> {
                        limpiarCampos();
                        consultarRegistros();
                    });
                } else {
                    mostrarError("Error HTTP: " + responseCode);
                }
            } catch (Exception e) {
                mostrarError(e.getMessage());
            }
        });
    }

    private void eliminarRegistro(int id) {
        executor.execute(() -> {
            try {
                URL url = new URL(SupabaseConfig.URL + "productos?id=eq." + id);
                HttpURLConnection conn = configurarConexion(url, "DELETE");
                if (conn.getResponseCode() == 204 || conn.getResponseCode() == 200) {
                    handler.post(this::consultarRegistros);
                }
            } catch (Exception e) {
                mostrarError(e.getMessage());
            }
        });
    }

    private HttpURLConnection configurarConexion(URL url, String method) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("apikey", SupabaseConfig.KEY);
        conn.setRequestProperty("Authorization", "Bearer " + SupabaseConfig.KEY);
        return conn;
    }

    private void prepararEdicion(Producto p) {
        editId = p.id;
        etProducto.setText(p.producto);
        etDescripcion.setText(p.descripcion);
        etPrecio.setText(String.valueOf(p.precio));
        btnGuardar.setText("Actualizar Producto");
    }

    private void limpiarCampos() {
        editId = null;
        etProducto.setText("");
        etDescripcion.setText("");
        etPrecio.setText("");
        btnGuardar.setText("Agregar Producto");
    }

    private void mostrarError(String error) {
        handler.post(() -> Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_LONG).show());
    }
}