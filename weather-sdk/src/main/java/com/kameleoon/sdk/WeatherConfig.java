package com.kameleoon.sdk;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import lombok.Getter;

@Getter
public class WeatherConfig {
  private static final String CONFIG_FILE = "weather-sdk.properties";

  private final int cacheMaxSize;
  private final long pollingIntervalMs;

  public WeatherConfig() {
    Properties props = new Properties();
    try (InputStream in = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
      if (in != null) {
        props.load(in);
      } else {
        System.err.println("Warning: weather-sdk.properties not found, using defaults");
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to load weather-sdk.properties", e);
    }

    this.cacheMaxSize = Integer.parseInt(props.getProperty("cache.maxSize", "10"));
    this.pollingIntervalMs = Long.parseLong(props.getProperty("polling.intervalMs", "600000"));
  }
}