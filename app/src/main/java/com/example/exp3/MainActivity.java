package com.example.exp3;

import android.app.Activity;
import android.app.AppComponentFactory;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.weather.forecast.Forecast;
import interfaces.heweather.com.interfacesmodule.view.HeConfig;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

public class MainActivity extends AppCompatActivity implements WeatherListFragment.Callbacks{
    private static final String TAG = "WeatherMainActivity";
    private static final String API_KEY="1d85412c9574418fb7c4a0159fda9d57";
    private static final String USERNAME="HE1911172120091256";
    private MyDatabaseHelper dbHelper;
    private WeatherLab weatherLab = WeatherLab.get(MainActivity.this);
    private String mLoc;
    private String mLongLoc = "北京市北京市东城区";
    private String mLat;
    private String mLon;
    private String mUnits = "Celsius";
    private List<Weather> mItems = new ArrayList<>();
    private Fragment fragment;
    private TextView mTodayTxtv;
    private TextView mMaxTxtv;
    private TextView mMinTxtv;
    private TextView mStatusTxtv;
    private ImageView mWeatherImg;
    private Weather mTodayWeather;
    private Date mToday = new Date();
    private SimpleDateFormat mFormat = new SimpleDateFormat("MMM dd");
    private Toolbar mToolbar;
    private boolean mNotice;
    private int num = 0;

    protected Fragment createFragment(){
        return WeatherListFragment.newInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masterdetail);
        num++;

        mTodayTxtv = (TextView) findViewById(R.id.weather_today);
        mStatusTxtv = (TextView) findViewById(R.id.today_status);
        mMaxTxtv = (TextView)findViewById(R.id.today_max);
        mMinTxtv = (TextView)findViewById(R.id.today_min);
        mWeatherImg = (ImageView)findViewById(R.id.today_img);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        FragmentManager fm = getSupportFragmentManager();
        fragment = fm.findFragmentById(
                R.id.fragment_container);

        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction().add(R.id.fragment_container,
                    fragment).commit();
        }

        dbHelper = new MyDatabaseHelper(this, "WeatherForecast.db", null, 1);
        dbHelper.getWritableDatabase();

        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        mLoc = pref.getString("loc", "北京市");


        new FetchItemTask().execute();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putString("loc", mLoc);
        editor.commit();
        Log.d(TAG,"onDestroy");
    }

    //用于区分平板和手机的布局
    @Override
    public void onWeatherSelected(Weather weather){
        if(findViewById(R.id.fragment_container_detail) == null){
            Intent i = WeatherPagerActivity.newIntent(this, weather.getmId(), mUnits);
            startActivity(i);
        }else{
            Fragment newDatail = WeatherFragment.newInstance(weather.getmId(),mUnits);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_detail, newDatail)
                    .commit();
        }
    }

    //右上角菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.fragment_weather_list, menu);
        return true;
    }
    //每个菜单按钮的动作
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.map_location:
                //调用手机地图
                StringBuffer sb = new StringBuffer();
                sb.append("geo:").append(mLat).append(",").append(mLon)
                        .append("?").append("z=").append(18).append("?").append("q=")
                        .append(mLongLoc);
                Uri mUri = Uri.parse(sb.toString());
                Intent mIntent = new Intent(Intent.ACTION_VIEW, mUri);
                startActivity(mIntent);
                return true;
            case R.id.setting:
                //进入设置界面
                Intent i = SettingActivity.newIntent(MainActivity.this, mLongLoc,mUnits, mNotice);
                startActivityForResult(i, 1);
                return true;
            case R.id.share:
                //分享功能
                Intent textIntent = new Intent(Intent.ACTION_SEND);
                textIntent.setType("text/plain");
                textIntent.putExtra(Intent.EXTRA_TEXT, "Forecast:" + mTodayWeather.getmStatus() + " High:" + mTodayWeather.getmTmp_max() + "°C Low:" + mTodayWeather.getmTmp_min() + "°C");
                startActivity(Intent.createChooser(textIntent, "分享"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK){
            Log.i(TAG,"resultCode" + resultCode);
            return;
        }
        if(requestCode == 1){
            if(data == null){
                return;
            }else{
                //温度单位是否变化
                String data2 = data.getStringExtra(SettingActivity.EXTRA_UNITS);
                if(data2 == null){

                }else {

                    if(data2.equals(mUnits)){
                        Log.d(TAG,data2+"没更新");
                    }else {
                        mUnits = data2;
                        updateUI();
                        Log.d(TAG, data2 + "更新了");
                    }
                }
                Log.i(TAG,"requestCode" + requestCode);
                //定位是否变化
                String data1 = data.getStringExtra(SettingActivity.EXTRA_LOCATION);
                if(data1 == null){

                }else{
                    //接收返回的新地址，重新从接口获取数据并显示
                    Log.i(TAG,"data received");

                    if(data1.equals(mLoc)){

                    }else{
                        mLoc = data.getStringExtra(SettingActivity.EXTRA_LOCATION);
                        weatherLab.setmLoc(mLoc);
                        mItems.clear();
                        new FetchItemTask().execute();
                    }
                }
                mLongLoc = data.getStringExtra(SettingActivity.EXTRA_LONG_LOCATION);
                mNotice = data.getBooleanExtra(SettingActivity.EXTRA_NOTICE,false);
            }
        }
    }

    public static Intent newIntent(Context packageContext, String loc,String unit){
        Intent i = new Intent(packageContext,MainActivity.class);
        i.putExtra(SettingActivity.EXTRA_LOCATION, loc);
        i.putExtra(SettingActivity.EXTRA_UNITS, unit);
        return i;
    }

    //更新当前视图
    public void updateUI(){
        mTodayWeather = mItems.get(0);
        if(mTodayTxtv == null){

        }else{
            mTodayTxtv.setText("今天," + mFormat.format(mToday));
            mStatusTxtv.setText(mTodayWeather.getmStatus());
            mTodayWeather.changePhoto(mWeatherImg, mTodayWeather.getmStatusCode());
            if(num == 1) {
                mItems.remove(0);
            }
            if(mUnits.equals("Celsius")) {
                mMaxTxtv.setText(mTodayWeather.getmTmp_max() + "°C");
                mMinTxtv.setText(mTodayWeather.getmTmp_min() + "°C");
                Log.d(TAG, "sheshidu");
            }else {
                double tmp_max = Double.parseDouble(mTodayWeather.getmTmp_max());
                tmp_max = tmp_max * 1.8 + 32;
                double tmp_min = Double.parseDouble(mTodayWeather.getmTmp_min());
                tmp_min = tmp_min * 1.8 + 32;
                DecimalFormat f = new DecimalFormat("0.0");
                if(mTodayWeather.getmTmp_max().contains("-")) {
                    mMaxTxtv.setText("-" + f.format(tmp_max) + "°F");
                }else{
                    mMaxTxtv.setText(f.format(tmp_max) + "°F");
                }
                if(mTodayWeather.getmTmp_min().contains("-")) {
                    mMinTxtv.setText("-" + f.format(tmp_min) + "°F");
                }else{
                    mMinTxtv.setText(f.format(tmp_min) + "°F");
                }
                Log.d(TAG, "huashidu");
            }
        }
        WeatherListFragment f = (WeatherListFragment)fragment;
        f.updateUI(mItems, mUnits);
    }

    private class FetchItemTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... params) {
            HeConfig.init(USERNAME, API_KEY);
            HeConfig.switchToFreeServerNode();
            HeWeather.getWeatherForecast(MainActivity.this, mLoc, new HeWeather.OnResultWeatherForecastBeanListener() {
                @Override
                public void onError(Throwable e) {
                    Log.d(TAG,"loc:"+mLoc);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    String[] selectionArgs = new  String[]{ mLoc };
                    Cursor cursor = db.query("Weather", null, "loc=?", selectionArgs, null, null, null); // 查询Book表中所有的数据
                    if (cursor.moveToFirst()) {
                        do {// 遍历Cursor对象，取出数据并打印
                            Weather item = new Weather();
                            item.setmDate(cursor.getString(cursor.getColumnIndex("date")));
                            item.setmStatus(cursor.getString(cursor.getColumnIndex("status")));
                            item.setmStatusCode(cursor.getString(cursor.getColumnIndex("status_code")));
                            item.setmTmp_max(cursor.getString(cursor.getColumnIndex("tmp_max")));
                            item.setmTmp_min(cursor.getString(cursor.getColumnIndex("tmp_min")));
                            item.setmHum(cursor.getString(cursor.getColumnIndex("hum")));
                            item.setmPres(cursor.getString(cursor.getColumnIndex("pre")));
                            item.setmWindSpd(cursor.getString(cursor.getColumnIndex("wind")));
                            mItems.add(item);
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                    WeatherLab.get(MainActivity.this).setmWeathers(mItems);
                    updateUI();
                    Log.i(TAG, "onError: " + e.getMessage(), e);
                }

                @Override
                public void onSuccess(Forecast dataObject) {
                    try {
                        JSONObject jsonBody = new JSONObject(new Gson().toJson(dataObject));
                        new WeatherFetchr().parseItems(mItems, jsonBody);
                        EnumMap<WeatherFetchr.Loc,String> map = new WeatherFetchr().parseLoc(jsonBody);
                        mLat = map.get(WeatherFetchr.Loc.lat);
                        mLon = map.get(WeatherFetchr.Loc.lon);
                        WeatherLab.get(MainActivity.this).setmWeathers(mItems);
                        updateUI();
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to sparse JSON:" + e.getMessage(), e);
                    }
                    for (Weather w : mItems){
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put("id",w.getmId().toString().replace("-",""));
                        values.put("date", w.getmDate());
                        values.put("tmp_max", w.getmTmp_max());
                        values.put("tmp_min", w.getmTmp_min());
                        values.put("status", w.getmStatus());
                        values.put("status_code", w.getmStatusCode());
                        values.put("hum", w.getmHum());
                        values.put("pre", w.getmPres());
                        values.put("wind", w.getmWindSpd());
                        values.put("loc", mLoc);
                        db.insert("Weather", null, values); // 插入第一条数据
                        values.clear();
                    }
                    Log.i(TAG, "Received JSON" + new Gson().toJson(dataObject));
                }
            });
            //该函数之后任何语句不能执行，必须在onSuccess中执行

            return null;
        }
    }

}
