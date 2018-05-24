package com.android.victory.schedule.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapShader;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.victory.schedule.R;
import com.android.victory.schedule.service.GetDirection;
import com.android.victory.schedule.service.Requests.LegsItem;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.android.victory.schedule.service.Requests.Response;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.android.victory.schedule.R.id.btn_check_out;
import static com.android.victory.schedule.R.id.start;

public class CheckOutActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener, View.OnClickListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient fuseLocationProviderClient;
    public GoogleApiClient googleApiClient;
    public LocationRequest locationRequest;
    public Double latitude1;
    public Double longitude1;
    private Button btnCheckOut;
    private TextView txtDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps);
        mapFragment.getMapAsync(CheckOutActivity.this);

        btnCheckOut = (Button) findViewById(R.id.btn_check_out);
        txtDistance = (TextView) findViewById(R.id.txtDistance);


        //ClickListener

        btnCheckOut.setOnClickListener(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .build();

        locationRequest = new LocationRequest()
                .setFastestInterval((5 * 1000))
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(5000);

        checkPermission();

        fuseLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastPosition();
        getCallbackPosition();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(CheckOutActivity.this, MainActivity.class));
        finish();
    }

    private void getCallbackPosition() {
        checkPermission();
        fuseLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                onLocationChanged(location);
            }
        }, Looper.myLooper());
    }

    private void getLastPosition() {
        checkPermission();
        fuseLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                latitude1 = location.getLatitude();
                longitude1 = location.getLongitude();

                Toast.makeText(getApplicationContext(), latitude1.toString() + "," + longitude1.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkPermission() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    ) {
            }
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        checkPermission();
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(Double.valueOf("-6.2155341"),Double.valueOf("106.8555813"));
        mMap.addMarker(new MarkerOptions().position(sydney).title("Kantor"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude1 = location.getLatitude();
        longitude1 = location.getLongitude();
        getDirection();
        createMarker();
    }

    private void createMarker() {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("titik");
        markerOptions.position(new LatLng(latitude1, longitude1));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(latitude1, longitude1));
        builder.include(new LatLng(Double.valueOf("-6.2155341"),Double.valueOf("106.8555813")));
        int padding = 25;
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), padding));
    }

    private void getDirection() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/directions/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GetDirection service = retrofit.create(GetDirection.class);

        final Call<Response> repos = service.getDirection(latitude1.toString() + "," + longitude1.toString(), "-6.2155341" + "," + "106.8555813");

        repos.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        Response res = response.body();
                        List<RoutesItem> routesItem = res.getRoutes();
                        if(routesItem.size() > 0){
                            RoutesItem item = routesItem.get(0);
                            LegsItem legsItem = item.getLegs().get(0);
                            Integer distance = item.getLegs().get(0).getDistance().getValue();
                            String textDistance = item.getLegs().get(0).getDistance().getText();
                            if(distance <= 10){
                                btnCheckOut.setEnabled(true);
                            } else
                                btnCheckOut.setEnabled(false);

                            txtDistance.setText(textDistance);

                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {

            }
        });
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btn_check_out:
                Toast.makeText(getApplicationContext(), "checkout", Toast.LENGTH_SHORT).show();
                break;

        }
    }
}
