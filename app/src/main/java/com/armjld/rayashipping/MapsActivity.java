package com.armjld.rayashipping;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.armjld.rayashipping.Models.Order;
import com.armjld.rayashipping.Models.UserInFormation;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnInfoWindowClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener,
        LocationListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int REQUEST_CHECK_SETTINGS = 102;
    private static final String TAG = "Maps";
    public static ArrayList<Order> filterList = new ArrayList<>();
    Location currentLocation;
    GoogleApiClient mGoogleApiClient;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest mLocationRequest;
    private GoogleMap mMap;
    private FloatingActionButton btnGCL;
    private ProgressDialog progressDialog;
    TextView txtPickLoc, txtPickDistance;
    ConstraintLayout cardLocation;
    Button btnGoogleDirc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

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

        cardLocation = findViewById(R.id.cardLocation);
        txtPickLoc = findViewById(R.id.txtPickLoc);
        txtPickDistance = findViewById(R.id.txtPickDistance);
        btnGoogleDirc = findViewById(R.id.btnGoogleDirc);
        cardLocation.setVisibility(View.GONE);
        btnGoogleDirc.setVisibility(View.GONE);


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
                FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(UserInFormation.getUser().getId()).child("latLang").setValue(location.getLatitude() + "," + location.getLongitude());
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
            Order thisOrder = filterList.get(i);

            String state = thisOrder.getStatue();
            String provider = thisOrder.getProvider();
            String orderId = thisOrder.getId();

            boolean toPick = state.equals("placed") || state.equals("accepted") || state.equals("recived");
            boolean toDelv = state.equals("readyD") || state.equals("supD") || state.equals("capDenied") || state.equals("supDenied");

            if (toPick) {
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
        Log.i("Maps", "Checking For Delivery");
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("clientsLocations").child(dPhone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // -------- This client added his location to our Database
                    double _lat = Double.parseDouble(Objects.requireNonNull(snapshot.child("_lat").getValue()).toString());
                    double _long = Double.parseDouble(Objects.requireNonNull(snapshot.child("_long").getValue()).toString());
                    LatLng latLng = new LatLng(_lat, _long);
                    addOrder(latLng, state, provider, id);
                    Log.i("Maps", "Order id for Map : " + id);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
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
        if(!UserInFormation.getUser().getAccountType().equals("Supervisor") && currentLocation != null) {
            for(int i = 0; i < filterList.size(); i++) {
                if(filterList.get(i).getId().equals(marker.getTag())) {
                    if(filterList.get(i).getStatue().equals("placed") || filterList.get(i).getStatue().equals("accepted") || filterList.get(i).getStatue().equals("capDenied") || filterList.get(i).getStatue().equals("supDenied")) {
                        txtPickLoc.setText(filterList.get(i).getTxtPState() + " - " + filterList.get(i).getmPRegion() + " - " + filterList.get(i).getmPAddress());
                    } else if (filterList.get(i).getStatue().equals("supD") || filterList.get(i).getStatue().equals("readyD")) {
                        txtPickLoc.setText(filterList.get(i).getTxtDState() + " - " + filterList.get(i).getmDRegion() + " - " + filterList.get(i).getDAddress());
                    }

                    txtPickDistance.setText(getDistanceFromLatLonInKm(currentLocation.getLatitude(), currentLocation.getLongitude(), marker.getPosition().latitude, marker.getPosition().longitude) + " كم");
                    cardLocation.setVisibility(View.VISIBLE);

                    /*progressDialog = new ProgressDialog(MapsActivity.this);
                    progressDialog.setMessage("جاري تحضير خط السير ..");
                    progressDialog.setCancelable(false);
                    progressDialog.show();*/

                    Uri navigationIntentUri = Uri.parse("google.navigation:q=" + marker.getPosition().latitude +"," + marker.getPosition().longitude);//creating intent with latlng
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);

                    /*LatLng current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    String url = getDirectionsUrl(current, marker.getPosition());
                    DownloadTask downloadTask = new DownloadTask();
                    downloadTask.execute(url);*/
                    break;
                }
            }
        }

        return true;
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, Drawable vectorDrawableResourceId) {
        vectorDrawableResourceId.setBounds(0, 0, vectorDrawableResourceId.getIntrinsicWidth(), vectorDrawableResourceId.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawableResourceId.getIntrinsicWidth(), vectorDrawableResourceId.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawableResourceId.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onClick(View view) { }

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
    public void onPointerCaptureChanged(boolean hasCapture) { }

    @Override
    public void onInfoWindowClick(Marker marker) { }

    @Override
    public void onLocationChanged(@NonNull Location location) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onProviderEnabled(@NonNull String provider) {}

    @Override
    public void onProviderDisabled(@NonNull String provider) { }

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
        /*recyclerView2.setVisibility(View.GONE);
        recyclerView2.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slid_down));*/
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

    @SuppressLint("StaticFieldLeak")
    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }


    /**
     * A class to parse the Google Places in JSON format
     */
    @SuppressLint("StaticFieldLeak")
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            progressDialog.dismiss();
            Log.d("result", result.toString());
            ArrayList points = null;
            PolylineOptions lineOptions = null;

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.BLUE);
                lineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
            } else {
                progressDialog.dismiss();
                Toast.makeText(MapsActivity.this, "لا يمكن تحديد خط سير للشحنه", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=AIzaSyCPGN2USqg7bCNaM9VBVYUAADbOK9m7JTc";


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

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();
            Log.d("data", data);

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private int getDistanceFromLatLonInKm(double lat1,double lon1,double lat2,double lon2) {
        int R = 6371; // Radius of the earth in km
        double dLat = deg2rad(lat2-lat1);  // deg2rad below
        double dLon = deg2rad(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return (int) (R * c);
    }

    private double deg2rad(double deg) {
        return deg * (Math.PI/180);
    }


}