package com.example.buinam.mapchatdemo.fragment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.buinam.mapchatdemo.R;
import com.example.buinam.mapchatdemo.activity.CreatGroupChatActivity;
import com.example.buinam.mapchatdemo.activity.LoginActivity;
import com.example.buinam.mapchatdemo.adapter.GroupChatAdapter;
import com.example.buinam.mapchatdemo.design.ReferenceUrl;
import com.example.buinam.mapchatdemo.model.RecentGroupChat;
import com.example.buinam.mapchatdemo.model.RoomGroupChat;
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
 * Created by buinam on 9/27/16.
 */

public class GroupChatFragment extends Fragment {
    Toolbar toolbarGroupChat;
    //
    private RecyclerView recyclerViewGroupChat;
    private GroupChatAdapter grouptChatAdapter;
    private ArrayList<RecentGroupChat> listRecentGroupChat;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference databaseGroupChat;
    AVLoadingIndicatorView avLoadingIndicatorView;
    RoomGroupChat roomGroupChat;

    public GroupChatFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_groupchat, container, false);
        avLoadingIndicatorView = (AVLoadingIndicatorView)view.findViewById(R.id.aviGroupChat);

        toolbarGroupChat = (Toolbar) view.findViewById(R.id.toolbarGroupChat);


        final FloatingActionButton fabListChat = (FloatingActionButton) view.findViewById(R.id.fabGroupChat);
        fabListChat.setBackgroundTintList(ColorStateList.valueOf(Color
                .parseColor("#EA2E49")));
        fabListChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), CreatGroupChatActivity.class);
                startActivity(i);

            }
        });
        //
        recyclerViewGroupChat = (RecyclerView) view.findViewById(R.id.recycler_view_groupchat);

        listRecentGroupChat = new ArrayList<>();
        grouptChatAdapter = new GroupChatAdapter(getContext(), listRecentGroupChat);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerViewGroupChat.setLayoutManager(mLayoutManager);
        recyclerViewGroupChat.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerViewGroupChat.setItemAnimator(new DefaultItemAnimator());
        recyclerViewGroupChat.setAdapter(grouptChatAdapter);
        //
        databaseGroupChat = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser == null) {
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        } else {

            getAllGroupChat(mUser);

        }

        recyclerViewGroupChat.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

    private void getAllGroupChat(FirebaseUser mUser) {
            String idUser = mUser.getUid().toString();
            databaseGroupChat.child(ReferenceUrl.CHILD_USERS).child(idUser).child("idRoomGroupChat").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    roomGroupChat = new RoomGroupChat();
                    roomGroupChat = dataSnapshot.getValue(RoomGroupChat.class);
                    getDataRecentGroupChat(roomGroupChat.getIdGroupChat());


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

    private void getDataRecentGroupChat(String idGroupChat) {
        String IDRGC = idGroupChat;
        avLoadingIndicatorView.hide();
        final RecentGroupChat recentGroupChat = new RecentGroupChat();

        recentGroupChat.setIdRoomGroupChat(IDRGC);
        databaseGroupChat.child(ReferenceUrl.CHILD_CHATGROUP).child(IDRGC)
                .child("nameGroupChat")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recentGroupChat.setNameGroupChat(dataSnapshot.getValue().toString());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseGroupChat.child(ReferenceUrl.CHILD_CHATGROUP)
                .child(IDRGC).child("urlAvaGroupChat").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recentGroupChat.setUrlAvatarGroupChat(dataSnapshot.getValue().toString());

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseGroupChat.child(ReferenceUrl.CHILD_CHATGROUP).child(IDRGC).child("sizeGroupChat").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recentGroupChat.setSizeGroupChat(dataSnapshot.getValue().toString());
                listRecentGroupChat.add(recentGroupChat);
                grouptChatAdapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    //
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


}
