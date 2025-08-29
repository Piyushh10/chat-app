package com.example.chatapp.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.model.Chat;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {
    private final List<Chat> items;

    public ChatListAdapter(List<Chat> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = items.get(position);
        String displayName = chat.getOtherUserName() != null ? chat.getOtherUserName() : "Chat " + chat.getId();
        holder.txtName.setText(displayName);
        holder.txtLast.setText(chat.getLastMessage());
        holder.itemView.setOnClickListener(v -> openChat(v.getContext(), chat));
    }

    private void openChat(Context context, Chat chat) {
        Intent i = new Intent(context, ChatActivity.class);
        i.putExtra("chatId", chat.getId());
        context.startActivity(i);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        TextView txtLast;
        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtLast = itemView.findViewById(R.id.txtLast);
        }
    }
}


