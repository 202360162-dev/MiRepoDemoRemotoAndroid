package com.example.clonewhatsapp.ui;

import android.content.Intent;
import android.os.Bundle;
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
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BuscarContactosActivity extends AppCompatActivity {
    private RecyclerView rvContactos;
    private ContactoAdapter adapter;
    private List<Contacto> listaGlobal = new ArrayList<>();
    private OkHttpClient client = new OkHttpClient();
    private SessionManager sessionManager;
    private String miUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos); // Puedes reusar el XML anterior

        sessionManager = new SessionManager(this);
        rvContactos = findViewById(R.id.rvContactos);
        rvContactos.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ContactoAdapter(listaGlobal, contacto -> iniciarConversacion(contacto));
        rvContactos.setAdapter(adapter);

        obtenerMiIdYUsuariosGlobales();
    }

    private void obtenerMiIdYUsuariosGlobales() {
        String token = sessionManager.fetchAuthToken();
        client.newCall(new Request.Builder().url(SupabaseConfig.URL + "/auth/v1/user")
                        .addHeader("apikey", SupabaseConfig.KEY).addHeader("Authorization", "Bearer " + token).build())
                .enqueue(new Callback() {
                    @Override public void onFailure(Call call, IOException e) {}
                    @Override public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                miUserId = new JSONObject(response.body().string()).getString("id");
                                cargarTodosLosUsuarios();
                            } catch (Exception ignored) {}
                        }
                    }
                });
    }

    private void cargarTodosLosUsuarios() {
        Request request = new Request.Builder()
                .url(SupabaseConfig.URL + "/rest/v1/profiles?id=neq." + miUserId) // Todos menos yo
                .addHeader("apikey", SupabaseConfig.KEY).addHeader("Authorization", "Bearer " + sessionManager.fetchAuthToken()).build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {}
            @Override public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONArray arr = new JSONArray(response.body().string());
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            listaGlobal.add(new Contacto(obj.getString("id"), obj.optString("email"), obj.optString("nombre", "Sin nombre"), obj.optString("foto_url")));
                        }
                        runOnUiThread(() -> adapter.notifyDataSetChanged());
                    } catch (Exception ignored) {}
                }
            }
        });
    }

    private void iniciarConversacion(Contacto contacto) {
        JSONObject json = new JSONObject();
        try {
            json.put("user1_id", miUserId);
            json.put("user2_id", contacto.getId());
        } catch (Exception e) { return; }

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(SupabaseConfig.URL + "/rest/v1/rpc/get_or_create_conversation")
                .addHeader("apikey", SupabaseConfig.KEY)
                .addHeader("Authorization", "Bearer " + sessionManager.fetchAuthToken())
                .post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(BuscarContactosActivity.this, "Error de red", Toast.LENGTH_SHORT).show());
            }
            @Override public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Supabase devuelve el ID de la conversación en formato JSON (ej: "uuid-xxxx-xxxx")
                        String conversacionId = response.body().string().replaceAll("\"", "");
                        Intent intent = new Intent(BuscarContactosActivity.this, ChatActivity.class);
                        intent.putExtra("nombreContacto", contacto.getNombre());
                        intent.putExtra("conversacionId", conversacionId);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) { e.printStackTrace(); }
                }
            }
        });
    }
}