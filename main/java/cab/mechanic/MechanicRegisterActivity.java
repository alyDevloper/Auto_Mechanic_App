package com.example.cab;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MechanicRegisterActivity extends AppCompatActivity {


    private EditText mechanic_email;
    private EditText mechanic_username;
    private EditText mechanic_password;
    private EditText mechanic_number;
    private EditText mechanic_CNIC;
    private EditText mechanic_address;
    private Button mechanic_registerButton;
    private TextView Mechanic_LoginLink;
    private ToggleButton passwordVisibilityToggle;


    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);

        setContentView(R.layout.activity_mechanic_register);

        mAuth = FirebaseAuth.getInstance();

        // Initialize DatabaseReference instance
        databaseReference = FirebaseDatabase.getInstance().getReference("mechanic");

        mechanic_email = findViewById(R.id.email_mechanic);
        mechanic_username = findViewById(R.id.username_mechanic);
        mechanic_password = findViewById(R.id.password_mechanic);
        mechanic_number = findViewById(R.id.phone_mechanic);
        mechanic_CNIC = findViewById(R.id.mechanic_id);
        mechanic_address = findViewById(R.id.mechanic_address);
        mechanic_registerButton = findViewById(R.id.mechanic_register_btn);
        Mechanic_LoginLink = findViewById(R.id.mechanic_login_link);
        passwordVisibilityToggle = findViewById(R.id.togglePasswordVisibility);

        // Add an OnCheckedChangeListener to the toggle button
        passwordVisibilityToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Toggle the password visibility based on the toggle state
                if (isChecked) {
                    // Show the password
                    mechanic_password.setTransformationMethod(null);
                } else {
                    // Hide the password
                    mechanic_password.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        Mechanic_LoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the login activity
                startActivity(new Intent(MechanicRegisterActivity.this, MechanicLoginActivity.class));
            }
        });

        mechanic_registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerMechanic();
            }
        });
    }

    private void registerMechanic() {
        // Get the values from the EditText fields
        String email = mechanic_email.getText().toString().trim();
        String username = mechanic_username.getText().toString().trim();
        String password = mechanic_password.getText().toString().trim();
        String phone = mechanic_number.getText().toString().trim();
        String CNIC = mechanic_CNIC.getText().toString().trim();
        String address = mechanic_address.getText().toString().trim();

        // Validate the input fields
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(CNIC)) {
            Toast.makeText(this, "Please enter CNIC", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Please enter address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new user with email and password
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // User registration successful
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();

                    // Store the user details in the Realtime Database
                    if (firebaseUser != null) {
                        String mechanicId = firebaseUser.getUid();
                        Mechanic mechanic = new Mechanic(mechanicId, email, username, password, phone, CNIC, address);
                        databaseReference.child(mechanicId).setValue(mechanic)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(MechanicRegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                            // Start the login activity
                                            startActivity(new Intent(MechanicRegisterActivity.this, MechanicLoginActivity.class));
                                            finish(); // Optional: Finish the current activity if needed
                                        } else {
                                            // Registration failed
                                            Toast.makeText(MechanicRegisterActivity.this, "Failed to store user data", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        // Registration failed
                        Toast.makeText(MechanicRegisterActivity.this, "Failed to create user", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Registration failed
                    Toast.makeText(MechanicRegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        finish();
    }
}
