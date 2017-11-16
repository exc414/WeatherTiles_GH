# Weather Tiles

[![API](https://img.shields.io/badge/API-16%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=16)
[![1.0.0](https://img.shields.io/badge/version-1.0.0-blue.svg)](http://git.bp.lan:3000/exc414/WeatherTiles)



-----


<a href="https://play.google.com/store/apps/details?id=io.bluephoenix.weathertiles.production.release&hl=en&utm_source=global_co&utm_medium=prtnr&utm_content=Mar2515&utm_campaign=PartBadge&pcampaignid=MKT-AC-global-none-all-co-pr-py-PartBadges-Oct1515-1"><img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/apps/en-play-badge-border.png" width="300" /></a>



-----

<br>

### Description

* Display weather for information for different cities.
* Up to 40 at the same time in a grid style.
* Weather and day/night information will automatically update itself through the day.
* Change between Celsius and Fahrenheit.
* Sort based on temperature, day or night and alphabetically order.
* Drag and drop to move sort tiles in any custom order.
* Swipe to delete any tile.


<br>

### Prerequisites

> *Requires Android Studio 3.0+*

This application requires API keys from Open Weather Map and Weatherbit (both at the time of writing are free). Inside gradle.properties add the following code: 

```
OPEN_WEATHER_API_KEY="YOUR-API-KEY-HERE"
WEATHER_BIT_API_KEY="YOUR-API-KEY-HERE"
```

* [OpenWeatherMap](https://openweathermap.org/api) - Open Weather Map API Documentation
* [Weatherbit](https://www.weatherbit.io/api) - Weather API Documentation

<br>

-----

<br>

### App Images

![Tiles](app_img/tiles.png?raw=true "Tiles"  width="300") ![Tiles](app_img/sort.png?raw=true "Tiles"  width="300")
![Tiles](app_img/search.png?raw=true "Tiles"  width="300") ![Tiles](app_img/settings.png?raw=true "Tiles"  width="300")

<br>

-----

<br>

## Libs Used

* [Calligraphy](https://github.com/chrisjenx/Calligraphy) - Custom Fonts
* [Stream Support](https://github.com/streamsupport/streamsupport) - Backport of Java 8 java.util.stream API
* [Retrofit](https://github.com/square/retrofit) - Type-safe HTTP client
* [Butterknife](https://github.com/JakeWharton/butterknife) - Bind views and callbacks to fields and methods
* [Event Bus](https://github.com/greenrobot/EventBus) - Event bus for Android
* [Material Dialogs](https://github.com/afollestad/material-dialogs) - A dialogs API
* [Leak Canary](https://github.com/square/leakcanary) - A memory leak detection library
* [Stetho](https://github.com/facebook/stetho) - Stetho is a debug bridge for Android applications
* [Realm](https://github.com/realm/realm-java) - Realm is a mobile database

<br>

## Acknowledgments

All the icons used in the application come from the following github project by Erick Flowers: [Icons](http://erikflowers.github.io/weather-icons/)

Used this library as reference when implementing the weather icons in my custom view: [Weather View](https://github.com/pwittchen/WeatherIconView) 

Used this library as reference for custom animations: [Animations](https://github.com/81813780/AVLoadingIndicatorView)

