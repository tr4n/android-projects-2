package com.example.buinam.mapchatdemo.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.buinam.mapchatdemo.R;
import com.example.buinam.mapchatdemo.adapter.ViewPagerAdapter;
import com.example.buinam.mapchatdemo.design.ReferenceUrl;
import com.example.buinam.mapchatdemo.fragment.GroupChatFragment;
import com.example.buinam.mapchatdemo.fragment.HomeFragment;
import com.example.buinam.mapchatdemo.fragment.ListChatFragment;
import com.example.buinam.mapchatdemo.fragment.PersonalPageFragment;
import com.example.buinam.mapchatdemo.model.User;
import com.example.buinam.mapchatdemo.service.MyService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// Giao diện màn hình chính, gồm viewpager, các Fragment và Tab Layout
public class MainActivity extends AppCompatActivity {
    private static final int C_FRAGMENTS_TO_KEEP_IN_MEMORY = 100;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.ic_home,
            R.drawable.ic_chat,
            R.drawable.ic_group,
            R.drawable.ic_social_person
    };
    //
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference dataUser;
    //
    LocationManager locationManager;
    private static final int GPS_REQUEST_CODE = 600;

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == GPS_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                Log.e("thanvanhai", "Ok");
//            } else if (resultCode == RESULT_CANCELED) {
//                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                    builder.setMessage("Để truy cập ứng dụng, bạn phải sử dụng GPS. Bật GPS?")
//                            .setCancelable(false)
//                            .setPositiveButton("Cài đặt", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                                    startActivityForResult(intent, GPS_REQUEST_CODE);
//                                }
//                            })
//                            .setNegativeButton("Thoát ứng dụng", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    dialog.cancel();
//                                    System.exit(1);
//                                }
//                            });
//                    AlertDialog alert = builder.create();
//                    alert.show();
//                }
//            } else {
//                Log.e("thanvanhai", "?");
//            }
//        }}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        // Turn On GPS:
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Để truy cập ứng dụng, bạn phải sử dụng GPS. Bật GPS?")
                    .setCancelable(false)
                    .setPositiveButton("Cài đặt", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, GPS_REQUEST_CODE);
                        }
                    })
                    .setNegativeButton("Thoát ứng dụng", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            System.exit(1);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.setOffscreenPageLimit(C_FRAGMENTS_TO_KEEP_IN_MEMORY);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        //Check user null
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        dataUser = FirebaseDatabase.getInstance().getReference();



        if (mUser == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            MainActivity.this.finish();
            return;
        } else {
            getDataFriend(mUser);
            Intent play = new Intent(MainActivity.this, MyService.class);
            startService(play);
           // checkMessageUnRead(mUser);
        }


    }

//    private void checkMessageUnRead(FirebaseUser mUser) {
//        dataUser.child(ReferenceUrl.CHILD_USERS).child(mUser.getUid()).child("idRoomChat").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                if (dataSnapshot.exists()) {
//                    getDataMessage(dataSnapshot.getKey().toString());
//                }
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    private void getDataMessage(String key) {
//        dataUser.child(ReferenceUrl.CHILD_CHAT).child(key).child("messages").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                if (dataSnapshot.exists()) {
//                    final Messages m = dataSnapshot.getValue(Messages.class);
//                    if ((m.getIdUserReceive().equals(mUser.getUid())) && (m.getStatus().equals("UnRead"))) {
//
//                        dataUser.child(ReferenceUrl.CHILD_USERS).child(m.getIdUserSend())
//                                .addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                        final User u1 = dataSnapshot.getValue(User.class);
//                                        dataUser.child(ReferenceUrl.CHILD_USERS).child(m.getIdUserReceive())
//                                                .addListenerForSingleValueEvent(new ValueEventListener() {
//                                                    @Override
//                                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                                        User u2 = dataSnapshot.getValue(User.class);
//                                                        if (u1.getIdUser().equals(mUser.getUid())) {
//                                                            intentNotification(u2, u1, m);
//                                                        } else intentNotification(u1, u2, m);
//
//                                                    }
//
//                                                    @Override
//                                                    public void onCancelled(DatabaseError databaseError) {
//
//                                                    }
//                                                });
//                                    }
//
//                                    @Override
//                                    public void onCancelled(DatabaseError databaseError) {
//
//                                    }
//                                });
//
//
//                    }
//                }
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//    }

//    public void intentNotification(User recentUser, User currentUser, Messages m) {
//        Intent dismissIntent = new Intent(MainActivity.this, ChatActivity.class);
//        Gson gson = new Gson();
//        dismissIntent.putExtra(ReferenceUrl.KEY_SEND_USER, gson
//                .toJson(recentUser).toString() + "---" + gson.toJson(currentUser).toString());
//        //
//        dismissIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent resultPendingIntent =
//                PendingIntent.getActivity(
//                        MainActivity.this,
//                        0,
//                        dismissIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//
//        NotificationCompat.Builder builder =
//                new NotificationCompat.Builder(getApplication())
//                        .setSmallIcon(R.drawable.ic_app)
//                        .setContentTitle(m.getDisplayNameChat().toString())
//                        .setContentText(m.getMessage().toString())
//                        .setPriority(NotificationCompat.PRIORITY_HIGH)
//                        .setAutoCancel(true)
//                        .setDefaults(Notification.DEFAULT_ALL)
//                        .setContentIntent(resultPendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(0, builder.build());
//    }


    private void getDataFriend(final FirebaseUser cUser) {
        dataUser.child(ReferenceUrl.CHILD_USERS).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    final User user = dataSnapshot.getValue(User.class);
                    if (!dataSnapshot.getKey().equals(cUser.getUid())) {
                        dataUser.child(ReferenceUrl.CHILD_USERS).child(cUser.getUid()).child("checkListFriend")
                                .child(user.getIdUser()).child("idUser").setValue(user.getIdUser());
                        //
                        dataUser.child(ReferenceUrl.CHILD_USERS).child(cUser.getUid()).child("checkListFriend")
                                .child(user.getIdUser())
                                .child("status").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {

                                    dataUser.child(ReferenceUrl.CHILD_USERS).child(cUser.getUid()).child("checkListFriend")
                                            .child(user.getIdUser()).child("status").setValue("NoFriend");

                                } else {
                                    Log.d("Ok", "ton tai");

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    } else {
                        Log.d("NAmBV", "Ok");
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

    }
    //

    // Set icon Tablayout
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);

        tabLayout.getTabAt(0).getIcon().mutate().setColorFilter(Color.parseColor("#235150"), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().mutate().setColorFilter(Color.parseColor("#b6b6b6"), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).getIcon().mutate().setColorFilter(Color.parseColor("#b6b6b6"), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(3).getIcon().mutate().setColorFilter(Color.parseColor("#b6b6b6"), PorterDuff.Mode.SRC_IN);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().mutate().setColorFilter(Color.parseColor("#235150"), PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().mutate().setColorFilter(Color.parseColor("#b6b6b6"), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    // Set ViewPager
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "Home");
        adapter.addFragment(new ListChatFragment(), "Chat");
        adapter.addFragment(new GroupChatFragment(), "GroupChat");
        adapter.addFragment(new PersonalPageFragment(), "PersonalPage");
        viewPager.setAdapter(adapter);
    }

}
