package org.example.weather;

import java.util.Optional;

public class WeatherSDK {
  private static final long POLLING_INTERVAL_MS = 10 * 60 * 1000;

  private final String apiKey;
  private final WeatherClient client;
  private final WeatherCache cache;
  private final boolean pollingMode;

  public WeatherSDK(String apiKey, boolean pollingMode) {

    if (apiKey == null || apiKey.isBlank()) {
      throw new IllegalArgumentException("API key cannot be null or empty");
    }
    this.apiKey = apiKey;
    this.pollingMode = pollingMode;
    this.client = new WeatherClient(apiKey);
    this.cache = new WeatherCache(10);

    if (pollingMode) {
      startPolling();
    }
  }

  public WeatherData getWeather(String city) throws WeatherException {

    if (city == null || city.isBlank()) {
      throw new IllegalArgumentException("City name must not be null or empty");
    }

    city = city.trim();
    Optional<WeatherData> cached = cache.get(city);
    if (cached.isPresent() && !cached.get().isExpired()) {
      return cached.get();
    }

    WeatherData fresh = client.fetchWeather(city);
    cache.put(city, fresh);
    return fresh;
  }

  private void startPolling() {
    Thread pollingThread = new Thread(() -> {
      while (true) {
        try {
          for (String city : cache.getStoredCities()) {
            WeatherData updated = client.fetchWeather(city);
            cache.put(city, updated);
          }
          Thread.sleep(POLLING_INTERVAL_MS);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          break;
        } catch (WeatherException e) {
          System.err.println("Polling error for city: " + e.getMessage());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
    pollingThread.setDaemon(true);
    pollingThread.start();
  }
}