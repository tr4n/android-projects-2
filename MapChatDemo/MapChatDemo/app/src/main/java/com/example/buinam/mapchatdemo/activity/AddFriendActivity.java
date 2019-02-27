package com.example.buinam.mapchatdemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.buinam.mapchatdemo.R;
import com.example.buinam.mapchatdemo.adapter.AddFriendAdapter;
import com.example.buinam.mapchatdemo.design.ReferenceUrl;
import com.example.buinam.mapchatdemo.model.Friend;
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

// Tìm kiếm bạn bè...
public class AddFriendActivity extends AppCompatActivity {
    Toolbar toolbarAddFriend;
    RecyclerView recyclerViewAddFriend;
    ArrayList<User> listUserNoFriend;
    AddFriendAdapter addFriendAdapter;

    DatabaseReference databaseUser;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    User currentUser;
    AVLoadingIndicatorView avLoadingIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        toolbarAddFriend = (Toolbar) findViewById(R.id.toolbarAddFriend);
        setSupportActionBar(toolbarAddFriend);
        getSupportActionBar().setTitle("Thêm bạn bè");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        avLoadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.aviAddFriend);
        recyclerViewAddFriend = (RecyclerView) findViewById(R.id.rvAddFriend);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(AddFriendActivity.this);
        recyclerViewAddFriend.setHasFixedSize(true);
        recyclerViewAddFriend.setLayoutManager(mLayoutManager);
        recyclerViewAddFriend.setItemAnimator(new DefaultItemAnimator());

        listUserNoFriend = new ArrayList<>();
        addFriendAdapter = new AddFriendAdapter(this, listUserNoFriend);
        recyclerViewAddFriend.setAdapter(addFriendAdapter);
        //
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        databaseUser = FirebaseDatabase.getInstance().getReference();
        getDataFriend(mUser);

    }

    private void getDataFriend(FirebaseUser cUser) {
        databaseUser.child(ReferenceUrl.CHILD_USERS).child(cUser.getUid()).child("checkListFriend").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Friend f = dataSnapshot.getValue(Friend.class);
                if(!f.getStatus().equals("isFriend")){
                    setDataRecycler(f);
                }
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

    private void setDataRecycler(Friend f) {
        String idUser = f.getIdUser();
        databaseUser.child(ReferenceUrl.CHILD_USERS).child(idUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                listUserNoFriend.add(u);
                avLoadingIndicatorView.hide();
                addFriendAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_listchat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.searchListChat:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
