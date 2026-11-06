package com.example.clonewhatsapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clonewhatsapp.adapters.ContactoAdapter;
import com.example.clonewhatsapp.config.SupabaseConfig;
import com.example.clonewhatsapp.models.Contacto;
import com.example.clonewhatsapp.realtime.SupabaseRealtimeClient;
import com.example.clonewhatsapp.ui.BuscarContactosActivity;
import com.example.clonewhatsapp.ui.ChatActivity;
import com.example.clonewhatsapp.ui.LoginActivity;
import com.example.clonewhatsapp.ui.PerfilActivity;
import com.example.clonewhatsapp.utils.NotificationHelper;
import com.example.clonewhatsapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rvChatsActivos;
    private ContactoAdapter adapter;
    private List<Contacto> listaChats = new ArrayList<>();
    private OkHttpClient client = new OkHttpClient();
    private SessionManager sessionManager;
    private String miUserId;
    private SupabaseRealtimeClient realtimeClient; // Cliente para notificaciones

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. PEDIR PERMISOS DE NOTIFICACIÓN (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        sessionManager = new SessionManager(this);

        // Configurar Toolbar con 3 puntitos
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        rvChatsActivos = findViewById(R.id.rvChatsActivos);
        rvChatsActivos.setLayoutManager(new LinearLayoutManager(this));

        // Reutilizamos ContactoAdapter para mostrar los chats activos
        adapter = new ContactoAdapter(listaChats, contacto -> {
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            intent.putExtra("nombreContacto", contacto.getNombre());
            intent.putExtra("conversacionId", contacto.getId()); // Aquí el ID es la conversacion_id gracias a la vista SQL
            startActivity(intent);
        });
        rvChatsActivos.setAdapter(adapter);

        FloatingActionButton fabAgregar = findViewById(R.id.fabAgregarContacto);
        fabAgregar.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, BuscarContactosActivity.class)));

        // 2. INICIALIZAR REALTIME PARA ESCUCHAR NUEVOS MENSAJES Y LANZAR NOTIFICACIÓN
        realtimeClient = new SupabaseRealtimeClient(() -> runOnUiThread(() -> {
            // Lanza la notificación local
            NotificationHelper.showNotification(MainActivity.this, "Nuevo Mensaje", "Has recibido un mensaje Hextech");

            // Recarga la lista de chats para que la interfaz se actualice
            if (miUserId != null) {
                cargarChatsActivos();
            }
        }));
        realtimeClient.conectar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        obtenerMiIdYCargarChats();
    }

    private void obtenerMiIdYCargarChats() {
        String token = sessionManager.fetchAuthToken();
        client.newCall(new Request.Builder().url(SupabaseConfig.URL + "/auth/v1/user")
                        .addHeader("apikey", SupabaseConfig.KEY).addHeader("Authorization", "Bearer " + token).build())
                .enqueue(new Callback() {
                    @Override public void onFailure(Call call, IOException e) {}
                    @Override public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                miUserId = new JSONObject(response.body().string()).getString("id");
                                cargarChatsActivos();
                            } catch (Exception ignored) {}
                        }
                    }
                });
    }

    private void cargarChatsActivos() {
        Request request = new Request.Builder()
                .url(SupabaseConfig.URL + "/rest/v1/vista_chats_activos?mi_id=eq." + miUserId)
                .addHeader("apikey", SupabaseConfig.KEY).addHeader("Authorization", "Bearer " + sessionManager.fetchAuthToken()).build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {}
            @Override public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONArray arr = new JSONArray(response.body().string());
                        listaChats.clear();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            listaChats.add(new Contacto(
                                    obj.getString("conversacion_id"), // Guardamos el conversacion_id en el campo ID
                                    "",
                                    obj.optString("nombre", "Sin nombre"),
                                    obj.optString("foto_url", "")
                            ));
                        }
                        runOnUiThread(() -> adapter.notifyDataSetChanged());
                    } catch (Exception ignored) {}
                }
            }
        });
    }

    // Menú de 3 puntitos
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_perfil) {
            startActivity(new Intent(this, PerfilActivity.class));
            return true;
        } else if (item.getItemId() == R.id.action_logout) {
            sessionManager.clearSession();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 3. APAGAR EL SOCKET AL DESTRUIR LA APP PARA EVITAR FUGAS DE MEMORIA
        if (realtimeClient != null) {
            realtimeClient.desconectar();
        }
    }
}