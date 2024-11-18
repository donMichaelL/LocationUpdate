package com.example.locationupdate;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private LocationManager locationManager;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Check and request permissions
        checkAndRequestPermissions();
    }

    private void checkAndRequestPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions if not granted
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        } else {
            // Permissions are already granted, start location updates
            fetchLastKnownLocation();  // Immediately get the last known location
            startLocationUpdates();   // Then start location updates
        }
    }

    private void fetchLastKnownLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (lastKnownLocation != null) {
                    double latitude = lastKnownLocation.getLatitude();
                    double longitude = lastKnownLocation.getLongitude();

                    // Display last known location
                    Toast.makeText(this, "Last known location: Lat: " + latitude + ", Long: " + longitude, Toast.LENGTH_SHORT).show();
                    textView.setText("Lat: " + latitude + ", Long: " + longitude);
                } else {
                    Toast.makeText(this, "No last known location available. Waiting for updates...", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error fetching last known location.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startLocationUpdates() {
        try {
            // Request location updates from GPS
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000, // Minimum time interval in milliseconds
                    10,   // Minimum distance in meters
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            // Update UI and show Toast
                            Toast.makeText(MainActivity.this, "Location received: Lat: " + latitude + ", Long: " + longitude, Toast.LENGTH_SHORT).show();
                            textView.setText("Lat: " + latitude + ", Long: " + longitude);
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                            // Optional: Handle status changes
                        }

                        @Override
                        public void onProviderEnabled(@NonNull String provider) {
                            Toast.makeText(MainActivity.this, "GPS Enabled", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onProviderDisabled(@NonNull String provider) {
                            Toast.makeText(MainActivity.this, "GPS Disabled", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        } catch (SecurityException e) {
            Toast.makeText(this, "Permission denied. Cannot start location updates.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // Check if permissions were granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Start location updates now that permissions are granted
                fetchLastKnownLocation();
                startLocationUpdates();
            } else {
                // Permission was denied
                textView.setText("Permission denied. Cannot fetch location.");
            }
        }
    }
}
