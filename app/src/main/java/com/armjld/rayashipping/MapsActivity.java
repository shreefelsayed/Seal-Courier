package com.armjld.rayashipping;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.rayashipping.Adapters.DeliveryAdapter;
import com.armjld.rayashipping.Adapters.MyAdapter;
import com.armjld.rayashipping.models.Data;
import com.armjld.rayashipping.models.UserInFormation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;

import java.util.ArrayList;
import java.util.Objects;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnInfoWindowClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener,
        LocationListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int REQUEST_CHECK_SETTINGS = 102;
    private static final String TAG = "Maps";
    public static ArrayList<Data> filterList = new ArrayList<>();
    Location currentLocation;
    GoogleApiClient mGoogleApiClient;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest mLocationRequest;
    RecyclerView recyclerView2;
    private GoogleMap mMap;
    private FloatingActionButton btnGCL;

    // Disable the Back Button
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        recyclerView2 = findViewById(R.id.recyclerView2);

        recyclerView2.setVisibility(View.GONE);

        recyclerView2.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView2.setLayoutManager(layoutManager);

        final LocationManager manager2 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager2.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            fetchLocation();
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //Database

        ImageView btnHome = findViewById(R.id.btnHome);
        btnGCL = findViewById(R.id.btnGCL);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        buildGoogleAPIClient();

        checkLocationPermission();

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("خريطة الشحنات");

        btnHome.setOnClickListener(v -> finish());

        btnGCL.setVisibility(View.GONE);
        btnGCL.setOnClickListener(v -> {
            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) { // Check if GPS is Enabled
                buildAlertMessageNoGps();
            } else {
                fetchLocation();
            }
        });
        fetchLocation();
    }

    private void buildAlertMessageNoGps() {
        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(MapsActivity.this).setMessage("الرجاء فتح اعدادات اللوكيشن ؟").setCancelable(true).setPositiveButton("حسنا", R.drawable.ic_tick_green, (dialogInterface, which) -> {
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            dialogInterface.dismiss();
        }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).setAnimation(R.raw.location).build();
        mBottomSheetDialog.show();
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (fusedLocationProviderClient == null) {
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;
                LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(UserInFormation.getId()).child("latLang").setValue(String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude()));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        buildGoogleAPIClient();
    }

    private void buildGoogleAPIClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        btnGCL.setVisibility(View.VISIBLE);
        checkGPS();
        //Initialize Google Play Services
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleAPIClient();
            mMap.setMyLocationEnabled(true);
        }

        for (int i = 0; i < filterList.size(); i++) {
            Data thisOrder = filterList.get(i);

            String state = thisOrder.getStatue();
            String provider = thisOrder.getProvider();
            String orderId = thisOrder.getId();

            boolean toPick = state.equals("placed") || state.equals("accepted") || state.equals("recived");
            boolean toDelv = state.equals("readyD") || state.equals("supD") || state.equals("capDenied") || state.equals("supDenied");

            if (toPick && !thisOrder.getLat().equals("") && !thisOrder.get_long().equals("")) {
                double newLat = Double.parseDouble(thisOrder.getLat());
                double newLong = Double.parseDouble(thisOrder.get_long());
                LatLng latLng = new LatLng(newLat, newLong);
                addOrder(latLng, state, provider, orderId);
            } else if (toDelv) {
                checkForDelvLocation(thisOrder.getDPhone(), state, provider, orderId);
            }

        }

        if (currentLocation != null) {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        }

        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
    }

    private void addOrder(LatLng latLng, String state, String provider, String id) {
        Drawable pinView = getDrawable(state, provider);
        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(bitmapDescriptorFromVector(MapsActivity.this, pinView)));
        marker.setTag(id);
    }

    private void checkForDelvLocation(String dPhone, String state, String provider, String id) {
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("clientsLocations").child(dPhone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // -------- This client added his location to our Database
                    double _lat = Double.parseDouble(Objects.requireNonNull(snapshot.child("_lat").getValue()).toString());
                    double _long = Double.parseDouble(Objects.requireNonNull(snapshot.child("_long").getValue()).toString());
                    LatLng latLng = new LatLng(_lat, _long);
                    addOrder(latLng, state, provider, id);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private Drawable getDrawable(String state, String provider) {
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_raya_pick);

        boolean toPick = state.equals("placed") || state.equals("accepted") || state.equals("recived");
        boolean toDelv = state.equals("readyD") || state.equals("supD") || state.equals("capDenied") || state.equals("supDenied");

        if (!provider.equals("Esh7nly")) {
            if (toPick) {
                drawable = ContextCompat.getDrawable(this, R.drawable.ic_raya_delv);
            } else if (toDelv) {
                drawable = ContextCompat.getDrawable(this, R.drawable.ic_raya_pick);
            }
        } else {
            if (toPick) {
                drawable = ContextCompat.getDrawable(this, R.drawable.ic_eshh7nly_delv);
            } else if (toDelv) {
                drawable = ContextCompat.getDrawable(this, R.drawable.ic_eshh7nly_pick);
            }
        }

        return drawable;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String gID = (String) marker.getTag();
        Data orderData = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            orderData = filterList.stream().filter(x -> x.getId().equals(gID)).findFirst().get();
        } else {
            for(int i = 0; i < filterList.size(); i ++) {
                if (filterList.get(i).getId().equals(gID)) {
                    orderData = filterList.get(i);
                    break;
                }
            }
        }


        setCardDate(orderData);

        recyclerView2.setVisibility(View.VISIBLE);
        recyclerView2.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slid_up));
        return true;
    }

    private void setCardDate(Data orderData) {
        ArrayList<Data> list = new ArrayList<>();
        list.add(orderData);
        if (UserInFormation.getAccountType().equals("Supervisor")) {
            MyAdapter myAdapter = new MyAdapter(this, list, "Home");
            recyclerView2.setAdapter(myAdapter);
        } else {
            DeliveryAdapter myAdapter = new DeliveryAdapter(this, list, "Home");
            recyclerView2.setAdapter(myAdapter);
        }

    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, Drawable vectorDrawableResourceId) {
        vectorDrawableResourceId.setBounds(0, 0, vectorDrawableResourceId.getIntrinsicWidth(), vectorDrawableResourceId.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawableResourceId.getIntrinsicWidth(), vectorDrawableResourceId.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawableResourceId.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onClick(View view) {
    }

    private void checkGPS() {

        if (mGoogleApiClient == null) {
            buildGoogleAPIClient();
        }

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    fetchLocation();
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException ignored) {
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    break;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                fetchLocation();
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        /*mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MapsActivity.this);
        }*/
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        recyclerView2.setVisibility(View.GONE);
        recyclerView2.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slid_down));
    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, 105);
            } catch (IntentSender.SendIntentException e) {
                mGoogleApiClient.connect();
            }
        } else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 1);
            assert dialog != null;
            dialog.show();
        }
    }

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    if (mGoogleApiClient == null) {
                        buildGoogleAPIClient();
                    }
                    mMap.setMyLocationEnabled(true);
                }

            } else {
                Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }


}