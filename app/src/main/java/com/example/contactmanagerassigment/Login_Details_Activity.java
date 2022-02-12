package com.example.contactmanagerassigment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Login_Details_Activity extends AppCompatActivity {

    TextInputEditText txtUserEmailId,txtUserPassword;
    Button btnLogin,btnCancel;
    GlobalDB DB = new GlobalDB();
    TextView txtNewUser,txtForgetPass;
    ProgressDialog pd;
    public SQLiteDatabase db;

    public void openCon() {
        db = openOrCreateDatabase("MyContactDB",MODE_PRIVATE,null);
        db.execSQL("create table if not exists ContactData(Name text,Mobile text,EmailID text,Gender text,DOB text,City text,Status text,MyImage blob)");
        db.execSQL("create table if not exists CallLogData(Name text,Mobile text,CDate text,CTime text)");
        db.execSQL("create table if not exists SmsLogData(Name text,Mobile text,SDate text,STime text)");

    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_details_layout);
        openCon();
        pd = new ProgressDialog(Login_Details_Activity.this);

        SharedPreferences sp = getSharedPreferences("UserData",MODE_PRIVATE);
        String status = sp.getString("Status","Fail");
        Toast.makeText(Login_Details_Activity.this, "Status:"+status, Toast.LENGTH_SHORT).show();
        if(status.equalsIgnoreCase("Success"))
        {
            Intent i =new Intent(Login_Details_Activity.this,MainActivity.class);
            startActivity(i);
            finish();
        }

        txtUserEmailId = findViewById(R.id.txtUserEmailId);
        txtUserPassword = findViewById(R.id.txtUserPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnCancel = findViewById(R.id.btnCancel);

        txtForgetPass = findViewById(R.id.txtForgetPass);
        txtNewUser = findViewById(R.id.txtNewUser);

    }

    public void gotoContact(View view) {


        if(txtUserEmailId.getText().toString().indexOf("@")<0 || txtUserEmailId.getText().toString().indexOf(".")<0)
        {
            txtUserEmailId.setError("Enter Valid EmailID");
            txtUserEmailId.requestFocus();
        }
        else if (txtUserPassword.getText().toString().isEmpty())
        {
            txtUserPassword.setError("Enter the Password");
            txtUserPassword.requestFocus();
        }
        else
        {
            //https://igccontact.000webhostapp.com/LoginUser.php?txtEmailId=hitesh291998@gmail.com&txtUPassword=1234566

            pd.setTitle("New User Registration");
            pd.setMessage("Pleasw wait... Data is Storing on Server.");
            pd.show();

            RequestQueue queue = Volley.newRequestQueue(this);
            String url ="https://igccontact.000webhostapp.com/LoginUser.php?txtEmailId="+txtUserEmailId.getText().toString().trim()+"&txtUPassword="+txtUserPassword.getText().toString().trim();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pd.dismiss();
                            Toast.makeText(Login_Details_Activity.this, "response - "+response, Toast.LENGTH_LONG).show();
                            if(response.equalsIgnoreCase("Failed")) {
                                Toast.makeText(Login_Details_Activity.this, "EMail ID or Password is Invalid.", Toast.LENGTH_SHORT).show();

                            }
                            else 
                            {
                                SharedPreferences sp = getSharedPreferences("UserData",MODE_PRIVATE);
                                SharedPreferences.Editor ed = sp.edit();
                                ed.putString("txtEmailId",txtUserEmailId.getText().toString().trim());
                                ed.putString("Status","Success");
                                ed.putString("txtMobileNo",response);
                                ed.commit();
                                startActivity(new Intent(Login_Details_Activity.this,MainActivity.class));
                                finish();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pd.dismiss();
                    Toast.makeText(Login_Details_Activity.this, "Error : "+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(stringRequest);
        }
    }


    public void newUser(View view)
    {
        Intent i = new Intent(Login_Details_Activity.this,NewUser_Activity.class);
        startActivity(i);
    }


}
