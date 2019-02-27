package com.example.buinam.mapchatdemo.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.buinam.mapchatdemo.R;
import com.example.buinam.mapchatdemo.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by buinam on 12/1/16.
 */

public class DetailGroupChatAdapter extends RecyclerView.Adapter<DetailGroupChatAdapter.MyViewHolder> {
    private List<User> friendList;
    private Context mContext;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView displayUserName;
        public CircleImageView imgAvatar;
        public ImageButton imgMenu;

        public MyViewHolder(View view) {
            super(view);
            displayUserName = (TextView) view.findViewById(R.id.tvDisplayUserName);
            imgAvatar = (CircleImageView) view.findViewById(R.id.imgAvatar);
            imgMenu = (ImageButton) view.findViewById(R.id.imgMenu);
        }
    }


    public DetailGroupChatAdapter(Context mContext, List<User> friendList) {
        this.friendList = friendList;
        this.mContext = mContext;
    }

    @Override
    public DetailGroupChatAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_listuser_detailgroupchat, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DetailGroupChatAdapter.MyViewHolder holder, final int position) {
        final User listFriend = friendList.get(position);

        if (listFriend != null) {
            holder.displayUserName.setText(listFriend.getDisplayName());
            Glide.with(mContext).load(listFriend.getUrlAvatar())
                    .centerCrop()
                    .error(R.drawable.avatarerror)
                    .into(holder.imgAvatar);

            //Bắt sự kiện PopupMenu
            holder.imgMenu.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick(View v) {
                    showPopupMenu(holder.imgMenu, position);
                }
            });
        }
    }

    private void showPopupMenu(View view, final int position) {
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_listfriend, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.info:
                        showDialog(position);
                        return true;
                    case R.id.location:
                        Toast.makeText(mContext, "Xem vị trí", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                }
                return false;
            }
        });
        popup.show();
    }

    public void showDialog(int position) {
        User u = friendList.get(position);
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.custom_dialogdetailfriend);
        ImageView imgExpandedFriend = (ImageView)dialog.findViewById(R.id.expandedImageFriend);
        CircleImageView imgAvatarFriend = (CircleImageView)dialog.findViewById(R.id.imgAvatarFriend);
        TextView tvNameFriend = (TextView)dialog.findViewById(R.id.tvDisplayUserNameFriend);
        TextView tvBithdayFriend = (TextView)dialog.findViewById(R.id.bithdayFriend);
        TextView tvGenderFriend = (TextView)dialog.findViewById(R.id.genderFriend);

        Glide.with(mContext).load(u.getUrlAvatar())
                .error(R.drawable.avatarerror)
                .centerCrop().into(imgAvatarFriend);
        Glide.with(mContext).load(u.getUrlCover())
                .error(R.drawable.backgrond)
                .centerCrop().into(imgExpandedFriend);
        tvNameFriend.setText(u.getDisplayName());
        tvBithdayFriend.setText(u.getDayBirthdayUser() + "-" + u.getMonthBirthdayUser() + "-" + u.getYearBirthdayUser());

        if (u.getGenderUser().equals("")){
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
