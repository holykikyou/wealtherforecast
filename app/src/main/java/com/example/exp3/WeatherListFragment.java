package com.example.exp3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class WeatherListFragment extends Fragment {
    private static final String TAG = "WeatherListFragment";
//    private static final String API_KEY="1d85412c9574418fb7c4a0159fda9d57";
//    private static final String USERNAME="HE1911172120091256";
    private WeatherLab weatherLab = WeatherLab.get(getActivity());
    private String mLoc = weatherLab.getmLoc();
    private RecyclerView mWeatherRevi;
    private WeatherAdapter mAdpt;
    private List<Weather> mItems = new ArrayList<>();
    private Callbacks mCallbacks;
    String mUnits = "Celsius";

    public interface Callbacks{
        void onWeatherSelected(Weather weather);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    public static WeatherListFragment newInstance() {
        return new WeatherListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"onCreated running");
        setRetainInstance(true);
        setHasOptionsMenu(true);

//        new FetchItemTask().execute();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_weather_list, container, false);

        mWeatherRevi = (RecyclerView) v.findViewById(R.id.weather_recycler_view);
        mWeatherRevi.setLayoutManager(new LinearLayoutManager(getActivity()));

//        updateUI(mItems);

        return v;
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode != Activity.RESULT_OK){
//            Log.i(TAG,"resultCode" + resultCode);
//            return;
//        }
//        if(requestCode == 1){
//            Log.i(TAG,"requestCode" + requestCode);
//            if(data == null || data.getStringExtra(SettingActivity.EXTRA_LOCATION).equals(mLoc)){
//                return;
//            }
//            //接收返回的新地址，重新从接口获取数据并显示
//            Log.i(TAG,"data received");
////            mLoc = data.getStringExtra(SettingActivity.EXTRA_LOCATION);
////            weatherLab.setmLoc(mLoc);
////            mItems.clear();
////            new FetchItemTask().execute();
//        }
//    }

    @Override
    public void onDetach(){
        super.onDetach();
        mCallbacks = null;
    }




    public void updateUI(List<Weather> items,String unit) {
        if(isAdded()) {
            mUnits = unit;
            mItems = weatherLab.getmWeathers();
            if(mAdpt == null) {
                mAdpt = new WeatherAdapter(mItems);
                mWeatherRevi.setAdapter(mAdpt);
            }else {
                mAdpt.notifyDataSetChanged();
            }
        }
    }


    private class WeatherHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Weather mWeather;
        private TextView mDateTxtv;
        private TextView mStatusTxtv;
        private TextView mMaxTxtv;
        private TextView mMinTxtv;
        private ImageView mWeatherImg;

        public WeatherHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_weather, parent, false));

            itemView.setOnClickListener(this);
            mDateTxtv = (TextView) itemView.findViewById(R.id.weather_week);
            mStatusTxtv = (TextView) itemView.findViewById(R.id.weather_status);
            mMaxTxtv = (TextView)itemView.findViewById(R.id.tmp_max);
            mMinTxtv = (TextView)itemView.findViewById(R.id.tmp_min);
            mWeatherImg = (ImageView)itemView.findViewById(R.id.weather_img);
        }

        public void bind(Weather Weather) {
            mWeather = Weather;
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = format1.parse(mWeather.getmDate());
            }catch (Exception e){ }
            SimpleDateFormat format2 = new SimpleDateFormat("EEEE");
            mDateTxtv.setText(format2.format(date));
            mStatusTxtv.setText(mWeather.getmStatus());
            mWeather.changePhoto(mWeatherImg, mWeather.getmStatusCode());
            if(mUnits.equals("Celsius")) {
                mMaxTxtv.setText(mWeather.getmTmp_max() + "°C");
                mMinTxtv.setText(mWeather.getmTmp_min() + "°C");
                Log.d(TAG, "sheshidu");
            }else {
                double tmp_max = Double.parseDouble(mWeather.getmTmp_max());
                tmp_max = tmp_max * 1.8 + 32;
                double tmp_min = Double.parseDouble(mWeather.getmTmp_min());
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
                Log.d(TAG, "huashidu");
            }
        }


        @Override
        public void onClick(View v) {
            mCallbacks.onWeatherSelected(mWeather);
        }
    }

    private class WeatherAdapter extends RecyclerView.Adapter<WeatherHolder> {
        private List<Weather> mWeathers;

        public WeatherAdapter(List<Weather> Weathers) {
            mWeathers = Weathers;
        }

        @NonNull
        @Override
        public WeatherHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new WeatherHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull WeatherHolder holder, int position) {
            Weather weather = mWeathers.get(position);
            holder.bind(weather);
        }

        @Override
        public int getItemCount() {
            Log.i(TAG,"list has "+mWeathers.size());
            return mWeathers.size();
        }
    }

//    class FetchItemTask extends AsyncTask<Void,Void,Void> {
//        @Override
//        protected Void doInBackground(Void...params){
//            HeConfig.init(USERNAME, API_KEY);
//            HeConfig.switchToFreeServerNode();
//            HeWeather.getWeatherForecast(context, mLoc, new HeWeather.OnResultWeatherForecastBeanListener() {
//                @Override
//                public void onError(Throwable e) {
//                    Log.i(TAG, "onError: "+e.getMessage(), e);
//                }
//
//                @Override
//                public void onSuccess(Forecast dataObject) {
//                    try {
//                        JSONObject jsonBody = new JSONObject(new Gson().toJson(dataObject));
//                        new WeatherFetchr().parseItems(mItems, jsonBody);
//                        updateUI();
//                    }catch (Exception e){
//                        Log.e(TAG,"Failed to sparse JSON:" + e.getMessage(),e);
//                    }
//                    Log.i(TAG,"Received JSON" + new Gson().toJson(dataObject));
//                }
//            });
//            //该函数之后任何语句不能执行，必须在onSuccess中执行
//
//            return null;
//        }

//        @Override
//        protected void onPostExecute(List<Weather> items){
//            mItems = items;
//            Log.i(TAG,"items has "+items.size());
//            updateUI();
//        }
//    }
}