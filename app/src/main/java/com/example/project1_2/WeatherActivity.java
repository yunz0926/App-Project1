package com.example.project1_2;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class WeatherActivity extends Activity {
    public static ArrayList<WeatherData> weatherList;
    double longitude;
    double latitude;

    public WeatherActivity(){
        weatherList = null;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public ArrayList<WeatherData> getWeatherData(Context c, LocationManager lm){
        double longitude;
        double latitude;

        if(ContextCompat.checkSelfPermission(c, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            String locationProvider = LocationManager.NETWORK_PROVIDER;
            Location location = lm.getLastKnownLocation(locationProvider);

            /*
            longitude = location.getLongitude();
            latitude = location.getLatitude();*/
            longitude = 127.36594836;
            latitude = 36.3741451;



            String queryURL = "http://api.openweathermap.org/data/2.5/onecall?lat=" + latitude + "&lon=" + longitude + "&exclude=current,minutely,daily,alerts&appid=260c05833f6d6608df17de1271ec4d50";
            try{
                URL url = new URL(queryURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(getStringFromInputStream(in));
                JSONObject json = (JSONObject)obj;
                weatherList = parseJSON(json);
            } catch(MalformedURLException e){
                System.err.println("Malformed URL");
                e.printStackTrace();
            } catch(JSONException e) {
                System.err.println("JSON parsing error");
                e.printStackTrace();
            } catch(IOException e){
                System.err.println("URL Connection failed");
                e.printStackTrace();
            } catch(ParseException e){
                System.err.println("Parse Excepption");
                e.printStackTrace();
            }

        }
        return weatherList;
    }

    private String getStringFromInputStream(InputStream is){
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try{
            br = new BufferedReader(new InputStreamReader(is));
            while((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    private ArrayList<WeatherData> parseJSON(JSONObject json) throws JSONException {
        ArrayList<WeatherData> dataList = new ArrayList<WeatherData>();
        JSONParser parser;
        JSONArray parsedJsonArray = new JSONArray();
        String jsonString = json.toString();
        org.json.JSONObject jsonObject = new org.json.JSONObject(jsonString);
        org.json.JSONArray hourly = jsonObject.getJSONArray("hourly");

        if(hourly != null){
            for(int i = 0; i < hourly.length() && i<= 24 ; i++){
                Object obj = (Object) hourly.getJSONObject(i);
                org.json.JSONObject jo = (org.json.JSONObject) obj;
                int temp = jo.getInt("temp");
                String weather = jo.getJSONArray("weather").getJSONObject(0).getString("main");

                WeatherData w = new WeatherData((double)temp - 273.15, weather, i);
                System.out.println(w.getTime() +": temp ->" + w.getTemp() + " weather -> " + w.getWeather());
                dataList.add(w);
            }
        }
        return dataList;
    }
}
