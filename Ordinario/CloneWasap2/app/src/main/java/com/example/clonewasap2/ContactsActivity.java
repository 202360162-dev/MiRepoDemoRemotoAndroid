package com.example.clonewasap2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clonewasap2.adapters.UserAdapter;
import com.example.clonewasap2.api.SupabaseClient;
import com.example.clonewasap2.models.User;
import com.example.clonewasap2.utils.SessionManager;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class ContactsActivity extends AppCompatActivity {

    private RecyclerView rvContacts;
    private UserAdapter adapter;
    private List<User> userList;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        session = new SessionManager(this);

        Toolbar toolbar = findViewById(R.id.toolbarContacts);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rvContacts = findViewById(R.id.rvContacts);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        adapter = new UserAdapter(userList, user -> {
            Intent intent = new Intent(ContactsActivity.this, ChatActivity.class);
            intent.putExtra("CONTACT_ID", user.getId());
            intent.putExtra("CONTACT_NAME", user.getName());
            startActivity(intent);
            finish();
        });
        rvContacts.setAdapter(adapter);

        fetchRealContacts();
    }

    private void fetchRealContacts() {
        Request request = new Request.Builder()
                .url(SupabaseClient.SUPABASE_URL + "/rest/v1/profiles?select=*")
                .addHeader("apikey", SupabaseClient.SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + session.getAuthToken())
                .get()
                .build();

        SupabaseClient.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(ContactsActivity.this, "Error de red al cargar contactos", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONArray array = new JSONArray(response.body().string());
                        userList.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String id = obj.getString("id");
                            String name = obj.optString("name", "Usuario sin nombre");

                            if (!id.equals(session.getUserId())) {
                                userList.add(new User(id, name, null));
                            }
                        }
                        runOnUiThread(() -> adapter.notifyDataSetChanged());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}