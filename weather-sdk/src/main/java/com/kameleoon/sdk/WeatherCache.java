package com.kameleoon.sdk;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class WeatherCache {
  private final Map<String, WeatherData> cache = new LinkedHashMap<>();
  private final int maxSize;

  public WeatherCache(int maxSize) {
    this.maxSize = maxSize;
  }

  public Optional<WeatherData> get(String city) {
    return Optional.ofNullable(cache.get(city.toLowerCase()));
  }

  public void put(String city, WeatherData data) {
    if (cache.size() >= maxSize) {
      Iterator<String> it = cache.keySet().iterator();
      if (it.hasNext()) it.remove(); //remove oldest
    }
    cache.put(city.toLowerCase(), data);
  }

  public Set<String> getStoredCities() {
    return cache.keySet();
  }
}
