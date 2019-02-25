package com.example.pixabayapi.networks;

import java.util.ArrayList;
import java.util.List;

public class PixabayResponse {

    public List<PhotoInfosJSON> hits = new ArrayList<>();
    public int total ;

    public class PhotoInfosJSON{
        public String webformatURL;
        public int webformatHeight;
        public int webformatWidth;
        public String largeImageURL;
    }
}
