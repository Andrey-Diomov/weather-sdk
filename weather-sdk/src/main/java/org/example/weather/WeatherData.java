package org.example.weather;

import com.google.gson.JsonObject;

public record WeatherData(
    Weather weather,
    Temperature temperature,
    int visibility,
    Wind wind,
    long datetime,
    Sys sys,
    int timezone,
    String name,
    long createdAt
) {

  private static final long EXPIRATION_MS = 10 * 60 * 1000;

  public static WeatherData fromJson(JsonObject json) {
    JsonObject weatherObj = json.getAsJsonArray("weather").get(0).getAsJsonObject();

    JsonObject mainObj = json.getAsJsonObject("temperature") != null
        ? json.getAsJsonObject("temperature")
        : json.getAsJsonObject("main");

    JsonObject windObj = json.getAsJsonObject("wind");
    JsonObject sysObj = json.getAsJsonObject("sys");

    Weather weather = new Weather(
        weatherObj.get("main").getAsString(),
        weatherObj.get("description").getAsString()
    );

    Temperature temperature = new Temperature(
        mainObj.get("temp").getAsDouble(),
        mainObj.get("feels_like") != null
            ? mainObj.get("feels_like").getAsDouble()
            : mainObj.get("feelsLike").getAsDouble()
    );

    Wind wind = new Wind(
        windObj.get("speed").getAsDouble()
    );

    Sys sys = new Sys(
        sysObj.get("sunrise").getAsLong(),
        sysObj.get("sunset").getAsLong()
    );

    return new WeatherData(
        weather,
        temperature,
        json.get("visibility").getAsInt(),
        wind,
        json.get("datetime") != null ? json.get("datetime").getAsLong() : json.get("dt").getAsLong(),
        sys,
        json.get("timezone").getAsInt(),
        json.get("name").getAsString(),
        System.currentTimeMillis()
    );
  }

  public boolean isExpired() {
    return System.currentTimeMillis() - createdAt > EXPIRATION_MS;
  }

  public record Weather(String main, String description) {}

  public record Temperature(double temp, double feels_like) {}

  public record Wind(double speed) {}

  public record Sys(long sunrise, long sunset) {}
}