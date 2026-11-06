package com.example.clonewhatsapp.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.clonewhatsapp.R;
import com.example.clonewhatsapp.config.SupabaseConfig;
import com.example.clonewhatsapp.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PerfilActivity extends AppCompatActivity {
    private TextInputEditText etNombre, etFotoUrl;
    private ImageView ivMiFoto;
    private final OkHttpClient client = new OkHttpClient();
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        sessionManager = new SessionManager(this);
        etNombre = findViewById(R.id.etNombrePerfil);
        etFotoUrl = findViewById(R.id.etFotoUrl);
        ivMiFoto = findViewById(R.id.ivMiFoto);
        Button btnGuardar = findViewById(R.id.btnGuardarPerfil);

        // AQUÍ ESTÁ LA MAGIA DE LA PREVISUALIZACIÓN DENTRO DEL CÍRCULO
        etFotoUrl.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String url = s.toString();
                if (!url.isEmpty()) {
                    Glide.with(PerfilActivity.this).load(url).circleCrop().into(ivMiFoto);
                } else {
                    ivMiFoto.setImageResource(android.R.drawable.ic_menu_camera);
                }
            }
        });

        cargarDatosActuales();
        btnGuardar.setOnClickListener(v -> obtenerUsuarioYActualizar());
    }

    private void cargarDatosActuales() {
        String token = sessionManager.fetchAuthToken();
        Request request = new Request.Builder().url(SupabaseConfig.URL + "/auth/v1/user")
                .addHeader("apikey", SupabaseConfig.KEY).addHeader("Authorization", "Bearer " + token).build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {}
            @Override public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String userId = new JSONObject(response.body().string()).getString("id");
                        Request profileReq = new Request.Builder().url(SupabaseConfig.URL + "/rest/v1/profiles?id=eq." + userId)
                                .addHeader("apikey", SupabaseConfig.KEY).addHeader("Authorization", "Bearer " + token).build();

                        client.newCall(profileReq).enqueue(new Callback() {
                            @Override public void onFailure(@NonNull Call c, @NonNull IOException e) {}
                            @Override public void onResponse(@NonNull Call c, @NonNull Response r) throws IOException {
                                if (r.isSuccessful() && r.body() != null) {
                                    try {
                                        JSONArray arr = new JSONArray(r.body().string());
                                        if (arr.length() > 0) {
                                            JSONObject perfil = arr.getJSONObject(0);
                                            String nombre = perfil.optString("nombre", "");
                                            String fotoUrl = perfil.optString("foto_url", "");
                                            runOnUiThread(() -> {
                                                etNombre.setText(nombre);
                                                etFotoUrl.setText(fotoUrl);
                                                // El TextWatcher se encargará de cargar la imagen en cuanto seteemos el texto
                                            });
                                        }
                                    } catch (Exception ignored) {}
                                }
                            }
                        });
                    } catch (Exception ignored) {}
                }
            }
        });
    }

    private void obtenerUsuarioYActualizar() {
        String token = sessionManager.fetchAuthToken();
        Request request = new Request.Builder().url(SupabaseConfig.URL + "/auth/v1/user")
                .addHeader("apikey", SupabaseConfig.KEY).addHeader("Authorization", "Bearer " + token).build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {}
            @Override public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String userId = new JSONObject(response.body().string()).getString("id");
                        actualizarTablaProfiles(userId);
                    } catch (Exception ignored) {}
                }
            }
        });
    }

    private void actualizarTablaProfiles(String userId) {
        String nombre = etNombre.getText() != null ? etNombre.getText().toString() : "";
        String foto = etFotoUrl.getText() != null ? etFotoUrl.getText().toString() : "";
        JSONObject json = new JSONObject();
        try {
            json.put("nombre", nombre);
            json.put("foto_url", foto);
        } catch (Exception ignored) {}

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json"));
        Request request = new Request.Builder().url(SupabaseConfig.URL + "/rest/v1/profiles?id=eq." + userId)
                .addHeader("apikey", SupabaseConfig.KEY).addHeader("Authorization", "Bearer " + sessionManager.fetchAuthToken())
                .addHeader("Prefer", "return=minimal").patch(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {}
            @Override public void onResponse(@NonNull Call call, @NonNull Response response) {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(PerfilActivity.this, "¡Perfil Forjado!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(PerfilActivity.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}