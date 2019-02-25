package com.example.pixabayapi;

import java.util.List;

public class PixabayResponse {

    public List<PhotoInfosJSON> hits;
    public int total ;

    public class PhotoInfosJSON{
        public String webformatURL;
        public int webformatHeight;
        public int webformatWidth;
        public String largeImageURL;
    }
}
