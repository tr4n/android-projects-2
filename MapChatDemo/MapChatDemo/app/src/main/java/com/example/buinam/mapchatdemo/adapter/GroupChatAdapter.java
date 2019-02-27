package com.example.buinam.mapchatdemo.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.buinam.mapchatdemo.R;
import com.example.buinam.mapchatdemo.activity.DetailGroupChat;
import com.example.buinam.mapchatdemo.activity.GroupChatActivity;
import com.example.buinam.mapchatdemo.model.RecentGroupChat;

import java.util.List;

/**
 * Created by buinam on 11/25/16.
 */

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.MyViewHolder> {

    private Context mContext;
    private List<RecentGroupChat> listRecentGroupChat;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nameGroupChat, sizeGroupChat;
        public ImageView avaGroupChat, chooseGroupChat;

        public MyViewHolder(View view) {
            super(view);
            nameGroupChat= (TextView) view.findViewById(R.id.nameGroupChat);
            sizeGroupChat = (TextView) view.findViewById(R.id.sizeGroupChat);
            avaGroupChat = (ImageView) view.findViewById(R.id.avaGroupChat);
            chooseGroupChat = (ImageView) view.findViewById(R.id.overflow);
        }
    }


    public GroupChatAdapter(Context mContext, List<RecentGroupChat> listGroupChat) {
        this.mContext = mContext;
        this.listRecentGroupChat = listGroupChat;
    }

    @Override
    public GroupChatAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_card_listgroupchat, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final GroupChatAdapter.MyViewHolder holder, final int position) {
        final RecentGroupChat rcGroupChat = listRecentGroupChat.get(position);
        holder.nameGroupChat.setText(rcGroupChat.getNameGroupChat());
        holder.sizeGroupChat.setText(rcGroupChat.getSizeGroupChat() + " thành viên");
        Glide.with(mContext).load(rcGroupChat.getUrlAvatarGroupChat())
                .error(R.drawable.avatarerror)
                .centerCrop().into(holder.avaGroupChat);
        holder.avaGroupChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, GroupChatActivity.class);
                i.putExtra("idRoomGroupChat", rcGroupChat.getIdRoomGroupChat().toString());
                i.putExtra("urlAvarGroupChat", rcGroupChat.getUrlAvatarGroupChat().toString());
                i.putExtra("nameGroupChat", rcGroupChat.getNameGroupChat().toString());
                i.putExtra("sizeGroupChat", rcGroupChat.getSizeGroupChat().toString());


                mContext.startActivity(i);
            }
        });

        holder.chooseGroupChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.chooseGroupChat, position);
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view, final int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_card_groupchat, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.seePeople:
                        showDialogListUserGroupChat(position);
                        return true;
                    case R.id.exitGroupChat:
                    {
                        new AlertDialog.Builder(mContext)
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
                                        deleteRecentChat(position);
                                        dialog.dismiss();
                                    }
                                }).show();
                    }
                        return true;
                    default:
                }
                return false;
            }
        });
        popup.show();
    }

    private void deleteRecentChat(int po) {


    }

    private void showDialogListUserGroupChat(int position) {
        RecentGroupChat rcGroupChat = listRecentGroupChat.get(position);
        String idGroupChat = rcGroupChat.getIdRoomGroupChat();
        String urlAvarGroupChat = rcGroupChat.getUrlAvatarGroupChat();
        String nameGroup = rcGroupChat.getNameGroupChat();
        String sizeGroupChat = rcGroupChat.getSizeGroupChat();
        Intent i = new Intent(mContext, DetailGroupChat.class);
        i.putExtra("idGroupChat", idGroupChat);
        i.putExtra("urlAvarGroupChat", urlAvarGroupChat);
        i.putExtra("nameGroup", nameGroup);
        i.putExtra("sizeGroupChat", sizeGroupChat);

        mContext.startActivity(i);


    }


    @Override
    public int getItemCount() {
        return listRecentGroupChat.size();
    }
}
