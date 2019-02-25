package com.example.pixabayapi.models;

public class PhotoModel {
    private String url;
    private String largeUrl;
    private int width;
    private int height;

    public PhotoModel(String url, String largeUrl, int width, int height) {
        this.url = url;
        this.largeUrl = largeUrl;
        this.width = width;
        this.height = height;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLargeUrl() {
        return largeUrl;
    }

    public void setLargeUrl(String largeUrl) {
        this.largeUrl = largeUrl;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "PhotoModel{" +
                "url='" + url + '\'' +
                ", largeUrl='" + largeUrl + '\'' +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
