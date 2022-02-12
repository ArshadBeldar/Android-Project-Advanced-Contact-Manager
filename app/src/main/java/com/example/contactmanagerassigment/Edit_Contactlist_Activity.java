package com.example.contactmanagerassigment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class Edit_Contactlist_Activity extends AppCompatActivity {
    ImageView imgView;
    Button btnBack;
    RadioButton rdoMale,rdoFemale;
    TextInputEditText txtName,txtMobile,txtEmailId,txtDOB,txtCity;

    GlobalDB DB = new GlobalDB();
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
        setContentView(R.layout.edit_contact_list_layout);
        imgView=findViewById(R.id.imgEdit);
        btnBack=findViewById(R.id.btnBack);
        rdoMale=findViewById(R.id.rdoMale);
        rdoFemale=findViewById(R.id.rdoFemale);
        txtName=findViewById(R.id.txtName);
        txtMobile=findViewById(R.id.txtMobile);
        txtEmailId=findViewById(R.id.txtEmailId);
        txtDOB=findViewById(R.id.txtDOB);
        txtCity=findViewById(R.id.txtCity);

        //for showing user data using mobileNO
        Bundle b = getIntent().getExtras();
        String usermobileno = b.getString("MobileNo");
        txtMobile.setText(usermobileno);

        openCon();
        Cursor c = db.rawQuery("select * from ContactData where Mobile='"+txtMobile.getText()+"'",null);
        if(c.getCount()>0)
        {
            c.moveToNext();
            txtName.setText(c.getString(0));
            txtMobile.setText(c.getString(1));
            txtEmailId.setText(c.getString(2));
            txtDOB.setText(c.getString(4));
            txtCity.setText(c.getString(5));
        }
        else
        {
            Toast.makeText(Edit_Contactlist_Activity.this, "No Contact Found", Toast.LENGTH_SHORT).show();
        }

    }

    public void returnBack(View view) {
        Intent i =new Intent(Edit_Contactlist_Activity.this,MainActivity.class);
        startActivity(i);
    }

    public void updateData(View view) {
        openCon();
        db.execSQL("create table if not exists ContactData(Name text,Mobile text,EmailID text,Gender text,DOB text,City text)");


        Toast.makeText(Edit_Contactlist_Activity.this, "Data Update Successfully!!", Toast.LENGTH_SHORT).show();


    }
}
