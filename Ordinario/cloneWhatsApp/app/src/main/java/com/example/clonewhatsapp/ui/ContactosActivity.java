package com.example.clonewhatsapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clonewhatsapp.R;
import com.example.clonewhatsapp.adapters.ContactoAdapter;
import com.example.clonewhatsapp.config.SupabaseConfig;
import com.example.clonewhatsapp.models.Contacto;
import com.example.clonewhatsapp.utils.SessionManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ContactosActivity extends AppCompatActivity {
    private RecyclerView rvContactos;
    private EditText etBuscar;
    private ContactoAdapter adapter;
    private List<Contacto> listaContactos = new ArrayList<>();
    private OkHttpClient client;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);

        client = new OkHttpClient();
        sessionManager = new SessionManager(this);

        etBuscar = findViewById(R.id.etBuscarContacto);
        rvContactos = findViewById(R.id.rvContactos);
        rvContactos.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ContactoAdapter(listaContactos, contacto -> {
            Toast.makeText(this, "Seleccionaste a: " + contacto.getNombre(), Toast.LENGTH_SHORT).show();
            // Aquí iría el código para iniciar un chat
            Intent intent = new Intent(ContactosActivity.this, ChatActivity.class);
            intent.putExtra("nombreContacto", contacto.getNombre());
            startActivity(intent);
        });
        rvContactos.setAdapter(adapter);

        cargarContactos();

        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarContactos(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void cargarContactos() {
        String token = sessionManager.fetchAuthToken();
        Request request = new Request.Builder()
                .url(SupabaseConfig.URL + "/rest/v1/profiles?select=*")
                .addHeader("apikey", SupabaseConfig.KEY)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(ContactosActivity.this, "Error de red", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        listaContactos.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            String id = obj.optString("id");
                            String email = obj.optString("email");
                            String nombre = obj.optString("nombre", "");
                            String fotoUrl = obj.optString("foto_url", "");
                            listaContactos.add(new Contacto(id, email, nombre, fotoUrl));
                        }
                        runOnUiThread(() -> adapter.notifyDataSetChanged());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void filtrarContactos(String texto) {
        List<Contacto> filtrados = new ArrayList<>();
        for (Contacto c : listaContactos) {
            String name = c.getNombre() != null ? c.getNombre().toLowerCase() : "";
            if (name.contains(texto.toLowerCase()) || c.getEmail().toLowerCase().contains(texto.toLowerCase())) {
                filtrados.add(c);
            }
        }
        adapter.actualizarLista(filtrados);
    }
}