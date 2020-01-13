package com.example.third;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class AlarmService extends Service {

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation = null;
    private boolean running = true;

    public AlarmService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            Bundle bundle = intent.getBundleExtra("bundle");
            Location location = bundle.getParcelable("destination");
            handleActionFoo(location, Integer.parseInt(intent.getExtras().getString("distance")));
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void handleActionFoo(Location location, final int distance) {

        final Location destination = location;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(running){
                    getDeviceLocation();
                    if(mLastKnownLocation != null && destination != null){
                        Log.d("tlqkf", "??" + distance);
                        if(mLastKnownLocation.distanceTo(destination) < distance){
                            Intent intent1 = new Intent(getApplicationContext(), AlarmActivity.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent1);
                            running = false;
                            stopSelf();
                        }
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    private void getDeviceLocation() {
        try {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getBaseContext());
            Task locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        mLastKnownLocation = (Location) task.getResult();
                    } else {
                        Log.d("TAG", "Current location is null. Using defaults.");
                        Log.e("TAG", "Exception: %s", task.getException());
                    }
                }
            });
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

}
