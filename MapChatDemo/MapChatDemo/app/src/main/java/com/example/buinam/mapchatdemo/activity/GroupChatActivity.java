package com.example.buinam.mapchatdemo.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.buinam.mapchatdemo.R;
import com.example.buinam.mapchatdemo.design.ReferenceUrl;
import com.example.buinam.mapchatdemo.model.Messages;
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
import com.turkialkhateeb.materialcolorpicker.ColorChooserDialog;
import com.turkialkhateeb.materialcolorpicker.ColorListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class GroupChatActivity extends AppCompatActivity {
    private Toolbar toolbarChatGroup;
    String idRoomGroupChat;
    String avaRoomGroupChat;
    String sizeGroupChat;
    String nameGroupChat;

    CircleImageView avaGroupChat;
    TextView tvNameGroupChat;
    User currentUser;
    ImageButton imgButtonBack;
    AVLoadingIndicatorView aviLoadChatGroup;
    ListView lvChatGroup;
    ImageButton imgButtonImageChatGroup;
    ImageButton btnSendChatGroup;
    //
    EmojiconEditText emojiconEditText;
    View rootView;
    EmojIconActions emojIcon;
    ImageView emojiButton;
    String widthImageChat;
    String heigthImageChat;
    FirebaseStorage storageImageChat;
    //

    DatabaseReference databaseChat;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    private ArrayList<Messages> arrMessage;
    private ChildEventListener childEventListenerMessage;
    GroupChatAdapter groupChatAdapter;
    int colorChat;
    private static final int MY_PERMISSIONS_REQUEST = 1;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int PICK_FROM_GALLERY = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        colorChat = getApplicationContext().getResources().getColor(R.color.colorPrimary);

        toolbarChatGroup = (Toolbar) findViewById(R.id.toolbarChatGroup);
        setSupportActionBar(toolbarChatGroup);
        imgButtonBack = (ImageButton) findViewById(R.id.imgButtonBackGroupChat);
        avaGroupChat = (CircleImageView) findViewById(R.id.imgAvatarGroupChat);
        tvNameGroupChat = (TextView) findViewById(R.id.tvNameGroupChat);
        aviLoadChatGroup = (AVLoadingIndicatorView) findViewById(R.id.aviLoadChatGroup);
        lvChatGroup = (ListView) findViewById(R.id.lvChatGroup);
        imgButtonImageChatGroup = (ImageButton) findViewById(R.id.imgButtonImageChatGroup);
//        edtMessage = (EditText) findViewById(R.id.inputMsgChatGroup);
        emojiButton = (ImageView) findViewById(R.id.emoji_btn_groupchat);
        storageImageChat = FirebaseStorage.getInstance();
        rootView = findViewById(R.id.root_view_groupchat);
        emojiconEditText = (EmojiconEditText) findViewById(R.id.emojicon_edit_text_groupchat);
        emojIcon = new EmojIconActions(this, rootView, emojiconEditText, emojiButton);
        emojIcon.ShowEmojIcon();
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e("Keyboard", "open");
            }

            @Override
            public void onKeyboardClose() {
                Log.e("Keyboard", "close");
            }
        });
        btnSendChatGroup = (ImageButton) findViewById(R.id.btnSendChatGroup);


        //
        Intent intent = getIntent();
        idRoomGroupChat = intent.getStringExtra("idRoomGroupChat").toString();
        nameGroupChat = intent.getStringExtra("nameGroupChat").toString();
        sizeGroupChat = intent.getStringExtra("sizeGroupChat").toString();
        avaRoomGroupChat = intent.getStringExtra("urlAvarGroupChat").toString();


        imgButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        databaseChat = FirebaseDatabase.getInstance().getReference();

        getCurrentUser(mUser);
        arrMessage = new ArrayList<Messages>();
        groupChatAdapter = new GroupChatAdapter(GroupChatActivity.this, 0, arrMessage);
        lvChatGroup.setAdapter(groupChatAdapter);
        getDataMessage();
        emojiconEditText.addTextChangedListener(mTextWatcher);
        checkFieldsForEmptyValues(databaseChat, currentUser);

        btnSendChatGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDataMessageText();

            }
        });

        getDataRoomGroupChat(idRoomGroupChat);

        databaseChat.child(ReferenceUrl.CHILD_CHATGROUP).child(idRoomGroupChat).child("colorChat").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d("TestColor", "Ton Tai");
                } else
                    databaseChat.child(ReferenceUrl.CHILD_CHATGROUP).child(idRoomGroupChat).child("colorChat").setValue(String.valueOf(colorChat));


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        setColorChat();

        imgButtonImageChatGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermission();
                } else Log.d("NamBv", "API <23");

            }
        });


    }

    //
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
    }//

    //
    public void showDialog() {
        final Dialog dialog = new Dialog(GroupChatActivity.this);

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

    private void chooseImagrGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PICK_FROM_GALLERY);
    }
    //

    /**
     * Checking device has camera hardware or not
     */
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

    private void takenPicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri resultUri = data.getData();
                aviLoadChatGroup.show();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    widthImageChat = String.valueOf(bitmap.getWidth());
                    heigthImageChat = String.valueOf(bitmap.getHeight());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                setDataImageFirebase(resultUri);

            } else if (resultCode == RESULT_CANCELED) {
                Log.d("NamBV", "User cancelled image capture");
            } else {
                Log.d("NamBV", "Sorry! Failed to capture image");
            }
        } else if (requestCode == PICK_FROM_GALLERY) {
            if (resultCode == RESULT_OK) {
                Uri resultUri = data.getData();
                aviLoadChatGroup.show();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    widthImageChat = String.valueOf(bitmap.getWidth());
                    heigthImageChat = String.valueOf(bitmap.getHeight());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                setDataImageFirebase(resultUri);
            } else if (resultCode == RESULT_CANCELED) {
                Log.d("NamBV", "User cancelled gallery");
            }
        }

    }


    private void setColorChat() {
        databaseChat.child(ReferenceUrl.CHILD_CHATGROUP).child(idRoomGroupChat).child("colorChat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String color = dataSnapshot.getValue().toString();
                    GroupChatActivity.this.getWindow().setStatusBarColor(Integer.parseInt(color));
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Integer.parseInt(color)));
                } else {
                    GroupChatActivity.this.getWindow().setStatusBarColor(colorChat);
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorChat));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getDataRoomGroupChat(String idRoomGroupChat) {
        databaseChat.child(ReferenceUrl.CHILD_CHATGROUP).child(idRoomGroupChat).child("nameGroupChat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvNameGroupChat.setText(dataSnapshot.getValue().toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //
        databaseChat.child(ReferenceUrl.CHILD_CHATGROUP).child(idRoomGroupChat).child("urlAvaGroupChat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Glide.with(getApplication())
                        .load(dataSnapshot.getValue().toString())
                        .error(R.drawable.photoview)
                        .centerCrop()
                        .into(avaGroupChat);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            checkFieldsForEmptyValues(databaseChat, currentUser);
        }
    };

    //
    void checkFieldsForEmptyValues(DatabaseReference dataChatTyPing, User currenUserTyPing) {
        final String finalRoomName = idRoomGroupChat;

        String s1 = emojiconEditText.getText().toString();
        if (!s1.equals("")) {
            btnSendChatGroup.setEnabled(true);

            databaseChat.child(ReferenceUrl.CHILD_CHATGROUP).child(idRoomGroupChat).child("colorChat").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String color = dataSnapshot.getValue().toString();
                        Drawable myIcon = getResources().getDrawable(R.drawable.ic_send2);
                        ColorFilter filter = new LightingColorFilter(Integer.parseInt(color), Integer.parseInt(color));
                        myIcon.setColorFilter(filter);
                        btnSendChatGroup.setImageDrawable(myIcon);
                    } else {
                        Drawable myIcon = getResources().getDrawable(R.drawable.ic_send2);
                        ColorFilter filter = new LightingColorFilter(colorChat, colorChat);
                        myIcon.setColorFilter(filter);
                        btnSendChatGroup.setImageDrawable(myIcon);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            //  dataChatTyPing.child(ReferenceUrl.CHILD_CHAT).child(finalRoomName).child("isTyping").child(currenUserTyPing.getUserName()).setValue("true");

        } else {
            //
            btnSendChatGroup.setEnabled(false);
            btnSendChatGroup.setBackgroundResource(R.drawable.ic_send);
            //     dataChatTyPing.child(ReferenceUrl.CHILD_CHAT).child(finalRoomName).child("isTyping").child(currenUserTyPing.getUserName()).setValue("false");
            //
        }
    }

    //
    public void getCurrentUser(FirebaseUser user) {

        String uID = user.getUid();
        databaseChat.child(ReferenceUrl.CHILD_USERS).child(uID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //
    public void getDataMessage() {
        childEventListenerMessage = databaseChat.child(ReferenceUrl.CHILD_CHATGROUP).child(idRoomGroupChat).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);
                arrMessage.add(messages);
                groupChatAdapter.notifyDataSetChanged();

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

    private void setDataImageFirebase(Uri resultUri2) {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date());
            StorageReference storageImageRoomChat = storageImageChat
                    .getReferenceFromUrl("gs://mapchat-144203.appspot.com").child("imagesRoomChat")
                    .child(idRoomGroupChat).child(timeStamp.toString());

            storageImageRoomChat.putFile(resultUri2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    aviLoadChatGroup.hide();
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    setDataImageMessages(downloadUrl.toString());

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    aviLoadChatGroup.hide();
                    Toast.makeText(GroupChatActivity.this, "Không thể gửi tin nhắn!", Toast.LENGTH_SHORT).show();

                }
            });


        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    private void setDataImageMessages(String url) {
        long timeChat = new Date().getTime();
        Messages messages = new Messages();
        messages.setIdUserSend(currentUser.getIdUser());
        messages.setDisplayNameChat(currentUser.getDisplayName());
        messages.setMessage("");
        messages.setTimeChatMessage(String.valueOf(timeChat));
        messages.setUrlImageChat(url);
        messages.setWidthImage(widthImageChat);
        messages.setHeightImage(heigthImageChat);
        databaseChat.child(ReferenceUrl.CHILD_CHATGROUP).child(idRoomGroupChat).child("lastMessage").setValue("Tin nhắn hình");
        databaseChat.child(ReferenceUrl.CHILD_CHATGROUP).child(idRoomGroupChat).child("timeLastMessage").setValue(timeChat);
        databaseChat.child(ReferenceUrl.CHILD_CHATGROUP).child(idRoomGroupChat).child("messages").push().setValue(messages);

        emojiconEditText.setText("");

    }

    //
    public void setDataMessageText() {
        final String finalRoomName = idRoomGroupChat;
        String message = emojiconEditText.getText().toString();
        if (!message.isEmpty()) {
            long timeChat = new Date().getTime();
            Messages messages = new Messages();
            messages.setIdUserSend(currentUser.getIdUser());
            messages.setDisplayNameChat(currentUser.getDisplayName());
            messages.setUrlAvarChat(currentUser.getUrlAvatar());
            messages.setMessage(message);
            messages.setTimeChatMessage(String.valueOf(timeChat));
            messages.setUrlImageChat("");
            databaseChat.child(ReferenceUrl.CHILD_CHATGROUP).child(finalRoomName).child("messages").push().setValue(messages);

            emojiconEditText.setText("");
        }

    }

    //
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            databaseChat.removeEventListener(childEventListenerMessage);
        } catch (Exception e) {
        }
    }

    //
    //Chat Adapter
    public class GroupChatAdapter extends ArrayAdapter<Messages> {
        public GroupChatAdapter(Context context, int resource, List<Messages> items) {
            super(context, resource, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            final Messages m = getItem(position);

            if (m.getIdUserSend().equals(currentUser.getIdUser())) {
                convertView = layoutInflater.inflate(R.layout.list_item_message_right,
                        null);
                TextView txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);
                txtMsg.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View arg0) {
                        new AlertDialog.Builder(getContext())
                                .setTitle("Xoá tin nhắn?")
                                .setMessage("Bạn chắc chắn muốn xoá?")
                                .setNegativeButton("Trở về", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteMessage(m);
                                        dialog.dismiss();
                                    }
                                }).show();

                        return false;
                    }
                });


            } else {
                convertView = layoutInflater.inflate(R.layout.list_item_message_left,
                        null);
                CircleImageView imgUser = (CircleImageView) convertView.findViewById(R.id.img_User);
                Glide.with(convertView.getContext())
                        .load(m.getUrlAvarChat()).placeholder(R.drawable.avatarerror)
                        .error(R.drawable.avatarerror)
                        .centerCrop()
                        .into(imgUser);

            }
            //
            ImageView imgTest = (ImageView) convertView.findViewById(R.id.imgTest);
            imgTest.setClipToOutline(true);
            TextView txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);
            if (m.getIdUserSend().equals(currentUser.getIdUser())) {
                GradientDrawable gd = new GradientDrawable();
                gd.setShape(GradientDrawable.RECTANGLE);
                setColorTextView(gd);
                gd.setCornerRadius(55.0f);
                txtMsg.setBackground(gd);
            }

            final TextView tv_Time = (TextView) convertView.findViewById(R.id.tv_Time);
            Date d = new Date(Long.parseLong(m.getTimeChatMessage()));
            tv_Time.setText(new SimpleDateFormat("dd/MM HH:mm", Locale.UK).format(d));

            if (m.getUrlImageChat().equals("")) {
                txtMsg.setText(m.getMessage());
                imgTest.setVisibility(View.GONE);
                final String[] t = {"Nam"};
                txtMsg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (t[0] == "Nam") {
                            t[0] = "Nu";
                            tv_Time.setVisibility(View.VISIBLE);
                        } else if (t[0] == "Nu") {
                            t[0] = "Nam";
                            tv_Time.setVisibility(View.GONE);
                        }

                    }
                });

            } else {
                imgTest.setVisibility(View.VISIBLE);
                Glide.with(convertView.getContext())
                        .load(m.getUrlImageChat())
                        .centerCrop()
                        .placeholder(R.drawable.backroundimagechat)
                        .error(R.drawable.photoview)
                        .into(imgTest);
                tv_Time.setVisibility(View.VISIBLE);
                txtMsg.setVisibility(View.GONE);

                imgTest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(GroupChatActivity.this, SeeDetailImagesChat.class);
                        i.putExtra("urlImageChat", m.getUrlImageChat().toString());
                        i.putExtra("displayUserSend", m.getDisplayNameChat().toString());
                        i.putExtra("timeSend", m.getTimeChatMessage().toString());
                        i.putExtra("widthImage", m.getWidthImage().toString());
                        i.putExtra("heightImage", m.getHeightImage());
                        startActivity(i);

                    }
                });
            }


            return convertView;
        }

        private void setColorTextView(final GradientDrawable gd) {

            databaseChat.child(ReferenceUrl.CHILD_CHATGROUP).child(idRoomGroupChat).child("colorChat").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String color = dataSnapshot.getValue().toString();
                        gd.setColors(new int[]{Integer.parseInt(color), Integer.parseInt(color)});
                    } else gd.setColors(new int[]{colorChat, colorChat});


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


        public void deleteMessage(Messages m2) {
            databaseChat.child(ReferenceUrl.CHILD_CHATGROUP).child(idRoomGroupChat).child("messages").orderByChild("timeChatMessage").equalTo(m2.getTimeChatMessage()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String key = dataSnapshot.getChildren().iterator().next().getKey();
                    Messages me = dataSnapshot.getChildren().iterator().next().getValue(Messages.class);
                    databaseChat.child(ReferenceUrl.CHILD_CHATGROUP).child(idRoomGroupChat).child("messages").child(key).removeValue();
                    arrMessage.clear();
                    childEventListenerMessage = databaseChat.child(ReferenceUrl.CHILD_CHATGROUP).child(idRoomGroupChat).child("messages").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Messages messages = dataSnapshot.getValue(Messages.class);
                            if (!messages.getMessage().equals("")) {
                                databaseChat.child(ReferenceUrl.CHILD_CHATGROUP).child(idRoomGroupChat).child("lastMessage").setValue(messages.getMessage());
                            } else
                                databaseChat.child(ReferenceUrl.CHILD_CHATGROUP).child(idRoomGroupChat).child("lastMessage").setValue("Tin nhắn hình");

                            arrMessage.add(messages);
                            groupChatAdapter.notifyDataSetChanged();

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

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }


    //inflate menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chatgroup, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Bắt sự kiện cho các item Menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.infoGroupChat: {

                Intent i = new Intent(GroupChatActivity.this, DetailGroupChat.class);
                i.putExtra("idGroupChat", idRoomGroupChat);
                i.putExtra("urlAvarGroupChat", avaRoomGroupChat);
                i.putExtra("nameGroup", nameGroupChat);
                i.putExtra("sizeGroupChat", sizeGroupChat);
                startActivity(i);


            }

            break;
            case R.id.changeColorGroupChat: {
                ColorChooserDialog dialog = new ColorChooserDialog(GroupChatActivity.this);
                dialog.setTitle("Đổi màu trò chuyện nào!");
                dialog.setColorListener(new ColorListener() {
                    @Override
                    public void OnColorClick(View v, int color) {
                        databaseChat.child(ReferenceUrl.CHILD_CHATGROUP).child(idRoomGroupChat).child("colorChat").setValue(String.valueOf(color));
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
                        GroupChatActivity.this.getWindow().setStatusBarColor(color);

                    }
                });
                dialog.show();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }


}
