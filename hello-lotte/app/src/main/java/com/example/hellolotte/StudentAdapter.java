package com.example.hellolotte;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentHolder> {

    public List<StudentModel> studentModelList;

    public StudentAdapter(List<StudentModel> studentModelList) {
        this.studentModelList = studentModelList;
    }

    @NonNull
    @Override
    public StudentHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.student_layout, null);

        return new StudentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentHolder studentHolder, int i) {
        studentHolder.setData(studentModelList.get(i));
    }

    @Override
    public int getItemCount() {
        return studentModelList.size();
    }


    public class StudentHolder extends RecyclerView.ViewHolder{

        TextView tvHisCode ;
        ImageView ivHisPhoto;
        public StudentHolder(@NonNull View itemView) {
            super(itemView);
            this.tvHisCode = itemView.findViewById(R.id.tv_his_code);
            this.ivHisPhoto = itemView.findViewById(R.id.iv_his_photo);
        }
        public void setData(StudentModel studentModel){
            this.tvHisCode.setText(studentModel.code);
            Picasso.get().load(studentModel.url).into(this.ivHisPhoto);

        }
    }
}
