package com.example.contactmanagerassigment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Sms_Log_Activity extends AppCompatActivity {
    ListView lstSmsLog;
    GlobalDB DB = new GlobalDB();
    String Name[],MobNo[],SDate[],STime[];
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
        setContentView(R.layout.sms_log_layout);
        lstSmsLog=findViewById(R.id.lstSmsLog);

        openCon();
        Cursor c = db.rawQuery("select * from SmsLogData order by STime desc",null);
        if (c.getCount()>0)
        {
            Name= new String[c.getCount()];
            MobNo= new String[c.getCount()];
            SDate= new String[c.getCount()];
            STime= new String[c.getCount()];

            int i=0;
            while (c.moveToNext())
            {
                Name[i]=c.getString(0);
                MobNo[i]=c.getString(1);
                SDate[i]=c.getString(2);
                STime[i]=c.getString(3);
                i++;
            }
        }
        else
        {
            Toast.makeText(Sms_Log_Activity.this, "Smslog Data Not Available", Toast.LENGTH_SHORT).show();
        }
        MyAdapter MA=new MyAdapter();
        lstSmsLog.setAdapter(MA);
        db.close();
    }
    class MyAdapter extends BaseAdapter{

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

            convertView = getLayoutInflater().inflate(R.layout.sms_log_design,null);
            TextView txtSmsName= convertView.findViewById(R.id.txtSmsName);
            TextView txtSmsMobileNo=convertView.findViewById(R.id.txtSmsMobileNo);
            TextView txtDate=convertView.findViewById(R.id.txtDate);
            TextView txtTime=convertView.findViewById(R.id.txtTime);

            txtSmsName.setText(Name[position]);
            txtSmsMobileNo.setText(MobNo[position]);
            txtDate.setText(SDate[position]);
            txtTime.setText(STime[position]);

            return convertView;
        }
    }
}
