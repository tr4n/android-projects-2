package com.example.buinam.mapchatdemo.fragment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.buinam.mapchatdemo.R;
import com.example.buinam.mapchatdemo.activity.AddFriendActivity;
import com.example.buinam.mapchatdemo.activity.LoginActivity;
import com.example.buinam.mapchatdemo.adapter.ListFriendAdapter;
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

/**
 * Created by buinam on 9/16/16.
 */
public class ListFriendFragment extends Fragment {

    private ArrayList<User> friendArrayList;
    private RecyclerView rvListFriend;

    public ListFriendFragment() {
    }

    //
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    //
    DatabaseReference databaseUser;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    String currenUserID;
    String currenUserEmail;
    private ArrayList<String> arrayUserName;
    private User currenUser;
    ListFriendAdapter ladapter;
    AVLoadingIndicatorView avLoadingIndicatorView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listfriend, container, false);

        avLoadingIndicatorView = (AVLoadingIndicatorView)view.findViewById(R.id.avi);
        rvListFriend = (RecyclerView) view.findViewById(R.id.rvListFriend);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvListFriend.setHasFixedSize(true);
        rvListFriend.setLayoutManager(mLayoutManager);
        rvListFriend.setItemAnimator(new DefaultItemAnimator());

        friendArrayList = new ArrayList<User>();
        ladapter = new ListFriendAdapter(getContext(), friendArrayList);
        rvListFriend.setAdapter(ladapter);
        //
        arrayUserName = new ArrayList<>();
        databaseUser = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser == null) {
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        } else {
            currenUserID = mUser.getUid();
            currenUserEmail = mUser.getEmail().toString();
            databaseUser.child(ReferenceUrl.CHILD_USERS).child(mUser.getUid()).child(ReferenceUrl.CHILD_CONNECTION).setValue(ReferenceUrl.KEY_ONLINE);
            getDataFriend(mUser);
        }

        //
        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fabListFriend);
        fab.setBackgroundTintList(ColorStateList.valueOf(Color
                .parseColor("#EA2E49")));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iAddFriend = new Intent(getContext(), AddFriendActivity.class);
                startActivity(iAddFriend);
            }
        });
        //
        rvListFriend.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    fab.hide();
                else if (dy < 0)
                    fab.show();
            }
        });
        return view;
    }

    //
    private void getDataFriend(FirebaseUser cUser) {
        databaseUser.child(ReferenceUrl.CHILD_USERS).child(cUser.getUid()).child("checkListFriend")
                .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Friend f = dataSnapshot.getValue(Friend.class);
                if(f.getStatus().equals("isFriend")){
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
                friendArrayList.add(u);
                avLoadingIndicatorView.hide();
                ladapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onStop() {
        super.onStop();
        try {
            mAuth.removeAuthStateListener(mAuthStateListener);
        } catch (Exception e) {
        }
    }



}
