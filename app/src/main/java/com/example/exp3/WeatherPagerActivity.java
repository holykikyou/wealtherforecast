package com.example.exp3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.List;
import java.util.UUID;

public class WeatherPagerActivity extends AppCompatActivity {
    private static final String EXTRA_WEATHER_ID =
            "com.example.exp3.weather_id";
    private ViewPager mViewPager;
    private List<Weather> mWeathers;
    private String mUnit;
    private Toolbar mToolbar;
    private Weather cur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_pager);
        UUID weatherId = (UUID)getIntent().getSerializableExtra(EXTRA_WEATHER_ID);
        mUnit = getIntent().getStringExtra(SettingActivity.EXTRA_UNITS);
        cur =WeatherLab.get(this).getmWeather(weatherId);

        //用于左右滑动
        mViewPager = (ViewPager)findViewById(R.id.weather_view_pager);
        //导航栏
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
//        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        mToolbar.inflateMenu(R.menu.fragment_weather_list);

        mWeathers = WeatherLab.get(this).getmWeathers();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                Weather weather = mWeathers.get(position);
                return WeatherFragment.newInstance(weather.getmId(),mUnit);
            }

            @Override
            public int getCount() {
                return mWeathers.size();
            }
        });

        //点击左上角返回
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("WeatherActivity", "return list");
                finish();
            }
        });


        for (int i = 0;i < mWeathers.size(); i++){
            if(mWeathers.get(i).getmId().equals(weatherId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.fragment_weather_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.map_location:
                return true;
            case R.id.setting:
                Log.i("WeatherFragment","111");
                Intent i = new Intent(WeatherPagerActivity.this, SettingActivity.class);
                startActivityForResult(i,2);
                return true;
            case R.id.share:
                Intent textIntent = new Intent(Intent.ACTION_SEND);
                textIntent.setType("text/plain");
                textIntent.putExtra(Intent.EXTRA_TEXT, "Forecast:" + cur.getmStatus() + " High:" + cur.getmTmp_max() + "°C Low:" + cur.getmTmp_min() + "°C");
                startActivity(Intent.createChooser(textIntent, "分享"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static Intent newIntent(Context packageContext, UUID weatherId,String unit){
        Intent i = new Intent(packageContext,WeatherPagerActivity.class);
        i.putExtra(EXTRA_WEATHER_ID, weatherId);
        i.putExtra(SettingActivity.EXTRA_UNITS, unit);
        return i;
    }
}
