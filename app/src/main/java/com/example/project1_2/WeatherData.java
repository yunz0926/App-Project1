package com.example.project1_2;

public class WeatherData {
    private double temp;
    private String weather;
    private int time;

    public WeatherData(double temp, String weather, int time){
        this.temp = temp;
        this.weather = weather;
        this.time = time;
    }

    public double getTemp(){
        return temp;
    }

    public String getWeather(){
        return weather;
    }

    public int getTime(){
        return time;
    }

}
