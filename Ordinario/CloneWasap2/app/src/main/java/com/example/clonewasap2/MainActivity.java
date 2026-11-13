package com.example.clonewasap2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clonewasap2.adapters.ConversationAdapter;
import com.example.clonewasap2.models.Conversation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.messaging.FirebaseMessaging;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;
import com.example.clonewasap2.api.SupabaseClient;
import com.example.clonewasap2.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvConversations;
    private FloatingActionButton fabNewChat;
    private ConversationAdapter adapter;
    private List<Conversation> conversationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        rvConversations = findViewById(R.id.rvConversations);
        fabNewChat = findViewById(R.id.fabNewChat);

        conversationList = new ArrayList<>();
        // Aquí iría el Call HTTP a Supabase a "rest/v1/conversaciones"
        // Simulando datos para que la UI funcione al probar:
        conversationList.add(new Conversation("1", false, "Juan Pérez", "Hola, ¿cómo estás?"));

        adapter = new ConversationAdapter(conversationList, conversation -> {
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            intent.putExtra("CONVERSATION_ID", conversation.getId());
            intent.putExtra("CONTACT_NAME", conversation.getName());
            startActivity(intent);
        });

        rvConversations.setLayoutManager(new LinearLayoutManager(this));
        rvConversations.setAdapter(adapter);

        fabNewChat.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ContactsActivity.class));
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Perfil");
        menu.add(0, 2, 0, "Cerrar Sesión");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (item.getItemId() == 2) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}