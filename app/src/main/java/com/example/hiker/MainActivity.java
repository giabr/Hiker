package com.example.hiker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;

    TextView latitude;
    TextView longitude;
    TextView altitude;
    TextView accuracy;
    TextView addressTextView;
    Button update;

    public void updateLocationInfo(Location location){
        Log.i("Loc", location.toString());

        latitude.setText("Latitude : " + location.getLatitude());
        longitude.setText("Longitude : " + location.getLongitude());
        altitude.setText("Altitude : " + location.getAltitude());
        accuracy.setText("Accuracy : " + location.getAccuracy());

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {

            String address = "Couldn't find address";

            List<Address> list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (list != null && list.size() > 0){
                Log.i("Place", list.get(0).toString());
                address = "Address : \n";

                if (list.get(0).getSubThoroughfare() != null){
                    address += list.get(0).getSubThoroughfare() + ", ";
                }
                if (list.get(0).getThoroughfare() != null){
                    address += list.get(0).getThoroughfare() + ", ";
                }
                if (list.get(0).getLocality() != null){
                    address += list.get(0).getLocality() + "\n";
                }
                if (list.get(0).getPostalCode() != null){
                    address += list.get(0).getPostalCode() + ", ";
                }
                if (list.get(0).getCountryName() != null){
                    address += list.get(0).getCountryName();
                }
            }

            addressTextView.setText(address);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startListening();
        }
    }

    public void startListening(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitude = findViewById(R.id.lat);
        longitude = findViewById(R.id.longi);
        altitude = findViewById(R.id.alt);
        accuracy = findViewById(R.id.acc);
        addressTextView = findViewById(R.id.address);
        update = findViewById(R.id.update);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                Log.i("Loc", location.toString());

                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateLocationInfo(location);
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
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, locationListener);

            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location!=null){
                updateLocationInfo(location);
            }
        }
    }
}
