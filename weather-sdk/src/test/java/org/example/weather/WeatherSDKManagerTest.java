package org.example.weather;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WeatherSDKManagerTest {

  @Test
  void shouldCreateAndRetrieveSdkInstance() {
    WeatherSDK sdk1 = WeatherSDKManager.create("key1", false);
    WeatherSDK sdk2 = WeatherSDKManager.get("key1");

    assertSame(sdk1, sdk2);
  }

  @Test
  void shouldThrowExceptionWhenCreatingDuplicateKey() {
    WeatherSDKManager.create("dupKey", false);
    assertThrows(IllegalStateException.class, () -> WeatherSDKManager.create("dupKey", true));
  }

  @Test
  void shouldRemoveSdkInstance() {
    WeatherSDKManager.create("keyToRemove", false);
    WeatherSDKManager.remove("keyToRemove");

    assertNull(WeatherSDKManager.get("keyToRemove"));
  }
}