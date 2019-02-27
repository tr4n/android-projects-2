package com.example.buinam.mapchatdemo.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.view.WindowManager;
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
import com.example.buinam.mapchatdemo.model.RoomChat;
import com.example.buinam.mapchatdemo.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.koushikdutta.ion.Ion;
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

// Giao diện chat
public class ChatActivity extends AppCompatActivity {
    private User receiverUser;
    private User currenUser;
    String roomName = "";
    private ArrayList<Messages> arrMessage;
    private ChatAdapter chatAdapter;
    private DatabaseReference dataChat;
    private ChildEventListener childEventListenerMessage;
    RoomChat roomChatCurrentChat;
    RoomChat roomChatRecentChat;
    ListView lvChat;
    //    EditText edtChat;
    ImageButton btChat;
    ImageButton imageButtonCamera;
    CircleImageView imgIsTyPing;
    LinearLayout lnlIsTyPing;
    ProgressDialog mProgressDialog;
    AVLoadingIndicatorView avLoadingIndicatorView;
    String widthImageChat;
    String heigthImageChat;
    ImageView isTypingGif;
    //
    EmojiconEditText emojiconEditText;
    View rootView;
    EmojIconActions emojIcon;
    ImageView emojiButton;

    private Toolbar toolbarChat;
    // Use camera
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int PICK_FROM_GALLERY = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "MapChat";
    private Uri fileUri;
    private static final int MY_PERMISSIONS_REQUEST = 1;
    FirebaseStorage storageImageChat;
    int colorChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //
        lvChat = (ListView) findViewById(R.id.list_view_messages);
        avLoadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.aviLoadImage);
        //
        isTypingGif = (ImageView) findViewById(R.id.isTypingGif);
        Ion.with(isTypingGif).load("android.resource://com.example.buinam.mapchatdemo/" + R.drawable.istyping_gif);
//        edtChat = (EditText) findViewById(R.id.inputMsg);
        emojiButton = (ImageView) findViewById(R.id.emoji_btn);
        rootView = findViewById(R.id.root_view);
        emojiconEditText = (EmojiconEditText) findViewById(R.id.emojicon_edit_text);
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
        //

        btChat = (ImageButton) findViewById(R.id.btnSend);
        imageButtonCamera = (ImageButton) findViewById(R.id.imgButtonImage);
        imgIsTyPing = (CircleImageView) findViewById(R.id.imgIsTyPing);
        lnlIsTyPing = (LinearLayout) findViewById(R.id.lnlIsTyPing);
        dataChat = FirebaseDatabase.getInstance().getReference();
        arrMessage = new ArrayList<Messages>();
        chatAdapter = new ChatAdapter(ChatActivity.this, 0, arrMessage);
        lvChat.setAdapter(chatAdapter);
        //
        mProgressDialog = new ProgressDialog(this);
        //Storge
        storageImageChat = FirebaseStorage.getInstance();

        setUserChat();
        Glide.with(getApplicationContext()).load(receiverUser.getUrlAvatar()).centerCrop().into(imgIsTyPing);

        // set toolbar
        toolbarChat = (Toolbar) findViewById(R.id.toolbarChat);
        setSupportActionBar(toolbarChat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(receiverUser.getDisplayName());


        ChatActivity.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ChatActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);


        getDataMessage();
        //

        emojiconEditText.addTextChangedListener(mTextWatcher);
        checkFieldsForEmptyValues(dataChat, currenUser);
        setUserIsTyping();
        //
        dataChat.child(ReferenceUrl.CHILD_CHAT).child(roomName).child("usersChat").child(receiverUser.getIdUser()).setValue(receiverUser.getDisplayName());
        dataChat.child(ReferenceUrl.CHILD_CHAT).child(roomName).child("usersChat").child(currenUser.getIdUser()).setValue(currenUser.getDisplayName());

        colorChat = getApplicationContext().getResources().getColor(R.color.colorPrimary);

        dataChat.child(ReferenceUrl.CHILD_CHAT).child(roomName).child("colorChat").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d("TestColor", "Ton Tai");
                } else dataChat.child(ReferenceUrl.CHILD_CHAT).child(roomName).child("colorChat").setValue(String.valueOf(colorChat));


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        setColorChat();
        setUserReadMessage();

        btChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRoomChatInUser();
                setDataMessageText();

            }
        });

        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermission();
                } else Log.d("NamBv", "API <23");

            }
        });

//        Intent play = new Intent(ChatActivity.this, MyService.class);
//        stopService(play);

    }

    private void setUserReadMessage() {
        dataChat.child(ReferenceUrl.CHILD_CHAT).child(roomName).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()){
                    Messages m = dataSnapshot.getValue(Messages.class);
                    if(m.getIdUserReceive().equals(currenUser.getIdUser())){
                        dataChat.child(ReferenceUrl.CHILD_CHAT).child(roomName).child("messages")
                                .child(dataSnapshot.getKey().toString()).child("status").setValue("Read");
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

    private void setColorChat() {
        dataChat.child(ReferenceUrl.CHILD_CHAT).child(roomName).child("colorChat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String color = dataSnapshot.getValue().toString();
                    ChatActivity.this.getWindow().setStatusBarColor(Integer.parseInt(color));
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Integer.parseInt(color)));
                } else {
                    ChatActivity.this.getWindow().setStatusBarColor(colorChat);
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorChat));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //
    public void showDialog() {
        final Dialog dialog = new Dialog(ChatActivity.this);

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
                avLoadingIndicatorView.show();
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
                avLoadingIndicatorView.show();
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


    //
    private void setDataImageFirebase(Uri resultUri2) {
        try {
            final String finalRoomName = roomName;
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date());
            StorageReference storageImageRoomChat = storageImageChat
                    .getReferenceFromUrl("gs://mapchat-144203.appspot.com").child("imagesRoomChat")
                    .child(finalRoomName).child(timeStamp.toString());

            storageImageRoomChat.putFile(resultUri2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    avLoadingIndicatorView.hide();
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    setRoomChatInUser();
                    setDataImageMessages(downloadUrl.toString());

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    avLoadingIndicatorView.hide();
                    Toast.makeText(ChatActivity.this, "Không thể gửi tin nhắn!", Toast.LENGTH_SHORT).show();

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
    private void showErrorMessageToUser(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setMessage(errorMessage)
                .setTitle("Lỗi")
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            checkFieldsForEmptyValues(dataChat, currenUser);
        }
    };

    void checkFieldsForEmptyValues(DatabaseReference dataChatTyPing, User currenUserTyPing) {
        final String finalRoomName = roomName;
        dataChatTyPing.child(ReferenceUrl.CHILD_CHAT).child(finalRoomName).child("isTyping").child(currenUser.getUserName()).setValue("false");
        dataChatTyPing.child(ReferenceUrl.CHILD_CHAT).child(finalRoomName).child("isTyping").child(receiverUser.getUserName()).setValue("false");

        String s1 = emojiconEditText.getText().toString();
        if (!s1.equals("")) {
            btChat.setEnabled(true);

            dataChat.child(ReferenceUrl.CHILD_CHAT).child(roomName).child("colorChat").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        String color = dataSnapshot.getValue().toString();
                        Drawable myIcon = getResources().getDrawable( R.drawable.ic_send2 );
                        ColorFilter filter = new LightingColorFilter( Integer.parseInt(color), Integer.parseInt(color) );
                        myIcon.setColorFilter(filter);
                        btChat.setImageDrawable(myIcon);
                    } else {
                        Drawable myIcon = getResources().getDrawable( R.drawable.ic_send2 );
                        ColorFilter filter = new LightingColorFilter( colorChat, colorChat );
                        myIcon.setColorFilter(filter);
                        btChat.setImageDrawable(myIcon);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            dataChatTyPing.child(ReferenceUrl.CHILD_CHAT).child(finalRoomName).child("isTyping").child(currenUserTyPing.getUserName()).setValue("true");

        } else {
            //
            btChat.setEnabled(false);
            btChat.setBackgroundResource(R.drawable.ic_send);
            dataChatTyPing.child(ReferenceUrl.CHILD_CHAT).child(finalRoomName).child("isTyping").child(currenUserTyPing.getUserName()).setValue("false");
            //
        }
    }

    //
    public void setUserChat() {
        String jsonReceiverUser = getIntent().getStringExtra(ReferenceUrl.KEY_SEND_USER).split("---")[0];
        String jsonCurrenUser = getIntent().getStringExtra(ReferenceUrl.KEY_SEND_USER).split("---")[1];
        Gson gson = new Gson();
        receiverUser = gson.fromJson(jsonReceiverUser, User.class);
        currenUser = gson.fromJson(jsonCurrenUser, User.class);
        long createReceiverUser = Long.parseLong(receiverUser.getTimeRegisterUser());
        long createCurrenUser = Long.parseLong(currenUser.getTimeRegisterUser());
        if (createReceiverUser > createCurrenUser) {
            roomName = String.valueOf(createReceiverUser) + String.valueOf(createCurrenUser);
        } else {
            roomName = String.valueOf(createCurrenUser) + String.valueOf(createReceiverUser);
        }
    }

    public void getDataMessage() {
        childEventListenerMessage = dataChat.child(ReferenceUrl.CHILD_CHAT).child(roomName).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);
                arrMessage.add(messages);
                chatAdapter.notifyDataSetChanged();

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
    public void setUserIsTyping() {
        final String finalRoomName = roomName;
        dataChat.child(ReferenceUrl.CHILD_CHAT).child(finalRoomName).child("isTyping").child(receiverUser.getUserName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    if (dataSnapshot.getValue().toString().equals("true")) {
                        lnlIsTyPing.setVisibility(View.VISIBLE);
                    } else lnlIsTyPing.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setDataImageMessages(String url) {
        final String finalRoomName = roomName;
        long timeChat = new Date().getTime();
        Messages messages = new Messages();
        messages.setIdUserSend(currenUser.getIdUser());
        messages.setIdUserReceive(receiverUser.getIdUser());
        messages.setDisplayNameChat(currenUser.getDisplayName());
        messages.setMessage("");
        messages.setStatus("UnRead");
        messages.setTimeChatMessage(String.valueOf(timeChat));
        messages.setUrlImageChat(url);
        messages.setWidthImage(widthImageChat);
        messages.setHeightImage(heigthImageChat);
        dataChat.child(ReferenceUrl.CHILD_CHAT).child(finalRoomName).child("lastMessage").setValue("Tin nhắn hình");
        dataChat.child(ReferenceUrl.CHILD_CHAT).child(finalRoomName).child("timeLastMessage").setValue(timeChat);
        dataChat.child(ReferenceUrl.CHILD_CHAT).child(finalRoomName).child("messages").push().setValue(messages);

        emojiconEditText.setText("");
    }

    //
    public void setDataMessageText() {
        final String finalRoomName = roomName;
        String message = emojiconEditText.getText().toString();
        if (!message.isEmpty()) {
            long timeChat = new Date().getTime();
            Messages messages = new Messages();
            messages.setIdUserSend(currenUser.getIdUser());
            messages.setIdUserReceive(receiverUser.getIdUser());
            messages.setDisplayNameChat(currenUser.getDisplayName());
            messages.setStatus("UnRead");
            messages.setMessage(message);
            messages.setTimeChatMessage(String.valueOf(timeChat));
            messages.setUrlImageChat("");
            dataChat.child(ReferenceUrl.CHILD_CHAT).child(finalRoomName).child("lastMessage").setValue(message);
            dataChat.child(ReferenceUrl.CHILD_CHAT).child(finalRoomName).child("timeLastMessage").setValue(timeChat);
            dataChat.child(ReferenceUrl.CHILD_CHAT).child(finalRoomName).child("messages").push().setValue(messages);

            emojiconEditText.setText("");
        }

    }

    public void setRoomChatInUser() {
        final String finalRoomName = roomName;
        roomChatRecentChat = new RoomChat();
        roomChatCurrentChat = new RoomChat();
        roomChatCurrentChat.setIdUserChat(receiverUser.getIdUser());
        roomChatRecentChat.setIdUserChat(currenUser.getIdUser());
        dataChat.child(ReferenceUrl.CHILD_USERS).child(receiverUser.getIdUser()).child(ReferenceUrl.KEY_IDROOMCHAT).child(finalRoomName).setValue(roomChatRecentChat);
        dataChat.child(ReferenceUrl.CHILD_USERS).child(currenUser.getIdUser()).child(ReferenceUrl.KEY_IDROOMCHAT).child(finalRoomName).setValue(roomChatCurrentChat);
    }

    //
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            dataChat.removeEventListener(childEventListenerMessage);
        } catch (Exception e) {
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }

    //Chat Adapter
    public class ChatAdapter extends ArrayAdapter<Messages> {
        public ChatAdapter(Context context, int resource, List<Messages> items) {
            super(context, resource, items);
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            final Messages m = getItem(position);

            if (m.getIdUserSend().equals(currenUser.getIdUser())) {
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
                        .load(receiverUser.getUrlAvatar())
                        .centerCrop()
                        .into(imgUser);


            }
            //
            ImageView imgTest = (ImageView) convertView.findViewById(R.id.imgTest);
            imgTest.setClipToOutline(true);
            TextView txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);
            if (m.getIdUserSend().equals(currenUser.getIdUser())) {
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
                        Intent i = new Intent(ChatActivity.this, SeeDetailImagesChat.class);
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
            dataChat.child(ReferenceUrl.CHILD_CHAT).child(roomName).child("colorChat").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
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
            dataChat.child(ReferenceUrl.CHILD_CHAT).child(roomName).child("messages").orderByChild("timeChatMessage").equalTo(m2.getTimeChatMessage()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String key = dataSnapshot.getChildren().iterator().next().getKey();
                    Messages me = dataSnapshot.getChildren().iterator().next().getValue(Messages.class);
                    dataChat.child(ReferenceUrl.CHILD_CHAT).child(roomName).child("messages").child(key).removeValue();
                    arrMessage.clear();
                    childEventListenerMessage = dataChat.child(ReferenceUrl.CHILD_CHAT).child(roomName).child("messages").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Messages messages = dataSnapshot.getValue(Messages.class);
                            if(!messages.getMessage().equals("")){
                                dataChat.child(ReferenceUrl.CHILD_CHAT).child(roomName).child("lastMessage").setValue(messages.getMessage());
                            } else dataChat.child(ReferenceUrl.CHILD_CHAT).child(roomName).child("lastMessage").setValue("Tin nhắn hình");

                            arrMessage.add(messages);
                            chatAdapter.notifyDataSetChanged();

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
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Bắt sự kiện cho các item Menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.info:
                showDialogDetailFriend();
                break;
            case R.id.deleteRoomChat:
            {
                new AlertDialog.Builder(ChatActivity.this)
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
                                deleteRecentChat();
                                dialog.dismiss();
                            }
                        }).show();
            }
                break;
            case R.id.changecolorchat: {
                ColorChooserDialog dialog = new ColorChooserDialog(ChatActivity.this);
                dialog.setTitle("Đổi màu trò chuyện nào!");
                dialog.setColorListener(new ColorListener() {
                    @Override
                    public void OnColorClick(View v, int color) {
                        dataChat.child(ReferenceUrl.CHILD_CHAT).child(roomName).child("colorChat").setValue(String.valueOf(color));
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
                        ChatActivity.this.getWindow().setStatusBarColor(color);

                    }
                });
                dialog.show();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteRecentChat() {
        dataChat.child(ReferenceUrl.CHILD_USERS).child(currenUser.getIdUser())
                .child("idRoomChat").child(roomName).removeValue();
        Toast.makeText(getApplication(), "Đã xoá cuộc trò chuyện rồi nhé", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(ChatActivity.this, MainActivity.class);
        ChatActivity.this.finish();
        startActivity(i);


    }

    public void showDialogDetailFriend() {
        final Dialog dialog = new Dialog(ChatActivity.this);
        dialog.setContentView(R.layout.custom_dialogdetailfriend);
        ImageView imgExpandedFriend = (ImageView)dialog.findViewById(R.id.expandedImageFriend);
        CircleImageView imgAvatarFriend = (CircleImageView)dialog.findViewById(R.id.imgAvatarFriend);
        TextView tvNameFriend = (TextView)dialog.findViewById(R.id.tvDisplayUserNameFriend);
        TextView tvBithdayFriend = (TextView)dialog.findViewById(R.id.bithdayFriend);
        TextView tvGenderFriend = (TextView)dialog.findViewById(R.id.genderFriend);

        Glide.with(ChatActivity.this).load(receiverUser.getUrlAvatar())
                .error(R.drawable.avatarerror)
                .centerCrop().into(imgAvatarFriend);
        Glide.with(ChatActivity.this).load(receiverUser.getUrlCover())
                .error(R.drawable.backgrond)
                .centerCrop().into(imgExpandedFriend);
        tvNameFriend.setText(receiverUser.getDisplayName());
        tvBithdayFriend.setText(receiverUser.getDayBirthdayUser() + "-" + receiverUser.getMonthBirthdayUser() + "-" + receiverUser.getYearBirthdayUser());

        if (receiverUser.getGenderUser().equals("")){
            tvGenderFriend.setText("Chưa cập nhật");
            tvGenderFriend.setTextColor(ChatActivity.this.getResources().getColor(R.color.textColor_per));
        } else tvGenderFriend.setText(receiverUser.getGenderUser());

        dialog.show();
    }

}
