package com.example.helloworld;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    public List<PhotoModel> photoModelList ;

    public PhotoAdapter(List<PhotoModel> photoModelList) {
        this.photoModelList = photoModelList;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_photo, null);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder photoViewHolder, int i) {
            photoViewHolder.setData(photoModelList.get(i));
    }

    @Override
    public int getItemCount() {
        return photoModelList.size();
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {

        private TextView tvStudentCode;
        private ImageView ivPhoto;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentCode = itemView.findViewById(R.id.tv_student_code);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
        }

        public void setData(PhotoModel photoModel){
            tvStudentCode.setText(photoModel.getStudentCode());
            Picasso.get().load(photoModel.getUrl()).into(ivPhoto);
        }
    }
}
