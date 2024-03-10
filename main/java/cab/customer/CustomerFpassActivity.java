package com.example.cab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class CustomerFpassActivity extends AppCompatActivity {
    private EditText emailEditText;
    private Button forgotPasswordButton;
    private FirebaseAuth firebaseAuth;
    private ImageButton resetToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_fpass);


        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.fpass_email);
        forgotPasswordButton = findViewById(R.id.forgot_password_btn);
        resetToLogin = findViewById(R.id.reset_to_login);

        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();

                if (!email.isEmpty()) {
                    sendPasswordResetEmail(email);
                } else {
                    Toast.makeText(CustomerFpassActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                }
            }
        });
        resetToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the registration activity
                startActivity(new Intent(CustomerFpassActivity.this, CustomerLoginActivity.class));
            }
        });
    }

    private void sendPasswordResetEmail(String email) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(CustomerFpassActivity.this, "Password reset email sent. Please check your email.", Toast.LENGTH_SHORT).show();
                        navigateToLoginActivity();
                    } else {
                        Toast.makeText(CustomerFpassActivity.this, "Failed to send password reset email. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToLoginActivity() {
        Intent intent = new Intent(CustomerFpassActivity.this, CustomerLoginActivity.class);
        intent.putExtra("message", "Check your email to reset password");
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
