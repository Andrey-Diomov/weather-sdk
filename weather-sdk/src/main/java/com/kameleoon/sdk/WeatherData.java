package com.kameleoon.sdk;

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

  private static final String WEATHER = "weather";
  private static final String MAIN = "main";
  private static final String DESCRIPTION = "description";

  private static final String TEMPERATURE = "temperature";
  private static final String TEMP = "temp";
  private static final String FEELS_LIKE = "feels_like";
  private static final String FEELS_LIKE_ALT = "feelsLike";

  private static final String VISIBILITY = "visibility";
  private static final String WIND = "wind";
  private static final String SPEED = "speed";

  private static final String SYS = "sys";
  private static final String SUNRISE = "sunrise";
  private static final String SUNSET = "sunset";

  private static final String DATETIME = "datetime";
  private static final String DT = "dt";

  private static final String TIMEZONE = "timezone";
  private static final String NAME = "name";


  public static WeatherData fromJson(JsonObject json) {
    JsonObject weatherObj = json.getAsJsonArray(WEATHER)
        .get(0)
        .getAsJsonObject();

    JsonObject mainObj = json.has(TEMPERATURE)
        ? json.getAsJsonObject(TEMPERATURE)
        : json.getAsJsonObject(MAIN);

    JsonObject windObj = json.getAsJsonObject(WIND);
    JsonObject sysObj = json.getAsJsonObject(SYS);

    Weather weather = new Weather(
        weatherObj.get(MAIN).getAsString(),
        weatherObj.get(DESCRIPTION).getAsString()
    );

    double feelsLike = mainObj.has(FEELS_LIKE)
        ? mainObj.get(FEELS_LIKE).getAsDouble()
        : mainObj.get(FEELS_LIKE_ALT).getAsDouble();

    Temperature temperature = new Temperature(
        mainObj.get(TEMP).getAsDouble(),
        feelsLike
    );

    Wind wind = new Wind(
        windObj.get(SPEED).getAsDouble()
    );

    Sys sys = new Sys(
        sysObj.get(SUNRISE).getAsLong(),
        sysObj.get(SUNSET).getAsLong()
    );

    long dateTime = json.has(DATETIME)
        ? json.get(DATETIME).getAsLong()
        : json.get(DT).getAsLong();

    return new WeatherData(
        weather,
        temperature,
        json.get(VISIBILITY).getAsInt(),
        wind,
        dateTime,
        sys,
        json.get(TIMEZONE).getAsInt(),
        json.get(NAME).getAsString(),
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