package com.example.nagarsurakha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;

public class electecity_dept extends AppCompatActivity {

    TextView tvDeptName, tvLocation;
    Button btnCamera, btnSubmit;
    EditText etDescription;
    ImageView imgPreview, btnBack;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int CAMERA_INTENT_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_dept);

        //  Bind UI
        tvDeptName = findViewById(R.id.tvDeptName);
        tvLocation = findViewById(R.id.tvLocation);
        btnCamera = findViewById(R.id.btnCamera);
        btnSubmit = findViewById(R.id.btnSubmit);
        etDescription = findViewById(R.id.etDescription);
        imgPreview = findViewById(R.id.imgPreview);
        btnBack = findViewById(R.id.btnBack); // custom back button

        //  Back Button click → HomeActivity par le jao
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(electecity_dept.this, HomeActivity.class);
            startActivity(intent);
            finish(); // current activity close ho jaaye
        });

        // Intent se department name receive
        String deptName = getIntent().getStringExtra("DEPT_NAME");
        if (deptName != null) {
            tvDeptName.setText(deptName);
        } else {
            tvDeptName.setText("Department");
        }

        // Demo ke liye static location
        tvLocation.setText(" Location: Fetching...");

        // Camera button
        btnCamera.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_REQUEST_CODE);
            } else {
                openCamera();
            }
        });

        // Submit button
        btnSubmit.setOnClickListener(v -> {
            String desc = etDescription.getText().toString().trim();
            if (desc.isEmpty()) {
                Toast.makeText(electecity_dept.this, "⚠ Please enter description!", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(electecity_dept.this,
                    deptName + " Report Submitted \nDescription: " + desc,
                    Toast.LENGTH_LONG).show();
        });
    }

    // Open Camera
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_INTENT_CODE);
        }
    }

    // Handle Camera Result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_INTENT_CODE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap photo = (Bitmap) extras.get("data");
                imgPreview.setImageBitmap(photo); // photo ImageView me show
            }

        }

    }

    // Permission Result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera Permission Denied ", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
