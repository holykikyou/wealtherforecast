package com.example.exp3;

import android.content.Context;
import android.util.Log;
import android.widget.Gallery;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.weather.forecast.Forecast;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now;
import interfaces.heweather.com.interfacesmodule.view.HeConfig;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

public class WeatherFetchr {
    enum Loc {
     lat, lon
    }
    private static final String TAG="WeatherFetchr";
    private static final String API_KEY="1d85412c9574418fb7c4a0159fda9d57";
    private static final String USERNAME="HE1911172120091256";

    public byte[] getUrlBytes(String urlSpec) throws IOException{
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new IOException(connection.getResponseMessage() +
                        ":with" + urlSpec);
            }

            int byteRead = 0;
            byte[] buffer = new byte[1024];
            while((byteRead = in.read(buffer)) > 0){
                out.write(buffer, 0, byteRead);
            }
            out.close();
            return out.toByteArray();
        }finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException{
        return new String(getUrlBytes(urlSpec));
    }

    public List<Weather> fetchItems(Context context) {
        final List<Weather> items = new ArrayList<>();
        HeConfig.init(USERNAME, API_KEY);
        HeConfig.switchToFreeServerNode();
        HeWeather.getWeatherForecast(context, "CN101010100", new HeWeather.OnResultWeatherForecastBeanListener() {
            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: "+e.getMessage(), e);
            }

            @Override
            public void onSuccess(Forecast dataObject) {
                try {
                    JSONObject jsonBody = new JSONObject(new Gson().toJson(dataObject));
                    parseItems(items, jsonBody);
                    Log.i(TAG,"items has" + items.size());
                }catch (Exception e){
                    Log.e(TAG,"Failed to sparse JSON:" + e.getMessage(),e);
                }
                Log.i(TAG,"Received JSON" + new Gson().toJson(dataObject));
            }
        });
        return items;

//以下代码时使用API时的代码，这里我使用了SDK
 //       List<Weather> items = new ArrayList<>();
//        StringBuilder sb = new StringBuilder();
//        InputStream is = null;
//        BufferedReader br = null;
//        PrintWriter out = null;
//        try {
//            //接口地址
//            String url = "https://free-api.heweather.net/s6/weather";
//            URL    uri = new URL(url);
//            HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
//            connection.setRequestMethod("POST");
//            connection.setReadTimeout(5000);
//            connection.setConnectTimeout(10000);
//            connection.setRequestProperty("accept", "*/*");
//            //发送参数
//            connection.setDoOutput(true);
//            out = new PrintWriter(connection.getOutputStream());
//            out.print(API_KEY + PARAM);
//            out.flush();
//            //接收结果
//            is = connection.getInputStream();
//            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//            String line;
//            //缓冲逐行读取
//            while ( ( line = br.readLine() ) != null ) {
//                sb.append(line);
//            }
//            String jsonString = sb.toString();
//            Log.i(TAG,"Received JSON" + jsonString);
//            JSONObject jsonBody = new JSONObject(jsonString);
//            parseItems(items,jsonBody);
//        } catch ( IOException ioe ) {
//            Log.i(TAG,"failed to fetch item",ioe);
//        }catch (JSONException je){
//            Log.e(TAG,"Failed to sparse JSON",je);
//        } finally{
//            //关闭流
//            try {
//                if(is!=null){
//                    is.close();
//                }
//                if(br!=null){
//                    br.close();
//                }
//                if (out!=null){
//                    out.close();
//                }
//            } catch ( Exception ignored ) {}
//        return items;
    }

    public void parseItems(List<Weather> items, JSONObject jsonBody) throws IOException, JSONException{
        JSONArray WeatherJsonArray = jsonBody.getJSONArray("daily_forecast");
        for (int i = 0;i < WeatherJsonArray.length(); i++){
            JSONObject forecastJsonObject = WeatherJsonArray.getJSONObject(i);

            Weather item = new Weather();
            item.setmDate(forecastJsonObject.getString("date"));
            item.setmStatus(forecastJsonObject.getString("cond_txt_d"));
            item.setmTmp_max(forecastJsonObject.getString("tmp_max"));
            item.setmTmp_min(forecastJsonObject.getString("tmp_min"));
            item.setmHum(forecastJsonObject.getString("hum"));
            item.setmPres(forecastJsonObject.getString("pres"));
            item.setmWindSpd(forecastJsonObject.getString("wind_spd"));
            item.setmStatusCode(forecastJsonObject.getString("cond_code_d"));
            Log.i(TAG,"item: date" + item.getmDate() + "status:" + item.getmStatus());

            items.add(item);
        }
        Log.i(TAG,"item has"+ items.size());
    }
    public EnumMap<Loc, String> parseLoc(JSONObject jsonBody) throws JSONException {
        JSONObject basicJsonObject = jsonBody.getJSONObject("basic");
        EnumMap<Loc, String> map = new EnumMap<Loc, String>(Loc.class);
        map.put(Loc.lat,basicJsonObject.getString("lat"));
        map.put(Loc.lon,basicJsonObject.getString("lon"));

        return map;
    }

}
