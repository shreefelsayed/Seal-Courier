package com.armjld.rayashipping.SuperCaptins;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import androidx.fragment.app.FragmentActivity;

import com.armjld.rayashipping.Chat.Messages;
import com.armjld.rayashipping.Chat.chatListclass;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.models.UserInFormation;
import com.armjld.rayashipping.models.userData;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.hypertrack.sdk.views.DeviceUpdatesHandler;
import com.hypertrack.sdk.views.HyperTrackViews;
import com.hypertrack.sdk.views.dao.Location;
import com.hypertrack.sdk.views.dao.MovementStatus;
import com.hypertrack.sdk.views.dao.StatusUpdate;
import com.hypertrack.sdk.views.dao.Trip;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import timber.log.Timber;

public class MapCaptinTrack extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int REQUEST_CHECK_SETTINGS = 102;
    private static final int PHONE_CALL_CODE = 100;
    private static final String PUBLISHABLE_KEY = "00F4wm01NAr4ZHVE4gjrtQiQw8BxYC9dJNjeoM0LE4eEpm928geMz-2Tt8bZgOzJnTlE64SKDs_ZUEYyBJE4wQ"; // declare your key here
    public static userData user;
    public static String DEVICE_ID;
    android.location.Location currentLocation;
    GoogleApiClient mGoogleApiClient;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest mLocationRequest;
    ImageView btnBack, btnMessage, btnCall, imgCaptin;
    TextView txtName, txtDeviceState, txtBattery;
    LatLng userLoc;
    private GoogleMap mMap;
    private HyperTrackViews mHyperTrackView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_captin_track);

        btnBack = findViewById(R.id.btnBack);
        btnMessage = findViewById(R.id.btnMessage);
        btnCall = findViewById(R.id.btnCall);
        imgCaptin = findViewById(R.id.imgCaptin);
        txtName = findViewById(R.id.txtName);
        txtDeviceState = findViewById(R.id.txtDeviceState);
        txtBattery = findViewById(R.id.txtBattery);

        btnBack.setOnClickListener(v -> finish());

        TextView tbTitle2 = findViewById(R.id.toolbar_title);
        tbTitle2.setText("تتبع المندوب");

        btnMessage.setOnClickListener(v -> {
            chatListclass _chatList = new chatListclass();
            _chatList.startChating(UserInFormation.getId(), user.getId(), this);
            Messages.cameFrom = "Profile";
        });

        btnCall.setOnClickListener(v -> {
            @SuppressLint("UseCheckPermission") BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) this).setMessage("هل تريد الاتصال بالمندوب ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_add_phone, (dialogInterface, which) -> {
                checkPermission(Manifest.permission.CALL_PHONE, PHONE_CALL_CODE);
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + user.getPhone()));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                this.startActivity(callIntent);
                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
            mBottomSheetDialog.show();
        });

        new LoadMarker().execute(user.getPpURL());

        final LocationManager manager2 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager2.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            fetchLocation();
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        buildGoogleAPIClient();

        setUserData();
        checkLocationPermission();
        fetchLocation();
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions((Activity) this, new String[]{permission}, requestCode);
        }
    }

    private void setUserData() {
        txtName.setText(user.getName());
        Picasso.get().load(Uri.parse(user.getPpURL())).into(imgCaptin);
    }

    @SuppressLint("LogNotTimber")
    @Override
    protected void onResume() {
        super.onResume();
        buildGoogleAPIClient();
        Timber.d("onResume: ");
        final Gson gson = new Gson();

        mHyperTrackView = HyperTrackViews.getInstance(this, PUBLISHABLE_KEY);

        mHyperTrackView.getDeviceMovementStatus(DEVICE_ID,
                (Consumer<MovementStatus>) movementStatus -> {
                    Log.i("TAG", "Got movement status data : " + gson.toJson(movementStatus));
                });

        mHyperTrackView.subscribeToDeviceUpdates(DEVICE_ID,
                new DeviceUpdatesHandler() {
                    @Override
                    public void onLocationUpdateReceived(@NonNull Location location) {
                        Timber.d("onLocationUpdateReceived: %s", gson.toJson(location));
                        setLocation(location);
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onBatteryStateUpdateReceived(@MovementStatus.BatteryState int i) {
                        // normal = 0 , // low = 1 // charging = 2
                        txtBattery.setText(i + "");
                    }

                    @Override
                    public void onStatusUpdateReceived(@NonNull StatusUpdate statusUpdate) {
                        txtDeviceState.setText(statusUpdate.value);
                    }

                    @Override
                    public void onTripUpdateReceived(@NonNull Trip trip) {
                    }

                    @Override
                    public void onError(@NotNull Exception exception, @NotNull String deviceId) {
                        txtDeviceState.setText(exception.getMessage());
                    }

                    @Override
                    public void onCompleted(@NotNull String deviceId) {
                    }
                }
        );

    }

    private void setLocation(Location location) {
        if (mMap != null) {
            userLoc = new LatLng(location.getLatitude(), location.getLongitude());
            new LoadMarker().execute(user.getPpURL());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHyperTrackView.stopAllUpdates();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        checkGPS();
        //Initialize Google Play Services
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleAPIClient();
            mMap.setMyLocationEnabled(true);
        }

        if (currentLocation != null) {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        }
    }

    private void buildAlertMessageNoGps() {
        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(MapCaptinTrack.this).setMessage("الرجاء فتح اعدادات اللوكيشن ؟").setCancelable(true).setPositiveButton("حسنا", R.drawable.ic_tick_green, (dialogInterface, which) -> {
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
        Task<android.location.Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;
                LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(UserInFormation.getId()).child("latLang").setValue(String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude()));
            }
        });
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
                        status.startResolutionForResult(MapCaptinTrack.this, REQUEST_CHECK_SETTINGS);
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
    public void onLocationChanged(@NonNull android.location.Location location) {

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

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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

    private class LoadMarker extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            // Get bitmap from server
            Bitmap overlay;
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                overlay = BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return overlay;
        }

        protected void onPostExecute(Bitmap bitmap) {
            mMap.clear();
            if (userLoc != null) {
                if (bitmap != null) {
                    View custom_layout = ((LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.places_pin_layout, null);
                    ImageView iv_category_logo = (ImageView) custom_layout.findViewById(R.id.profile_image);
                    Bitmap pinbit = Bitmap.createScaledBitmap(bitmap, 40, 60, false);
                    iv_category_logo.setImageBitmap(pinbit);
                    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapCaptinTrack.this, custom_layout));
                    mMap.addMarker(new MarkerOptions().position(userLoc).icon(bitmapDescriptor));
                } else {
                    // Add the new marker to the map
                    mMap.addMarker(new MarkerOptions().position(userLoc));
                }
            }
        }

        private Bitmap createDrawableFromView(Context context, View view) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
            view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
            view.buildDrawingCache();
            Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            return bitmap;
        }
    }
}