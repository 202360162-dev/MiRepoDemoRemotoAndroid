package com.example.clonewhatsapp.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clonewhatsapp.R;
import com.example.clonewhatsapp.adapters.MensajeAdapter;
import com.example.clonewhatsapp.config.SupabaseConfig;
import com.example.clonewhatsapp.models.Mensaje;
import com.example.clonewhatsapp.realtime.SupabaseRealtimeClient;
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

public class ChatActivity extends AppCompatActivity {
    private RecyclerView rvChatMensajes;
    private EditText etChatInput;
    private Button btnChatEnviar;
    private TextView tvChatNombre;

    private MensajeAdapter adapter;
    private List<Mensaje> listaMensajes = new ArrayList<>();
    private OkHttpClient client = new OkHttpClient();
    private SessionManager sessionManager;
    private SupabaseRealtimeClient realtimeClient;

    private String miUserId;
    private String conversacionId; // Ahora se declara sin el valor quemado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sessionManager = new SessionManager(this);
        rvChatMensajes = findViewById(R.id.rvChatMensajes);
        etChatInput = findViewById(R.id.etChatInput);
        btnChatEnviar = findViewById(R.id.btnChatEnviar);
        tvChatNombre = findViewById(R.id.tvChatNombre);

        // Recibimos los datos reales pasados desde MainActivity o BuscarContactosActivity
        tvChatNombre.setText(getIntent().getStringExtra("nombreContacto"));
        conversacionId = getIntent().getStringExtra("conversacionId");

        obtenerMiUserId();

        // Fase 7: Tiempo Real
        realtimeClient = new SupabaseRealtimeClient(() -> runOnUiThread(this::cargarHistorialMensajes));
        realtimeClient.conectar();

        btnChatEnviar.setOnClickListener(v -> enviarMensaje());
    }

    private void obtenerMiUserId() {
        client.newCall(new Request.Builder().url(SupabaseConfig.URL + "/auth/v1/user")
                        .addHeader("apikey", SupabaseConfig.KEY).addHeader("Authorization", "Bearer " + sessionManager.fetchAuthToken()).build())
                .enqueue(new Callback() {
                    @Override public void onFailure(Call call, IOException e) {}
                    @Override public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                miUserId = new JSONObject(response.body().string()).getString("id");
                                runOnUiThread(() -> {
                                    LinearLayoutManager lm = new LinearLayoutManager(ChatActivity.this);
                                    lm.setStackFromEnd(true);
                                    rvChatMensajes.setLayoutManager(lm);
                                    adapter = new MensajeAdapter(listaMensajes, miUserId);
                                    rvChatMensajes.setAdapter(adapter);
                                    cargarHistorialMensajes(); // Fase 6: Cargar historial
                                });
                            } catch (Exception ignored) {}
                        }
                    }
                });
    }

    // Fase 6: Insertar mensajes
    private void enviarMensaje() {
        String texto = etChatInput.getText().toString().trim();
        if (texto.isEmpty() || miUserId == null || conversacionId == null) return;
        etChatInput.setText("");

        JSONObject json = new JSONObject();
        try {
            json.put("conversacion_id", conversacionId);
            json.put("sender_id", miUserId);
            json.put("content", texto);
            json.put("estado", "enviado"); // Fase 8: Estado inicial
        } catch (Exception e) { return; }

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json"));
        Request request = new Request.Builder().url(SupabaseConfig.URL + "/rest/v1/messages")
                .addHeader("apikey", SupabaseConfig.KEY).addHeader("Authorization", "Bearer " + sessionManager.fetchAuthToken())
                .post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {}
            @Override public void onResponse(Call call, Response response) {}
        });
    }

    // Fase 6: Consultar historial
    private void cargarHistorialMensajes() {
        if (miUserId == null || conversacionId == null) return;
        Request request = new Request.Builder().url(SupabaseConfig.URL + "/rest/v1/messages?conversacion_id=eq." + conversacionId + "&order=created_at.asc")
                .addHeader("apikey", SupabaseConfig.KEY).addHeader("Authorization", "Bearer " + sessionManager.fetchAuthToken()).build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {}
            @Override public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONArray arr = new JSONArray(response.body().string());
                        listaMensajes.clear();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            listaMensajes.add(new Mensaje(obj.getString("id"), obj.getString("sender_id"), obj.getString("content"), obj.optString("estado", "enviado")));
                        }
                        runOnUiThread(() -> {
                            adapter.notifyDataSetChanged();
                            if(listaMensajes.size() > 0) rvChatMensajes.scrollToPosition(listaMensajes.size() - 1);
                        });
                    } catch (Exception ignored) {}
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realtimeClient != null) realtimeClient.desconectar();
    }
}