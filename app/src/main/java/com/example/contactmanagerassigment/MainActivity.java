package com.example.contactmanagerassigment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    GlobalDB DB = new GlobalDB();
    ArrayList<String> Name = new ArrayList<>();
    ArrayList<String> MobNo = new ArrayList<>();
    ArrayList<String> EMailID = new ArrayList<>();
    ArrayList<String> Gender= new ArrayList<>();
    ArrayList<String> DOB = new ArrayList<>();
    ArrayList<String> City = new ArrayList<>();
    ArrayList<Bitmap> Image = new ArrayList<Bitmap>();
    ListView lstContactList;
    String MyMobileNo;
    public SQLiteDatabase db;

    public void openCon() {
        db = openOrCreateDatabase("MyContactDB",MODE_PRIVATE,null);
        db.execSQL("create table if not exists ContactData(Name text,Mobile text,EmailID text,Gender text,DOB text,City text,Status text,MyImage blob)");
        db.execSQL("create table if not exists CallLogData(Name text,Mobile text,CDate text,CTime text)");
        db.execSQL("create table if not exists SmsLogData(Name text,Mobile text,SDate text,STime text)");

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA,Manifest.permission.CALL_PHONE,Manifest.permission.SEND_SMS,Manifest.permission.ACCESS_FINE_LOCATION},100);
        }

        SharedPreferences sp = getSharedPreferences("UserData",MODE_PRIVATE);
        MyMobileNo=sp.getString("txtMobileNo","Failed");
        Toast.makeText(MainActivity.this, "MobileNo="+MyMobileNo, Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_main);
        lstContactList = findViewById(R.id.lstContactList);
        showContacts();

    }

    public void showContacts(){
        openCon();
        Cursor c = db.rawQuery("select * from ContactData",null);
        if(c.getCount()>0)
        {
            while(c.moveToNext())
            {
                Name.add(c.getString(0));
                MobNo.add(c.getString(1));
                EMailID.add(c.getString(2));
                Gender.add(c.getString(3));
                DOB.add(c.getString(4));
                City.add(c.getString(5));
                byte[] img = c.getBlob(7);
                Bitmap bmp = BitmapFactory.decodeByteArray(img,0,img.length);
                Image.add(bmp);
            }
            MyAdapter MA = new MyAdapter();
            lstContactList.setAdapter(MA);
        }
        else
        {
            Toast.makeText(MainActivity.this, "Data Not Found...", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    public void addContact(View view) {
        Intent i = new Intent(MainActivity.this,Add_Contact_Activity.class);
        startActivity(i);
        finish();
    }

    public void openCallLog(View view) {
        Intent i = new Intent(MainActivity.this,Call_Log_Activity.class);
        startActivity(i);
    }

    public void openSmsLog(View view) {
        Intent i = new Intent(MainActivity.this,Sms_Log_Activity.class);
        startActivity(i);
    }

    public void synContact(View view)
    {
        openCon();
        Cursor c = db.rawQuery("select * from ContactData where Status='Pending'",null);
        if(c.getCount()>0)
        {
            Name.clear();
            MobNo.clear();;
            EMailID.clear();
            Gender.clear();
            DOB.clear();
            City.clear();
            while(c.moveToNext())
            {
                Name.add(c.getString(0));
                MobNo.add(c.getString(1));
                EMailID.add(c.getString(2));
                Gender.add(c.getString(3));
                DOB.add(c.getString(4));
                City.add(c.getString(5));
            }
        }
        else
        {
            Toast.makeText(MainActivity.this, "Data Not Found...", Toast.LENGTH_SHORT).show();
        }
        db.close();
        //ADD THE CONTACT ON ONLINE DATABASE

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://igccontact.000webhostapp.com/addcontact.php";

        for(int i=0;i<Name.size();i++) {

            // Request a string response from the provided URL.
            int finalI = i;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            openCon();
                            db.execSQL("Update ContactData set Status='Synced'");
                            Toast.makeText(MainActivity.this, "Data sent Successfull!!", Toast.LENGTH_SHORT).show();

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> P = new HashMap<>();

                    P.put("Name", Name.get(finalI));
                    P.put("MobileNo", MobNo.get(finalI));
                    P.put("EmailId", EMailID.get(finalI));
                    P.put("Gender", Gender.get(finalI));
                    P.put("Dob", DOB.get(finalI));
                    P.put("City", City.get(finalI));
                    P.put("MyMobileNo",MyMobileNo);
                    return P;

                }
            };

// Add the request to the RequestQueue.
            queue.add(stringRequest);
        }

    }

    //Creating Base Adapter for Custome ListeView of Contact
    class MyAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return Name.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.contactlist_view,null);
            TextView txtName = convertView.findViewById(R.id.txtName);
            TextView txtMobileNo = convertView.findViewById(R.id.txtMobileNo);
            ImageView imgEdit = convertView.findViewById(R.id.imgEdit);
            ImageView imgContact = convertView.findViewById(R.id.imgContact);

            imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(MainActivity.this,Edit_Contactlist_Activity.class);
                    i.putExtra("MobileNo",txtMobileNo.getText().toString());
                    startActivity(i);

                    Toast.makeText(MainActivity.this, "Name:"+Name.get(position), Toast.LENGTH_SHORT).show();
                }
            });
            ImageView imgCall=convertView.findViewById(R.id.imgCall);
            imgCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCon();
                    Calendar c = Calendar.getInstance();
                    String dd = c.get(Calendar.DAY_OF_MONTH)+"/"+(c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.YEAR);
                    String tt = c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND);
                    ContentValues cv = new ContentValues();
                    cv.put("Name",Name.get(position));
                    cv.put("Mobile",MobNo.get(position));
                    cv.put("CDate",dd);
                    cv.put("CTime",tt);
                    db.insert("CallLogData",null,cv);
                    Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+MobNo.get(position)));
                    startActivity(i);

                }
            });
            ImageView imgSms=convertView.findViewById(R.id.imgSms);
            imgSms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCon();
                    //for getting system date and time
                    Calendar c = Calendar.getInstance();
                    String dd= c.get(Calendar.DAY_OF_MONTH)+"/"+(c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.YEAR);
                    String tt=c.get(Calendar.HOUR)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND);

                    //for inserting values in table of database
                     ContentValues cv=new ContentValues();
                    cv.put("Name",Name.get(position));
                    cv.put("Mobile",MobNo.get(position));
                    cv.put("SDate",dd);
                    cv.put("STime",tt);
                    db.insert("SmsLogData",null,cv);

                    //for goto next Activity(System Activity for msg)
                    Intent i =new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"+MobNo.get(position)));
                    startActivity(i);

                }
            });
            ImageView imgDel = convertView.findViewById(R.id.imgDel);
            imgDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCon();
                    db.execSQL("delete from ContactData where Mobile='"+txtMobileNo.getText().toString()+"'");

                    Toast.makeText(MainActivity.this, "Contact Deleted!!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(MainActivity.this,MainActivity.class);
                    startActivity(i);
                }
            });

            txtName.setText(Name.get(position));
            txtMobileNo.setText(MobNo.get(position));
            imgContact.setImageBitmap(Image.get(position));
            return convertView;
        }
    }

    @Override
    public void onBackPressed() {
      AlertDialog.Builder adp = new AlertDialog.Builder(MainActivity.this);
      adp.setMessage("Do you wnat to exit..?");
      adp.setNegativeButton("No",null);
      adp.setCancelable(false);
      adp.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
              System.exit(0);
          }
      });
      adp.show();
    }
}