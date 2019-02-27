package com.example.buinam.mapchatdemo.fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.buinam.mapchatdemo.R;
import com.example.buinam.mapchatdemo.activity.AddFriendActivity;
import com.example.buinam.mapchatdemo.activity.ChatActivity;
import com.example.buinam.mapchatdemo.activity.LoginActivity;
import com.example.buinam.mapchatdemo.design.DataParser;
import com.example.buinam.mapchatdemo.design.ReferenceUrl;
import com.example.buinam.mapchatdemo.model.Friend;
import com.example.buinam.mapchatdemo.model.LocationUser;
import com.example.buinam.mapchatdemo.model.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by buinam on 9/21/16.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static String TAG = "thanvanhai";
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference dataUser;

    private GoogleMap mMap;
    ArrayList<LatLng> MarkerPoints;
    GoogleApiClient mGoogleApiClient;
    Location mLocation;
    Marker marker;
    LocationRequest mLocationRequest;
    ProgressDialog myProgress;
    ArrayList<User> arrayFriend;
    User currentUser;
    CoordinatorLayout coordinatorLayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        dataUser = FirebaseDatabase.getInstance().getReference();
        arrayFriend = new ArrayList<>();
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);

        setMap();
        // Cho phép Google Maps truy cập vị trí:
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        //
        MarkerPoints = new ArrayList<>();
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fabMap);
        fab.setBackgroundTintList(ColorStateList.valueOf(Color
                .parseColor("#EA2E49")));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iAddFriend = new Intent(getContext(), AddFriendActivity.class);
                startActivity(iAddFriend);
            }
        });
        return view;


    }


    private GoogleMap.OnMarkerClickListener onMarkerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker3) {
            if (marker3.isInfoWindowShown()) {
//                marker.showInfoWindow();
            } else {
                marker3.showInfoWindow();
            }
            if (marker3.getPosition().toString().equals(setLatLongUser(currentUser))) {
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, marker3.getTitle() + ": " +
                                marker3.getSnippet().toString(), Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                snackbar.show();
            } else {
                showDialog(marker3.getPosition().toString());
            }


            return true;
        }
    };

    public void setMap() {
        SupportMapFragment mSupportMapFragment;
        mSupportMapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mSupportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);


        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // Do not thing!
            }
        });
        getLocationFriend(mMap);


    }

    private void getLocationFriend(final GoogleMap gMap) {
        dataUser.child(ReferenceUrl.CHILD_USERS).child(mUser.getUid()).child("checkListFriend")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.exists()) {
                            Friend f = dataSnapshot.getValue(Friend.class);
                            if (f.getStatus().equals("isFriend")) {
                                getDataUserLocation(f, gMap);
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

    public String setLatLongUser(User u) {
        LocationUser l = u.getLocationUser();
        LatLng latLng = new LatLng(Double.parseDouble(l.getLatitudeUser()), Double.parseDouble(l.getLongitudeUser()));
        String latLog = String.valueOf(latLng);

        return latLog;
    }

    private void getDataUserLocation(Friend f, final GoogleMap gMap) {
        dataUser.child(ReferenceUrl.CHILD_USERS).child(f.getIdUser()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                arrayFriend.add(u);
                LocationUser l = u.getLocationUser();
                addMarkerFriend(u, l, gMap);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void addMarkerFriend(User u, LocationUser l, GoogleMap gMap) {
        Marker marker2 = null;
        LatLng latLng = new LatLng(Double.parseDouble(l.getLatitudeUser()), Double.parseDouble(l.getLongitudeUser()));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(u.getDisplayName());
        markerOptions.snippet("" + getAddress(getActivity(), Double.parseDouble(l.getLatitudeUser()), Double.parseDouble(l.getLongitudeUser())));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(u.getUrlAvatar().toString())));
        marker2 = mMap.addMarker(markerOptions);
        marker2.showInfoWindow();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(final Location location) {
        mLocation = location;
        if (marker != null) {
            marker.remove();
        }
        final LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
// Lấy vị trí hiện tại: Ahihi:
        dataUser.child(ReferenceUrl.CHILD_USERS).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(currentUser.getDisplayName());
                markerOptions.snippet("" + getAddress(getActivity(), location.getLatitude(), location.getLongitude()));
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(currentUser.getUrlAvatar().toString())));
                marker = mMap.addMarker(markerOptions);
                marker.showInfoWindow();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //push long lat FireBase

        if (mUser == null) {
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
            return;
        } else {
            LocationUser l = new LocationUser();
            l.setLatitudeUser(String.valueOf(location.getLatitude()));
            l.setLongitudeUser(String.valueOf(location.getLongitude()));
            l.setAddressUser(String.valueOf(getAddress(getActivity(), location.getLatitude(), location.getLongitude())));
            dataUser.child(ReferenceUrl.CHILD_USERS).child(mUser.getUid()).child("locationUser").setValue(l);

        }
        Log.d("thanvanhai", "" + location.getLatitude() + location.getLongitude());
        Log.d("thanvanhai", "" + getAddress(getContext(), location.getLatitude(), location.getLongitude()));

        // Move Camera:
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(7));

        new CountDownTimer(2000, 2000) {

            @Override
            public void onTick(long l) {
//                Do not something
            }

            @Override
            public void onFinish() {
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);
            }
        }.start();
        // Stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
        mMap.setOnMarkerClickListener(onMarkerClickListener);
    }

    public String getAddress(Context context, double lat, double lng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);

            String add = "";
            add = add + "" + obj.getAddressLine(0); // Số nhà
            add = add + " - " + obj.getSubAdminArea(); // Quận
//            add = add + " - " + obj.getAdminArea(); // Tỉnh
//            add = add + " - " + obj.getCountryName(); // Quốc gia
            return add;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    // Copy:
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

// Tìm đường đi giữa hai vị trí

    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                Log.d("thanvanhai", "onPostExecute: onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
            } else {
                Log.d("thanvanhai", "onPostExecute: without Polylines drawn");
            }
        }
    }

    private Bitmap getMarkerBitmapFromView(String url) {

        View customMarkerView = ((LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
        //  markerImageView.setImageResource(resId);
        Glide.with(getContext()).load(url)
                .centerCrop()
                .into(markerImageView);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        returnedBitmap.compress(Bitmap.CompressFormat.PNG, 4, out);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    public void showDialog(String latlong) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.custom_dialog_marker);
        for (int i = 0; i < arrayFriend.size(); i++) {
            if (setLatLongUser(arrayFriend.get(i)).equals(latlong)) {
                LinearLayout lnlchooseChat = (LinearLayout) dialog.findViewById(R.id.lnlchooseChat);
                final int finalI = i;
                lnlchooseChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent itent = new Intent(getContext(), ChatActivity.class);
                        Gson gson = new Gson();
                        itent.putExtra(ReferenceUrl.KEY_SEND_USER, gson.toJson(arrayFriend.get(finalI)).toString() + "---" + gson.toJson(currentUser).toString());
                        startActivity(itent);
                        //
                        dialog.dismiss();
                    }
                });

                LinearLayout lnlFindTheWay = (LinearLayout) dialog.findViewById(R.id.lnlFindTheWay);
                lnlFindTheWay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LatLng origin = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                        LocationUser l = arrayFriend.get(finalI).getLocationUser();
                        LatLng dest = new LatLng(Double.parseDouble(l.getLatitudeUser()), Double.parseDouble(l.getLongitudeUser()));


                        // Getting URL to the Google Directions API
                        String url = getUrl(origin, dest);
                        Log.d(TAG, "onMapClick: " + url.toString());
                        FetchUrl FetchUrl = new FetchUrl();

                        // Start downloading json data from Google Directions API
                        FetchUrl.execute(url);
                        //move map camera
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

                        int Radius = 6371; // radius of earth in Km
                        double lat1 = origin.latitude;
                        double lat2 = dest.latitude;
                        double lon1 = origin.longitude;
                        double lon2 = dest.longitude;
                        double dLat = Math.toRadians(lat2 - lat1);
                        double dLon = Math.toRadians(lon2 - lon1);
                        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
                        double c = 2 * Math.asin(Math.sqrt(a));
                        double valueResult = Radius * c;
                        double km = valueResult / 1;
                        DecimalFormat newFormat = new DecimalFormat("####");
                        int kmInDec = Integer.valueOf(newFormat.format(km));
                        double meter = valueResult % 1000;
                        int meterInDec = Integer.valueOf(newFormat.format(meter));

                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, valueResult + " KM " + kmInDec + " Meter " + meterInDec, Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.WHITE);
                        snackbar.show();
                        Log.i("thanvanhai", "Radius Value: " + valueResult + "   KM  " + kmInDec + " Meter   " + meterInDec);

                        Log.d("thanvanhai", "D = " + Radius * c);
                        dialog.dismiss();
                    }
                });

                LinearLayout lnlViewProfile = (LinearLayout) dialog.findViewById(R.id.lnlViewProfile);
                lnlViewProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showInfo(arrayFriend.get(finalI));
                        dialog.dismiss();
                    }
                });

            }
        }

        dialog.show();
    }

    private void showInfo(User user) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_dialogdetailfriend);
        ImageView imgExpandedFriend = (ImageView)dialog.findViewById(R.id.expandedImageFriend);
        CircleImageView imgAvatarFriend = (CircleImageView)dialog.findViewById(R.id.imgAvatarFriend);
        TextView tvNameFriend = (TextView)dialog.findViewById(R.id.tvDisplayUserNameFriend);
        TextView tvBithdayFriend = (TextView)dialog.findViewById(R.id.bithdayFriend);
        TextView tvGenderFriend = (TextView)dialog.findViewById(R.id.genderFriend);

        Glide.with(getContext()).load(user.getUrlAvatar())
                .error(R.drawable.avatarerror)
                .centerCrop().into(imgAvatarFriend);
        Glide.with(getContext()).load(user.getUrlCover())
                .error(R.drawable.backgrond)
                .centerCrop().into(imgExpandedFriend);
        tvNameFriend.setText(user.getDisplayName());
        tvBithdayFriend.setText(user.getDayBirthdayUser() + "-" + user.getMonthBirthdayUser() + "-" + user.getYearBirthdayUser());

        if (user.getGenderUser().equals("")){
            tvGenderFriend.setText("Chưa cập nhật");
            tvGenderFriend.setTextColor(getContext().getResources().getColor(R.color.textColor_per));
        } else tvGenderFriend.setText(user.getGenderUser());

        dialog.show();
    }
}
