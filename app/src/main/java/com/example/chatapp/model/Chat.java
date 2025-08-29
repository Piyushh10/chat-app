package com.example.chatapp.model;

import java.util.List;

public class Chat {
    private String id;
    private List<String> memberIds;
    private String lastMessage;
    private long updatedAt;
    private String otherUserName;
    private String otherUserPhotoUrl;

    public Chat() {}

    public Chat(String id, List<String> memberIds, String lastMessage, long updatedAt) {
        this.id = id;
        this.memberIds = memberIds;
        this.lastMessage = lastMessage;
        this.updatedAt = updatedAt;
    }

    public Chat(String id, List<String> memberIds, String lastMessage, long updatedAt, String otherUserName, String otherUserPhotoUrl) {
        this.id = id;
        this.memberIds = memberIds;
        this.lastMessage = lastMessage;
        this.updatedAt = updatedAt;
        this.otherUserName = otherUserName;
        this.otherUserPhotoUrl = otherUserPhotoUrl;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public List<String> getMemberIds() { return memberIds; }
    public void setMemberIds(List<String> memberIds) { this.memberIds = memberIds; }
    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
    public String getOtherUserName() { return otherUserName; }
    public void setOtherUserName(String otherUserName) { this.otherUserName = otherUserName; }
    public String getOtherUserPhotoUrl() { return otherUserPhotoUrl; }
    public void setOtherUserPhotoUrl(String otherUserPhotoUrl) { this.otherUserPhotoUrl = otherUserPhotoUrl; }
}


