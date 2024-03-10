package com.example.cab;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    private boolean backButtonPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Button welcome_customer_btn = findViewById(R.id.welcome_customer_btn);
        Button welcome_driver_btn = findViewById(R.id.welcome_driver_btn);

        welcome_customer_btn.setOnClickListener(v -> {
            Intent LoginCustomerIntent = new Intent(WelcomeActivity.this, CustomerLoginActivity.class);
            startActivity(LoginCustomerIntent);
        });
        welcome_driver_btn.setOnClickListener(v -> {
            Intent LoginDriverIntent = new Intent(WelcomeActivity.this, MechanicLoginActivity.class);
            startActivity(LoginDriverIntent);
        });
    }

    @Override
    public void onBackPressed() {
        if (!backButtonPressed) {
            backButtonPressed = true;
            Toast.makeText(this, "Tap again to close app", Toast.LENGTH_SHORT).show();
        } else {
            finishAffinity();
        }
    }
}
