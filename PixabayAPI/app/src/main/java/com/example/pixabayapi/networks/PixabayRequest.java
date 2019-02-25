package com.example.pixabayapi.networks;

public class PixabayRequest {
    private String key;
    private String q;
    private String imageType;
    private int page;
    private int perPage;

    public PixabayRequest(String q, int page) {
        this.q = q;
        this.page = page;
        this.key = "11705223-2e906401dbe44ed955bacd2ac";
        this.imageType = "photo";
        this.perPage = 50;
    }

    public String getKey() {
        return key;
    }

    public String getQ() {
        return q;
    }

    public String getImageType() {
        return imageType;
    }

    public int getPage() {
        return page;
    }

    public int getPerPage() {
        return perPage;
    }

    @Override
    public String toString() {
        return "PixabayRequest{" +
                "key='" + key + '\'' +
                ", q='" + q + '\'' +
                ", imageType='" + imageType + '\'' +
                ", page=" + page +
                ", perPage=" + perPage +
                '}';
    }
}
