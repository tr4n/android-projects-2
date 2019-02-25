package com.example.hellolotte2.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hellolotte2.R;
import com.example.hellolotte2.databases.RealmHandle;
import com.example.hellolotte2.models.StudentModel;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView ivPhoto, ivSearch, ivHistory;
    EditText etCode;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Realm.init(this);
        initialization();
        setupUI();

    }



    private void initialization() {
        ivPhoto =  findViewById(R.id.iv_photo);
        ivSearch = findViewById(R.id.iv_search);
        ivHistory = findViewById(R.id.iv_history);
        etCode = findViewById(R.id.et_code);

    }
    private void setupUI() {
        ivSearch.setOnClickListener(this);
        ivHistory.setOnClickListener(this);

    }
    private void loadPhoto() {
        String code = etCode.getText().toString();
        if(code.length() != 8){
            Toast.makeText(MainActivity.this, "Code error!", Toast.LENGTH_SHORT).show();
        }else{
            Log.d(TAG, "loadPhoto: " + "no error");
            String url = "http://anhsv.hust.edu.vn/Student/" + code + ".jpg";
            Picasso.get().load(url).into(ivPhoto);
            StudentModel studentModel = new StudentModel();
            studentModel.init(code);
            RealmHandle.getInstance().saveStudent(studentModel);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_search:
                loadPhoto();
                break;
            case R.id.iv_history:
                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                break;
        }
    }


}
