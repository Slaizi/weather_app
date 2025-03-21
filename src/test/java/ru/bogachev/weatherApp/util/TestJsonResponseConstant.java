package ru.bogachev.weatherApp.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestJsonResponseConstant {

    public static final String GEOLOCATION_JSON = """
                [
                    {
                        "name": "Moscow",
                        "local_names": {
                            "lt": "Maskva",
                            "lg": "Moosko",
                            "fa": "مسکو",
                            "fi": "Moskova",
                            "ps": "مسکو",
                            "hr": "Moskva",
                            "et": "Moskva",
                            "kk": "Мәскеу",
                            "mt": "Moska",
                            "ka": "მოსკოვი"
                        },
                        "lat": 55.7558,
                        "lon": 37.6173,
                        "country": "RU",
                        "state": "Moscow"
                    }
                ]
            """;

    public static final String WEATHER_JSON = """
            {
                "coord": {
                    "lon": 37.6234,
                    "lat": 55.7621
                },
                "weather": [
                    {
                        "id": 803,
                        "main": "Clouds",
                        "description": "облачно с прояснениями",
                        "icon": "04d"
                    }
                ],
                "base": "stations",
                "main": {
                    "temp": 8.09,
                    "feels_like": 5.62,
                    "temp_min": 7.18,
                    "temp_max": 8.48,
                    "pressure": 1021,
                    "humidity": 63,
                    "sea_level": 1021,
                    "grnd_level": 1001
                },
                "visibility": 10000,
                "wind": {
                    "speed": 4.08,
                    "deg": 236,
                    "gust": 9.07
                },
                "clouds": {
                    "all": 63
                },
                "dt": 1738233424,
                "sys": {
                    "type": 2,
                    "id": 2095214,
                    "country": "RU",
                    "sunrise": 1738214829,
                    "sunset": 1738245496
                },
                "timezone": 10800,
                "id": 524901,
                "name": "Москва",
                "cod": 200
            }
            """;
}
