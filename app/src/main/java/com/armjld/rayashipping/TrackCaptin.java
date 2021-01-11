package com.armjld.rayashipping;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.hypertrack.sdk.views.DeviceUpdatesHandler;
import com.hypertrack.sdk.views.HyperTrackViews;
import com.hypertrack.sdk.views.dao.Location;
import com.hypertrack.sdk.views.dao.MovementStatus;
import com.hypertrack.sdk.views.dao.StatusUpdate;
import com.hypertrack.sdk.views.dao.Trip;

public class TrackCaptin extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String PUBLISHABLE_KEY = "00F4wm01NAr4ZHVE4gjrtQiQw8BxYC9dJNjeoM0LE4eEpm928geMz-2Tt8bZgOzJnTlE64SKDs_ZUEYyBJE4wQ"; // declare your key here
    public static String DEVICE_ID; // paste your device id here

    private HyperTrackViews mHyperTrackView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_captin);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        final Gson gson = new Gson();

        mHyperTrackView = HyperTrackViews.getInstance(this, PUBLISHABLE_KEY);

        mHyperTrackView.getDeviceMovementStatus(DEVICE_ID,
                (androidx.core.util.Consumer<MovementStatus>) movementStatus -> Log.d(TAG, "Got movement status data " + gson.toJson(movementStatus)));

        mHyperTrackView.subscribeToDeviceUpdates(DEVICE_ID,
                new DeviceUpdatesHandler() {
                    @Override
                    public void onLocationUpdateReceived(@NonNull Location location) {
                        Log.d(TAG, "onLocationUpdateReceived: " + gson.toJson(location));

                    }

                    @Override
                    public void onBatteryStateUpdateReceived(@MovementStatus.BatteryState int i) {
                        Log.d(TAG, "onBatteryStateUpdateReceived: " + i);
                    }

                    @Override
                    public void onStatusUpdateReceived(@NonNull StatusUpdate statusUpdate) {
                        Log.d(TAG, "onStatusUpdateReceived: " + gson.toJson(statusUpdate));
                    }

                    @Override
                    public void onTripUpdateReceived(@NonNull Trip trip) {
                        Log.d(TAG, "onTripUpdateReceived: " + gson.toJson(trip));
                    }

                    @Override
                    public void onError(Exception exception, String deviceId) {
                        Log.w(TAG, "onError: ", exception);
                    }

                    @Override
                    public void onCompleted(String deviceId) {
                        Log.d(TAG, "onCompleted: " + deviceId);

                    }
                }
        );

    }

    @Override
    protected void onPause() {
        super.onPause();
        mHyperTrackView.stopAllUpdates();
    }

}