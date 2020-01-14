package com.example.third;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;


import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private String apiKey = "AIzaSyAXwSnZgn6ur-o0PioeJPf3P_LfDAtz2ag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ask_music_Permissions();
        while (!checkPermission()){
            //ㅈㄴ 임시방편임
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent2=new Intent(this, LoadingActivity.class);
        startActivity(intent2);

        Places.initialize(getApplicationContext(), apiKey);
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("ID", place.getId());
                intent.putExtra("address", place.getAddress());
                intent.putExtra("name", place.getName());
                Bundle bundle = new Bundle();
                bundle.putParcelable("bundle", place.getLatLng());
                intent.putExtra("latlng", bundle);
                startActivity(intent);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("asdf", "An error occurred: " + status);
            }
        });
    }

    private void ask_music_Permissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
    }
    private boolean checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                return true;
            }
        }
        return false;
    }
}
