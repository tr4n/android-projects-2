package com.example.helloworld;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.fab_add)
    FloatingActionButton fabAdd;
    @BindView(R.id.rv_photos)
    RecyclerView rvPhotos;
    @BindView(R.id.iv_pre_photo)
    ImageView ivPrePhoto;
    @BindView(R.id.et_student_code)
    EditText etStudentCode;
    @BindView(R.id.iv_check)
    ImageView ivCheck;
    @BindView(R.id.iv_cancel)
    ImageView ivCancel;
    @BindView(R.id.iv_save)
    ImageView ivSave;
    @BindView(R.id.cl_add_student)
    ConstraintLayout clAddStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.fab_add, R.id.iv_check, R.id.iv_cancel, R.id.iv_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fab_add:
                clAddStudent.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_check:
                break;
            case R.id.iv_cancel:
                clAddStudent.setVisibility(View.GONE);
                break;
            case R.id.iv_save:
                break;
        }
    }
}
