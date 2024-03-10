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

public class CustomerSettingActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference customerRef;
    private StorageReference storageReference;
    private String customerId;
    private EditText usernameEditText, passwordEditText, emailEditText;
    private Button updateButton;
    private TextView profileStatusTextView;
    private ImageView profileImageView;
    private ToggleButton passwordVisibilityToggle;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageButton customerProfileBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_activity_setting);

        // Initialize Firebase instances
        firebaseAuth = FirebaseAuth.getInstance();
        customerRef = FirebaseDatabase.getInstance().getReference("customers");

        // Find views by ID
        usernameEditText = findViewById(R.id.cust_profile_name);
        passwordEditText = findViewById(R.id.cust_profile_passkey);
        emailEditText = findViewById(R.id.cust_profile_email);
        updateButton = findViewById(R.id.update_info_cust);
        profileStatusTextView = findViewById(R.id.customer_status);
        profileImageView = findViewById(R.id.imageView_profile_dp);
        passwordVisibilityToggle = findViewById(R.id.togglePasswordVisibility);
        customerProfileBack = findViewById(R.id.customer_profile_back);

        customerProfileBack.setOnClickListener(v -> {
            Intent ProfileBackIntent = new Intent(CustomerSettingActivity.this, CustModule.class);
            startActivity(ProfileBackIntent);
        });

        // Set current user's data
        firebaseAuth = FirebaseAuth.getInstance();
        customerId = firebaseAuth.getCurrentUser().getUid();
        customerRef.child(customerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Customer customer = dataSnapshot.getValue(Customer.class);
                    if (customer != null) {
                        String username = customer.getUsername();
                        String password = customer.getPassword();
                        String email = customer.getEmail();

                        // Set the retrieved values to the respective EditText fields
                        usernameEditText.setText(username);
                        passwordEditText.setText(password);
                        emailEditText.setText(email);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error gracefully
                Toast.makeText(CustomerSettingActivity.this, "Failed to fetch customer data", Toast.LENGTH_SHORT).show();
            }
        });

        // Update button click listener
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCustomerInfo();
                // Disable focus and cursor in text fields
                usernameEditText.clearFocus();
                passwordEditText.clearFocus();
                emailEditText.clearFocus();
            }
        });

        // Set the initial state of password visibility
        passwordEditText.setTransformationMethod(new PasswordTransformationMethod());

        // Add an OnCheckedChangeListener to the toggle button
        passwordVisibilityToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Toggle the password visibility based on the toggle state
                if (isChecked) {
                    // Show the password
                    passwordEditText.setTransformationMethod(null);
                } else {
                    // Hide the password
                    passwordEditText.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        // Initialize Firebase Storage
        storageReference = FirebaseStorage.getInstance().getReference();

        // Set click listener for the profile image view
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        // Load the profile image from Firebase Cloud Storage
        loadProfileImage();
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
                profileImageView.setImageBitmap(bitmap);

                // Apply circular transformation using Glide
                Glide.with(this)
                        .load(bitmap)
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(profileImageView);

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
        SharedPreferences sharedPreferences = getSharedPreferences("CustomerSettings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("profileImageUrl", downloadUri.toString());
        editor.apply();
    }

    private void loadProfileImage() {
        SharedPreferences sharedPreferences = getSharedPreferences("CustomerSettings", MODE_PRIVATE);
        String profileImageUrl = sharedPreferences.getString("profileImageUrl", null);
        if (profileImageUrl != null) {
            Glide.with(CustomerSettingActivity.this)
                    .load(Uri.parse(profileImageUrl))
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(profileImageView);
        } else {
            StorageReference imageRef = storageReference.child("profile_images/profile.jpg");
            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Image download success
                    // Load the image into the profile image view using Glide with circular transformation
                    Glide.with(CustomerSettingActivity.this)
                            .load(uri)
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .into(profileImageView);
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

    private void updateCustomerInfo() {
        // Get the updated values from the EditText fields
        String newUsername = usernameEditText.getText().toString().trim();
        String newPassword = passwordEditText.getText().toString().trim();
        String newEmail = emailEditText.getText().toString().trim();

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

        // Update the customer's information in the Realtime Database
        if (firebaseAuth.getCurrentUser() != null) {
            String customerId = firebaseAuth.getCurrentUser().getUid();
            Customer updatedCustomer = new Customer(customerId, newEmail, newUsername, newPassword);

            customerRef.child(customerId).setValue(updatedCustomer).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(CustomerSettingActivity.this, "Customer information updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CustomerSettingActivity.this, "Failed to update customer information", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
