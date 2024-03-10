package com.example.cab;

import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class CustModule extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Button logoutButton;
    DrawerLayout drawerlayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cust_module);

        ImageButton Mechanic = findViewById(R.id.mechi);
        ImageButton Elect = findViewById(R.id.elect);
        ImageButton Tow = findViewById(R.id.tow);
        ImageButton Fuel = findViewById(R.id.fuel);
        logoutButton = findViewById(R.id.lgout);
        drawerlayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.customer_nav_view);
        toolbar = findViewById(R.id.toolbar_customer);
        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerlayout, toolbar, R.string.navigation_open, R.string.navigation_close);
        drawerlayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        Mechanic.setOnClickListener(v -> {
            Intent FormCustIntent = new Intent(CustModule.this, CustForm.class);
            startActivity(FormCustIntent);
        });

        Tow.setOnClickListener(v -> {
            Intent FormTowIntent = new Intent(CustModule.this, TowForm.class);
            startActivity(FormTowIntent);
        });

        Elect.setOnClickListener(v -> {
            Intent FormElectIntent = new Intent(CustModule.this, ElectForm.class);
            startActivity(FormElectIntent);
        });

        Fuel.setOnClickListener(v -> {
            Intent FuelElectIntent = new Intent(CustModule.this, FuelForm.class);
            startActivity(FuelElectIntent);
        });

        logoutButton.setOnClickListener(v -> {
            // Clear user session data
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            // Navigate to login screen
            Intent logout = new Intent(CustModule.this, CustomerLoginActivity.class);
            logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logout);
            finish(); // Close the current activity to prevent going back to it using the back button
            showToast("Logout successful");
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerlayout.isDrawerOpen(GravityCompat.START)) {
            drawerlayout.closeDrawer(GravityCompat.START);
        } else {
            // Prevent the user from going back to the dashboard
            // Show a message and do nothing
            showToast("Press the Logout button to go back");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.customer_home:
                // Handle the customer_home item selection
                showToast("Home selected");
                break;

            case R.id.customer_profile:
                // Handle the customer_profile item selection
                Intent intent = new Intent(CustModule.this, CustomerSettingActivity.class);
                startActivity(intent);
                showToast("Profile selected");
                break;
        }

        drawerlayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
