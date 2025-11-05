package org.example.weather;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WeatherSDKTest {

  @Mock
  private WeatherClient client;

  private WeatherSDK sdk;

  @BeforeEach
  void setUp() {
    sdk = new WeatherSDK("fake-key", false);

    try {
      var field = WeatherSDK.class.getDeclaredField("client");
      field.setAccessible(true);
      field.set(sdk, client);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private WeatherData createWeatherData(String city, long createdAtMillis) {
    return new WeatherData(
        new WeatherData.Weather("Clear", "Sunny"),
        new WeatherData.Temperature(20.0, 19.0),
        10000,
        new WeatherData.Wind(3.5),
        System.currentTimeMillis() / 1000,
        new WeatherData.Sys(1719999999L, 1720009999L),
        10800,
        city,
        createdAtMillis
    );
  }

  @Test
  void shouldThrowExceptionWhenCityIsNull() {
    assertThrows(IllegalArgumentException.class, () -> sdk.getWeather(null));
  }

  @Test
  void shouldThrowExceptionWhenCityIsEmpty() {
    assertThrows(IllegalArgumentException.class, () -> sdk.getWeather("   "));
  }

  @Test
  void shouldFetchWeatherFromApiWhenNotInCache() throws Exception {
    WeatherData data = createWeatherData("Minsk", System.currentTimeMillis());
    when(client.fetchWeather("Minsk")).thenReturn(data);

    WeatherData result = sdk.getWeather("Minsk");

    assertEquals("Minsk", result.name());
    verify(client, times(1)).fetchWeather("Minsk");
  }

  @Test
  void shouldReturnCachedWeatherIfNotExpired() throws Exception {
    WeatherData cachedData = createWeatherData("London", System.currentTimeMillis());

    try {
      var cacheField = WeatherSDK.class.getDeclaredField("cache");
      cacheField.setAccessible(true);
      WeatherCache cache = (WeatherCache) cacheField.get(sdk);
      cache.put("London", cachedData);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    WeatherData result = sdk.getWeather("London");
    assertEquals(cachedData, result);
    verify(client, never()).fetchWeather(anyString());
  }

  @Test
  void shouldUpdateCacheIfDataExpired() throws Exception {
    long expiredTime = System.currentTimeMillis() - 3600_000;
    WeatherData expired = createWeatherData("Berlin", expiredTime);
    WeatherData fresh = createWeatherData("Berlin", System.currentTimeMillis());

    try {
      var cacheField = WeatherSDK.class.getDeclaredField("cache");
      cacheField.setAccessible(true);
      WeatherCache cache = (WeatherCache) cacheField.get(sdk);
      cache.put("Berlin", expired);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    when(client.fetchWeather("Berlin")).thenReturn(fresh);

    WeatherData result = sdk.getWeather("Berlin");
    assertEquals(fresh, result);
    verify(client, times(1)).fetchWeather("Berlin");
  }

  @Test
  void shouldHandleWeatherExceptionFromClient() throws Exception {
    when(client.fetchWeather("Oslo")).thenThrow(new WeatherException("API down"));

    WeatherException ex = assertThrows(WeatherException.class, () -> sdk.getWeather("Oslo"));
    assertTrue(ex.getMessage().contains("API down"));
  }
}