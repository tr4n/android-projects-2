package com.example.hellolotte2.databases;

import com.example.hellolotte2.models.StudentModel;

import java.util.List;

import io.realm.Realm;

public class RealmHandle {
    Realm realm = Realm.getDefaultInstance();
    private static RealmHandle instance;

    public static RealmHandle getInstance() {
        if (instance == null) {
            instance = new RealmHandle();
        }
        return instance;
    }

    public void saveStudent(StudentModel studentModel) {
        realm.beginTransaction();
        realm.copyToRealm(studentModel);
        realm.commitTransaction();
    }

    ;

    public List<StudentModel> getAll() {
        return realm.where(StudentModel.class).findAll();
    }

}
