package com.example.exp3;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WeatherLab {
    private static WeatherLab sWeatherLab;
    private List<Weather> mWeathers;
    private String mLoc;

    public static WeatherLab get(Context context){
        if(sWeatherLab == null){
            sWeatherLab = new WeatherLab(context);
        }
        return sWeatherLab;
    }

    private WeatherLab(Context context){
        mWeathers = new ArrayList<>();
        //生成100个crime
//        for (int i = 0;i < 100; i++){
//            Crime crime = new Crime();
//            crime.setmTitle("Crime #" + i);
//            crime.setmSolved(i % 2 == 0);
//            mCrimes.add(crime);
//        }
    }

    public List<Weather> getmWeathers() {
        return mWeathers;
    }

    public Weather getmWeather(UUID id){
        for (Weather crime : mWeathers){
            if(crime.getmId().equals(id)){
                return crime;
            }
        }
        return null;
    }
    public void setmWeathers(List<Weather> weathers) {
        mWeathers = weathers;
    }

    public String getmLoc() {
        return mLoc;
    }

    public void setmLoc(String mLoc) {
        this.mLoc = mLoc;
    }
}
