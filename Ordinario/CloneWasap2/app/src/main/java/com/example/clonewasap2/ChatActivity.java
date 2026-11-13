package com.example.clonewasap2;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clonewasap2.adapters.MessageAdapter;
import com.example.clonewasap2.api.SupabaseClient;
import com.example.clonewasap2.models.Message;
import com.example.clonewasap2.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvMessages;
    private EditText etMessageInput;
    private FloatingActionButton fabSend;
    private MessageAdapter adapter;
    private List<Message> messageList;
    private SessionManager session;
    private String contactId;
    private String conversationId = null;

    // Variables para el Polling (recarga automática)
    private Handler pollingHandler;
    private Runnable pollingRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        session = new SessionManager(this);
        contactId = getIntent().getStringExtra("CONTACT_ID");
        String contactName = getIntent().getStringExtra("CONTACT_NAME");

        Toolbar toolbar = findViewById(R.id.toolbarChat);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(contactName != null ? contactName : "Chat");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rvMessages = findViewById(R.id.rvMessages);
        etMessageInput = findViewById(R.id.etMessageInput);
        fabSend = findViewById(R.id.fabSend);

        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList, session.getUserId());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setAdapter(adapter);

        fabSend.setOnClickListener(v -> sendMessage());

        initConversationId();
    }

    private void initConversationId() {
        String jsonPayload = "{\"other_user_id\":\"" + contactId + "\"}";
        RequestBody body = RequestBody.create(jsonPayload, SupabaseClient.JSON);

        Request request = new Request.Builder()
                .url(SupabaseClient.SUPABASE_URL + "/rest/v1/rpc/get_or_create_conversation")
                .addHeader("apikey", SupabaseClient.SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + session.getAuthToken())
                .post(body)
                .build();

        SupabaseClient.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String respString = response.body().string();
                        // Limpiamos las comillas del ID recibido
                        conversationId = respString.replace("\"", "");

                        // Iniciamos la recarga automática de mensajes
                        runOnUiThread(() -> startPollingMessages());
                    } catch (Exception e) { e.printStackTrace(); }
                }
            }
        });
    }

    private void startPollingMessages() {
        // Asegurarnos de que no haya otro polling corriendo
        stopPolling();

        pollingHandler = new Handler(Looper.getMainLooper());
        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                loadMessages();
                pollingHandler.postDelayed(this, 3000); // Se recarga cada 3 segundos
            }
        };
        pollingHandler.post(pollingRunnable);
    }

    // Detiene el ciclo de recarga de mensajes
    private void stopPolling() {
        if (pollingHandler != null && pollingRunnable != null) {
            pollingHandler.removeCallbacks(pollingRunnable);
        }
    }

    private void loadMessages() {
        if (conversationId == null) return;

        Request request = new Request.Builder()
                .url(SupabaseClient.SUPABASE_URL + "/rest/v1/messages?conversation_id=eq." + conversationId + "&order=created_at.asc")
                .addHeader("apikey", SupabaseClient.SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + session.getAuthToken())
                .get()
                .build();

        SupabaseClient.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONArray array = new JSONArray(response.body().string());
                        List<Message> newMessages = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            newMessages.add(new Message(
                                    obj.getString("id"),
                                    obj.getString("conversation_id"),
                                    obj.getString("sender_id"),
                                    obj.getString("content"),
                                    obj.optString("status", "enviado")
                            ));
                        }

                        runOnUiThread(() -> {
                            int oldSize = messageList.size();
                            messageList.clear();
                            messageList.addAll(newMessages);
                            adapter.notifyDataSetChanged();

                            if (messageList.size() > oldSize && messageList.size() > 0) {
                                rvMessages.scrollToPosition(messageList.size() - 1);
                            }
                        });
                    } catch (Exception e) { e.printStackTrace(); }
                }
            }
        });
    }

    private void sendMessage() {
        String text = etMessageInput.getText().toString().trim();
        if (text.isEmpty() || conversationId == null) return;

        // Limpiar caja de texto inmediatamente por fluidez visual
        etMessageInput.setText("");

        String cleanText = text.replace("\"", "\\\"");
        String jsonPayload = "{\"conversation_id\":\"" + conversationId + "\", \"sender_id\":\"" + session.getUserId() + "\", \"content\":\"" + cleanText + "\"}";
        RequestBody body = RequestBody.create(jsonPayload, SupabaseClient.JSON);

        Request request = new Request.Builder()
                .url(SupabaseClient.SUPABASE_URL + "/rest/v1/messages")
                .addHeader("apikey", SupabaseClient.SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + session.getAuthToken())
                .post(body)
                .build();

        SupabaseClient.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Forzar carga de mensaje para que aparezca al instante
                    runOnUiThread(() -> loadMessages());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (conversationId != null) {
            startPollingMessages();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPolling();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPolling();
    }
}