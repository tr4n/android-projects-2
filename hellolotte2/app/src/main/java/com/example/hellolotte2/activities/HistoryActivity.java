package com.example.hellolotte2.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.example.hellolotte2.R;
import com.example.hellolotte2.adapters.StudentAdapter;
import com.example.hellolotte2.databases.RealmHandle;
import com.example.hellolotte2.models.StudentModel;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    RecyclerView rvStudent;
    List<StudentModel> studentModelList;
    StudentAdapter studentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        rvStudent = findViewById(R.id.rv_student);

        studentModelList = RealmHandle.getInstance().getAll();
        studentAdapter = new StudentAdapter(studentModelList);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        StaggeredGridLayoutManager staggeredGridLayoutManager= new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);

        rvStudent.setLayoutManager(staggeredGridLayoutManager);
        rvStudent.setAdapter(studentAdapter);
    }
}
