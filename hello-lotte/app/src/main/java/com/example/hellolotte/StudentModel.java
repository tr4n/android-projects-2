package com.example.hellolotte;

import io.realm.RealmModel;
import io.realm.RealmObject;

public class StudentModel extends RealmObject {
    public String code;
    public String url;


    public void init(String code){
        this.code = code;
        this.url = "http://anhsv.hust.edu.vn/Student/" + code + ".jpg" ;
    }

}
