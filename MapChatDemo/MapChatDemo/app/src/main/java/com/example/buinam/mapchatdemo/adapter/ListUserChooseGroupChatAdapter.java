package com.example.buinam.mapchatdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.buinam.mapchatdemo.R;
import com.example.buinam.mapchatdemo.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by buinam on 11/22/16.
 */

public class ListUserChooseGroupChatAdapter extends RecyclerView.Adapter<ListUserChooseGroupChatAdapter.MyViewHolder> {
    private List<User> listUser;
    private Context mContext;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView imgAvatarChooseGroupChat;
        public TextView tvUserChooseGroupChat;

        public MyViewHolder(View view) {
            super(view);

            tvUserChooseGroupChat = (TextView)view.findViewById(R.id.tviewUserChooseGroupChat) ;
            imgAvatarChooseGroupChat = (CircleImageView)view.findViewById(R.id.imgAvatarListUserChooseGroupChat);

        }
    }


    public ListUserChooseGroupChatAdapter(Context mContext, List<User> listUser) {
        this.listUser = listUser;
        this.mContext = mContext;
    }

    @Override
    public ListUserChooseGroupChatAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_listuserchoosegroupchat, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListUserChooseGroupChatAdapter.MyViewHolder holder, final int position) {
        final User user = listUser.get(position);

        if (user!=null) {
            Glide.with(mContext).load(user.getUrlAvatar())
                    .placeholder(R.drawable.avatarerror)
                    .error(R.drawable.avatarerror)
                    .centerCrop()
                    .into(holder.imgAvatarChooseGroupChat);
            holder.tvUserChooseGroupChat.setText(user.getDisplayName().toString());
        }
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

}


