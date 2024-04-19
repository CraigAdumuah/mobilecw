package com.example.mobilecwnew;
//
// Name                 Craig Adumuah_________________
// Student ID           S2026435_________________
// Programme of Study   Computing_________________
//

import androidx.lifecycle.LiveData;
        import androidx.lifecycle.MutableLiveData;
        import androidx.lifecycle.ViewModel;

public class WeatherViewModel extends ViewModel {
    private WeatherRepository weatherRepository = new WeatherRepository();
    private MutableLiveData<String> weatherData = new MutableLiveData<>();

    public LiveData<String> getWeatherData() {
        return weatherData;
    }

    public void fetchWeatherData(String location) {
        weatherRepository.fetchWeatherData(location);
        weatherData = weatherRepository.getWeatherData();
    }
}
