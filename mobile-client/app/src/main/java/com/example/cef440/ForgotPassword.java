package com.example.cef440;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class ForgotPassword extends AppCompatActivity {
    EditText username, password, password2;
    Button btnReset;
    TextView gotologin;
    ProgressDialog pDialog;

    RequestParams requestParams;
    AsyncHttpClient asyncHttpClient;
    String url = "http://172.20.10.14:8080/CEF440/ForgotPasswordServlet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //Get data from form
        username = findViewById(R.id.inputEmail);
        password = findViewById(R.id.inputPassword);
        password2 = findViewById(R.id.inputPassword2);
        btnReset = findViewById(R.id.btnReset);
        gotologin = findViewById(R.id.gotoLogin);

        //Go to login button clicked
        gotologin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent i = new Intent(getApplicationContext(),
                        Login.class);
                startActivity(i);
                finish();
            }
        });

        //Reset button clicked
        btnReset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){

                pDialog = new ProgressDialog(ForgotPassword.this);
                pDialog.setCancelable(false);
                pDialog.setIndeterminate(false);
                pDialog.setMessage("Resetting password...");
                pDialog.show();

                //Convert input to string
                String txtUsername = username.getText().toString();
                String txtPassword = password.getText().toString();
                String txtPassword2 = password2.getText().toString();

                //Check for empty fields
                if (TextUtils.isEmpty(txtUsername) || TextUtils.isEmpty(txtPassword) ||TextUtils.isEmpty(txtPassword2) ){
                    pDialog.dismiss();
                    Toast.makeText(ForgotPassword.this, "All fields are required", Toast.LENGTH_LONG).show();
                }
                else if (!txtPassword.equals(txtPassword2)){
                    pDialog.dismiss();
                    Toast.makeText(ForgotPassword.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                }
                else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(txtUsername).matches()){
                    pDialog.dismiss();
                    Toast.makeText(ForgotPassword.this, "Please enter a valid email", Toast.LENGTH_LONG).show();
                }
                else if (txtPassword.length() < 6 || txtPassword2.length() < 6){
                    pDialog.dismiss();
                    Toast.makeText(ForgotPassword.this, "Password must be of at least six (06) characters", Toast.LENGTH_LONG).show();
                }
                else if (!txtPassword.equals(txtPassword2)){
                    pDialog.dismiss();
                    Toast.makeText(ForgotPassword.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                }
                else{
                    requestParams = new RequestParams();
                    requestParams.put("name", txtUsername);
                    requestParams.put("password", txtPassword);

                    asyncHttpClient = new AsyncHttpClient();
                    asyncHttpClient.post(url, requestParams, new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            pDialog.dismiss();
                            super.onSuccess(statusCode, headers, response);
                            Toast.makeText(ForgotPassword.this, "Account password successfully reset, login to continue", Toast.LENGTH_LONG).show();

                            username.getText().clear();
                            password.getText().clear();
                            password2.getText().clear();

                            startActivity(new Intent(ForgotPassword.this, Login.class));
                            finish();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            pDialog.dismiss();
                            super.onFailure(statusCode, headers, responseString, throwable);
                            Toast.makeText(ForgotPassword.this, "There was an error resetting your password, please try again later", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ForgotPassword.this, Login.class));
        finish();
    }

}
