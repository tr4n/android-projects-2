package com.example.buinam.mapchatdemo.model;

/**
 * Created by buinam on 11/25/16.
 */

public class RecentGroupChat {
    String idRoomGroupChat;
    String urlAvatarGroupChat;
    String nameGroupChat;
    String sizeGroupChat;

    public RecentGroupChat() {
    }

    public String getSizeGroupChat() {
        return sizeGroupChat;
    }

    public void setSizeGroupChat(String sizeGroupChat) {
        this.sizeGroupChat = sizeGroupChat;
    }


    public String getIdRoomGroupChat() {
        return idRoomGroupChat;
    }

    public void setIdRoomGroupChat(String idRoomGroupChat) {
        this.idRoomGroupChat = idRoomGroupChat;
    }

    public String getNameGroupChat() {
        return nameGroupChat;
    }

    public void setNameGroupChat(String nameGroupChat) {
        this.nameGroupChat = nameGroupChat;
    }


    public String getUrlAvatarGroupChat() {
        return urlAvatarGroupChat;
    }

    public void setUrlAvatarGroupChat(String urlAvatarGroupChat) {
        this.urlAvatarGroupChat = urlAvatarGroupChat;
    }
}
