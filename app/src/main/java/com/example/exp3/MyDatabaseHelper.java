package com.example.exp3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public  static final String CREATE_WEATHER = "create table Weather(id varchar(32) primary key,"
            +"date varchar(30),tmp_max varchar(10),tmp_min varchar(10),status varchar(10),status_code varchar(10),hum varchar(10),pre varchar(10),wind varchar(10),loc varchar(50))";
    private Context mContext;
    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory cf, int version){
        super(context, name, cf, version);
        mContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_WEATHER);
       //Toast.makeText(mContext, "Create succeed",Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion) { }

}
