package com.example.chatapp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.model.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {
    private final List<Message> items;

    public MessagesAdapter(List<Message> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = items.get(position);
        holder.txt.setText(message.getText());
        
        // Align message based on sender
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null ? 
                FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
        
        if (message.getSenderId().equals(currentUserId)) {
            // Current user's message - align right with blue background
            holder.messageContainer.setGravity(android.view.Gravity.END);
            holder.txt.setBackgroundResource(R.drawable.modern_message_bubble);
            holder.txt.setTextColor(android.graphics.Color.WHITE);
        } else {
            // Other user's message - align left with gray background
            holder.messageContainer.setGravity(android.view.Gravity.START);
            holder.txt.setBackgroundResource(R.drawable.other_user_message_bubble);
            holder.txt.setTextColor(android.graphics.Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView txt;
        LinearLayout messageContainer;
        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            txt = itemView.findViewById(R.id.txtMessage);
            messageContainer = itemView.findViewById(R.id.messageContainer);
        }
    }
}


