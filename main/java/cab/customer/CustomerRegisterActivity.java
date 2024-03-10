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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class CustomerRegisterActivity extends AppCompatActivity {

    private EditText emailEditText, usernameEditText, passwordEditText, passwordEditText2;
    private Button registerButton;
    private TextView login_Link_customer;
    private ToggleButton passwordVisibilityToggle;
    private ToggleButton passwordVisibilityToggle2;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_register);

        // Initialize FirebaseAuth instance
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize DatabaseReference instance
        databaseReference = FirebaseDatabase.getInstance().getReference("customers");

        // Find views by ID
        emailEditText = findViewById(R.id.email_customer);
        usernameEditText = findViewById(R.id.username_customer);
        passwordEditText = findViewById(R.id.password_customer);
        registerButton = findViewById(R.id.customer_register_btn);
        login_Link_customer = findViewById(R.id.customer_login_link);
        passwordVisibilityToggle = findViewById(R.id.togglePasswordVisibility);
        passwordVisibilityToggle2 = findViewById(R.id.togglePasswordVisibility2);
        passwordEditText2 = findViewById(R.id.customer_password2);


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

        // Add an OnCheckedChangeListener to the toggle button
        passwordVisibilityToggle2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Toggle the password visibility based on the toggle state
                if (isChecked) {
                    // Show the password
                    passwordEditText2.setTransformationMethod(null);
                } else {
                    // Hide the password
                    passwordEditText2.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        login_Link_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the registration activity
                startActivity(new Intent(CustomerRegisterActivity.this, CustomerLoginActivity.class));
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerCustomer();
            }
        });
    }


    private void registerCustomer() {
        // Get the values from the EditText fields
        String email = emailEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

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

        // Create a new user with email and password
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // User registration successful
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                    // Store the user details in the Realtime Database
                    if (firebaseUser != null) {
                        String customerId = firebaseUser.getUid();
                        Customer customer = new Customer(customerId, email, username, password);
                        databaseReference.child(customerId).setValue(customer);
                    }

                    Toast.makeText(CustomerRegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    // Start the login activity
                    startActivity(new Intent(CustomerRegisterActivity.this, CustomerLoginActivity.class));
                    finish(); // Optional: Finish the current activity if neededS
                } else {
                    // Registration failed
                    Toast.makeText(CustomerRegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
