package com.example.clonewhatsapp.realtime;

import android.os.Handler;
import android.os.Looper;
import com.example.clonewhatsapp.config.SupabaseConfig;
import org.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class SupabaseRealtimeClient {
    private WebSocket webSocket;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final RealtimeListener listener;

    public interface RealtimeListener {
        void onNewMessage();
    }

    public SupabaseRealtimeClient(RealtimeListener listener) {
        this.listener = listener;
    }

    public void conectar() {
        OkHttpClient client = new OkHttpClient();
        String wsUrl = SupabaseConfig.URL.replace("https://", "wss://") + "/realtime/v1/websocket?apikey=" + SupabaseConfig.KEY + "&vsn=1.0.0";

        Request request = new Request.Builder().url(wsUrl).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, okhttp3.Response response) {
                // Suscribirse a la tabla messages
                String payload = "{\"topic\":\"realtime:public:messages\",\"event\":\"phx_join\",\"payload\":{\"config\":{\"postgres_changes\":[{\"event\":\"INSERT\",\"schema\":\"public\",\"table\":\"messages\"}]}},\"ref\":\"1\"}";
                webSocket.send(payload);
                enviarHeartbeat(); // Mantener conexión viva
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                try {
                    JSONObject json = new JSONObject(text);
                    if (json.optString("event").equals("INSERT")) {
                        mainHandler.post(listener::onNewMessage);
                    }
                } catch (Exception ignored) {}
            }
        });
    }

    private void enviarHeartbeat() {
        mainHandler.postDelayed(() -> {
            if (webSocket != null) {
                webSocket.send("{\"topic\":\"phoenix\",\"event\":\"heartbeat\",\"payload\":{},\"ref\":\"2\"}");
                enviarHeartbeat();
            }
        }, 30000); // Cada 30 segundos
    }

    public void desconectar() {
        if (webSocket != null) {
            webSocket.close(1000, "User exit");
            webSocket = null;
        }
    }
}