package com.example.buinam.mapchatdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.buinam.mapchatdemo.R;
import com.example.buinam.mapchatdemo.model.RecentChat;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by buinam on 11/11/16.
 */

public class ListChatAdapter extends RecyclerView.Adapter<ListChatAdapter.MyViewHolder> {
    private List<RecentChat> listRecentChat;
    private Context mContext;

//    FirebaseAuth mAuth;
//    FirebaseUser mUser;
//    String currenUserID;
//    private User currenUser;
    //
    DatabaseReference databaseUser;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView displayUserNameRecentChat;
        public CircleImageView imgAvatarRecentChat;
        public TextView lastMessage;
        public TextView timeLastMessage;

        public MyViewHolder(View view) {
            super(view);

            displayUserNameRecentChat = (TextView) view.findViewById(R.id.tvDisplayUserNameRecentChat);
            imgAvatarRecentChat = (CircleImageView)view.findViewById(R.id.imgAvatarRecentChat);
            lastMessage = (TextView)view.findViewById(R.id.tvLastMessage);
            timeLastMessage = (TextView)view.findViewById(R.id.tvTimeLastMessage);
        }
    }


    public ListChatAdapter(Context mContext, List<RecentChat> listRecentChat) {
        this.listRecentChat = listRecentChat;
        this.mContext = mContext;
    }

    @Override
    public ListChatAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_listchat, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListChatAdapter.MyViewHolder holder, final int position) {
        final RecentChat recentChat = listRecentChat.get(position);

        if (recentChat!=null) {
            holder.displayUserNameRecentChat.setText(recentChat.getDisplayNameRecentChat());
            Glide.with(mContext).load(recentChat.getUrlAvatarRecentChat()).centerCrop().error(R.drawable.avatarerror).into(holder.imgAvatarRecentChat);
            if(recentChat.getTimeLastMessage() !=null){
                Date d = new Date(Long.parseLong(recentChat.getTimeLastMessage()));
//            holder.timeLastMessage.setText(recentChat.getTimeLastMessage());
                holder.timeLastMessage.setText(new SimpleDateFormat("dd/MM HH:mm", Locale.UK).format(d));
            } else holder.timeLastMessage.setText("Nam");


            holder.lastMessage.setText(recentChat.getLastMessage());
        }
    }

    @Override
    public int getItemCount() {
        return listRecentChat.size();
    }

}

