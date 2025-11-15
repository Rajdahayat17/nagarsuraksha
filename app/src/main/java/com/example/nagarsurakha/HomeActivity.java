package com.example.nagarsurakha;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;

    CardView cardWater, cardPWD, cardNagar, cardElectricity, cardAdopt;
    TextView tvUserName, tvWelcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // ✅ Toolbar Setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // ✅ Drawer Toggle
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // ✅ Default selected tab
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_report) {
                startActivity(new Intent(HomeActivity.this, ReportPage.class));
                return true;
            } else if (id == R.id.nav_profile) {
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
            return false;
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_report) {
                startActivity(new Intent(HomeActivity.this, ReportPage.class));
            } else if (id == R.id.action_logout) {
                SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                prefs.edit().clear().apply();
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        cardWater = findViewById(R.id.cardWater);
        cardPWD = findViewById(R.id.cardPWD);
        cardNagar = findViewById(R.id.cardNagar);
        cardElectricity = findViewById(R.id.cardElectricity);
        cardAdopt = findViewById(R.id.cardAdopt);

        cardWater.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, water_dept.class)));
        cardPWD.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, pwd_dept.class)));
        cardNagar.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, nagarnigam_dept.class)));
        cardElectricity.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, electecity_dept.class)));

        tvUserName = findViewById(R.id.tvUserName);
        tvWelcomeText = findViewById(R.id.tvWelcome);
        updateUserName();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUserName();

        // ✅ Yeh line add karo (this fixes black screen when returning)
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    private void updateUserName() {
        String fullname = getIntent().getStringExtra("fullname");

        if (fullname == null || fullname.isEmpty()) {
            SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
            fullname = prefs.getString("fullname", "");
        }

        if (fullname != null && !fullname.isEmpty()) {
            tvUserName.setText("Hello " + fullname + " 👋");
        } else {
            tvUserName.setText("Hello User 👋");
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
