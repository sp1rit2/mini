package com.example.cc;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.GeoApiContext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class GetPath extends AppCompatActivity implements OnMapReadyCallback {
    private static final int REQUEST_CODE = 1;
    private static final String TAG =null ;
    Location curLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    String uidValue,sname;
    Intent intent;
    DatabaseReference ref;
    double latitude,longitude;
    GeoApiContext geoApiContext;
    private UiSettings mUiSettings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getpath);

        intent=getIntent();
        uidValue=intent.getStringExtra("location");
        System.out.println("in Getpath "+uidValue);
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();

        ref= FirebaseDatabase.getInstance().getReference("Shopkeeper").child(uidValue);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("location")) {
                    latitude = (double) dataSnapshot.child("location").child("latitude").getValue();
                    longitude = (double) dataSnapshot.child("location").child("longitude").getValue();
                    sname = dataSnapshot.child("sname").getValue().toString();
                }
                else
                    Toast.makeText(getApplicationContext(),"mrchnt Havnt entered location yet",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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


        task.addOnSuccessListener(location -> {
            if(location!=null)
            {
                System.out.println("here");
                curLocation=location;
                //Toast.makeText(getApplicationContext(),curLocation.getLatitude()+" "+curLocation.getLongitude(),Toast.LENGTH_LONG).show();
                SupportMapFragment supportMapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.pathfinder);
                supportMapFragment.getMapAsync(GetPath.this);
            }
        });
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mUiSettings = googleMap.getUiSettings();
        LatLng latLng=new LatLng(latitude,longitude);
        LatLng latLng1=new LatLng(curLocation.getLatitude(),curLocation.getLongitude());
        MarkerOptions markerOptions=new MarkerOptions().position(latLng).title(sname);
        MarkerOptions markerOptions1=new MarkerOptions().position(latLng1).title("Current location");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng1));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng1,17));
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        googleMap.setMyLocationEnabled(true);
        mUiSettings.setIndoorLevelPickerEnabled(true);
        mUiSettings.setMapToolbarEnabled(true);
        googleMap.addMarker(markerOptions);
        //googleMap.addMarker(markerOptions1);





    }


}
