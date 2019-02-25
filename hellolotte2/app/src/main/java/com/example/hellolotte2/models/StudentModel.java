package com.example.hellolotte2.models;

import io.realm.RealmObject;

public class StudentModel extends RealmObject {


    private String code;
    private String url ;

    public void init(String code) {
        this.code = code;
        this.url = "http://anhsv.hust.edu.vn/Student/" + code + ".jpg";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    @Override
    public String toString() {
        return "{" +
                "code='" + code + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
