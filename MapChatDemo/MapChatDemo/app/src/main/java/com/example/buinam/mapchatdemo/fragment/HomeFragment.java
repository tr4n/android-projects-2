package com.example.buinam.mapchatdemo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.buinam.mapchatdemo.R;
import com.example.buinam.mapchatdemo.activity.LoginActivity;
import com.example.buinam.mapchatdemo.adapter.SearchAdapter;
import com.example.buinam.mapchatdemo.design.ConnectivityReceiver;
import com.example.buinam.mapchatdemo.design.InternetCheck;
import com.example.buinam.mapchatdemo.design.ReferenceUrl;
import com.example.buinam.mapchatdemo.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.example.buinam.mapchatdemo.R.id.listFriend;

/**
 * Created by buinam on 9/27/16.
 */

public class HomeFragment extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener {
    private Toolbar toolbar;
    //
    private MaterialSearchView searchView;
    int i = 1;
    FragmentManager manager;
    View view;
    TextView tvConnectInternet;


    FirebaseAuth.AuthStateListener mAuthStateListener;
    String currenUserID;
    private ArrayList<String> arrayUserName;
    private ArrayList<User> friendArrayList;

    DatabaseReference databaseUser;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    SearchAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);
        toolbar = (Toolbar) view.findViewById(R.id.toolbarHome);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        friendArrayList = new ArrayList<>();
        databaseUser = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        //
        tvConnectInternet = (TextView) view.findViewById(R.id.tvConnectInternet);
        //check internet
        checkConnection();
        //Set Map when start App
        manager = this.getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.flMap, new MapFragment());
        transaction.addToBackStack(null);
        transaction.commit();
        //
        //getlistUser
        arrayUserName = new ArrayList<>();
        databaseUser = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser == null) {
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        } else {
            currenUserID = mUser.getUid();
            getAllUser(mUser);
        }
        //

        // set search View
        searchView = (MaterialSearchView) view.findViewById(R.id.search_view);
        searchView.setVoiceSearch(true);
        adapter = new SearchAdapter(getContext(), friendArrayList);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                getFriendListSearh(newText);
                return true;
            }

        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });

        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("adapter", String.valueOf(i));

                searchView.closeSearch();
                searchView.setQuery(adapterView.getAdapter().getItem(i).toString(), false);
                User u = (User) adapterView.getAdapter().getItem(i);
                Toast.makeText(getContext(), u.getIdUser(), Toast.LENGTH_SHORT).show();
            }
        });


        return view;


    }

    private void getFriendListSearh(String newText) {
        if(newText.equals("")){
            searchView.setAdapter(adapter);

        }
        else {
            for(int i =0; i < friendArrayList.size(); i++){
                if(!friendArrayList.get(i).getDisplayName().equals(newText)){
                    friendArrayList.remove(i);
                }
            }
            searchView.setAdapter(adapter);
        }

    }
    //


    //ham check internet
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showTextviewConnect(isConnected);
    }

    private void showTextviewConnect(boolean isConnected) {
        if (isConnected) {
            tvConnectInternet.setVisibility(View.GONE);
            databaseUser.child(ReferenceUrl.CHILD_USERS).child(mUser.getUid()).child(ReferenceUrl.CHILD_CONNECTION).setValue(ReferenceUrl.KEY_ONLINE);
        } else {
            tvConnectInternet.setVisibility(View.VISIBLE);
            databaseUser.child(ReferenceUrl.CHILD_USERS).child(mUser.getUid()).child(ReferenceUrl.CHILD_CONNECTION).setValue(ReferenceUrl.KEY_OFFLINE);
        }


    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem Item = menu.findItem(listFriend);
        if (i == 1) {
            Item.setIcon(R.drawable.ic_list_friend);
        } else if (i == 2) {
            Item.setIcon(R.drawable.ic_map);
        }

        MenuItem searchMenuItem = menu.findItem(R.id.search);
        searchView.setMenuItem(searchMenuItem);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == listFriend) {
            if (i == 1) {
                manager = this.getChildFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.flMap, new ListFriendFragment());
                transaction.addToBackStack(null);
                transaction.commit();
                item.setIcon(getResources().getDrawable(R.drawable.ic_map));
                i = 2;
            } else if (i == 2) {
                manager = this.getChildFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.flMap, new MapFragment());

                transaction.addToBackStack(null);
                transaction.commit();
                item.setIcon(getResources().getDrawable(R.drawable.ic_list_friend));
                i = 1;
            }

            return true;
        }
        if (id == R.id.search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //
    @Override
    public void onResume() {

        super.onResume();
        InternetCheck.getInstance().setConnectivityListener(this);

//        if(getView() == null){
//            return;
//        }
//
//        getView().setFocusableInTouchMode(true);
//        getView().requestFocus();
//        getView().setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
//                    // handle back button's click listener
//                    //
//                    //
//
//                    searchView.closeSearch();
//                    return true;
//                }
//                return false;
//            }
//        });

    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showTextviewConnect(isConnected);
    }

    private void getAllUser(FirebaseUser firebaseUser) {

        databaseUser.child(ReferenceUrl.CHILD_USERS).addChildEventListener(ChildEventListenerAllDataUser);
    }

    private ChildEventListener ChildEventListenerAllDataUser = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            if (dataSnapshot.exists()) {
                User user = dataSnapshot.getValue(User.class);
                if (!dataSnapshot.getKey().equals(currenUserID)) {
                    arrayUserName.add(user.getUserName());
                    friendArrayList.add(user);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d("NamBV", "");
                }
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            if (dataSnapshot.exists()) {
                if (!dataSnapshot.getKey().equals(currenUserID)) {
                    User user = dataSnapshot.getValue(User.class);
                    int index = arrayUserName.indexOf(user.getUserName());
                    friendArrayList.set(index, user);
                    adapter.notifyDataSetChanged();

                }
            }

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
    };

    @Override
    public void onStop() {
        super.onStop();
        try {
            mAuth.removeAuthStateListener(mAuthStateListener);
        } catch (Exception e) {
        }
        try {
            databaseUser.removeEventListener(ChildEventListenerAllDataUser);
        } catch (Exception e) {
        }
    }

}
