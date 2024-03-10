package com.example.cab;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MechanicLoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText emailEditText, passwordEditText;
    private Button Mechanic_loginButton;
    private TextView mechanic_register_Link, Mechanic_forgotPasswordLink;
    private ToggleButton passwordVisibilityToggle;
    private ImageButton Mechanic_ProfileBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_login);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.email_mechanic);
        passwordEditText = findViewById(R.id.password_mechanic);
        Mechanic_loginButton = findViewById(R.id.mechanic_login_btn);
        mechanic_register_Link = findViewById(R.id.mechanic_register_link);
        Mechanic_forgotPasswordLink = findViewById(R.id.mech_forgot_pass);
        passwordVisibilityToggle = findViewById(R.id.togglePasswordVisibility);
        Mechanic_ProfileBack = findViewById(R.id.back_mechanic);

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

        Mechanic_loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginMechanic();
            }
        });

        mechanic_register_Link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the registration activity
                startActivity(new Intent(MechanicLoginActivity.this, MechanicRegisterActivity.class));
            }
        });

        Mechanic_ProfileBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the welcome screen when the back button is pressed on the login screen
                Intent intent = new Intent(MechanicLoginActivity.this, WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        Mechanic_forgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Check if email and password are entered
                if (email.isEmpty() && password.isEmpty()) {
                    // Ask the customer to enter email and password
                    Toast.makeText(MechanicLoginActivity.this, "Please enter your email and password.", Toast.LENGTH_SHORT).show();
                } else if (email.isEmpty()) {
                    // Ask the customer to enter email
                    Toast.makeText(MechanicLoginActivity.this, "Please enter your email.", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    // Ask the customer to enter password
                    Toast.makeText(MechanicLoginActivity.this, "Please enter your password.", Toast.LENGTH_SHORT).show();
                } else {
                    // Authenticate the email and password
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Authentication successful
                                        Toast.makeText(MechanicLoginActivity.this, "Valid Credentials, No changes required", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Authentication failed, allow the forgot password flow
                                        startActivity(new Intent(MechanicLoginActivity.this, MechanicFpassActivity.class));
                                    }
                                }
                            });
                }
            }
        });
    }

    private void loginMechanic() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Check if email and password are entered
        if (email.isEmpty() && password.isEmpty()) {
            // Display a toast message asking the user to fill in the details
            Toast.makeText(MechanicLoginActivity.this, "Please enter your email and password.", Toast.LENGTH_SHORT).show();
        } else if (email.isEmpty()) {
            // Display a toast message asking the user to enter the email
            Toast.makeText(MechanicLoginActivity.this, "Please enter your email.", Toast.LENGTH_SHORT).show();
        } else if (password.isEmpty()) {
            // Display a toast message asking the user to enter the password
            Toast.makeText(MechanicLoginActivity.this, "Please enter your password.", Toast.LENGTH_SHORT).show();
        } else {
            // Attempt to log in the user
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Login successful, display a toast message
                                Toast.makeText(MechanicLoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();

                                // Navigate to the next screen (e.g., DriverHome)
                                startActivity(new Intent(MechanicLoginActivity.this, MechanicModule.class));
                                finish();
                            } else {
                                // Login failed, display an error message
                                Toast.makeText(MechanicLoginActivity.this, "Login failed, Incorrect Email or Password.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        {
            // Navigate to the welcome screen when the back button is pressed on the login screen
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}