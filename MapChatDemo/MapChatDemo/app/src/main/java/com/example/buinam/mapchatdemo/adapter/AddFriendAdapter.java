package com.example.buinam.mapchatdemo.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.buinam.mapchatdemo.R;
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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by buinam on 12/4/16.
 */

public class AddFriendAdapter extends RecyclerView.Adapter<AddFriendAdapter.MyViewHolder> {
    private List<User> friendList;
    private Context mContext;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String currenUserID;
    private User currenUser;

    String isSendAddFriend;
    //
    DatabaseReference databaseUser;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView displayUserNameAddFriend;
        public CircleImageView imgAvatarAddFriend;
        public TextView tv_addFriend;
        public LinearLayout lnlListFriendAddFriend;
        public TextView tv_checkStatus;

        public MyViewHolder(View view) {
            super(view);
            lnlListFriendAddFriend = (LinearLayout) view.findViewById(R.id.detailAddFriend);
            displayUserNameAddFriend = (TextView) view.findViewById(R.id.tvDisplayUserNameAddFriend);
            imgAvatarAddFriend = (CircleImageView) view.findViewById(R.id.imgAvatarAddFriend);
            tv_addFriend = (TextView) view.findViewById(R.id.tv_addFriend);
            tv_checkStatus = (TextView)view.findViewById(R.id.tv_checksattus);
        }
    }


    public AddFriendAdapter(Context mContext, List<User> friendList) {
        this.friendList = friendList;
        this.mContext = mContext;
    }

    @Override
    public AddFriendAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_addfriend, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AddFriendAdapter.MyViewHolder holder, final int position) {
        final User listFriend = friendList.get(position);
        databaseUser = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        currenUserID = mUser.getUid();


        if (listFriend != null) {
            holder.displayUserNameAddFriend.setText(listFriend.getDisplayName());
            Glide.with(mContext).load(listFriend.getUrlAvatar())
                    .centerCrop()
                    .error(R.drawable.avatarerror)
                    .into(holder.imgAvatarAddFriend);

            //Bắt sự kiện vào giao diện chat
            holder.lnlListFriendAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(position);

                }
            });

            databaseUser.child(ReferenceUrl.CHILD_USERS).child(mUser.getUid()).child("checkListFriend").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Friend f = dataSnapshot.getValue(Friend.class);
                    if (!f.getStatus().equals("isFriend")) {
                        if (f.getStatus().equals("NoFriend")) {
                            for (int i = 0; i < friendList.size(); i++) {
                                if (friendList.get(i).getIdUser().equals(f.getIdUser())) {
                                    if (position == i) {
                                        holder.tv_addFriend.setText("Kết bạn");
                                        holder.tv_checkStatus.setText("Chưa là bạn bè");
                                    }

                                }
                            }
                        } else if (f.getStatus().equals("sendAddFriend")) {
                            for (int i = 0; i < friendList.size(); i++) {
                                if (friendList.get(i).getIdUser().equals(f.getIdUser())) {
                                    if (position == i) {
                                        holder.tv_addFriend.setText("Huỷ lời mời");
                                        holder.tv_checkStatus.setText("Đã gửi lời mời kết bạn");
                                    }
                                }
                            }
                        }
                        if (f.getStatus().equals("recentAddFriend")) {
                            for (int i = 0; i < friendList.size(); i++) {
                                if (friendList.get(i).getIdUser().equals(f.getIdUser())) {
                                    if (position == i) {
                                        holder.tv_addFriend.setText("Đồng ý");
                                        holder.tv_checkStatus.setText("Lời mời kết bạn");

                                    }
                                }
                            }
                        }
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

            holder.tv_addFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseUser.child(ReferenceUrl.CHILD_USERS).child(mUser.getUid()).child("checkListFriend")
                            .child(listFriend.getIdUser()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Friend f = dataSnapshot.getValue(Friend.class);
                            if (f.getStatus().equals("NoFriend")) {
                                holder.tv_addFriend.setText("Huỷ lời mời");
                                holder.tv_checkStatus.setText("Đã gửi lời mời kết bạn");
                                databaseUser.child(ReferenceUrl.CHILD_USERS).child(mUser.getUid())
                                        .child("checkListFriend").child(listFriend.getIdUser())
                                        .child("status").setValue("sendAddFriend");
                                databaseUser.child(ReferenceUrl.CHILD_USERS).child(listFriend.getIdUser())
                                        .child("checkListFriend").child(mUser.getUid())
                                        .child("status").setValue("recentAddFriend");

                            } else if (f.getStatus().equals("sendAddFriend")) {
                                holder.tv_addFriend.setText("Kết bạn");
                                holder.tv_checkStatus.setText("Chưa là bạn bè");
                                databaseUser.child(ReferenceUrl.CHILD_USERS).child(mUser.getUid())
                                        .child("checkListFriend").child(listFriend.getIdUser())
                                        .child("status").setValue("NoFriend");
                                databaseUser.child(ReferenceUrl.CHILD_USERS).child(listFriend.getIdUser())
                                        .child("checkListFriend").child(mUser.getUid())
                                        .child("status").setValue("NoFriend");

                            } else if (f.getStatus().equals("recentAddFriend")) {
                                holder.tv_addFriend.setText("Bạn bè");
                                holder.tv_checkStatus.setText("Là bạn bè");
                                databaseUser.child(ReferenceUrl.CHILD_USERS).child(mUser.getUid())
                                        .child("checkListFriend").child(listFriend.getIdUser())
                                        .child("status").setValue("isFriend");
                                databaseUser.child(ReferenceUrl.CHILD_USERS).child(listFriend.getIdUser())
                                        .child("checkListFriend").child(mUser.getUid())
                                        .child("status").setValue("isFriend");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });


        }
    }


    public void showDialog(int position) {
        User u = friendList.get(position);
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.custom_dialogdetailfriend);
        ImageView imgExpandedFriend = (ImageView) dialog.findViewById(R.id.expandedImageFriend);
        CircleImageView imgAvatarFriend = (CircleImageView) dialog.findViewById(R.id.imgAvatarFriend);
        TextView tvNameFriend = (TextView) dialog.findViewById(R.id.tvDisplayUserNameFriend);
        TextView tvBithdayFriend = (TextView) dialog.findViewById(R.id.bithdayFriend);
        TextView tvGenderFriend = (TextView) dialog.findViewById(R.id.genderFriend);

        Glide.with(mContext).load(u.getUrlAvatar())
                .error(R.drawable.avatarerror)
                .centerCrop().into(imgAvatarFriend);
        Glide.with(mContext).load(u.getUrlCover())
                .error(R.drawable.backgrond)
                .centerCrop().into(imgExpandedFriend);
        tvNameFriend.setText(u.getDisplayName());
        tvBithdayFriend.setText(u.getDayBirthdayUser() + "-" + u.getMonthBirthdayUser() + "-" + u.getYearBirthdayUser());

        if (u.getGenderUser().equals("")) {
            tvGenderFriend.setText("Chưa cập nhật");
            tvGenderFriend.setTextColor(mContext.getResources().getColor(R.color.textColor_per));
        } else tvGenderFriend.setText(u.getGenderUser());

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

}
