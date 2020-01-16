package com.example.exp3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class WeatherFragment extends Fragment {

    private static final String ARG_WEATHER_ID="weather_id";
    private Weather mWeather;
    private TextView mWeekTxtv;
    private TextView mDateTxtv;
    private TextView mStatusTxtv;
    private TextView mMaxTxtv;
    private TextView mMinTxtv;
    private TextView mHumTxtv;
    private TextView mPreTxtv;
    private TextView mWindTxtv;
    private ImageView mWeatherImg;


    public static WeatherFragment newInstance(UUID weatherId,String unit) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_WEATHER_ID, weatherId);
        args.putSerializable(SettingActivity.EXTRA_UNITS,unit);

        WeatherFragment fragment = new WeatherFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID weatherId = (UUID)getArguments().getSerializable(ARG_WEATHER_ID);
        mWeather = WeatherLab.get(getActivity()).getmWeather(weatherId);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_weather,
                parent, false);
        mWeekTxtv = (TextView)v.findViewById(R.id.weather_week2);
        mDateTxtv = (TextView) v.findViewById(R.id.weather_date);
        mStatusTxtv = (TextView) v.findViewById(R.id.weather_status2);
        mMaxTxtv = (TextView)v.findViewById(R.id.temp_max);
        mMinTxtv = (TextView)v.findViewById(R.id.temp_min);
        mWeekTxtv = (TextView) v.findViewById(R.id.weather_week2);
        mHumTxtv = (TextView) v.findViewById(R.id.weather_hum);
        mPreTxtv = (TextView)v.findViewById(R.id.weather_wind);
        mWindTxtv = (TextView)v.findViewById(R.id.weather_pre);
        mWeatherImg = (ImageView)v.findViewById(R.id.weather_img2);



        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format1.parse(mWeather.getmDate());
        }catch (Exception e){ }
        SimpleDateFormat format2 = new SimpleDateFormat("EEEE");
        SimpleDateFormat format3 = new SimpleDateFormat("MMM dd");
        mWeekTxtv.setText(format2.format(date));
        mDateTxtv.setText(format3.format(date));
        mStatusTxtv.setText(mWeather.getmStatus());
        String unit = getArguments().getString(SettingActivity.EXTRA_UNITS);
        if(unit.equals("Celsius")) {
            mMaxTxtv.setText(mWeather.getmTmp_max() + "°C");
            mMinTxtv.setText(mWeather.getmTmp_min() + "°C");
        }else {
            double tmp_max = Double.parseDouble(mWeather.getmTmp_max());
            tmp_max = tmp_max * 1.8 + 32;
            double tmp_min = Double.parseDouble(mWeather.getmTmp_max());
            tmp_min = tmp_min * 1.8 + 32;
            if(mWeather.getmTmp_max().contains("-")) {
                mMaxTxtv.setText("-" + tmp_max + "°F");
            }else{
                mMaxTxtv.setText(tmp_max + "°F");
            }
            if(mWeather.getmTmp_min().contains("-")) {
                mMinTxtv.setText("-" + tmp_min + "°F");
            }else{
                mMinTxtv.setText(tmp_min + "°F");
            }
        }
        mPreTxtv.setText(mWeather.getmPres() + "hPa");
        mHumTxtv.setText(mWeather.getmHum() + "%");
        mWindTxtv.setText(mWeather.getmWindSpd() + "km/h SE");
        mWeather.changePhoto(mWeatherImg, mWeather.getmStatusCode());


        return v;
    }


}
