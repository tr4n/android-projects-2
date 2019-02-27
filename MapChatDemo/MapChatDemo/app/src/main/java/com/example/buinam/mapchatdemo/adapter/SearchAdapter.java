package com.example.buinam.mapchatdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.buinam.mapchatdemo.R;
import com.example.buinam.mapchatdemo.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by buinam on 11/15/16.
 */

public class SearchAdapter extends ArrayAdapter<User> implements Filterable{
    Context context;
    List<User> locationsList;

    public SearchAdapter(Context context, ArrayList<User> objects) {
        super(context, 0, objects);
        this.context=context;
        this.locationsList=objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View v= inflater.inflate(R.layout.custom_listfriend_searchview,null);

        TextView txtName = (TextView) v.findViewById(R.id.tvUserList);
        ImageView imgView = (ImageView) v.findViewById(R.id.img_UserSearch);

        txtName.setText(locationsList.get(position).getDisplayName());
        Glide.with(context).load(locationsList.get(position).getUrlAvatar())

                .error(R.drawable.avatarerror)
                .centerCrop().into(imgView);

        return v;
    }
//    public Filter getFilter() {
//        return new Filter() {
//
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                final FilterResults oReturn = new FilterResults();
//
//                Log.d("tag",constraint.toString());
//                final ArrayList<String> results = new ArrayList<String>();
//                if (orig == null)
//                    orig = locationsList;
//                if (constraint != null) {
//                    if (orig != null && orig.size() > 0) {
//                        for (final String g : orig) {
//                            if (g.toLowerCase()
//                                    .contains(constraint.toString()))
//                                results.add(g);
//
//                        }
//                    }
//                    oReturn.values = results;
//                }
//                return oReturn;
//            }
//
//            @SuppressWarnings("unchecked")
//            @Override
//            protected void publishResults(CharSequence constraint,
//                                          Filter.FilterResults results) {
//                locationsList = (ArrayList<String>) results.values;
//                notifyDataSetChanged();
//            }
//        };
//    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return locationsList.size();
    }

    @Override
    public User getItem(int position) {
        return locationsList.get(position);
    }

}
