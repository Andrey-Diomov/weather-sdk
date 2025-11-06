package com.kameleoon.sdk;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WeatherSDKManager {
  private static final Map<String, WeatherSDK> instances = new ConcurrentHashMap<>();

  public static WeatherSDK create(String apiKey, boolean pollingMode) {
    if (instances.containsKey(apiKey)) {
      throw new IllegalStateException("SDK with this API key already exists");
    }
    WeatherSDK sdk = new WeatherSDK(apiKey, pollingMode);
    instances.put(apiKey, sdk);
    return sdk;
  }

  public static WeatherSDK get(String apiKey) {
    return instances.get(apiKey);
  }

  public static void remove(String apiKey) {
    instances.remove(apiKey);
  }
}