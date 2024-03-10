package com.example.cab;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DriverHome extends AppCompatActivity {

    private TextView formStatus;
    private EditText customerRequest;
    private EditText customerPhone;
    private EditText customerLocation;
    private EditText customerDescription;
    private EditText customerName;
    private EditText customerAddress;
    private Button startJob;
    private Button cancelJob;

    private DatabaseReference databaseReference;

    private String locationLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);

        formStatus = findViewById(R.id.form_status);
        customerRequest = findViewById(R.id.customer_request);
        customerPhone = findViewById(R.id.customer_phone);
        customerLocation = findViewById(R.id.customer_location);
        customerDescription = findViewById(R.id.customer_description);
        customerName = findViewById(R.id.customer_name);
        customerAddress = findViewById(R.id.customer_address);
        startJob = findViewById(R.id.start_job_btn);
        cancelJob = findViewById(R.id.cancel_request);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("store");

        // Read data from Firebase Realtime Database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        StoreData storeData = snapshot.getValue(StoreData.class);
                        if (storeData != null) {
                            // Display data in the corresponding fields
                            formStatus.setText("Customer Request");
                            customerRequest.setText(storeData.getModuleName());
                            customerPhone.setText(storeData.getPhone());
                            customerLocation.setText(storeData.getLocation());
                            customerDescription.setText(storeData.getDescription());
                            customerName.setText(storeData.getName());
                            customerAddress.setText(storeData.getAddress());
                            locationLink = storeData.getLocation();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error if any
            }
        });

        // Start Job button click listener
        startJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the location in Google Maps for navigation
                openLocationInMaps();
            }
        });

        cancelJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the registration activity
                startActivity(new Intent(DriverHome.this, MechanicModule.class));
            }
        });
    }

    // Method to open the location in Google Maps
    private void openLocationInMaps() {
        if (locationLink != null && !locationLink.isEmpty()) {
            // Create the intent to open the location in Google Maps
            Uri gmmIntentUri = Uri.parse(locationLink);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            // Verify that the Google Maps app is installed before launching the intent
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
