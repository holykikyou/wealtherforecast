package com.example.exp3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SettingActivity extends AppCompatActivity {
    public static final String EXTRA_LOCATION =
            "com.example.exp3.location";
    public static final String EXTRA_UNITS =
            "com.example.exp3.units";
    public static final String EXTRA_LONG_LOCATION =
            "com.example.exp3.LongLoc";
    public static final String EXTRA_NOTICE =
            "com.example.exp3.Notice";
    private TextView locTxtv;
    private TextView unitsTxtv;
    private TextView noticeTxtv;
    private CheckBox noticeCkbx;
    private Toolbar mToolbar;
    //  省
    private List<ShengBean> options1Items = new ArrayList<ShengBean>();
    //  市
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    //  区
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private String mLoc;
    private String mLongLoc;
    private String mUnits;
    private boolean cnt = true;
    private boolean mNotice = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
//        String loc = getIntent().getStringExtra(EXTRA_LOCATION);

        locTxtv = (TextView)findViewById(R.id.location);
        unitsTxtv = (TextView)findViewById(R.id.units);
        noticeTxtv = (TextView)findViewById(R.id.notice_status);
        noticeCkbx = (CheckBox)findViewById(R.id.is_noticed);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        String loc = getIntent().getStringExtra(EXTRA_LOCATION);
        String unit = getIntent().getStringExtra(EXTRA_UNITS);
        boolean notice = getIntent().getBooleanExtra(EXTRA_NOTICE,false);
        if(loc == null){

        }else{
            locTxtv.setText(loc);
            mLongLoc = loc;
        }
        if(unit == null){

        }else{
            unitsTxtv.setText(unit);
            mUnits = unit;
        }
        if(notice == true){
            noticeCkbx.setChecked(notice);
            mNotice = true;
        }


        locTxtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parseData();
                showPickerView();
            }
        });

        mUnits = unitsTxtv.getText().toString();
        unitsTxtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cnt == true){
                    unitsTxtv.setText(R.string.hua);
                    mUnits = unitsTxtv.getText().toString();
                    cnt = false;
                    Log.d("WeatherSetting", "华氏度" + mUnits);
                }else {
                    unitsTxtv.setText(R.string.she);
                    mUnits = unitsTxtv.getText().toString();
                    cnt = true;
                    Log.d("WeatherSetting", "摄氏度" + mUnits);
                }
            }
        });

        noticeCkbx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mNotice = true;
                    noticeTxtv.setText("Enabled");

                    List<Weather> weathers = WeatherLab.get(SettingActivity.this).getmWeathers();
                    Weather weather = weathers.get(0);;
                    Resources resources = getResources();
                    //这个是主界面的生成调用函数，不要的话把下面一句也删掉，notification里面和pi有关的也删掉
                    Intent i = MainActivity.newIntent(SettingActivity.this,null,null);
                    PendingIntent pi = PendingIntent.getActivity(SettingActivity.this, 0, i,0);

                    Notification notification = new NotificationCompat.Builder(SettingActivity.this)
                            .setTicker(resources.getString(R.string.app_name))//小标题，一般用的app名字
                            .setSmallIcon(R.drawable.icon)//图标
                            .setContentTitle("WeatherForecast")//消息标题
                            .setContentText("Forcast:" + weather.getmStatus() + " High:" + weather.getmTmp_max() + "°C Low:" + weather.getmTmp_min() + "°C")//消息内容（今天天气情况）
                            .setContentIntent(pi)
                            .setAutoCancel(true)
                            .build();

                    NotificationManagerCompat notificationManagerCompat =
                            NotificationManagerCompat.from(SettingActivity.this);
                    notificationManagerCompat.notify(0,notification);
                }else{
                    mNotice = false;
                    noticeTxtv.setText("Enable");
                }
            }
        });

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //在用户选择地点后返回结果，再次点击会覆盖上一次
                Log.i("WeatherSetting","msg1:" + mLoc + "  " + mUnits);
                setInfoResult(mLoc, mUnits,mLongLoc,mNotice);
                finish();
            }
        });
//        locTxtv.setText(loc);

    }

    public static Intent newIntent(Context packageContext, String loc,String unit,boolean notice){
        Intent i = new Intent(packageContext,SettingActivity.class);
        i.putExtra(EXTRA_LOCATION,loc);
        i.putExtra(EXTRA_UNITS,unit);
        i.putExtra(EXTRA_NOTICE,notice);
        return i;
    }

    private void setInfoResult(String loc,String unit,String longLoc,boolean notice){
        Intent data = new Intent();
        data.putExtra(EXTRA_LOCATION, loc);
        data.putExtra(EXTRA_UNITS, unit);
        data.putExtra(EXTRA_LONG_LOCATION, longLoc);
        data.putExtra(EXTRA_NOTICE, notice);
        setResult(RESULT_OK, data);
    }

    //从json文件中取出数据到字符串中
    public String getJson(Context context,String fileName) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 解析数据并组装成自己想要的list
     */
    private void parseData(){
        String jsonStr = getJson(this, "province.json");//获取assets目录下的json文件数据
//     数据解析
        Gson gson =new Gson();
        java.lang.reflect.Type type =new TypeToken<List<ShengBean>>(){}.getType();
        List<ShengBean> shengList = gson.fromJson(jsonStr, type);
//     把解析后的数据组装成想要的list
        options1Items = shengList;
//     遍历省
        for(int i = 0; i <shengList.size() ; i++) {
//         存放城市
            ArrayList<String> cityList = new ArrayList<>();
//         存放区
            ArrayList<ArrayList<String>> province_AreaList = new ArrayList<>();
//         遍历市
            for(int c = 0; c <shengList.get(i).city.size() ; c++) {
//        拿到城市名称
                String cityName = shengList.get(i).city.get(c).name;
                cityList.add(cityName);

                ArrayList<String> city_AreaList = new ArrayList<>();//该城市的所有地区列表
                if (shengList.get(i).city.get(c).area == null || shengList.get(i).city.get(c).area.size() == 0) {
                    city_AreaList.add("");
                } else {
                    city_AreaList.addAll(shengList.get(i).city.get(c).area);
                }
                province_AreaList.add(city_AreaList);
            }
            /**
             * 添加城市数据
             */
            options2Items.add(cityList);
            /**
             * 添加地区数据
             */
            options3Items.add(province_AreaList);
        }

    }

    /**
     * 展示选择器
     * 核心代码
     */
    private void showPickerView() {
//      监听选中
        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                mLongLoc = options1Items.get(options1).name +
                        options2Items.get(options1).get(options2) +
                        options3Items.get(options1).get(options2).get(options3);
                mLoc = options2Items.get(options1).get(options2);
                mLongLoc = mLongLoc.replace(" ", "");

//                Toast.makeText(SettingActivity.this, tx, Toast.LENGTH_SHORT).show();
                locTxtv.setText(mLongLoc);
            }
        })

                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .build();

        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }

}
