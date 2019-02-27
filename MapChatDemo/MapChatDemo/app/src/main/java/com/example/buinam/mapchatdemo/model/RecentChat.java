package com.example.buinam.mapchatdemo.model;

/**
 * Created by buinam on 11/11/16.
 */

public class RecentChat {
    String idRoomChat;
    String lastMessage;
    String timeLastMessage;
    String urlAvatarRecentChat;
    String idUserRecentChat;
    String displayNameRecentChat;

    public RecentChat() {
    }

    public String getDisplayNameRecentChat() {
        return displayNameRecentChat;
    }

    public void setDisplayNameRecentChat(String displayNameRecentChat) {
        this.displayNameRecentChat = displayNameRecentChat;
    }

    public String getIdRoomChat() {
        return idRoomChat;
    }

    public void setIdRoomChat(String idRoomChat) {
        this.idRoomChat = idRoomChat;
    }

    public String getIdUserRecentChat() {
        return idUserRecentChat;
    }

    public void setIdUserRecentChat(String idUserRecentChat) {
        this.idUserRecentChat = idUserRecentChat;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getTimeLastMessage() {
        return timeLastMessage;
    }

    public void setTimeLastMessage(String timeLastMessage) {
        this.timeLastMessage = timeLastMessage;
    }

    public String getUrlAvatarRecentChat() {
        return urlAvatarRecentChat;
    }

    public void setUrlAvatarRecentChat(String urlAvatarRecentChat) {
        this.urlAvatarRecentChat = urlAvatarRecentChat;
    }
}
