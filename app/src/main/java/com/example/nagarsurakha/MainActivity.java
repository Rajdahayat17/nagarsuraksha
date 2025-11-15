package com.example.nagarsurakha;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    TextInputEditText textusername, textpas;
    TextView logsignup;
    MaterialButton loginbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textusername = findViewById(R.id.textusername);
        textpas = findViewById(R.id.textpas);
        loginbtn = findViewById(R.id.loginbtn);
        logsignup = findViewById(R.id.logsignup);

        // 🔹 Login button click
        loginbtn.setOnClickListener(v -> {
            String number = textusername.getText() != null ? textusername.getText().toString().trim() : "";
            String password = textpas.getText() != null ? textpas.getText().toString().trim() : "";

            if (number.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter number and password", Toast.LENGTH_SHORT).show();
                return;
            }

            new LoginTask().execute(number, password);
        });

        // 🔹 Go to Signup page
        logsignup.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, singup.class);
            startActivity(i);
        });
    }

    // 🔹 AsyncTask for login API
    private class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String number = params[0];
            String password = params[1];
            StringBuilder result = new StringBuilder();

            try {
                // ⚠️ Emulator ke liye: 10.0.2.2
                URL url = new URL("http://10.38.36.199/nagarsuraksha/login.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                // ✅ Send POST data
                String postData = "number=" + URLEncoder.encode(number, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8");

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(postData);
                writer.flush();
                writer.close();
                os.close();

                // ✅ Read response
                int responseCode = conn.getResponseCode();
                BufferedReader br;
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }

                String line;
                while ((line = br.readLine()) != null) {
                    result.append(line);
                }
                br.close();
                conn.disconnect();

            } catch (Exception e) {
                return "{\"success\":false,\"message\":\"Network Error: " + e.getMessage() + "\"}";
            }

            return result.toString();
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                JSONObject json = new JSONObject(response.trim());
                boolean success = json.optBoolean("success", false);
                String message = json.optString("message", "Something went wrong");

                if (success) {
                    Toast.makeText(MainActivity.this, "✅ Login Successful", Toast.LENGTH_SHORT).show();

                    // 🔹 Get user object from JSON
                    JSONObject user = json.getJSONObject("user");
                    String fullname = user.optString("fullname", "");
                    String email = user.optString("email", "");
                    String number = user.optString("number", "");

                    // 🔹 Save data in SharedPreferences (for later use)
                    SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("fullname", fullname);
                    editor.putString("email", email);
                    editor.putString("number", number);
                    editor.apply();

                    // 🔹 Move to Water Report page
                    Intent i = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(i);
                    finish();

                } else {
                    Toast.makeText(MainActivity.this, "❌ " + message, Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Response Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
