package com.example.hellolotte;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView ivPhoto, ivSearch, ivHistory;
    EditText etStudentCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Realm.init(this);
        initialization();
        setupUI();
    }
    private void initialization() {
        ivPhoto = findViewById(R.id.iv_photo);
        ivSearch = findViewById(R.id.iv_search);
        etStudentCode = findViewById(R.id.et_student_code);
        ivHistory = findViewById(R.id.iv_history);

    }

    private void setupUI() {
        ivSearch.setOnClickListener(this);
        ivHistory.setOnClickListener(this);
        etStudentCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                   loadPhoto();
                    return true;
                }
                return false;
            }
        });




    }

    private void loadPhoto(){
        if(etStudentCode.getText().length() != 8){
            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
        }else{
            int code = Integer.parseInt(etStudentCode.getText().toString());
            if(code < 20150000 || code > 20186969){
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }else{
                String url = "http://anhsv.hust.edu.vn/Student/" + code + ".jpg";
                Picasso.get().load(url).into(ivPhoto);
                StudentModel studentModel = new StudentModel();
                studentModel.init(code+"");
                RealmHandle.getInstance().save(studentModel);
            }

        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_search:
                loadPhoto();
                break;
            case R.id.iv_history:
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
                break;
        }
    }
}
