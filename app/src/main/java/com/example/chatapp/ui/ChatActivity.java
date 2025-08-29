package com.example.chatapp.ui;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private final List<Message> messages = new ArrayList<>();
    private MessagesAdapter adapter;
    private String chatId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatId = getIntent().getStringExtra("chatId");

        // Setup header
        setupHeader();
        
        // Setup back button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.recyclerMessages);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessagesAdapter(messages);
        rv.setAdapter(adapter);

        EditText input = findViewById(R.id.editMessage);
        ImageButton send = findViewById(R.id.btnSend);
        send.setOnClickListener(v -> sendMessage(input.getText().toString().trim(), input));

        FirebaseFirestore.getInstance()
                .collection("chats").document(chatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snap, e) -> {
                    if (snap == null) return;
                    messages.clear();
                    for (var doc : snap.getDocuments()) {
                        Message m = doc.toObject(Message.class);
                        if (m != null) messages.add(m);
                    }
                    adapter.notifyDataSetChanged();
                    rv.scrollToPosition(Math.max(messages.size() - 1, 0));
                });
    }

    private void sendMessage(String text, EditText input) {
        if (text.isEmpty()) return;
        String uid = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
        long now = System.currentTimeMillis();
        Map<String, Object> data = new HashMap<>();
        data.put("senderId", uid);
        data.put("text", text);
        data.put("timestamp", now);
        data.put("chatId", chatId);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("chats").document(chatId)
                .collection("messages")
                .add(data)
                .addOnSuccessListener(ref -> {
                    db.collection("chats").document(chatId)
                            .update("lastMessage", text, "updatedAt", now);
                    input.setText("");
                });
    }

    private void setupHeader() {
        // Get the other user's information for the header
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null ? 
                FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
        
        if (!currentUserId.isEmpty()) {
            FirebaseFirestore.getInstance()
                    .collection("chats").document(chatId)
                    .get()
                    .addOnSuccessListener(chatDoc -> {
                        if (chatDoc.exists()) {
                            List<String> memberIds = (List<String>) chatDoc.get("memberIds");
                            if (memberIds != null) {
                                String otherUserId = memberIds.stream()
                                        .filter(id -> !id.equals(currentUserId))
                                        .findFirst()
                                        .orElse("");
                                
                                if (!otherUserId.isEmpty()) {
                                    FirebaseFirestore.getInstance()
                                            .collection("users").document(otherUserId)
                                            .get()
                                            .addOnSuccessListener(userDoc -> {
                                                if (userDoc.exists()) {
                                                    String userName = userDoc.getString("name");
                                                    String userPhotoUrl = userDoc.getString("photoUrl");
                                                    
                                                    // Update header
                                                    TextView txtChatName = findViewById(R.id.txtChatName);
                                                    if (txtChatName != null && userName != null) {
                                                        txtChatName.setText(userName);
                                                    }
                                                    
                                                    // Load profile picture
                                                    com.google.android.material.imageview.ShapeableImageView imgProfile = findViewById(R.id.imgChatProfile);
                                                    if (imgProfile != null && userPhotoUrl != null && !userPhotoUrl.isEmpty()) {
                                                        com.bumptech.glide.Glide.with(this)
                                                                .load(userPhotoUrl)
                                                                .placeholder(android.R.drawable.ic_menu_myplaces)
                                                                .error(android.R.drawable.ic_menu_myplaces)
                                                                .circleCrop()
                                                                .into(imgProfile);
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    });
        }
    }
}


