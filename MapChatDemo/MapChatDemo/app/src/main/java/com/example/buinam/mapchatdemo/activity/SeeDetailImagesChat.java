package com.example.buinam.mapchatdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.buinam.mapchatdemo.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SeeDetailImagesChat extends AppCompatActivity {
    ImageView imgChat;
    ImageButton imgButtonBack;
    LinearLayout lnlInfo;
    TextView tvUserSend;
    TextView tvTimeSend;
    AVLoadingIndicatorView avLoadingIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_detail_images_chat);
        imgChat = (ImageView) findViewById(R.id.imgDetailChat);
        avLoadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.aviLoadImage);
        imgButtonBack = (ImageButton) findViewById(R.id.imgButtonBack);
        lnlInfo = (LinearLayout) findViewById(R.id.lnlInfo);
        tvUserSend = (TextView) findViewById(R.id.tvUserSend);
        tvTimeSend = (TextView) findViewById(R.id.tvTimeSend);

        imgButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Intent intent = getIntent();
        String widthImage = intent.getStringExtra("widthImage").toString();
        String heightImage = intent.getStringExtra("heightImage").toString();
        Glide.with(this)
                .load(intent.getStringExtra("urlImageChat").toString())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        if (e instanceof UnknownHostException)
                            avLoadingIndicatorView.show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        avLoadingIndicatorView.hide();
                        return false;
                    }
                })
                .override(Integer.parseInt(widthImage), Integer.parseInt(heightImage))
                .centerCrop()
                .error(R.drawable.photoview)
                .into(imgChat);


        tvUserSend.setText(intent.getStringExtra("displayUserSend").toString());
        Date d = new Date(Long.parseLong(intent.getStringExtra("timeSend").toString()));
        tvTimeSend.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK).format(d));


    }
}
