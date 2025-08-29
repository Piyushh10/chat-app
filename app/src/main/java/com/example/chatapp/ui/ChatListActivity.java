package com.example.chatapp.ui;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.chatapp.R;
import com.example.chatapp.model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import com.example.chatapp.model.User;
import com.example.chatapp.ui.UserListAdapter;

public class ChatListActivity extends AppCompatActivity {

    private final List<User> users = new ArrayList<>();
    private RecyclerView recyclerView;
    private UserListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        recyclerView = findViewById(R.id.recyclerChats);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserListAdapter(users, this::onUserClicked);
        recyclerView.setAdapter(adapter);



        // Logout button
        findViewById(R.id.btnLogout).setOnClickListener(v -> logout());

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
        
        // Listen to all users in the system
        FirebaseFirestore.getInstance()
                .collection("users")
                .addSnapshotListener((snap, e) -> {
                    if (snap == null) return;
                    users.clear();
                    for (var doc : snap.getDocuments()) {
                        User user = doc.toObject(User.class);
                        if (user != null && !user.getId().equals(currentUserId)) {
                            // Don't show current user in the list
                            users.add(user);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    
                    // Fetch last messages for all users
                    for (User user : users) {
                        fetchLastMessageForUser(user);
                    }
                });
    }



    private void onUserClicked(User user) {
        // Start a chat with the selected user
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null ? 
                FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
        
        if (!currentUserId.isEmpty()) {
            // Check if chat already exists
            FirebaseFirestore.getInstance()
                    .collection("chats")
                    .whereArrayContains("memberIds", currentUserId)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        boolean chatExists = false;
                        String existingChatId = "";
                        
                        for (var doc : querySnapshot.getDocuments()) {
                            @SuppressWarnings("unchecked")
                            List<String> memberIds = (List<String>) doc.get("memberIds");
                            if (memberIds != null && memberIds.contains(user.getId()) && memberIds.size() == 2) {
                                chatExists = true;
                                existingChatId = doc.getId();
                                break;
                            }
                        }
                        
                        if (chatExists) {
                            // Open existing chat
                            openChat(existingChatId);
                        } else {
                            // Create new chat
                            createChatWithUser(currentUserId, user.getId());
                        }
                    });
        }
    }

    private void createChatWithUser(String currentUserId, String otherUserId) {
        java.util.Map<String, Object> chatData = new java.util.HashMap<>();
        chatData.put("memberIds", java.util.Arrays.asList(currentUserId, otherUserId));
        chatData.put("lastMessage", "Chat started");
        chatData.put("updatedAt", System.currentTimeMillis());
        
        FirebaseFirestore.getInstance()
                .collection("chats")
                .add(chatData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Chat started with " + otherUserId, Toast.LENGTH_SHORT).show();
                    openChat(documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to create chat", Toast.LENGTH_SHORT).show();
                });
    }

    private void openChat(String chatId) {
        // Navigate to ChatActivity
        android.content.Intent intent = new android.content.Intent(this, ChatActivity.class);
        intent.putExtra("chatId", chatId);
        startActivity(intent);
    }

    private void logout() {
        // Sign out from Firebase
        FirebaseAuth.getInstance().signOut();
        
        // Sign out from Google
        com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(this, 
            com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN)
            .signOut()
                            .addOnCompleteListener(task -> {
                    // Navigate back to MainActivity
                    android.content.Intent intent = new android.content.Intent(this, com.example.chatapp.MainActivity.class);
                    intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
    }
    
    private void fetchLastMessageForUser(User user) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null ? 
                FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
        
        if (!currentUserId.isEmpty()) {
            // Find chat between current user and this user
            FirebaseFirestore.getInstance()
                    .collection("chats")
                    .whereArrayContains("memberIds", currentUserId)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        for (var doc : querySnapshot.getDocuments()) {
                            @SuppressWarnings("unchecked")
                            List<String> memberIds = (List<String>) doc.get("memberIds");
                            if (memberIds != null && memberIds.contains(user.getId()) && memberIds.size() == 2) {
                                // Found the chat, get last message
                                String lastMessage = doc.getString("lastMessage");
                                Long updatedAt = doc.getLong("updatedAt");
                                
                                // Update the adapter with this information
                                updateUserLastMessage(user.getId(), lastMessage, updatedAt);
                                break;
                            }
                        }
                    });
        }
    }
    
    private void updateUserLastMessage(String userId, String lastMessage, Long timestamp) {
        // Find the user in the list and update their last message info
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(userId)) {
                // Update the user's last message data
                users.get(i).setLastMessage(lastMessage != null ? lastMessage : "No messages yet");
                users.get(i).setLastMessageTime(timestamp != null ? timestamp : 0L);
                
                // Notify adapter of the change
                adapter.notifyItemChanged(i);
                break;
            }
        }
    }
}


