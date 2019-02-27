package com.example.buinam.mapchatdemo.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.buinam.mapchatdemo.R;
import com.example.buinam.mapchatdemo.adapter.DetailGroupChatAdapter;
import com.example.buinam.mapchatdemo.design.ReferenceUrl;
import com.example.buinam.mapchatdemo.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailGroupChat extends AppCompatActivity {

    Toolbar toolbarDetailGroupChat;
    CircleImageView avaDetailGroupChat;
    TextView tv_nameGroupChat2;
    TextView tv_nameGroupChat;
    TextView tv_sizeGroupChat;
    //
    String urlAvarGroupChat;
    String idGroupChat;
    String nameGroup;
    String sizeGroupChat;
    RecyclerView recyclerView;
    DetailGroupChatAdapter detailGroupChatAdapter;
    ArrayList<User> userArrayList;
    LinearLayout exitGroupChathehe;
    //

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference databaseUser;
    AVLoadingIndicatorView aviDetailGroupChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_group_chat);
        Intent intent = getIntent();
        urlAvarGroupChat = intent.getStringExtra("urlAvarGroupChat").toString();
        idGroupChat = intent.getStringExtra("idGroupChat").toString();
        nameGroup = intent.getStringExtra("nameGroup").toString();
        sizeGroupChat = intent.getStringExtra("sizeGroupChat").toString();
        toolbarDetailGroupChat = (Toolbar) findViewById(R.id.toolbarDetailGroupChat);
        aviDetailGroupChat = (AVLoadingIndicatorView) findViewById(R.id.aviDetailGroupChat);
        aviDetailGroupChat.show();
        exitGroupChathehe = (LinearLayout)findViewById(R.id.exitGroupChathehe);
        //
        recyclerView = (RecyclerView)findViewById(R.id.rv_DetailGroupChat);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);

        userArrayList = new ArrayList<>();
        detailGroupChatAdapter = new DetailGroupChatAdapter(DetailGroupChat.this, userArrayList);
        recyclerView.setAdapter(detailGroupChatAdapter);




        //
        databaseUser = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        getDataRecyclerView(idGroupChat);

        setSupportActionBar(toolbarDetailGroupChat);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Window w = DetailGroupChat.this.getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

//        avLoadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.aviDetailGroupChat);
        tv_nameGroupChat = (TextView)findViewById(R.id.tv_nameGroupChat);
        tv_nameGroupChat2 = (TextView)findViewById(R.id.tv_nameGroupChat2);
        tv_sizeGroupChat = (TextView)findViewById(R.id.tv_sizeGroupChat);


        final FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fabDetailGroupChat);
        fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EA2E49")));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        avaDetailGroupChat = (CircleImageView) findViewById(R.id.avaDetailGroupChat);
        Glide.with(DetailGroupChat.this).load(urlAvarGroupChat)
                .centerCrop()
                .error(R.drawable.avatarerror)
                .into(avaDetailGroupChat);
        tv_nameGroupChat.setText(nameGroup);
        tv_nameGroupChat2.setText(nameGroup);
        tv_sizeGroupChat.setText(sizeGroupChat + " thành viên");
        exitGroupChathehe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(DetailGroupChat.this)
                        .setTitle("Xoá cuộc trò chuyện?")
                        .setMessage("Bạn thực sự muốn xoá à?")
                        .setNegativeButton("Trở về", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteRecentChat(idGroupChat);
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

    }

    private void deleteRecentChat(String idGroupChat) {
        databaseUser.child(ReferenceUrl.CHILD_USERS).child(mUser.getUid()).child("idRoomGroupChat")
                .child(idGroupChat).removeValue();
        Toast.makeText(this, "Đã xoá cuộc hội thoại rồi nhé", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getApplication(), MainActivity.class);
        DetailGroupChat.this.finish();
        startActivity(i);
    }

    private void getDataRecyclerView(String idGroupChat) {
        databaseUser.child(ReferenceUrl.CHILD_CHATGROUP).child(idGroupChat).child("usersChat").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                getDataUser(dataSnapshot.getKey().toString());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getDataUser(String s) {
        databaseUser.child(ReferenceUrl.CHILD_USERS).child(s).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                aviDetailGroupChat.hide();
                userArrayList.add(u);
                detailGroupChatAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_personal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Bắt sự kiện cho các item Menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.editInfoPer:
                Toast.makeText(this, "edit", Toast.LENGTH_SHORT).show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}
