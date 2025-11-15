package com.example.nagarsurakha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.*;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class water_dept extends AppCompatActivity {

    Spinner spinnerDistrict;
    TextView tvLocation;
    EditText etDescription;
    ImageView imgPreview;
    Button btnCamera, btnSubmit, btnEnableLocation;
    ImageButton btnBack;

    FusedLocationProviderClient fusedLocationClient;
    Bitmap bitmap;
    String encodedImage = "";
    double latitude = 0.0, longitude = 0.0;
    String locationText = "";

    String fullName, number;

    private static final int CAMERA_CODE = 101;
    private static final int LOCATION_CODE = 102;

    String[] districts = {"Select District","Alirajpur","Anuppur","Ashoknagar","Balaghat","Barwani",
            "Betul","Bhind","Bhopal","Burhanpur","Chhatarpur","Chhindwara","Damoh","Datia",
            "Dewas","Dindori","Guna","Gwalior","Hoshangabad","Indore","Jabalpur","Jhabua",
            "Katni","Khandwa","Khargone","Mandla","Mandsaur","Morena","Narsinghpur",
            "Neemuch","Panna","Raisen","Rajgarh","Ratlam","Rewa","Sagar","Satna","Sehore",
            "Seoni","Shahdol","Shajapur","Sheopur","Shivpuri","Sidhi","Singrauli",
            "Tikamgarh","Ujjain","Umaria","Vidisha"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_dept);

        tvLocation = findViewById(R.id.tvLocation);
        etDescription = findViewById(R.id.etDescription);
        spinnerDistrict = findViewById(R.id.spinner);
        imgPreview = findViewById(R.id.imgPreview);
        btnCamera = findViewById(R.id.btnCamera);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnEnableLocation = findViewById(R.id.btnEnableLocation);
        btnBack = findViewById(R.id.btnBack);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Get user data from SharedPreferences
        SharedPreferences sp = getSharedPreferences("UserData", MODE_PRIVATE);
        fullName = sp.getString("fullname", "");
        number = sp.getString("number", "");

        Log.d("DEBUG_USER", "fullName: " + fullName + " | number: " + number);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, districts);
        spinnerDistrict.setAdapter(adapter);

        btnCamera.setOnClickListener(v -> checkCameraPermission());
        btnEnableLocation.setOnClickListener(v -> getLocation());
        btnSubmit.setOnClickListener(v -> submitReport());

        // 🔹 Custom back button click
        btnBack.setOnClickListener(v -> {
            Intent i = new Intent(water_dept.this, HomeActivity.class); // Change HomeActivity to your homepage class
            startActivity(i);
            finish();
        });
    }

    private void checkCameraPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, CAMERA_CODE);
        } else openCamera();
    }

    private void openCamera(){
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==CAMERA_CODE && resultCode==RESULT_OK){
            bitmap = (Bitmap) data.getExtras().get("data");
            imgPreview.setImageBitmap(bitmap);
            encodedImage = convertToBase64(bitmap);
        }
    }

    private String convertToBase64(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,80,baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    private void getLocation(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_CODE);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if(location!=null){
                updateLocationUI(location.getLatitude(),location.getLongitude());
            } else tvLocation.setText("Unable to get location!");
        });
    }

    private void updateLocationUI(double lat, double lng){
        latitude = lat;
        longitude = lng;

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try{
            List<Address> addresses = geocoder.getFromLocation(latitude,longitude,1);
            if(addresses!=null && !addresses.isEmpty()){
                locationText = addresses.get(0).getAddressLine(0);
                tvLocation.setText("📍 "+locationText+"\nLat:"+latitude+"\nLng:"+longitude);
            }
        } catch (IOException e){
            locationText="";
            tvLocation.setText("Lat:"+latitude+"\nLng:"+longitude+"\nError fetching address");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(requestCode==CAMERA_CODE && grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) openCamera();
        if(requestCode==LOCATION_CODE && grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) getLocation();
    }

    private void submitReport(){
        String description = etDescription.getText().toString().trim();
        String district = spinnerDistrict.getSelectedItem().toString();

        if(fullName.isEmpty() || number.isEmpty()){
            Toast.makeText(this,"User data missing!",Toast.LENGTH_LONG).show();
            return;
        }
        if(district.equals("Select District")){
            Toast.makeText(this,"Please select district!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(description.isEmpty()){
            Toast.makeText(this,"Please enter description!",Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.38.36.199/nagarsuraksha/submit_water_report.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "✅ Report Submitted!", Toast.LENGTH_LONG).show();

                    // Clear all input fields after successful submit
                    etDescription.setText("");
                    spinnerDistrict.setSelection(0);
                    imgPreview.setImageResource(0); // Remove image
                    encodedImage = "";
                    latitude = 0.0;
                    longitude = 0.0;
                    locationText = "";
                    tvLocation.setText("Location not fetched");

                },
                error -> Toast.makeText(this, "❌ Error: " + error.getMessage(), Toast.LENGTH_LONG).show()
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("FullName", fullName);
                map.put("Number", number);
                map.put("district", district);
                map.put("latitude", String.valueOf(latitude));
                map.put("longitude", String.valueOf(longitude));
                map.put("location_text", locationText);
                map.put("description", description);
                map.put("image", encodedImage);
                map.put("department", "Water");
                return map;
            }
        };


        Volley.newRequestQueue(this).add(request);
    }
}
