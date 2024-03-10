package com.example.cab;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class MechanicSettingActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mechanicRef;
    private StorageReference storageReference;
    private String mechanicId;
    private EditText userName_mechanic, email_mechanic, password_mechanic, phoneNumber_mechanic, CNIC_mechanic, address_mechanic;
    private Button updateButton;
    private TextView profileStatusTextView;
    private ImageView mechanicImageView;
    private ToggleButton passwordVisibilityToggle;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageButton mechanicProfileBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mechanic_activity_setting);

        // Initialize Firebase instances
        firebaseAuth = FirebaseAuth.getInstance();
        mechanicRef = FirebaseDatabase.getInstance().getReference("mechanic");

        // Find views by ID
        userName_mechanic = findViewById(R.id.mechanic_profile_name);
        email_mechanic = findViewById(R.id.mechanic_profile_email);
        password_mechanic = findViewById(R.id.mechanic_profile_password);
        phoneNumber_mechanic = findViewById(R.id.mechanic_profile_number);
        CNIC_mechanic = findViewById(R.id.mechanic_profile_CNIC);
        address_mechanic = findViewById(R.id.mechanic_profile_address);
        updateButton = findViewById(R.id.mechanic_profile_update_btn);
        profileStatusTextView = findViewById(R.id.mechanic_status);
        mechanicImageView = findViewById(R.id.mechanic_profile_pic);
        passwordVisibilityToggle = findViewById(R.id.togglePasswordVisibility);
        mechanicProfileBack = findViewById(R.id.mechanic_profile_back);

        mechanicProfileBack.setOnClickListener(v -> {
            Intent ProfileBackIntent = new Intent(MechanicSettingActivity.this, MechanicModule.class);
            startActivity(ProfileBackIntent);
        });

        // Set the initial state of password visibility
        password_mechanic.setTransformationMethod(new PasswordTransformationMethod());

        // Add an OnCheckedChangeListener to the toggle button
        passwordVisibilityToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Toggle the password visibility based on the toggle state
                if (isChecked) {
                    // Show the password
                    password_mechanic.setTransformationMethod(null);
                } else {
                    // Hide the password
                    password_mechanic.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        // Initialize Firebase Storage
        storageReference = FirebaseStorage.getInstance().getReference();

        // Set click listener for the profile image view
        mechanicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        // Load the profile image from Firebase Cloud Storage
        loadProfileImage();

        // Fetch mechanic data from Firebase Realtime Database
        fetchMechanicData();

        // Set click listener for the update button
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMechanicInfo();
            }
        });
    }
    private void chooseImage() {
        // Open image picker
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                mechanicImageView.setImageBitmap(bitmap);

                // Apply circular transformation using Glide
                Glide.with(this)
                        .load(bitmap)
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(mechanicImageView);

                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            StorageReference imageRef = storageReference.child("profile_images/" + System.currentTimeMillis() + ".jpg");
            imageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Image upload success
                    // You can store the download URL or perform any other operation here
                    Log.d("UPLOAD", "Image uploaded successfully");
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Save the download URL
                            saveImage(uri);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Image download failed
                            Log.e("DOWNLOAD", "Image download failed: " + e.getMessage());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Image upload failed
                    Log.e("UPLOAD", "Image upload failed: " + e.getMessage());
                }
            });
        }
    }

    private void saveImage(Uri downloadUri) {
        // Save the download URL to a shared preference or any other storage mechanism
        // You can retrieve this URL when the user switches to this activity and load the image from the URL
        // Here, I'll demonstrate using SharedPreferences to store the URL
        SharedPreferences sharedPreferences = getSharedPreferences("MechanicSettings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("profileImageUrl", downloadUri.toString());
        editor.apply();
    }

    private void loadProfileImage() {
        SharedPreferences sharedPreferences = getSharedPreferences("MechanicSettings", MODE_PRIVATE);
        String profileImageUrl = sharedPreferences.getString("profileImageUrl", null);
        if (profileImageUrl != null) {
            Glide.with(MechanicSettingActivity.this)
                    .load(Uri.parse(profileImageUrl))
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(mechanicImageView);
        } else {
            StorageReference imageRef = storageReference.child("profile_images/profile.jpg");
            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Image download success
                    // Load the image into the profile image view using Glide with circular transformation
                    Glide.with(MechanicSettingActivity.this)
                            .load(uri)
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .into(mechanicImageView);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Image download failed
                    Log.e("DOWNLOAD", "Image download failed: " + e.getMessage());
                }
            });
        }
    }

    private void fetchMechanicData() {
        // Fetch the mechanic data from the Realtime Database
        firebaseAuth = FirebaseAuth.getInstance();
        mechanicId = firebaseAuth.getCurrentUser().getUid();
        mechanicRef.child(mechanicId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Mechanic mechanic = dataSnapshot.getValue(Mechanic.class);
                    if (mechanic != null) {
                        String username = mechanic.getUsername();
                        String password = mechanic.getPassword();
                        String email = mechanic.getEmail();
                        String phone = mechanic.getPhone();
                        String CNIC = mechanic.getCNIC();
                        String address = mechanic.getAddress();

                        // Set the retrieved values to the respective EditText fields
                        userName_mechanic.setText(username);
                        password_mechanic.setText(password);
                        email_mechanic.setText(email);
                        phoneNumber_mechanic.setText(phone);
                        CNIC_mechanic.setText(CNIC);
                        address_mechanic.setText(address);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error gracefully
                Toast.makeText(MechanicSettingActivity.this, "Failed to fetch mechanic data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateMechanicInfo() {
        // Get the updated values from the EditText fields
        String newUsername = userName_mechanic.getText().toString().trim();
        String newPassword = password_mechanic.getText().toString().trim();
        String newEmail = email_mechanic.getText().toString().trim();
        String newPhone = phoneNumber_mechanic.getText().toString().trim();
        String newCNIC = CNIC_mechanic.getText().toString().trim();
        String newAddress = address_mechanic.getText().toString().trim();

        // Validate the input fields
        if (TextUtils.isEmpty(newUsername)) {
            Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(newEmail)) {
            Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(newPhone)) {
            Toast.makeText(this, "Please enter a number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(newCNIC)) {
            Toast.makeText(this, "Please enter a CNIC", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(newAddress)) {
            Toast.makeText(this, "Please enter an address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the mechanic's information in the Realtime Database
        if (firebaseAuth.getCurrentUser() != null) {
            String mechanicId = firebaseAuth.getCurrentUser().getUid();
            Mechanic updatedMechanic = new Mechanic(mechanicId, newEmail, newUsername, newPassword, newPhone, newCNIC, newAddress);

            mechanicRef.child(mechanicId).setValue(updatedMechanic).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(MechanicSettingActivity.this, "Information updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MechanicSettingActivity.this, "Failed to update information", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
