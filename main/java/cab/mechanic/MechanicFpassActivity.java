package com.example.cab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MechanicFpassActivity extends AppCompatActivity {
    private EditText email_mechanic_fp;
    private Button mechanic_forgotPasswordButton;
    private FirebaseAuth firebaseAuth;
    private ImageButton mechanic_resetToLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_fpass);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        email_mechanic_fp = findViewById(R.id.mechanic_emailFP);
        mechanic_forgotPasswordButton= findViewById(R.id.mech_forgot_btn);
        mechanic_resetToLogin = findViewById(R.id.back_login_mechanic);

        mechanic_forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_mechanic_fp.getText().toString().trim();

                if (!email.isEmpty()) {
                    sendPasswordResetEmail(email);
                } else {
                    Toast.makeText(MechanicFpassActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mechanic_resetToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the registration activity
                startActivity(new Intent(MechanicFpassActivity.this, MechanicLoginActivity.class));
            }
        });
    }

    private void sendPasswordResetEmail(String email) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MechanicFpassActivity.this, "Password reset email sent. Please check your email.", Toast.LENGTH_SHORT).show();
                        navigateTo_Mechanic_LoginActivity();
                    } else {
                        Toast.makeText(MechanicFpassActivity.this, "Failed to send password reset email. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void navigateTo_Mechanic_LoginActivity() {
        Intent intent = new Intent(MechanicFpassActivity.this, MechanicLoginActivity.class);
        intent.putExtra("message", "Check your email to reset password");
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
