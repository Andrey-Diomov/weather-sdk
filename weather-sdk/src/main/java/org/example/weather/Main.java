package org.example.weather;

public class Main {
  public static void main(String[] args) throws Exception {
    WeatherSDK sdk =  WeatherSDKManager.create("163ba81cae33a6ba70a346785dc949e6", false);
    WeatherData data = sdk.getWeather("London");

    System.out.printf(
        "Weather in %s: %s (%s), %.1f°C, feels like %.1f°C%n",
        data.name(),
        data.weather().main(),
        data.weather().description(),
        data.temperature().temp(),
        data.temperature().feels_like()
    );

    System.out.printf("Wind speed: %.1f m/s, visibility: %d m%n",
        data.wind().speed(),
        data.visibility()
    );

    System.out.printf("Sunrise: %d, Sunset: %d, Timezone: %d%n",
        data.sys().sunrise(),
        data.sys().sunset(),
        data.timezone()
    );
  }
}