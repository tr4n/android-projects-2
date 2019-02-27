package com.example.buinam.mapchatdemo.model;

/**
 * Created by buinam on 11/7/16.
 */

public class User {
    public static String urlAvatarDefault = "https://firebasestorage.googleapis.com/v0/b/mapchat-144203.appspot.com/o/AvatarUser%2FavatarUserDefault.png?alt=media&token=4ccbb72b-ae2b-4a90-8acd-c2ddd989ab23";
    public static String urlCoverDefault = "https://firebasestorage.googleapis.com/v0/b/mapchat-144203.appspot.com/o/AvatarUser%2FcoverDefault.jpg?alt=media&token=30d18e84-80bd-4629-84fb-386f43a8ff22";
    private String idUser;
    private String urlAvatar;
    private String urlCover;
    private String displayName;
    private String userName;
    private String dayBirthdayUser;
    private String monthBirthdayUser;
    private String yearBirthdayUser;
    private String genderUser;
    private String connection;
    private String timeRegisterUser;
    private String token;
    private String timeDisconnectUser;
    private LocationUser locationUser;
    private RoomChat idRoomChat;
    private boolean isSelected;
    private RoomGroupChat idRoomGroupChat;
    private Friend checkListFriend;


    public User() {
    }

    public Friend getCheckListFriend() {
        return checkListFriend;
    }

    public void setCheckListFriend(Friend checkListFriend) {
        this.checkListFriend = checkListFriend;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public String getDayBirthdayUser() {
        return dayBirthdayUser;
    }

    public void setDayBirthdayUser(String dayBirthdayUser) {
        this.dayBirthdayUser = dayBirthdayUser;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getGenderUser() {
        return genderUser;
    }

    public void setGenderUser(String genderUser) {
        this.genderUser = genderUser;
    }

    public RoomChat getIdRoomChat() {
        return idRoomChat;
    }

    public void setIdRoomChat(RoomChat idRoomChat) {
        this.idRoomChat = idRoomChat;
    }

    public RoomGroupChat getIdRoomGroupChat() {
        return idRoomGroupChat;
    }

    public void setIdRoomGroupChat(RoomGroupChat idRoomGroupChat) {
        this.idRoomGroupChat = idRoomGroupChat;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public LocationUser getLocationUser() {
        return locationUser;
    }

    public void setLocationUser(LocationUser locationUser) {
        this.locationUser = locationUser;
    }

    public String getMonthBirthdayUser() {
        return monthBirthdayUser;
    }

    public void setMonthBirthdayUser(String monthBirthdayUser) {
        this.monthBirthdayUser = monthBirthdayUser;
    }

    public String getTimeDisconnectUser() {
        return timeDisconnectUser;
    }

    public void setTimeDisconnectUser(String timeDisconnectUser) {
        this.timeDisconnectUser = timeDisconnectUser;
    }

    public String getTimeRegisterUser() {
        return timeRegisterUser;
    }

    public void setTimeRegisterUser(String timeRegisterUser) {
        this.timeRegisterUser = timeRegisterUser;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUrlAvatar() {
        return urlAvatar;
    }

    public void setUrlAvatar(String urlAvatar) {
        this.urlAvatar = urlAvatar;
    }

    public static String getUrlAvatarDefault() {
        return urlAvatarDefault;
    }

    public static void setUrlAvatarDefault(String urlAvatarDefault) {
        User.urlAvatarDefault = urlAvatarDefault;
    }

    public String getUrlCover() {
        return urlCover;
    }

    public void setUrlCover(String urlCover) {
        this.urlCover = urlCover;
    }

    public static String getUrlCoverDefault() {
        return urlCoverDefault;
    }

    public static void setUrlCoverDefault(String urlCoverDefault) {
        User.urlCoverDefault = urlCoverDefault;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getYearBirthdayUser() {
        return yearBirthdayUser;
    }

    public void setYearBirthdayUser(String yearBirthdayUser) {
        this.yearBirthdayUser = yearBirthdayUser;
    }
}
