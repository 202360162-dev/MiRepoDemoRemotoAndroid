package com.example.clonewhatsapp.auth;

import android.os.Handler;
import android.os.Looper;
import com.example.clonewhatsapp.config.SupabaseConfig;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AutenticacionManager {
    private final OkHttpClient client;
    private final Handler mainHandler;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public interface AuthCallback {
        void onSuccess(String token);
        void onError(String error);
    }

    public AutenticacionManager() {
        client = new OkHttpClient();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public void registro(String email, String password, AuthCallback callback) {
        ejecutarAuthPost("/auth/v1/signup", email, password, callback);
    }

    public void login(String email, String password, AuthCallback callback) {
        ejecutarAuthPost("/auth/v1/token?grant_type=password", email, password, callback);
    }

    public void recuperarContrasena(String email, AuthCallback callback) {
        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
        } catch (JSONException e) {
            callback.onError("Error creando JSON");
            return;
        }

        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = buildRequest("/auth/v1/recover", body);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> callback.onError("Error de red: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mainHandler.post(() -> {
                    if (response.isSuccessful()) {
                        callback.onSuccess("Correo de recuperación enviado");
                    } else {
                        callback.onError("Error al recuperar contraseña");
                    }
                });
            }
        });
    }

    private void ejecutarAuthPost(String endpoint, String email, String password, AuthCallback callback) {
        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("password", password);
        } catch (JSONException e) {
            callback.onError("Error creando JSON");
            return;
        }

        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = buildRequest(endpoint, body);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> callback.onError("Error de red: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body() != null ? response.body().string() : "";
                mainHandler.post(() -> {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonResponse = new JSONObject(responseData);
                            String token = jsonResponse.optString("access_token", "registro_exitoso_requiere_confirmacion");
                            callback.onSuccess(token);
                        } catch (JSONException e) {
                            callback.onError("Error parseando respuesta");
                        }
                    } else {
                        // AQUÍ REVELAMOS EL ERROR EXACTO DE SUPABASE
                        try {
                            JSONObject errorJson = new JSONObject(responseData);
                            String errorMsg = errorJson.optString("error_description", errorJson.optString("msg", "Error desconocido"));
                            callback.onError("Error Supabase: " + errorMsg);
                        } catch (Exception ex) {
                            callback.onError("Error HTTP " + response.code() + ": " + responseData);
                        }
                    }
                });
            }
        });
    }

    private Request buildRequest(String endpoint, RequestBody body) {
        return new Request.Builder()
                .url(SupabaseConfig.URL + endpoint)
                .addHeader("apikey", SupabaseConfig.KEY)
                .addHeader("Authorization", "Bearer " + SupabaseConfig.KEY)
                .post(body)
                .build();
    }
}