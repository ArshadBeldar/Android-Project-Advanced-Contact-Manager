package com.example.contactmanagerassigment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Call_Log_Activity extends AppCompatActivity {
    ListView lstCallLog;
    String[] Name,MobNo,CDate,CTime;
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
        setContentView(R.layout.call_log_layout);
        lstCallLog=findViewById(R.id.lstCallLog);

        openCon();
        Cursor c = db.rawQuery("Select * from CallLogData order by ctime desc",null);
        if(c.getCount()>0)
        {
            Name = new String[c.getCount()];
            MobNo = new String[c.getCount()];
            CDate = new String[c.getCount()];
            CTime = new String[c.getCount()];
            int i=0;
            while(c.moveToNext())
            {
                Name[i] = c.getString(0);
                MobNo[i] = c.getString(1);
                CDate[i] = c.getString(2);
                CTime[i] = c.getString(3);
                i++;
            }
            MyAdapter MA = new MyAdapter();
            lstCallLog.setAdapter(MA);
        }
        else
        {
            Toast.makeText(Call_Log_Activity.this, "No Call Log Data Available", Toast.LENGTH_SHORT).show();
        }
        
    }
    class MyAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return Name.length;
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
            convertView = getLayoutInflater().inflate(R.layout.call_log_design,null);
            TextView txtName = convertView.findViewById(R.id.txtName);
            TextView txtMobNo = convertView.findViewById(R.id.txtMobileNo);
            TextView txtDate = convertView.findViewById(R.id.txtDate);
            TextView txtTime = convertView.findViewById(R.id.txtTime);

            txtName.setText(Name[position]);
            txtMobNo.setText(MobNo[position]);
            txtDate.setText(CDate[position]);
            txtTime.setText(CTime[position]);
            return convertView;
        }
    }
}
