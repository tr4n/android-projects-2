package com.example.helloworld;

public class PhotoModel {
    private String studentCode;
    private String url;

    public PhotoModel(String studentCode) {
        this.studentCode = studentCode;
        this.url = "http://anhsv.hust.edu.vn/Student/"+studentCode + ".jpg";
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
