# KameleoonWeatherSDK

WeatherSDK is a Java SDK for fetching current weather data from the OpenWeatherMap API.
It supports two modes of operation:

* **On-demand** — updates weather data only when requested by the user.
* **Polling** — automatically refreshes weather data for all stored locations to provide zero-latency responses.

The SDK is simple, thread-safe, and easy to integrate into any Java application.

## Requirements

- Java 21 or higher
- Maven/Gradle build system

---

## Table of Contents

* [Installation](#installation)
* [Configuration](#configuration)
* [Usage](#usage)
* [Examples](#examples)
* [Publishing](#publishing)
* [WeatherData Structure](#weatherdata-structure)
* [Notes](#notes)

---

## Installation

### 1. Locally via Gradle (for SDK developers / testing)

Build and publish the SDK to your local Maven repository:

```bash
./gradlew clean build publishToMavenLocal
```

Then, in your project, add:

```gradle
repositories {
    mavenLocal()  
    mavenCentral()
}

dependencies {
    implementation 'com.example.weather:weather-sdk:1.0.0'
}
```

This allows testing the SDK locally without requiring GitHub credentials.

---

### 2. Using GitHub Packages (for end-users)

If you want to share the SDK via GitHub Packages:

1. **Create a GitHub Personal Access Token (PAT):**

    * `read:packages` — needed to download the SDK
    * `write:packages` — needed if publishing new versions

2. **Set environment variables** (do **not** commit your token):

```bash
export USERNAME=YourGitHubUsername
export TOKEN=ghp_YourGeneratedToken
```

3. **Add the GitHub Packages repository in your Gradle project**:

```gradle
repositories {
    mavenLocal() // optional: check local first
    maven {
        url = uri("https://maven.pkg.github.com/Andrey-Diomov/weather-sdk")
        credentials {
            username = System.getenv("USERNAME")
            password = System.getenv("TOKEN")
        }
    }
    mavenCentral()
}

dependencies {
    implementation 'com.example.weather:weather-sdk:1.0.0'
}
```

> ⚠️ Security note: Never commit your token to a public repository. Always use environment variables or `gradle.properties`.

---

## Configuration

Use `WeatherSDKManager` to create, retrieve, or remove SDK instances:

```java
String apiKey = "YOUR_API_KEY";

// Create a new SDK instance in on-demand mode
WeatherSDK sdk = WeatherSDKManager.create(apiKey, false);

// Create a new SDK instance in polling mode
WeatherSDK sdkPolling = WeatherSDKManager.create(apiKey, true);

// Retrieve an existing SDK instance
WeatherSDK existing = WeatherSDKManager.get(apiKey);

// Remove an SDK instance
WeatherSDKManager.remove(apiKey);
```

> **Note:** Creating two SDK instances with the same API key will throw an `IllegalStateException`.

---

## Usage

### Fetch weather for a single city

```java
try {
    WeatherData berlinWeather = sdk.getWeather("Berlin");
    System.out.println("Temperature: " + berlinWeather.temperature().temp());
    System.out.println("Condition: " + berlinWeather.weather().main());
} catch (WeatherException e) {
    e.printStackTrace();
}
```

### Check if cached data is still valid

```java
if (!berlinWeather.isExpired()) {
    System.out.println("Cached data is fresh, using cache.");
}
```

---

## Examples: Multiple cities

```java
String[] cities = {"Berlin", "Moscow", "Paris"};

for (String city : cities) {
    try {
        WeatherData data = sdk.getWeather(city);
        System.out.printf("%s: %.1f°C, %s%n", 
            data.name(), 
            data.temperature().temp(), 
            data.weather().main()
        );
    } catch (WeatherException e) {
        System.err.println("Failed to fetch weather for " + city);
    }
}
```

---

## Publishing

### Locally

```bash
./publish.sh
```

This publishes the SDK to your local Maven repository (`mavenLocal()`).

### GitHub Packages

1. Create a GitHub Personal Access Token with `write:packages` scope.
2. Set environment variables:

```bash
export USERNAME=YourGitHubUsername
export TOKEN=ghp_YourGeneratedToken
```

3. Publish using Gradle:

```bash
./gradlew publish
```

> ⚠️ If a version already exists, you may encounter a `409 Conflict` error. Increase the version number in `build.gradle`.

---

## WeatherData Structure

`WeatherData` is the main object returned by the SDK:

| Field         | Type          | Description                           |
| ------------- | ------------- | ------------------------------------- |
| `weather`     | `Weather`     | Weather condition (main, description) |
| `temperature` | `Temperature` | Temperature and feels_like            |
| `visibility`  | `int`         | Visibility in meters                  |
| `wind`        | `Wind`        | Wind speed                            |
| `datetime`    | `long`        | Measurement timestamp                 |
| `sys`         | `Sys`         | Sunrise and sunset                    |
| `timezone`    | `int`         | Timezone offset in seconds            |
| `name`        | `String`      | City name                             |
| `createdAt`   | `long`        | Cache timestamp                       |




```java
public record Weather(String main, String description) {}
public record Temperature(double temp, double feels_like) {}
public record Wind(double speed) {}
public record Sys(long sunrise, long sunset) {}
```


---

### Example API Response

Example of the JSON structure returned by the SDK:

```json
{
  "weather": {
    "main": "Clouds",
    "description": "scattered clouds"
  },
  "temperature": {
    "temp": 269.6,
    "feels_like": 267.57
  },
  "visibility": 10000,
  "wind": {
    "speed": 1.38
  },
  "datetime": 1675744800,
  "sys": {
    "sunrise": 1675751262,
    "sunset": 1675787560
  },
  "timezone": 3600,
  "name": "Zocca"
}
```
---

## Notes

* The SDK supports **on-demand** and **polling** modes.
* Cached weather data expires after 10 minutes.
* `WeatherSDKManager` ensures only one SDK instance exists per API key.
* `mavenLocal()` is optional for users — Gradle caches downloaded SDKs automatically.
* Publishing to GitHub Packages or another Maven repository is optional but recommended for sharing.

---

This README provides installation instructions, usage examples, and guidance for managing SDK instances through `WeatherSDKManager`.
