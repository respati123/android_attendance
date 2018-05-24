package com.android.victory.schedule.activity;

import com.android.victory.schedule.R;
import com.android.victory.schedule.service.DirectionMapsV2;
import com.android.victory.schedule.service.Requests.LegsItem;
import com.android.victory.schedule.service.Requests.Polyline;
import com.android.victory.schedule.service.Requests.Response;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.android.victory.schedule.service.GetDirection;
import com.android.victory.schedule.service.Requests.RoutesItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class CheckInActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private int UPDATE_INTERVAL = (5 * 1000);
    private long FASTEST_INTERVAL = 5000;
    private Location mLocation;
    private Double latitude1;
    private Double longitude1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationManager locationManager;
    private LocationCallback mLocationCallback;
    private Boolean mRequestLocation = true;
    private String valueDistance;
    private String polyPoints;
    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 101;
    private Boolean permissionGranted = false;
    private String distance;
    private String duration;
    private Polyline polyline;

    //Views
    private Button btnCheckIn;
    private TextView txtHour, txtDistance;
    private ProgressBar prgCheckIn;
    GoogleMap mMap;
    Marker marker;
    MarkerOptions markerOptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps);
        mapFragment.getMapAsync(CheckInActivity.this);


        txtHour = (TextView) findViewById(R.id.txtHour);
        txtDistance = (TextView) findViewById(R.id.txtDistance);
        prgCheckIn = (ProgressBar) findViewById(R.id.progress);
        btnCheckIn = (Button) findViewById(R.id.btn_check_in);


        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        locationRequest = new LocationRequest()
                .setInterval(UPDATE_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(FASTEST_INTERVAL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        btnCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CheckInActivity.this, "hadir", Toast.LENGTH_SHORT).show();
            }
        });

    }


//    private void requestLocationUpdates() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
//            }
//            return;
//        }
//    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CheckInActivity.this, MainActivity.class));
        finish();
    }

    public void checkPermission() {


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {

        }

    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        markerOptions = new MarkerOptions();

        mMap.addMarker(
                markerOptions
                        .title("kantor")
                        .position(new LatLng(Double.valueOf("-6.2155341"), Double.valueOf("106.8555813"))));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 2);
            checkPermission();
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {

                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        Location location = locationResult.getLastLocation();
                        onLocationChanged(location);
                    }
                },
                Looper.myLooper());

    }

    public void getDirection() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/directions/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GetDirection service = retrofit.create(GetDirection.class);

        final Call<Response> repos = service.getDirection(latitude1.toString() + "," + longitude1.toString(), "-6.2155341" + "," + "106.8555813");

        repos.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Response res = response.body();
                        Log.d("logdata", "" + res.toString());

                        List<RoutesItem> objects = res.getRoutes();
                        if (objects.size() > 0) {
                            RoutesItem getRoute = objects.get(0);

                            String getPoints = getRoute.getOverviewPolyline().getPoints();
                            LegsItem legsItem = getRoute.getLegs().get(0);
                            String distance = legsItem.getDistance().getText();
                            String duration = legsItem.getDuration().getText();
                            Integer valueDistance = legsItem.getDistance().getValue();
                            Log.d("valueDistance", "" + valueDistance);
                            String stringDistance = distance.substring(0, 1);
                            Integer result = Integer.parseInt(stringDistance);
                            Log.d("hasil", "" + result);
                            if (valueDistance <= 10) {
                                btnCheckIn.setEnabled(true);
                            } else {
                                btnCheckIn.setEnabled(false);
                            }
//                            DirectionMapsV2 directionMapsV2 = new DirectionMapsV2(CheckInActivity.this);
//                            directionMapsV2.gambarRoute(mMap, getPoints);

                            //setText
                            txtHour.setText(duration);
                            txtDistance.setText(distance);
                            Log.d("textSEMUA", "" + duration + "," + distance);

                            //set progress gone
                            prgCheckIn.setVisibility(View.GONE);
                            txtHour.setVisibility(View.VISIBLE);
                            txtDistance.setVisibility(View.VISIBLE);
                        }
                    }
                } else
                    Log.d("databody", "kosong");

            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Log.d("data111", "" + t.getMessage());
            }
        });


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 2);
                    checkPermission();
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    mLocation = task.getResult();
                    latitude1 = mLocation.getLatitude();
                    longitude1 = mLocation.getLongitude();
                    Toast.makeText(getApplicationContext(), latitude1.toString() +","+longitude1.toString(), Toast.LENGTH_LONG).show();
                } else {
                    Log.w("Tag", "getLastLocation:exception", task.getException());
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(googleApiClient.isConnected()){
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onLocationChanged(Location location) {

        latitude1 = location.getLatitude();
        longitude1 = location.getLongitude();

        getDirection();
        createMarker();
    }

//    private void removeMarker() {
//
//       marker = mMap.addMarker(markerOptions);
//       marker.remove();
//    }

    private void createMarker() {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("titik");
        markerOptions.position(new LatLng(latitude1, longitude1));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(latitude1, longitude1));
        builder.include(new LatLng(Double.valueOf("-6.2155341"),Double.valueOf("106.8555813")));
        LatLngBounds bounds = builder.build();
        int padding = 25;
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
