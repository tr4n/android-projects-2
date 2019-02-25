package com.example.pixabayapi.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.pixabayapi.R;
import com.example.pixabayapi.Utils;
import com.example.pixabayapi.models.PhotoModel;
import com.example.pixabayapi.networks.PixabayResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Callback;

import static android.content.ContentValues.TAG;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    public List<PhotoModel> photoModelList = new ArrayList<>();
    public Context context;


    public PhotoAdapter(List<PhotoModel> photoModelList, Context context) {
        this.photoModelList = photoModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_photo, null);

        return new PhotoViewHolder(itemView, context);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder photoViewHolder, int i) {
        photoViewHolder.setData(photoModelList.get(i));
    }

    @Override
    public int getItemCount() {
        return photoModelList.size();
    }


    public class PhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivPhotoItem;
        Context context;

        public PhotoViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            ivPhotoItem = itemView.findViewById(R.id.iv_photo_item);
        }

        public void setData(PhotoModel photoModel){
            Log.d(TAG, "setData: " + photoModel);
            int fixedWidth = (int)(Utils.WIDTH_PIXELS >> 1);
            int fixedHeight = (fixedWidth * photoModel.getHeight() )/(photoModel.getWidth());
            ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    fixedWidth,
                    fixedHeight
            );
        //    ivPhotoItem.setLayoutParams(layoutParams);
            Picasso.get()
                    .load(photoModel.getUrl())
                    .into(ivPhotoItem);

        }



        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_photo_item:
                    Toast.makeText(context, "click", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
