package com.example.chatapp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.material.imageview.ShapeableImageView;
import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.model.User;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {
    private final List<User> items;
    private final OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    public UserListAdapter(List<User> items, OnUserClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

            @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            User user = items.get(position);
            holder.txtName.setText(user.getName() != null ? user.getName() : "Unknown User");

            // Load profile image
            if (user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
                Glide.with(holder.imgProfile.getContext())
                        .load(user.getPhotoUrl())
                        .placeholder(android.R.drawable.ic_menu_myplaces)
                        .error(android.R.drawable.ic_menu_myplaces)
                        .circleCrop()
                        .into(holder.imgProfile);
            } else {
                holder.imgProfile.setImageResource(android.R.drawable.ic_menu_myplaces);
            }

            // Show online status with just the dot
            View onlineIndicator = holder.itemView.findViewById(R.id.onlineIndicator);
            if (onlineIndicator != null) {
                if (user.isOnline()) {
                    onlineIndicator.setBackgroundResource(R.drawable.online_dot);
                } else {
                    onlineIndicator.setBackgroundResource(R.drawable.offline_dot);
                }
            }

            // Set last message and time
            TextView txtLastMessage = holder.itemView.findViewById(R.id.txtLastMessage);
            TextView txtLastMessageTime = holder.itemView.findViewById(R.id.txtLastMessageTime);
            
            if (txtLastMessage != null) {
                String lastMsg = user.getLastMessage();
                if (lastMsg != null && !lastMsg.isEmpty()) {
                    txtLastMessage.setText(lastMsg);
                } else {
                    txtLastMessage.setText("Tap to start chatting...");
                }
            }
            
            if (txtLastMessageTime != null) {
                long timestamp = user.getLastMessageTime();
                if (timestamp > 0) {
                    String timeText = formatTime(timestamp);
                    txtLastMessageTime.setText(timeText);
                } else {
                    txtLastMessageTime.setText("--");
                }
            }

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUserClick(user);
                }
            });
        }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        ShapeableImageView imgProfile;
        
        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtUserName);
            imgProfile = itemView.findViewById(R.id.imgUserProfile);
        }
    }
    
    private String formatTime(long timestamp) {
        long currentTime = System.currentTimeMillis();
        long diff = currentTime - timestamp;
        
        // Convert to seconds
        long seconds = diff / 1000;
        
        if (seconds < 60) {
            return "now";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return minutes + "m";
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            return hours + "h";
        } else {
            long days = seconds / 86400;
            if (days < 7) {
                return days + "d";
            } else {
                // Show date for older messages
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault());
                return sdf.format(new java.util.Date(timestamp));
            }
        }
    }
}
