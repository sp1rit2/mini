package com.example.cc;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    Location curLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE=101;
    private UiSettings mUiSettings;
    FirebaseUser user;
    DatabaseReference ref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();



    }

    private void fetchLastLocation() {

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]
                    {
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },REQUEST_CODE);
            return;
        }
        Task<Location> task=fusedLocationProviderClient.getLastLocation();

        Marker marker = null;
        task.addOnSuccessListener(location -> {
            if(location!=null)
            {
                System.out.println("here");
                curLocation=location;
                Toast.makeText(getApplicationContext(),curLocation.getLatitude()+" "+curLocation.getLongitude(),Toast.LENGTH_LONG).show();
                SupportMapFragment supportMapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
                supportMapFragment.getMapAsync(MapsActivity.this);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        System.out.println("in on map ready");
        user=FirebaseAuth.getInstance().getCurrentUser();
        String uid=user.getUid();
        ref=FirebaseDatabase.getInstance().getReference("Shopkeeper").child(uid).child("location");
        final ValueEventListener ve=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    LatLng latLng;
                    mUiSettings = googleMap.getUiSettings();
                if(dataSnapshot.getChildrenCount()!=2) {
                    latLng = new LatLng(curLocation.getLatitude(), curLocation.getLongitude());
                }
                else {
                    double latitude=Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
                    double longitude=Double.parseDouble(dataSnapshot.child("longitude").getValue().toString());
                    latLng=new LatLng(latitude,longitude);
                }
                    MarkerOptions markerOptions=new MarkerOptions().position(latLng).title("loc");
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,18));
                    mUiSettings.setZoomControlsEnabled(true);
                    mUiSettings.setCompassEnabled(true);
                    googleMap.setMyLocationEnabled(true);
                    mUiSettings.setIndoorLevelPickerEnabled(true);
                    mUiSettings.setMapToolbarEnabled(true);
                    googleMap.addMarker(markerOptions);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.addListenerForSingleValueEvent(ve);

        googleMap.setOnMapClickListener(MapsActivity.this);

    }
    public void onMapClick(LatLng point) {
        finish();
        user= FirebaseAuth.getInstance().getCurrentUser();
        String uid=user.getUid();
        ref= FirebaseDatabase.getInstance().getReference("Shopkeeper").child(uid).child("location");

        ref.child("latitude").setValue(point.latitude);
        ref.child("longitude").setValue(point.longitude);
        startActivity(new Intent(this,MapsActivity.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLastLocation();
                }
                break;
        }
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


}
