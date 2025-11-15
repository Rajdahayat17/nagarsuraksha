package com.example.nagarsurakha;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class singup extends AppCompatActivity {

    EditText etFullName, etNumber, etEmail, etPassword;
    Button btnSignup;
    TextView loginbut;

    // Emulator ke liye
    String url = "http://10.38.36.199/nagarsuraksha/sign-up.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);

        etFullName = findViewById(R.id.etFullName);
        etNumber = findViewById(R.id.etNumber);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignup = findViewById(R.id.btnSignup);
        loginbut = findViewById(R.id.loginbut);

        loginbut.setOnClickListener(v -> {
            startActivity(new Intent(singup.this, MainActivity.class));
            finish();
        });

        btnSignup.setOnClickListener(v -> {
            String fullname = etFullName.getText().toString().trim();
            String number = etNumber.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if(fullname.isEmpty() || number.isEmpty() || email.isEmpty() || password.isEmpty()){
                Toast.makeText(singup.this,"Please fill all fields",Toast.LENGTH_SHORT).show();
                return;
            }

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            String message = jsonObject.getString("message");

                            Toast.makeText(singup.this, message, Toast.LENGTH_LONG).show();

                            if(success){
                                SharedPreferences sp = getSharedPreferences("UserData", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("fullname", fullname);
                                editor.putString("number", number);
                                editor.apply();

                                startActivity(new Intent(singup.this, HomeActivity.class));
                                finish();
                            }

                        } catch (JSONException e) {
                            Toast.makeText(singup.this,"Parse Error: "+e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    },
                    error -> Toast.makeText(singup.this,"Network Error: "+error.getMessage(),Toast.LENGTH_LONG).show()
            ){
                @Override
                protected Map<String, String> getParams(){
                    Map<String,String> data = new HashMap<>();
                    data.put("fullname", fullname);
                    data.put("mobile", number);
                    data.put("email", email);
                    data.put("password", password);
                    return data;
                }
            };

            request.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            RequestQueue queue = Volley.newRequestQueue(singup.this);
            queue.add(request);
        });
    }
}
