package com.example.buinam.mapchatdemo.adapter;

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
import com.example.buinam.mapchatdemo.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by buinam on 11/22/16.
 */

public class ListFriendGroupChatAdapter extends RecyclerView.Adapter<ListFriendGroupChatAdapter.MyViewHolder> {
    private List<User> listUser;
    private Context mContext;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView imgAvatarGroupChat;
        public TextView tvUserGroupChat;
        public LinearLayout lnlUserGroupChat;
        ImageView imgCheck;

        public MyViewHolder(View view) {
            super(view);

            tvUserGroupChat = (TextView) view.findViewById(R.id.tvDisplayUserNameGroupChat);
            imgAvatarGroupChat = (CircleImageView) view.findViewById(R.id.imgAvatarListUserGroupChat);
            imgCheck = (ImageView) view.findViewById(R.id.imgCheck);
            lnlUserGroupChat = (LinearLayout) view.findViewById(R.id.lnlCustomUserGroupChat);

        }
    }


    public ListFriendGroupChatAdapter(Context mContext, List<User> listUser) {
        this.listUser = listUser;
        this.mContext = mContext;
    }

    @Override
    public ListFriendGroupChatAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_listusergroupchat, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListFriendGroupChatAdapter.MyViewHolder holder, final int position) {
        final User user = listUser.get(position);

        if (user != null) {
            Glide.with(mContext).load(user.getUrlAvatar())
                    .error(R.drawable.avatarerror)
                    .centerCrop()
                    .into(holder.imgAvatarGroupChat);
            holder.tvUserGroupChat.setText(user.getDisplayName().toString());
            final String[] t = {"Nam"};
            holder.lnlUserGroupChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (t[0] == "Nam") {
                        user.setSelected(true);
                        holder.imgCheck.setVisibility(View.VISIBLE);
                        holder.tvUserGroupChat.setTextColor(mContext.getResources().getColor(R.color.colorNamTrau));
                        t[0] = "Nu";
                    } else if (t[0] == "Nu") {

                        user.setSelected(false);
                        holder.imgCheck.setVisibility(View.INVISIBLE);
                        holder.tvUserGroupChat.setTextColor(mContext.getResources().getColor(R.color.textColor));
                        t[0] = "Nam";

                    }

                }
            });


        }
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    public List<User> getUserList() {
        return listUser;
    }

}

