package com.example.hellolotte;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView ;
    StudentAdapter studentAdapter;
    List<StudentModel> studentModelList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        studentModelList = RealmHandle.getInstance().findAll();
        recyclerView = findViewById(R.id.recycler_view);
        studentAdapter = new StudentAdapter(studentModelList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(studentAdapter);

    }
}
