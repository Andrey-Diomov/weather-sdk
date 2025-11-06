package com.kameleoon.sdk;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.util.Timeout;

public class WeatherClient {

  private static final String FIELD_COD = "cod";
  private static final String FIELD_MESSAGE = "message";
  private static final int HTTP_OK = 200;
  private static final int HTTP_ERROR_THRESHOLD = 400;
  private static final String BASE_URL =
      "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric";

  private final String apiKey;
  private final CloseableHttpClient httpClient;
  private final Gson gson = new Gson();

  public WeatherClient(String apiKey) {
    this.apiKey = apiKey;

    RequestConfig config = RequestConfig.custom()
        .setResponseTimeout(Timeout.ofSeconds(5))
        .build();

    this.httpClient = HttpClients.custom()
        .setDefaultRequestConfig(config)
        .build();
  }

  public WeatherData fetchWeather(String city) throws WeatherException {
    if (city == null || city.isBlank()) {
      throw new IllegalArgumentException("City name cannot be null or empty");
    }

    String url = String.format(BASE_URL, city.trim(), apiKey);
    HttpGet request = new HttpGet(url);

    try {
      String json = httpClient.execute(request, response -> {
        int status = response.getCode();
        if (status >= HTTP_ERROR_THRESHOLD) {
          throw new IOException("HTTP error: " + status);
        }
        return EntityUtils.toString(response.getEntity());
      });

      JsonObject root = gson.fromJson(json, JsonObject.class);

      if (root.has(FIELD_COD) && root.get(FIELD_COD).getAsInt() != HTTP_OK) {
        throw new WeatherException("API error: " + root.get(FIELD_MESSAGE).getAsString());
      }

      return WeatherData.fromJson(root);

    } catch (UnknownHostException e) {
      throw new WeatherException("Unable to resolve host: " + e.getMessage(), e);
    } catch (SocketTimeoutException e) {
      throw new WeatherException("Connection timed out: " + e.getMessage(), e);
    } catch (JsonParseException e) {
      throw new WeatherException("Invalid JSON received from API", e);
    } catch (IOException e) {
      throw new WeatherException("Network or HTTP error: " + e.getMessage(), e);
    } catch (Exception e) {
      throw new WeatherException("Unexpected error: " + e.getMessage(), e);
    }
  }
}