package com.example.hellolotte2.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hellolotte2.R;
import com.example.hellolotte2.models.StudentModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    public List<StudentModel> studentModelList;

    public StudentAdapter(List<StudentModel> studentModelList) {
        this.studentModelList = studentModelList;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_student, null);
        return new StudentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder studentViewHolder, int i) {
        studentViewHolder.setData(studentModelList.get(i));
    }

    @Override
    public int getItemCount() {
        return studentModelList.size();
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder {

        ImageView ivHistoryPhoto;
        TextView tvHistoryCode;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            this.ivHistoryPhoto = itemView.findViewById(R.id.iv_history_photo);
            this.tvHistoryCode = itemView.findViewById(R.id.tv_history_code);
        }

        public void setData(StudentModel studentModel) {
            Picasso.get().load(studentModel.getUrl()).into(this.ivHistoryPhoto);
            this.tvHistoryCode.setText(studentModel.getCode());
        }
    }
}
