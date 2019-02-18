package com.example.hellolotte;

import java.util.List;

import io.realm.Realm;

public class RealmHandle {
    Realm realm = Realm.getDefaultInstance();
    private static RealmHandle instance;
    public static RealmHandle getInstance(){
        if(instance == null){
            instance = new RealmHandle();
        }
        return instance;
    }

    public boolean isExist(StudentModel studentModel){
        return realm.where(StudentModel.class).equalTo("code",studentModel.code).findFirst() != null;
    }

    public void save(StudentModel studentModel){

        if(isExist(studentModel)) return;
        realm.beginTransaction();
        realm.copyToRealm(studentModel);
        realm.commitTransaction();
    }

    public List<StudentModel> findAll(){
        return realm.where(StudentModel.class).findAll();
    }
}
