package com.example.pixabayapi.activities;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pixabayapi.adapters.PhotoAdapter;
import com.example.pixabayapi.models.PhotoModel;
import com.example.pixabayapi.R;
import com.example.pixabayapi.networks.PixabayRequest;
import com.example.pixabayapi.networks.PixabayResponse;
import com.example.pixabayapi.networks.RetrofitInstance;
import com.example.pixabayapi.networks.iPixabayService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.cl_header)
    ConstraintLayout clHeader;
    @BindView(R.id.tv_page_number)
    TextView tvPageNumber;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.cl_footer)
    ConstraintLayout clFooter;
    @BindView(R.id.rv_photos)
    RecyclerView rvPhotos;
    List<PhotoModel> photoModelList = new ArrayList<>();
    private static final String TAG = "MainActivity";
    GridLayoutManager girdLayoutManager = new GridLayoutManager(this,2);
    PhotoAdapter photoAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initialization();
        setupUI();

    }


    @OnClick({R.id.iv_search, R.id.iv_right, R.id.iv_left})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_search:
                // Toast.makeText(MainActivity.this, "No results", Toast.LENGTH_SHORT).show();
                //     Log.d(TAG, "onViewClicked: " + etSearch.getText().toString());
                if (etSearch.getText().toString().length() == 0) break;
                PixabayRequest pixabayRequest = new PixabayRequest(etSearch.getText().toString(), 1);
                Log.d(TAG, "request: " + pixabayRequest);
                loadPhotos(pixabayRequest);
                break;
            case R.id.iv_right:
                break;
            case R.id.iv_left:
                break;
        }
    }


    private void initialization() {
        girdLayoutManager = new GridLayoutManager(this,2);

    }

    private void setupUI() {

    }

    private void loadPhotos(PixabayRequest pixabayRequest) {
        //   Log.d(TAG, "loadPhotos: loadphoto");
        RetrofitInstance.getInstance()
                .create(iPixabayService.class)
                .get(pixabayRequest.getKey(), pixabayRequest.getQ(), pixabayRequest.getImageType(), pixabayRequest.getPage(), pixabayRequest.getPerPage())
                .enqueue(new Callback<PixabayResponse>() {
                    @Override
                    public void onResponse(Call<PixabayResponse> call, Response<PixabayResponse> response) {
                        Log.d(TAG, "onResponse: " + response.toString());
                        if (response.body() == null || response.body().hits.isEmpty()) {
                            Log.d(TAG, "onResponse: " + "response null");
                            return;
                        }
                        if (response.body().total == 0) {
                            Toast.makeText(MainActivity.this, "No results", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        photoModelList = new ArrayList<>();
                        for (PixabayResponse.PhotoInfosJSON photoInfosJSON : response.body().hits) {
                            photoModelList.add(new PhotoModel(
                                    photoInfosJSON.webformatURL,
                                    photoInfosJSON.largeImageURL,
                                    photoInfosJSON.webformatWidth,
                                    photoInfosJSON.webformatHeight
                            ));
                        }
                        Log.d(TAG, "onResponse: " + photoModelList);
                        photoAdapter = new PhotoAdapter(photoModelList, MainActivity.this);
                        rvPhotos.setLayoutManager(girdLayoutManager);
                        rvPhotos.setAdapter(photoAdapter);

                    }

                    @Override
                    public void onFailure(Call<PixabayResponse> call, Throwable t) {
                        Log.d(TAG, "onFailure: " + t);
                        Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();

                    }
                });

    }
}
