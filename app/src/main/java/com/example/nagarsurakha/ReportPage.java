package com.example.nagarsurakha;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReportPage extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;

    RecyclerView recyclerView;
    ReportAdapter adapter;
    List<ReportModel> reportList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportpage);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // BottomNavigationView default selection
        bottomNavigationView.setSelectedItemId(R.id.nav_report);

// Bottom navigation item click
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(ReportPage.this, HomeActivity.class));
                return true;
            } else if (id == R.id.nav_report) {
                return true; // already on report page
            } else if (id == R.id.nav_profile) {
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
            return false;
        });

// NavigationView item click
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(ReportPage.this, HomeActivity.class));
            } else if (id == R.id.action_logout) {
                SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                prefs.edit().clear().apply();
                Intent intent = new Intent(ReportPage.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });


        // RecyclerView setup
        recyclerView = findViewById(R.id.reportRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReportAdapter(reportList);
        recyclerView.setAdapter(adapter);

        // Load Reports
        loadReports();
    }

    private void loadReports() {
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        String number = prefs.getString("number", null);

        if (number == null || number.isEmpty()) {
            Toast.makeText(this, "⚠ User Number Not Found!", Toast.LENGTH_SHORT).show();
            return;
        }

   //     Toast.makeText(this, "Saved Number: " + number, Toast.LENGTH_SHORT).show(); // debug

        String url = "http://10.38.36.199/nagarsuraksha/getreport.php?number=" + number;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        // Debug server response
             //           Toast.makeText(this, "Server Response: " + response, Toast.LENGTH_LONG).show();

                        if (response == null || response.equals("[]") || response.isEmpty()) {
                            Toast.makeText(this, "No reports found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray array = new JSONArray(response);
                        reportList.clear();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);

                            reportList.add(new ReportModel(
                                    obj.optString("report_id", "N/A"),
                                    obj.optString("report_time", "N/A"),
                                    obj.optString("status", "N/A"),
                                    obj.optString("department", "N/A")
                            ));
                        }

                        adapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        Toast.makeText(this, "❌ Parsing Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "❌ Failed to Load Reports: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.nav_report);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else super.onBackPressed();
    }
}
