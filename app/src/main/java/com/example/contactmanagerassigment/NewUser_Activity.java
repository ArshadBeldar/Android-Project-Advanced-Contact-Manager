package com.example.contactmanagerassigment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64DataException;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.security.Permission;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NewUser_Activity extends AppCompatActivity {

    TextInputEditText txtUName, txtUMobileNo, txtUEmailId,txtUDob,txtUCity,txtUPassword,txtUConfirmPassword;
    ImageView imgUser;
    Button btnRegister,btnCancel;
    String UDate;
    ProgressDialog pd;

    Bitmap bmp;
    byte[] imgBytes;
    ByteArrayOutputStream baos;
    String imgToStr;

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
        setContentView(R.layout.newuser_layout);

        txtUName = findViewById(R.id.txtUName);
        txtUMobileNo = findViewById(R.id.txtUMobileNo);
        txtUEmailId = findViewById(R.id.txtUEmailId);
        txtUDob =findViewById(R.id.txtUDob);
        txtUCity = findViewById(R.id.txtUCity);
        txtUPassword = findViewById(R.id.txtUPassword);
        txtUConfirmPassword =findViewById(R.id.txtUConfirmPassword);
        imgUser = findViewById(R.id.imgUser);
        btnRegister = findViewById(R.id.btnRegister);
        btnCancel = findViewById(R.id.btnCancel);

        Calendar c = Calendar.getInstance();
        UDate = c.get(Calendar.DAY_OF_MONTH)+"/"+(c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.YEAR);

        pd = new ProgressDialog(NewUser_Activity.this);

    }

    public void newUserRegister(View view) {

        pd.setTitle("New User Registration");
        pd.setMessage("Pleasw wait... Data is Storing on Server.");
        pd.show();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://igccontact.000webhostapp.com/AddUser.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        Toast.makeText(NewUser_Activity.this, "User Registered Successfully !!!", Toast.LENGTH_LONG).show();
                        SharedPreferences sp = getSharedPreferences("UserData",MODE_PRIVATE);
                        SharedPreferences.Editor ed = sp.edit();
                        ed.putString("txtUName",txtUName.getText().toString().trim());
                        ed.putString("txtMobileNo",txtUMobileNo.getText().toString().trim());
                        ed.putString("txtEmailId",txtUEmailId.getText().toString().trim());
                        ed.putString("txtUDob",txtUDob.getText().toString().trim());
                        ed.putString("txtUCity",txtUCity.getText().toString().trim());
                        ed.putString("txtUPassword",txtUPassword.getText().toString().trim());
                        ed.putString("imgUser",txtUMobileNo.getText().toString().trim()+".jpg");
                        ed.putString("uDate",UDate);
                        ed.putString("Status","Success");
                        ed.commit();
                        Intent i = new Intent(NewUser_Activity.this,MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(NewUser_Activity.this, "Error : "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> P = new HashMap<>();
                P.put("txtUName",txtUName.getText().toString().trim());
                P.put("txtUMobileNo",txtUMobileNo.getText().toString().trim());
                P.put("txtEmailId",txtUEmailId.getText().toString().trim());
                P.put("txtUDob",txtUDob.getText().toString().trim());
                P.put("txtUCity",txtUCity.getText().toString().trim());
                P.put("txtUPassword",txtUPassword.getText().toString().trim());
                P.put("imgUser",txtUMobileNo.getText().toString().trim()+".jpg");
                P.put("uDate",UDate);
                P.put("UserImage",imgToStr);
                return P;
            }
        };
        queue.add(stringRequest);
    }

    public void openCam(View view) {
        if(ActivityCompat.checkSelfPermission(NewUser_Activity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(NewUser_Activity.this,new String[]{Manifest.permission.CAMERA},100);
        }
        else
        {
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(i,111);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==111 && data != null) {
            bmp = (Bitmap) data.getExtras().get("data");
            imgUser.setImageBitmap(bmp);
            baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG,100,baos);
            imgBytes = baos.toByteArray();
            imgToStr = Base64.getEncoder().encodeToString(imgBytes);
        }
    }
}
