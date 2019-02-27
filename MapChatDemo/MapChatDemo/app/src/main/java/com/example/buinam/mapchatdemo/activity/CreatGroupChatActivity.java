package com.example.buinam.mapchatdemo.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.buinam.mapchatdemo.R;
import com.example.buinam.mapchatdemo.adapter.ListFriendGroupChatAdapter;
import com.example.buinam.mapchatdemo.adapter.ListUserChooseGroupChatAdapter;
import com.example.buinam.mapchatdemo.design.ReferenceUrl;
import com.example.buinam.mapchatdemo.model.Friend;
import com.example.buinam.mapchatdemo.model.RoomGroupChat;
import com.example.buinam.mapchatdemo.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.string.ok;


public class CreatGroupChatActivity extends AppCompatActivity {
    Toolbar toolbarGroupChat;
    CircleImageView circleImageViewAva;
    AVLoadingIndicatorView avLoadingIndicatorView;
    //
    private ArrayList<User> userArrayList;
    private RecyclerView rvListFriend;
    //
    private ArrayList<User> userArrayListChoose;
    private ArrayList<User> userArrayListChoose2;
    private ArrayList<String> listIdRoomGroupChat;

    private RecyclerView rvListFriendChoose;
    private EditText edtNameGroup;
    //
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ProgressDialog mProgressDialog;
    //
    DatabaseReference databaseUser;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    String currenUserID;
    String currenUserEmail;
    private ArrayList<String> arrayUserName;
    private User currenUser;
    ListFriendGroupChatAdapter lGroupChatadapter;
    ListUserChooseGroupChatAdapter lChooseAdapter;
    //use camera
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int PICK_FROM_GALLERY = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    String urlAvaRoomGroupChat = "";
    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "MapChat";
    private Uri fileUri;
    private static final int MY_PERMISSIONS_REQUEST = 1;
    FirebaseStorage storageImageChat;
    Uri uriAva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_group_chat);
        //
        toolbarGroupChat = (Toolbar) findViewById(R.id.toolbarCreatGroupChat);
        setSupportActionBar(toolbarGroupChat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Tạo nhóm chat");
        avLoadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.aviCreatGroupChat);
        edtNameGroup = (EditText) findViewById(R.id.edtNameGroup);

        circleImageViewAva = (CircleImageView) findViewById(R.id.imgAvaGroupChat);
        circleImageViewAva.setImageResource(R.drawable.ic_photo_camera);

        //
        listIdRoomGroupChat = new ArrayList<>();
        mProgressDialog = new ProgressDialog(this);
        storageImageChat = FirebaseStorage.getInstance();
        //
        rvListFriendChoose = (RecyclerView) findViewById(R.id.recyclerviewListUserChoose);

        LinearLayoutManager layoutManagerChoose = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManagerChoose.scrollToPosition(0);
        rvListFriendChoose.setLayoutManager(layoutManagerChoose);
        rvListFriendChoose.setHasFixedSize(true);
        rvListFriendChoose.setItemAnimator(new DefaultItemAnimator());

        rvListFriend = (RecyclerView) findViewById(R.id.recyclerviewListUser);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvListFriend.setHasFixedSize(true);
        rvListFriend.setLayoutManager(mLayoutManager);
        rvListFriend.setItemAnimator(new DefaultItemAnimator());


        userArrayListChoose = new ArrayList<User>();
        userArrayListChoose2 = new ArrayList<User>();

        lChooseAdapter = new ListUserChooseGroupChatAdapter(this, userArrayListChoose);
        rvListFriendChoose.setAdapter(lChooseAdapter);
        //
        userArrayList = new ArrayList<User>();
        lGroupChatadapter = new ListFriendGroupChatAdapter(this, userArrayList);
        rvListFriend.setAdapter(lGroupChatadapter);

        arrayUserName = new ArrayList<>();
        databaseUser = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            currenUserID = mUser.getUid();
            currenUserEmail = mUser.getEmail().toString();
            getCurrentUser(currenUserID);
            getListIdRoomGroupChat(mUser);
            getDataFriend(mUser);
            //

        }

        circleImageViewAva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermission();
                } else Log.d("NamBv", "API <23");
            }
        });


    }

    private void getCurrentUser(final String currenUserID) {
        databaseUser.child(ReferenceUrl.CHILD_USERS).child(currenUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currenUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //
    public void showDialog() {
        final Dialog dialog = new Dialog(CreatGroupChatActivity.this);

        dialog.setContentView(R.layout.custom_dialogimage);

        LinearLayout lnlTakePicture = (LinearLayout) dialog.findViewById(R.id.lnltakepicture);
        lnlTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takenPicture();
                if (!isDeviceSupportCamera()) {
                    Toast.makeText(getApplicationContext(),
                            "Sorry! Your device doesn't support camera",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
                dialog.dismiss();
            }
        });

        LinearLayout lnlChooseImage = (LinearLayout) dialog.findViewById(R.id.lnlchooseImage);
        lnlChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImagrGallery();
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    //
    private void chooseImagrGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PICK_FROM_GALLERY);
    }

    private void takenPicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri resultUri = data.getData();
                CropImage.activity(resultUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);
            } else if (resultCode == RESULT_CANCELED) {
                Log.d("NamBV", "User cancelled image capture");
            } else {
                // failed to capture image
                Log.d("NamBV", "Sorry! Failed to capture image");
            }
        } else if (requestCode == PICK_FROM_GALLERY) {
            if (resultCode == RESULT_OK) {

                Uri resultUri = data.getData();
                CropImage.activity(resultUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);
            } else if (resultCode == RESULT_CANCELED) {
                Log.d("NamBV", "User cancelled gallery");
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri2 = result.getUri();
                uriAva = resultUri2;
                Glide.with(getApplication())
                        .load(resultUri2)
                        .error(R.drawable.photoview)
                        .centerCrop()
                        .into(circleImageViewAva);
                setDataImageFirebase(resultUri2);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d("NamBV", error + "");
            }
        }

    }

    private void setDataImageFirebase(Uri resultUri2) {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date());
            StorageReference storageImageRoomChat = storageImageChat
                    .getReferenceFromUrl("gs://mapchat-144203.appspot.com")
                    .child("AvatarGroupChat").child(timeStamp.toString());

            storageImageRoomChat.putFile(resultUri2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    urlAvaRoomGroupChat = downloadUrl.toString();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CreatGroupChatActivity.this, "Fail!", Toast.LENGTH_SHORT).show();

                }
            });


        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    //

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d("size", ">> " + grantResults.length);
        if (requestCode == 1) {

            if ((grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    && (grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    && (grantResults[2] == PackageManager.PERMISSION_GRANTED)) {
                showDialog();
            }

        }
    }
    //

    private void checkPermission() {
        Log.d("checkPermission", "run");
        String[] listPermission = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        boolean isOn = false;

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            showDialog();
        } else {
//            showErrorMessageToUser("Không thể gửi tin nhắn hình ảnh!");
            isOn = true;
        }
        if (isOn) {
            Log.d("request", "go");
            ActivityCompat.requestPermissions(this, listPermission, MY_PERMISSIONS_REQUEST);
        }
    }

    //
    public ArrayList<User> setArrayUserGroupChat() {

        List<User> uList = ((ListFriendGroupChatAdapter) lGroupChatadapter)
                .getUserList();
        userArrayListChoose2.clear();
        for (int i = 0; i < uList.size(); i++) {
            if (uList.get(i).isSelected() == true) {
                userArrayListChoose2.add(uList.get(i));
            }
        }
        userArrayListChoose2.add(currenUser);
        return userArrayListChoose2;

    }

    public String setIDRoomGroupChat(ArrayList<User> listUser) {
        Collections.sort(listUser, new Comparator<User>() {
            @Override
            public int compare(User rc1, User rc2) {
                if ((Long.parseLong(rc1.getTimeRegisterUser())) < (Long.parseLong(rc2.getTimeRegisterUser()))) {
                    return 1;
                } else {
                    if ((Long.parseLong(rc1.getTimeRegisterUser())) == (Long.parseLong(rc2.getTimeRegisterUser()))) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            }
        });
        String idRoomGroupChat = "";
        for (int i = 0; i < listUser.size(); i++) {
            idRoomGroupChat = idRoomGroupChat + listUser.get(i).getTimeRegisterUser();
        }
        return idRoomGroupChat;

    }

    //
    private void getListIdRoomGroupChat(FirebaseUser user) {
        String idUser = user.getUid().toString();
        databaseUser.child(ReferenceUrl.CHILD_USERS).child(idUser).child("idRoomGroupChat").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                RoomGroupChat roomGroupChat = new RoomGroupChat();
                roomGroupChat = dataSnapshot.getValue(RoomGroupChat.class);
                listIdRoomGroupChat.add(roomGroupChat.getIdGroupChat().toString());

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


    public void checkIntentGroupChat(String nameGroupChat) {
        if (uriAva != null) {
            if (!urlAvaRoomGroupChat.equals("")) {
                mProgressDialog.hide();
                String idRoomGroupChat = setIDRoomGroupChat(setArrayUserGroupChat());
                RoomGroupChat rGoupChat = new RoomGroupChat();
                rGoupChat.setIdGroupChat(idRoomGroupChat);
                for (int i = 0; i < setArrayUserGroupChat().size(); i++) {
                    databaseUser.child(ReferenceUrl.CHILD_CHATGROUP).child(idRoomGroupChat).child("usersChat").child(setArrayUserGroupChat().get(i).getIdUser()).setValue(setArrayUserGroupChat().get(i).getDisplayName());
                    databaseUser.child(ReferenceUrl.CHILD_USERS).child(setArrayUserGroupChat().get(i).getIdUser()).child(ReferenceUrl.KEY_IDROOMGROUPCHAT).child(idRoomGroupChat).setValue(rGoupChat);
                    databaseUser.child(ReferenceUrl.CHILD_CHATGROUP).child(idRoomGroupChat).child("isTyping").child(setArrayUserGroupChat().get(i).getIdUser()).setValue("false");
                }
                databaseUser.child(ReferenceUrl.CHILD_CHATGROUP).child(idRoomGroupChat).child("nameGroupChat").setValue(nameGroupChat.toString());
                databaseUser.child(ReferenceUrl.CHILD_CHATGROUP).child(idRoomGroupChat).child("urlAvaGroupChat").setValue(urlAvaRoomGroupChat.toString());
                databaseUser.child(ReferenceUrl.CHILD_CHATGROUP).child(idRoomGroupChat).child("sizeGroupChat").setValue(String.valueOf(setArrayUserGroupChat().size()));


                Intent i = new Intent(this, GroupChatActivity.class);
                i.putExtra("idRoomGroupChat", idRoomGroupChat.toString());
                i.putExtra("urlAvarGroupChat", urlAvaRoomGroupChat);
                i.putExtra("nameGroupChat", nameGroupChat);
                i.putExtra("sizeGroupChat", setArrayUserGroupChat().size());





                startActivity(i);
                CreatGroupChatActivity.this.finish();
            } else {
                mProgressDialog.setMessage("Đang load nhé");
                mProgressDialog.show();
            }
        } else {
            Toast.makeText(this, "Chưa chọn ảnh nhé!", Toast.LENGTH_SHORT).show();
            mProgressDialog.hide();
        }
    }


    public void setRoomGroupChat() {
        String nameGroupChat = edtNameGroup.getText().toString();
        if (!nameGroupChat.equals("")) {
            if (setArrayUserGroupChat().size() >= 3) {
                checkRoomGroupChat(setIDRoomGroupChat(setArrayUserGroupChat()), nameGroupChat);
            } else {
                mProgressDialog.hide();
                showErrorMessageToUser("Chọn nhóm từ 3 người trở nên nhé!");

            }
        } else {
            mProgressDialog.hide();
            edtNameGroup.setError("Điền tên đi nhé!");
        }


    }

    private void checkRoomGroupChat(String idRoomGroupChat, String nameGroupChat) {
        if (listIdRoomGroupChat.contains(idRoomGroupChat)) {
            mProgressDialog.hide();
            showErrorMessageToUser("Nhóm đã tồn tại rồi nhé!");
        } else {
            checkIntentGroupChat(nameGroupChat);

        }
    }


    //
    private void getDataFriend(FirebaseUser cUser) {
        databaseUser.child(ReferenceUrl.CHILD_USERS).child(cUser.getUid()).child("checkListFriend").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Friend f = dataSnapshot.getValue(Friend.class);
                if (f.getStatus().equals("isFriend")) {
                    setDataRecycler(f);
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

    private void setDataRecycler(Friend f) {
        String idUser = f.getIdUser();
        databaseUser.child(ReferenceUrl.CHILD_USERS).child(idUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                userArrayList.add(u);
                avLoadingIndicatorView.hide();
                lGroupChatadapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onStop() {
        super.onStop();
        try {
            mAuth.removeAuthStateListener(mAuthStateListener);
        } catch (Exception e) {
        }
    }

    //
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_groupchat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Bắt sự kiện cho các item Menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.creatGroupChat: {
                setRoomGroupChat();
            }

            break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showErrorMessageToUser(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreatGroupChatActivity.this);
        builder.setMessage(errorMessage)
                .setTitle(getString(R.string.creat_group_chat))
                .setPositiveButton(ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
