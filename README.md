# Overview

The main task for this assignment is to develop a weather application, which fetches the weather details from OpenWeatherMap API. Edit text input for the user to enter city name, temperature, clouds, humidity, minimum temperature, maximum temperature, weather, and other details mentioned in the assignment specification document are the must have for this application. The default city for this application is set to Halifax. Hence, when the application launches, it will display Halifax’s weather.

# Tools and Technologies required to run this application

**Android Studio Version :** 3.3.2

**Minimum SDK Version :** 23

**Gradle Version** : 3.3.2

**API Used  :** Open Weather Map API available at

[https://openweathermap.org/current](https://openweathermap.org/current)

**API Request Sample URL  :** [http://api.openweathermap.org/data/2.5/weather?q=halifax&APPID=cf225fe20ad01f6b21279d6ce21eebf4](http://api.openweathermap.org/data/2.5/weather?q=halifax&APPID=cf225fe20ad01f6b21279d6ce21eebf4)

Where parameter **q is the city name** and **APPID is the API key** for the request.

**Dependencies:** There are no extra dependencies for this project. I am using AsyncTask for the HTTP GET request. More information about AsyncTask can be found at [https://developer.android.com/reference/android/os/AsyncTask](https://developer.android.com/reference/android/os/AsyncTask)

The complete report of this application is attached in the submission folder.

Apart from this, tools and technologies information, no other special notes/information is required for the marker to run the application.