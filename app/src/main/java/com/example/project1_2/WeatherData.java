package com.example.project1_2;

public class WeatherData {
    private int temp;
    private String weather;
    private int time;
    private String iconUrl;

    public WeatherData(int temp, String weather, int time, String iconUrl){
        this.temp = temp;
        this.weather = weather;
        this.time = time;
        this.iconUrl = iconUrl;
    }

    public int getTemp(){
        return temp;
    }

    public String getWeather(){
        return weather;
    }

    public int getTime(){
        return time;
    }

    public String getIconUrl() {return iconUrl; };

}
