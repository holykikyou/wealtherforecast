package com.example.exp3;

import android.util.Log;
import android.widget.ImageView;

import java.util.UUID;

public class Weather {
    private UUID mId;
    private String mStatus;
    private String mDate;
    private String mTmp_max;
    private String mTmp_min;
    private String mWindSpd;
    private String mPres;
    private String mHum;
    private String mStatusCode;

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public Weather() {
        mId = UUID.randomUUID();
    }

    public String getmStatus() {
        return mStatus;
    }
    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }
    public UUID getmId() {
        return mId;
    }
    public String getmTmp_max() {
        return mTmp_max;
    }

    public void setmTmp_max(String tmp_max) {
        this.mTmp_max = tmp_max;
    }

    public String getmTmp_min() {
        return mTmp_min;
    }

    public void setmTmp_min(String tmp_min) {
        this.mTmp_min = tmp_min;
    }

    public String getmWindSpd() {
        return mWindSpd;
    }

    public void setmWindSpd(String mWindSpd) {
        this.mWindSpd = mWindSpd;
    }

    public String getmPres() {
        return mPres;
    }

    public void setmPres(String mPres) {
        this.mPres = mPres;
    }

    public String getmHum() {
        return mHum;
    }

    public void setmHum(String mHum) {
        this.mHum = mHum;
    }

    public String getmStatusCode() {
        return mStatusCode;
    }

    public void setmStatusCode(String mStatusCode) {
        this.mStatusCode = mStatusCode;
    }

    //根据状态码生成相应图片
    public void changePhoto(ImageView imageView, String statusCode){
        System.out.println(statusCode);
        Log.i("Weather",statusCode);
        if(statusCode.equals("100")){
            imageView.setImageResource(R.drawable.sunny);
        }else if(statusCode.equals("101") || statusCode.equals("102")){
            imageView.setImageResource(R.drawable.cloud);
        }else if(statusCode.equals("103") || statusCode.equals("104")){
            imageView.setImageResource(R.drawable.overcast);
        }else if(statusCode.equals("200") || statusCode.equals("202") || statusCode.equals("203") || statusCode.equals("204")){
            imageView.setImageResource(R.drawable.windy);
        }else if(statusCode.equals("302") || statusCode.equals("303")){
            imageView.setImageResource(R.drawable.thundershower);
        }else if(statusCode.equals("304")){
            imageView.setImageResource(R.drawable.hail);
        }else if(statusCode.equals("305") || statusCode.equals("306") || statusCode.equals("307") || statusCode.equals("399")){
            imageView.setImageResource(R.drawable.rain);
        }else if(statusCode.equals("401") || statusCode.equals("401") || statusCode.equals("402") || statusCode.equals("403") || statusCode.equals("499") || statusCode.equals("499")){
            imageView.setImageResource(R.drawable.snow);
        }else if(statusCode.equals("500") || statusCode.equals("501")){
            imageView.setImageResource(R.drawable.foggy);
        }else if(statusCode.equals("507") || statusCode.equals("508")){
            imageView.setImageResource(R.drawable.sand_storm);
        }else {
            imageView.setImageResource(R.drawable.unknown);
        }
    }
}
