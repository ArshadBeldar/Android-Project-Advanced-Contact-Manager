package com.example.contactmanagerassigment;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;

public class Add_Contact_Activity extends AppCompatActivity {

    ImageView imgView;
    RadioButton rdoMale,rdoFemale;
    Button btnSubmit,btnCancel;
    TextInputEditText txtName,txtMobile,txtEmailId,txtDOB,txtCity;
    String Gender;
    String[] MobNo;
    DatePickerDialog dpd;
    GlobalDB DB = new GlobalDB();
    Bitmap bmp;
    boolean isCaptured = false;
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
        setContentView(R.layout.addconctact_layout);

        imgView=findViewById(R.id.imgView);
        rdoMale=findViewById(R.id.rdoMale);
        rdoFemale=findViewById(R.id.rdoFemale);
        btnSubmit=findViewById(R.id.btnSubmit);
        btnCancel=findViewById(R.id.btnCancel);
        txtName=findViewById(R.id.txtName);
        txtMobile=findViewById(R.id.txtMobile);
        txtEmailId=findViewById(R.id.txtEmailId);
        txtDOB=findViewById(R.id.txtDOB);
        txtCity=findViewById(R.id.txtCity);


        rdoMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Gender="Male";
            }
        });
        rdoFemale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Gender="Female";
            }
        });
    }

    public void registerUser(View view)
    {
        if (txtName.getText().toString().isEmpty())
        {
            txtName.setError("Enter Name");
            txtName.requestFocus();
        }
        else if (txtMobile.getText().toString().length()<10 || txtMobile.getText().length()>10)
        {
            txtMobile.setError("Enter 10 Digit No");
        }

        else if (txtEmailId.getText().toString().indexOf("@")<=0 || txtEmailId.getText().toString().indexOf(".")<=0)
        {
            txtEmailId.setError("Enter Valid EmailID");
            txtEmailId.requestFocus();
        }
        else if (txtCity.getText().toString().isEmpty())
        {
            txtCity.setError("Enter City Name");
            txtCity.requestFocus();
        }
        else if(Gender.equals(null) || Gender == null) {
            Toast.makeText(Add_Contact_Activity.this, "Please Select the Gender.", Toast.LENGTH_SHORT).show();
        }
        else if(isCaptured == false) {
            Toast.makeText(Add_Contact_Activity.this, "Please Take the Pic.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            openCon();
            Cursor c = db.rawQuery("Select * from ContactData where Mobile='"+txtMobile.getText()+"'",null);
            if(c.getCount()>0) {
                Toast.makeText(Add_Contact_Activity.this, "This Mobile No is Already Taken by User", Toast.LENGTH_SHORT).show();
            }
            else
            {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG,100,baos);
                byte[] img = baos.toByteArray();

                    ContentValues cv=new ContentValues();
                    cv.put("Name",txtName.getText().toString());
                    cv.put("Mobile",txtMobile.getText().toString());
                    cv.put("EmailId",txtEmailId.getText().toString());
                    cv.put("Gender",Gender);
                    cv.put("DOB",txtDOB.getText().toString());
                    cv.put("City",txtCity.getText().toString());
                    cv.put("Status","Pending");
                    cv.put("MyImage",img);
                    db.insert("ContactData",null,cv);

                    Toast.makeText(Add_Contact_Activity.this, "User Register", Toast.LENGTH_SHORT).show();

                    txtName.getText().clear();
                    txtMobile.getText().clear();
                    txtEmailId.getText().clear();
                    Gender=null;
                    txtDOB.getText().clear();
                    txtCity.getText().clear();
                    txtName.requestFocus();
                    rdoFemale.setSelected(false);
                    rdoMale.setSelected(false);
                }
        }


    }

    public void returnHome(View view) {
        Intent i = new Intent(Add_Contact_Activity.this,MainActivity.class);
        startActivity(i);
    }

    public void openDOB(View view) {
        dpd = new DatePickerDialog(Add_Contact_Activity.this, new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String DD= String.valueOf(dayOfMonth);
                String MM= String.valueOf(month);
                if (dayOfMonth < 9)
                {
                    DD="0"+DD;
                }
                if ((month+1) < 9)
                {
                    MM="0"+MM;
                }
                txtDOB.setText(DD+"/"+MM+"/"+year);
            }
        },2000,1,1);
        dpd.show();
    }

    public void openCamera(View view)
    {
      Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      startActivityForResult(i,111);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 111 && data != null)
        {
            bmp = (Bitmap) data.getExtras().get("data");
            imgView.setImageBitmap(bmp);
            isCaptured = true;
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(Add_Contact_Activity.this,MainActivity.class);
        startActivity(i);
       // super.onBackPressed();
    }
}
