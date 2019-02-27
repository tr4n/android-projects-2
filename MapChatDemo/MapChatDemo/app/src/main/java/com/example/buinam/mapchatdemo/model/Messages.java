package com.example.buinam.mapchatdemo.model;

/**
 * Created by buinam on 11/8/16.
 */

public class Messages {

    String message;
    String timeChatMessage;
    String urlImageChat;
    String idUserSend;
    String idUserReceive;
    String urlAvarChat;
    String widthImage;
    String heightImage;
    String displayNameChat;
    String status;

    public Messages() {

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHeightImage() {
        return heightImage;
    }

    public void setHeightImage(String heightImage) {
        this.heightImage = heightImage;
    }

    public String getWidthImage() {
        return widthImage;
    }

    public void setWidthImage(String widthImage) {
        this.widthImage = widthImage;
    }

    public String getUrlAvarChat() {
        return urlAvarChat;
    }

    public String getDisplayNameChat() {
        return displayNameChat;
    }

    public void setDisplayNameChat(String displayNameChat) {
        this.displayNameChat = displayNameChat;
    }

    public void setUrlAvarChat(String urlAvarChat) {
        this.urlAvarChat = urlAvarChat;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimeChatMessage() {
        return timeChatMessage;
    }

    public void setTimeChatMessage(String timeChatMessage) {
        this.timeChatMessage = timeChatMessage;
    }

    public String getUrlImageChat() {
        return urlImageChat;
    }

    public void setUrlImageChat(String urlImageChat) {
        this.urlImageChat = urlImageChat;
    }

    public String getIdUserReceive() {
        return idUserReceive;
    }

    public void setIdUserReceive(String idUserReceive) {
        this.idUserReceive = idUserReceive;
    }

    public String getIdUserSend() {
        return idUserSend;
    }

    public void setIdUserSend(String idUserSend) {
        this.idUserSend = idUserSend;
    }
}
