package com.example.mobilecwnew;
//
// Name                 Craig Adumuah_________________
// Student ID           S2026435_________________
// Programme of Study   Computing_________________
//

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.Timer;
import java.util.TimerTask;

public class WeatherRepository {
    private MutableLiveData<String> weatherData = new MutableLiveData<>();
    private Timer timer;

    public LiveData<String> getWeatherData() {
        // Start periodic data fetching
        startPeriodicDataFetch();
        return weatherData;
    }

    private void startPeriodicDataFetch() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Fetch weather data periodically
                fetchWeatherData("Glasgow"); // You can change the location here
            }
        }, 0, 1000 * 60 * 60 * 2); // Fetch data every 2 hours (adjust as needed)
    }

    public void fetchWeatherData(String location) {
        // Implement logic to fetch weather data from RSS feed or API
        // For demonstration, simply set a sample weather data
        String sampleData = "Weather data for " + location;
        weatherData.postValue(sampleData);
    }

    // Stop the timer when not needed (e.g., when activity is destroyed)
    public void stopPeriodicDataFetch() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }
}
