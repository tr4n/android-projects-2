package com.example.buinam.mapchatdemo.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.buinam.mapchatdemo.R;
import com.example.buinam.mapchatdemo.activity.ChatActivity;
import com.example.buinam.mapchatdemo.design.ReferenceUrl;
import com.example.buinam.mapchatdemo.model.Messages;
import com.example.buinam.mapchatdemo.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

public class MyService extends Service {
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference dataUser;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        dataUser = FirebaseDatabase.getInstance().getReference();
        //

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        dataUser.child(ReferenceUrl.CHILD_USERS).child(mUser.getUid()).child("idRoomChat").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                   // getDataMessage(dataSnapshot.getKey().toString());
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

        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

    }


//    private void getDataMessage(String key) {
//      dataUser.child(ReferenceUrl.CHILD_CHAT).child(key).child("messages").addChildEventListener(new ChildEventListener() {
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
//                                                            intentNotification(u2, u1, m, true);
//                                                        } else intentNotification(u1, u2, m, true);
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

    public void intentNotification(User recentUser, User currentUser, Messages m, boolean a) {
        a=true;
        if(a=false){
            Log.d("OK", "OK");
        } else {
            Intent dismissIntent = new Intent(this, ChatActivity.class);
            dismissIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            dismissIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            Gson gson = new Gson();
            dismissIntent.putExtra(ReferenceUrl.KEY_SEND_USER, gson
                    .toJson(recentUser).toString() + "---" + gson.toJson(currentUser).toString());
            //
            dismissIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            getApplication(),
                            0,
                            dismissIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(getApplication())
                            .setSmallIcon(R.drawable.ic_app)
                            .setContentTitle(m.getDisplayNameChat().toString())
                            .setContentText(m.getMessage().toString())
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setAutoCancel(true)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setContentIntent(resultPendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, builder.build());
        }

    }
}