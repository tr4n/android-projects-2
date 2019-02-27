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
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.buinam.mapchatdemo.R;
import com.example.buinam.mapchatdemo.activity.ChatActivity;
import com.example.buinam.mapchatdemo.activity.LoginActivity;
import com.example.buinam.mapchatdemo.adapter.ListChatAdapter;
import com.example.buinam.mapchatdemo.design.RecyclerItemClickListener;
import com.example.buinam.mapchatdemo.design.ReferenceUrl;
import com.example.buinam.mapchatdemo.model.RecentChat;
import com.example.buinam.mapchatdemo.model.RoomChat;
import com.example.buinam.mapchatdemo.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by buinam on 9/27/16.
 */
// Fragment List Chat
public class ListChatFragment extends Fragment {
    Toolbar toolbarListChat;
    RecyclerView rvListChat;
    private ArrayList<RecentChat> listRecentChat;
    RoomChat roomChat;
    ListChatAdapter radapter;
    //
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    //
    DatabaseReference databaseUser;
    AVLoadingIndicatorView avLoadingIndicatorView;


    public ListChatFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listchat, container, false);

        toolbarListChat = (Toolbar) view.findViewById(R.id.toolbarListChat);
        customMenuToolBar();
        //
        avLoadingIndicatorView = (AVLoadingIndicatorView) view.findViewById(R.id.aviListChat);
        rvListChat = (RecyclerView) view.findViewById(R.id.rvListChat);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvListChat.setHasFixedSize(true);
        rvListChat.setLayoutManager(mLayoutManager);
        rvListChat.setItemAnimator(new DefaultItemAnimator());

        listRecentChat = new ArrayList<RecentChat>();
        radapter = new ListChatAdapter(getContext(), listRecentChat);
        rvListChat.setAdapter(radapter);

        //
        databaseUser = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser == null) {
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        } else {

            getAllRecentChat(mUser);


        }

        //
        rvListChat.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), rvListChat, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                intentChatActivity(mUser, position);
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        }));

        final FloatingActionButton fabListChat = (FloatingActionButton) view.findViewById(R.id.fabListChat);
        fabListChat.setBackgroundTintList(ColorStateList.valueOf(Color
                .parseColor("#EA2E49")));
        fabListChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //
        rvListChat.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    fabListChat.hide();
                else if (dy < 0)
                    fabListChat.show();
            }
        });

        return view;
    }

    //
    public void intentChatActivity(FirebaseUser u, final int position) {
        String idUser = u.getUid().toString();
        databaseUser.child(ReferenceUrl.CHILD_USERS).child(idUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User userCurrent = dataSnapshot.getValue(User.class);
                getUserRecent(userCurrent, position);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //
    public void getUserRecent(final User uCurrent, int position) {
        String idUser = listRecentChat.get(position).getIdUserRecentChat();
        databaseUser.child(ReferenceUrl.CHILD_USERS).child(idUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User userRecent = dataSnapshot.getValue(User.class);
                Intent i = new Intent(getContext(), ChatActivity.class);
                Gson gson = new Gson();
                i.putExtra(ReferenceUrl.KEY_SEND_USER, gson.toJson(userRecent).toString() + "---" + gson.toJson(uCurrent).toString());
                startActivity(i);
                //

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void customMenuToolBar() {

        toolbarListChat.inflateMenu(R.menu.menu_listchat);

        toolbarListChat.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.searchListChat:
                        Toast.makeText(getContext(), "Nam", Toast.LENGTH_LONG).show();
                        return true;
                }
                return false;
            }
        });
    }

    public void getAllRecentChat(FirebaseUser u) {

        String idUser = u.getUid().toString();
        databaseUser.child(ReferenceUrl.CHILD_USERS).child(idUser).child("idRoomChat").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                roomChat = new RoomChat();
                roomChat = dataSnapshot.getValue(RoomChat.class);
                getDataRecentChat(roomChat.getIdUserChat(), dataSnapshot.getKey().toString());

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


    public void getDataRecentChat(String idUserChat, String idRoomChat) {
        final String idRC = idRoomChat;
        final RecentChat recentChat = new RecentChat();
        databaseUser.child(ReferenceUrl.CHILD_USERS).child(idUserChat)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        avLoadingIndicatorView.hide();
                        User u = dataSnapshot.getValue(User.class);
                        recentChat.setDisplayNameRecentChat(u.getDisplayName());
                        recentChat.setIdUserRecentChat(u.getIdUser());
                        recentChat.setUrlAvatarRecentChat(u.getUrlAvatar());
                        recentChat.setIdRoomChat(idRC);
                        databaseUser.child(ReferenceUrl.CHILD_CHAT).child(idRC).child("lastMessage")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String lastMessage = dataSnapshot.getValue().toString();
                                        recentChat.setLastMessage(lastMessage);
                                        radapter.notifyDataSetChanged();

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                        databaseUser.child(ReferenceUrl.CHILD_CHAT).child(idRC).child("timeLastMessage")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        recentChat.setTimeLastMessage(dataSnapshot.getValue().toString());

                                        radapter.notifyDataSetChanged();

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                        listRecentChat.add(recentChat);
                        radapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    //
    public void arrangementListChat(ArrayList<RecentChat> listRChat) {
        Collections.sort(listRChat, new Comparator<RecentChat>() {
            @Override
            public int compare(RecentChat rc1, RecentChat rc2) {
                if ((Long.parseLong(rc1.getTimeLastMessage())) < (Long.parseLong(rc2.getTimeLastMessage()))) {
                    return 1;
                } else {
                    if ((Long.parseLong(rc1.getTimeLastMessage())) == (Long.parseLong(rc2.getTimeLastMessage()))) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            }
        });

        radapter.notifyDataSetChanged();
    }
}

