package com.example.buinam.mapchatdemo.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.buinam.mapchatdemo.R;
import com.example.buinam.mapchatdemo.design.ReferenceUrl;
import com.example.buinam.mapchatdemo.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.example.buinam.mapchatdemo.R.id.editInfoPer;

/**
 * Created by buinam on 9/27/16.
 */
// Fragment Trang ca nhan
public class PersonalPageFragment extends Fragment {

    private Toolbar toolbarP;
    CollapsingToolbarLayout collapsingToolbar;
    ImageView expandedImage;
    CircleImageView imgAvatarPer;
    TextView tvDisPlayNamePer;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    //
    DatabaseReference databaseUser;
    int i = 1;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    LinearLayout lnlFeedBack;
    LinearLayout lnlInfoMapChat;
    LinearLayout lnlShare;
    //
    AVLoadingIndicatorView avLoadingIndicatorView;
    TextView birthdayUser;
    TextView genderUser;
    LinearLayout lnlLogout;
    EditText edtNameUser;
    ImageButton imgChangeBirthDay;
    ImageButton imgGenderUser;
    private int mYear, mMonth, mDay;
    private static final int MY_PERMISSIONS_REQUEST = 1;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int PICK_FROM_GALLERY = 200;
    FirebaseStorage storageImageChat;

    public PersonalPageFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_personalpage, container, false);
        setHasOptionsMenu(true);
        //
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        databaseUser = FirebaseDatabase.getInstance().getReference();
//        lnlLogout = (LinearLayout) view.findViewById(R.id.lnlLogout);
//        edtNameUser = (EditText) view.findViewById(R.id.edtNameUser);
//        imgChangeBirthDay = (ImageButton) view.findViewById(R.id.imgChangeBirthDayUser);
//        imgGenderUser = (ImageButton) view.findViewById(R.id.imgChangeGenderUser);
//        storageImageChat = FirebaseStorage.getInstance();
//        lnlFeedBack = (LinearLayout) view.findViewById(R.id.lnlFeedBack);
//        lnlInfoMapChat = (LinearLayout) view.findViewById(R.id.lnlInfoMapChat);
//        lnlShare = (LinearLayout) view.findViewById(R.id.lnlShare);

//        Window w = getActivity().getWindow();
//        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        toolbarP = (Toolbar) view.findViewById(R.id.toolbarPer);
//        toolbarP.inflateMenu(R.menu.menu_personal);
//        //
//        customMenuPer();
//        avLoadingIndicatorView = (AVLoadingIndicatorView) view.findViewById(R.id.aviPer);
//        birthdayUser = (TextView) view.findViewById(R.id.bithdayUser);
//        genderUser = (TextView) view.findViewById(R.id.genderUser);
////
//        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fabPer);
//        fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EA2E49")));
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                checkPermission();
//            }
//        });
//
//        collapsingToolbar = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
//        //
//        expandedImage = (ImageView) view.findViewById(R.id.expandedImage);
//        imgAvatarPer = (CircleImageView) view.findViewById(R.id.imgAvatarPer);
//        tvDisPlayNamePer = (TextView) view.findViewById(R.id.tvDisplayUserNamePer);
//        //
//        if (mUser != null) {
//            getDataUser(mUser);
//
//        } else {
//            startActivity(new Intent(getContext(), LoginActivity.class));
//            getActivity().finish();
//
//        }
//        //
//        // Share MapChat:
//        lnlShare.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String urlToShare = "https://play.google.com/store/apps/details?id=com.ubercab&hl=vi";
//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.setType("text/plain");
////                intent.putExtra(Intent.EXTRA_SUBJECT, "Foo bar"); // NB: has no effect!
//                intent.putExtra(Intent.EXTRA_TEXT, urlToShare);
//
//                // See if official Facebook app is found
//                intent.setPackage("com.facebook.katana");
//                boolean facebookAppFound = false;
//                List<ResolveInfo> list = getActivity().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
//                if (list.size() > 0){
//                    facebookAppFound = true;
//                }
//                // As fallback, launch sharer.php in a browser
//                if (!facebookAppFound) {
//                    String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + urlToShare;
//                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
//                }
//
//                startActivity(intent);
//            }
//        });
//        // FeedBack:
//        lnlFeedBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("thanvanhai", "lnlFeedBack");
//                Intent intent_email = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "thanhaibkk58@gmail.com", null));
//                intent_email.putExtra(Intent.EXTRA_SUBJECT, "Android MapChat - Feedback");
//                intent_email.putExtra(Intent.EXTRA_TEXT, "Content:");
//                startActivity(Intent.createChooser(intent_email, "Send email"));
//            }
//        });
//        //Info MapChat:
//        lnlInfoMapChat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), AboutActivity.class));
//            }
//        });
//
//
//        //Logout
//        lnlLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new AlertDialog.Builder(getContext())
//                        .setTitle("Đăng Xuất?")
//                        .setMessage("Bạn chắc chắn muốn đăng xuất?")
//                        .setNegativeButton("Trở về", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        })
//                        .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                if (mUser != null) {
//                                    Glide.clear(imgAvatarPer);
//                                    databaseUser.child(ReferenceUrl.CHILD_USERS).child(mUser.getUid()).child(ReferenceUrl.CHILD_CONNECTION).setValue(ReferenceUrl.KEY_OFFLINE);
//                                    mAuth.removeAuthStateListener(mAuthStateListener);
//                                    mAuth.signOut();
//                                    startActivity(new Intent(getContext(), LoginActivity.class));
//                                    getActivity().finish();
//                                }
//                                dialog.dismiss();
//                            }
//                        }).show();
//
//            }
//        });
//
//        imgChangeBirthDay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                final Calendar c = Calendar.getInstance();
////                mYear = c.get(Calendar.YEAR);
////                mMonth = c.get(Calendar.MONTH);
////                mDay = c.get(Calendar.DAY_OF_MONTH);
//                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
//                        new DatePickerDialog.OnDateSetListener() {
//
//                            @Override
//                            public void onDateSet(DatePicker view, int year,
//                                                  int monthOfYear, int dayOfMonth) {
//                                birthdayUser.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
//                                databaseUser.child(ReferenceUrl.CHILD_USERS).child(mUser.getUid()).child("dayBirthdayUser").setValue(String.valueOf(dayOfMonth));
//                                databaseUser.child(ReferenceUrl.CHILD_USERS).child(mUser.getUid()).child("monthBirthdayUser").setValue(String.valueOf(monthOfYear + 1));
//                                databaseUser.child(ReferenceUrl.CHILD_USERS).child(mUser.getUid()).child("yearBirthdayUser").setValue(String.valueOf(year));
//
//                            }
//                        }, mYear, mMonth - 1, mDay);
//                datePickerDialog.show();
//            }
//        });
//        imgGenderUser.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final Dialog dialog = new Dialog(getContext());
//
//                dialog.setContentView(R.layout.custom_dialog_changegender);
//                final RadioButton radioButton1 = (RadioButton) dialog.findViewById(R.id.radioButton1);
//                final RadioButton radioButton2 = (RadioButton) dialog.findViewById(R.id.radioButton2);
//                final RadioButton radioButton3 = (RadioButton) dialog.findViewById(R.id.radioButton3);
//                TextView tvOK = (TextView) dialog.findViewById(R.id.ok);
//                TextView tvCancel = (TextView) dialog.findViewById(R.id.exit);
//                if (genderUser.getText().equals("Không rõ")) {
//                    radioButton1.setChecked(false);
//                    radioButton2.setChecked(false);
//                    radioButton3.setChecked(true);
//                } else if (genderUser.getText().equals("Nam")) {
//                    radioButton1.setChecked(true);
//                    radioButton2.setChecked(false);
//                    radioButton3.setChecked(false);
//                } else if (genderUser.getText().equals("Nữ")) {
//                    radioButton1.setChecked(false);
//                    radioButton2.setChecked(true);
//                    radioButton3.setChecked(false);
//                }
//                tvCancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//                tvOK.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        if (radioButton1.isChecked()) {
//                            databaseUser.child(ReferenceUrl.CHILD_USERS).child(mUser.getUid()).child("genderUser").setValue("Nam");
//
//                        } else if (radioButton2.isChecked()) {
//                            databaseUser.child(ReferenceUrl.CHILD_USERS).child(mUser.getUid()).child("genderUser").setValue("Nữ");
//
//
//                        } else if (radioButton3.isChecked()) {
//                            databaseUser.child(ReferenceUrl.CHILD_USERS).child(mUser.getUid()).child("genderUser").setValue("");
//
//                        }
//                        dialog.dismiss();
//                    }
//                });
//
//
//                dialog.show();
//            }
//        });


        return view;
    }

    //
    public void showDialog() {
        final Dialog dialog = new Dialog(getContext());

        dialog.setContentView(R.layout.custom_dialogimage);

        LinearLayout lnlTakePicture = (LinearLayout) dialog.findViewById(R.id.lnltakepicture);
        lnlTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takenPicture();
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


    //
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri resultUri = data.getData();
                CropImage.activity(resultUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(getContext(), this);
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
                        .start(getContext(), this);
            } else if (resultCode == RESULT_CANCELED) {
                Log.d("NamBV", "User cancelled gallery");
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                avLoadingIndicatorView.show();
                Uri resultUri2 = result.getUri();

                setDataImageFirebase(resultUri2);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d("Checkcam", error + "");
            }
        }

    }

    private void setDataImageFirebase(Uri resultUri2) {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date());
            StorageReference storageImageRoomChat = storageImageChat
                    .getReferenceFromUrl("gs://mapchat-144203.appspot.com")
                    .child("AvatarUser").child(timeStamp.toString());

            storageImageRoomChat.putFile(resultUri2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    //       Toast.makeText(getContext(), "" + downloadUrl.toString(), Toast.LENGTH_SHORT).show();
                     updateAvaUser(downloadUrl.toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Fail!", Toast.LENGTH_SHORT).show();

                }
            });


        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void updateAvaUser(String url) {
        databaseUser.child(ReferenceUrl.CHILD_USERS).child(mUser.getUid()).child("urlAvatar").setValue(url.toString());
        Glide.with(getContext()).load(url)
                .centerCrop()
                .error(R.drawable.avatarerror)
                .into(imgAvatarPer);
        avLoadingIndicatorView.hide();
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

        if ((ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            showDialog();
        } else {
            isOn = true;
        }
        if (isOn) {
            Log.d("request", "go");
            ActivityCompat.requestPermissions(getActivity(), listPermission, MY_PERMISSIONS_REQUEST);
        }
    }

    public void getDataUser(FirebaseUser user) {
        String uID = user.getUid();
        databaseUser.child(ReferenceUrl.CHILD_USERS).child(uID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    avLoadingIndicatorView.hide();
                    User u = dataSnapshot.getValue(User.class);
//                    Glide.with(PersonalPageFragment.this).load(u.getUrlAvatar())
//                            .centerCrop()
//                            .into(imgAvatarPer);
//                    Glide.with(PersonalPageFragment.this).load(u.getUrlCover())
//                            .centerCrop()
//                            .into(expandedImage);
                    tvDisPlayNamePer.setText(u.getDisplayName());
                    edtNameUser.setText(u.getDisplayName());
                    mDay = Integer.parseInt(u.getDayBirthdayUser().toString());
                    mMonth = Integer.parseInt(u.getMonthBirthdayUser().toString());
                    mYear = Integer.parseInt(u.getYearBirthdayUser().toString());

                    birthdayUser.setText(u.getDayBirthdayUser() + "-" + u.getMonthBirthdayUser() + "-" + u.getYearBirthdayUser());

                    if (u.getGenderUser().equals("")) {
                        genderUser.setText("Không rõ");
                    } else {
                        genderUser.setText(u.getGenderUser());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void customMenuPer() {
        toolbarP.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == editInfoPer) {
                    if (i == 1) {
                        menuItem.setIcon(getResources().getDrawable(R.drawable.ic_save));
                        edtNameUser.setVisibility(View.VISIBLE);
                        tvDisPlayNamePer.setVisibility(View.GONE);

                        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                        edtNameUser.requestFocus();
                        i = 2;
                        inputMethodManager.showSoftInput(edtNameUser, 0);
                    } else if (i == 2) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                        menuItem.setIcon(getResources().getDrawable(R.drawable.ic_edit));
                        updateInfoUser();
                        edtNameUser.setVisibility(View.GONE);
                        tvDisPlayNamePer.setVisibility(View.VISIBLE);
                        i = 1;
                    }

                    return true;
                }


                return false;
            }
        });
    }

    private void updateInfoUser() {
        databaseUser.child(ReferenceUrl.CHILD_USERS).child(mUser.getUid()).child("displayName").setValue(edtNameUser.getText().toString());

    }


}
