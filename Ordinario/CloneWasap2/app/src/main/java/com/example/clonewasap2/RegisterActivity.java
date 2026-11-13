package com.example.clonewasap2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.clonewasap2.api.SupabaseClient;
import com.example.clonewasap2.utils.SessionManager; // Importación corregida
import org.json.JSONObject; // Importación corregida
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {
    private EditText etName, etEmailReg, etPasswordReg;
    private Button btnRegisterAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etEmailReg = findViewById(R.id.etEmailReg);
        etPasswordReg = findViewById(R.id.etPasswordReg);
        btnRegisterAction = findViewById(R.id.btnRegisterAction);

        btnRegisterAction.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmailReg.getText().toString().trim();
            String password = etPasswordReg.getText().toString().trim();

            // Validar que no haya campos vacíos
            if(!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                registerUser(name, email, password);
            } else {
                Toast.makeText(RegisterActivity.this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerUser(String name, String email, String password) {
        String jsonPayload = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
        RequestBody body = RequestBody.create(jsonPayload, SupabaseClient.JSON);

        Request request = new Request.Builder()
                .url(SupabaseClient.SUPABASE_URL + "/auth/v1/signup")
                .post(body)
                .build();

        SupabaseClient.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Error de red", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String respString = response.body().string();
                        JSONObject json = new JSONObject(respString);

                        if (json.has("access_token")) {
                            String token = json.getString("access_token");
                            String userId = json.getJSONObject("user").getString("id");

                            SessionManager session = new SessionManager(RegisterActivity.this);
                            session.saveAuthToken(token, userId);

                            // En lugar de ir a MainActivity directo, actualizamos su nombre real primero
                            updateProfileName(token, userId, name);

                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(RegisterActivity.this, "Cuenta creada. Por favor inicia sesión.", Toast.LENGTH_LONG).show();
                                finish();
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Fallo al registrar. Revisa el correo/contraseña.", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    // Función nueva para guardar el nombre en la tabla 'profiles'
    private void updateProfileName(String token, String userId, String name) {
        String jsonPayload = "{\"name\":\"" + name + "\"}";
        RequestBody body = RequestBody.create(jsonPayload, SupabaseClient.JSON);

        Request request = new Request.Builder()
                .url(SupabaseClient.SUPABASE_URL + "/rest/v1/profiles?id=eq." + userId)
                .addHeader("apikey", SupabaseClient.SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + token)
                .patch(body)
                .build();

        SupabaseClient.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Si falla por red, de todas formas lo mandamos al inicio
                goToMain();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                goToMain();
            }
        });
    }

    private void goToMain() {
        runOnUiThread(() -> {
            Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        });
    }
}