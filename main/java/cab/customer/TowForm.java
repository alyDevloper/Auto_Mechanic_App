package com.example.cab;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TowForm extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int LOCATION_REQUEST_CHECK_SETTINGS = 2;

    private EditText editTextName;
    private EditText editTextPhone;
    private EditText editTextDescription;
    private EditText editTextAddress;
    private EditText editTextLocation;
    private Button buttonLocation;
    private Button buttonSubmit;
    private TextView Request_Type;
    private ImageButton textViewBack;

    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tow_form);

        FirebaseApp.initializeApp(this);

        Request_Type = findViewById(R.id.textViewModuleName);
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextLocation = findViewById(R.id.editTextLocation);
        buttonLocation = findViewById(R.id.buttonLocation);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        textViewBack = findViewById(R.id.form_back_btn);

        // Initialize fused location provider client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        textViewBack.setOnClickListener(v -> {
            Intent FuelElectIntent = new Intent(TowForm.this, CustModule.class);
            startActivity(FuelElectIntent);
        });

        // Request location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        // Location button click listener
        buttonLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Request permission to enable location services
                requestLocationPermission();
            }
        });

        // Submit button click listener
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    saveData();
                    Intent intent = new Intent(TowForm.this, CustModule.class);
                    startActivity(intent);
                    finish(); // Close the current activity to prevent going back to it using the back button
                }
            }
        });

        // Landmark field click listener
        editTextLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextLocation.isEnabled()) {
                    String mapLink = editTextLocation.getText().toString().trim();

                    // Open Google Maps link in a web browser
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapLink));
                    startActivity(intent);
                }
            }
        });

        // Set the hint for landmark field
        editTextLocation.setHint("Press button to set the location");

        // Disable location field initially
        editTextLocation.setEnabled(false);
    }

    // Method to request location permission
    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, check if location is enabled
            checkLocationEnabled();
        }
    }

    // Method to check if location services are enabled
    private void checkLocationEnabled() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY));

        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // Location services are enabled, proceed with getting location
                    getLastLocation();
                } catch (ApiException exception) {
                    if (exception.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                        // Location services are not enabled, show a dialog to enable them
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            resolvable.startResolutionForResult(TowForm.this, LOCATION_REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    // Method to get last known location
    private void getLastLocation() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(0)
                .setFastestInterval(0);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        // Location retrieved successfully
                        handleLocationResult(location);
                        fusedLocationProviderClient.removeLocationUpdates(this);
                        return;
                    }
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    // Method to handle location result
    private void handleLocationResult(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        // Create Google Maps link with accurate location
        String mapLink = "https://maps.google.com/maps?q=loc:" + latitude + "," + longitude;

        // Update Landmark field with the Google Maps link
        editTextLocation.setText(mapLink);
        editTextLocation.setEnabled(true);
    }

    // Method to save form data to the database
    private void saveData() {
        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String landmark = editTextLocation.getText().toString().trim();
        String moduleName = Request_Type.getText().toString().trim();

        // Create a new instance of the database reference
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("store");

        // Create a new instance of the data object and set its values
        StoreData storeData = new StoreData(name, phone, description, address, landmark, moduleName);

        // Push the data object to the database reference
        databaseReference.push().setValue(storeData);

        Toast.makeText(this, "Form Submitted Successfully", Toast.LENGTH_SHORT).show();
    }

    // Method to validate the form fields
    private boolean validateForm() {
        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String landmark = editTextLocation.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(TowForm.this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (phone.isEmpty()) {
            Toast.makeText(TowForm.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (description.isEmpty()) {
            Toast.makeText(TowForm.this, "Please enter a description", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (address.isEmpty()) {
            Toast.makeText(TowForm.this, "Please enter your address", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (landmark.isEmpty()) {
            Toast.makeText(TowForm.this, "Please set the location", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // Handle the result of the location permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, check if location is enabled
                checkLocationEnabled();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Handle the result of the location settings resolution
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOCATION_REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                // Location services enabled by the user, proceed with getting location
                getLastLocation();
            } else {
                Toast.makeText(this, "Location services disabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
