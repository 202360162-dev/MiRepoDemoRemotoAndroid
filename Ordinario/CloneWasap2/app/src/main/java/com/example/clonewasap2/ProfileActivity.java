package com.example.clonewasap2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.clonewasap2.api.SupabaseClient;
import com.example.clonewasap2.utils.SessionManager;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileActivity extends AppCompatActivity {

    private EditText etProfileName;
    private Button btnSaveProfile;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etProfileName = findViewById(R.id.etProfileName);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        session = new SessionManager(this);

        loadProfile();

        btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private void loadProfile() {
        Request request = new Request.Builder()
                .url(SupabaseClient.SUPABASE_URL + "/rest/v1/profiles?id=eq." + session.getUserId())
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
                        if (array.length() > 0) {
                            JSONObject profile = array.getJSONObject(0);
                            String name = profile.optString("name", "");
                            runOnUiThread(() -> etProfileName.setText(name));
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                }
            }
        });
    }

    private void saveProfile() {
        String newName = etProfileName.getText().toString().trim();
        if (newName.isEmpty()) return;

        String jsonPayload = "{\"name\":\"" + newName + "\"}";
        RequestBody body = RequestBody.create(jsonPayload, SupabaseClient.JSON);

        Request request = new Request.Builder()
                .url(SupabaseClient.SUPABASE_URL + "/rest/v1/profiles?id=eq." + session.getUserId())
                .addHeader("apikey", SupabaseClient.SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + session.getAuthToken())
                .patch(body)
                .build();

        SupabaseClient.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "Error de red", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(ProfileActivity.this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            }
        });
    }
}