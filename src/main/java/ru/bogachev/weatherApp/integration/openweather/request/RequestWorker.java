package ru.bogachev.weatherApp.integration.openweather.request;

import ru.bogachev.weatherApp.integration.openweather.Strategies;

public interface RequestWorker {

    Strategies getStrategy();

}
