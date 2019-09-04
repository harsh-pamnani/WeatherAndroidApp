/**
 * This class deals with fetching current weather from openweathermap API and display it on the hone screen.
 * The class uses helper class AsyncTask to perform the GET request in the background.
 * Main layout background is changed according to the weather description (eg. for cloudy weather clouds are displayed in background)
 *
 * @author   Harsh Pamnani - B00802614
 * @version  1.0
 * Created on - 18 March 2019
 */

package com.example.harsh.pamnani.myweatherapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    // Setting up constants used in the class
    private static final String URL_REQ_START = "http://api.openweathermap.org/data/2.5/weather?q=";
    private static final String URL_REQ_END = "&APPID=";
    private static final String DEFAULT_CITY = "Halifax";
    private static final String LOG_TAG = "Harsh Weather TAG";
    private static final double KEL_TO_C = 273.5;

    // Declaring variables for all the components in the UI
    AutoCompleteTextView cityEditText;
    Button getWeatherButton;
    TextView tempratureView;
    TextView cityView;
    TextView minTempView;
    TextView maxTempView;
    TextView weatherMainView;
    TextView weatherDescriptionView;
    TextView humidityView;
    TextView cloudsAllView;
    String cityName;
    String apiKey;
    LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting Default city as "Halifax" to be displayed on application launch.
        cityName = DEFAULT_CITY;
        apiKey = getResources().getString(R.string.api_key);

        // Checking whether internet connectivity is present or not
        if(isNetworkAvailable()) {
            // Displaying default city "Halifax" information on application launch
            new getCityWeather().execute(URL_REQ_START + cityName + URL_REQ_END + apiKey);
        } else {
            // If Internet (network connectivity is not there, it will be displayed to used)
            Toast.makeText(MainActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }

        // Finding all the components from UI
        cityEditText = (AutoCompleteTextView) findViewById(R.id.cityEditText);
        getWeatherButton = (Button) findViewById(R.id.submitButton);
        tempratureView = (TextView) findViewById(R.id.temprature);
        cityView = (TextView) findViewById(R.id.city);
        minTempView = (TextView) findViewById(R.id.minTemprature);
        maxTempView = (TextView) findViewById(R.id.maxTemprature);
        weatherMainView = (TextView) findViewById(R.id.weather_main);
        weatherDescriptionView = (TextView) findViewById(R.id.weather_description);
        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        humidityView = (TextView) findViewById(R.id.humidity);
        cloudsAllView = (TextView) findViewById(R.id.clouds_all);

        // Setting up city array for displaying dropdown when user starts typing city name
        String[] citiesArray = getResources().getStringArray(R.array.cities);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, R.layout.city_dropdown, citiesArray);
        cityEditText.setThreshold(1);
        cityEditText.setAdapter(adapter);

        // OnClickListener for get weather button
        getWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the keyboard when button pressed
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                // Check if network (internet) connectivity is available or not
                if(isNetworkAvailable()) {
                    cityName = cityEditText.getText().toString();

                    // Check city name entered by the user is not null and it contains only alphabets
                    if (cityName.equals("")) {
                        Toast.makeText(MainActivity.this, getString(R.string.toast_no_city), Toast.LENGTH_SHORT).show();
                    } else if (!isCityNameOnlyAlphabet(cityName)) {
                        Toast.makeText(MainActivity.this, getString(R.string.toast_invalid_city), Toast.LENGTH_SHORT).show();
                    } else {
                        // Make a connection request to weather map API
                        new getCityWeather().execute(URL_REQ_START + cityName + URL_REQ_END + apiKey);
                    }
                } else {
                    // Display no internet connection toast
                    Toast.makeText(MainActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * This method checks whether a city name contains only alphabets or not.
     *
     * @param cityName City name to be checked
     * @return True if city name contains only alphabets, false otherwise
     */
    private boolean isCityNameOnlyAlphabet(String cityName) {
        return cityName.matches("^[a-zA-Z ]*$");
    }

    /**
     * This method checks whether device is connected to internet or not.
     *
     * @return Method return true if connected, false otherwise
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * Creating helper class AsyncTask for performing activities in background
     */
    public class getCityWeather extends AsyncTask<String, String, String> {

        // Setting final variables for http GET method
        private static final String HTTP_METHOD = "GET";

        // Declaring variables needed in the class
        HttpURLConnection httpconnection = null;
        String weather_data="";
        URL url = null;

        /**
         * This method executes the HTTP requests provided in the input in background
         *
         * @param urls URLs to be executed
         * @return Returns response of the HTTP GET requests in string format
         */
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder weatherStringBuilder = new StringBuilder();
            try {
                // Taking first URL from the given urls
                url = new URL(urls[0]);

                // Writing urls[0] to Logcat for debug purposes
                Log.i(LOG_TAG, urls[0]);

                // Opening the http connection and setting the GET method
                httpconnection = (HttpURLConnection) url.openConnection();
                httpconnection.setRequestMethod(HTTP_METHOD);
                httpconnection.connect();
                BufferedReader data_reader = new BufferedReader(new InputStreamReader(httpconnection.getInputStream()));

                // Writing weather data to Logcat for debug purposes
                Log.i(LOG_TAG, weather_data);

                // Loop to iterate through the response and building string
                while (weather_data!=null)
                {
                    weather_data = data_reader.readLine();
                    weatherStringBuilder.append(weather_data);
                }

                //Streams must be flushed before exiting the program. This helps freeing up the resources faster.
                httpconnection.getInputStream().close();
                data_reader.close();
            } catch (Exception e) {
                // Writing the exception message to Logcat for debug purposes
                Log.i(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }
            return weatherStringBuilder.toString();
        }

        /**
         * This method is executed once the request is completed. It uses the result from doInBackground metho
         * We can set the text views of our application in this method.
         * Got help from https://www.tutorialspoint.com/android/android_json_parser.htm for JSON parsing
         *
         * @param result Response from the HTTP GET request to weather API
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                // Writing the result to Logcat for debug purposes
                Log.i(LOG_TAG, result);
                JSONObject responseFromAPI = new JSONObject(result);

                // Getting all the required objects from response
                JSONObject main = responseFromAPI.getJSONObject(getString(R.string.response_tag_main));
                int temp = convertTemperatureToC(main.getDouble(getString(R.string.response_tag_temp)));
                int minTemp = convertTemperatureToC(main.getDouble(getString(R.string.response_tag_temp_min)));
                int maxTemp = convertTemperatureToC(main.getDouble(getString(R.string.response_tag_temp_max)));
                int humidity = main.getInt(getString(R.string.response_tag_humidity));

                JSONObject clouds = responseFromAPI.getJSONObject(getString(R.string.response_tag_clouds));
                int clouds_all = clouds.getInt(getString(R.string.response_tag_all));

                JSONObject sysObject = responseFromAPI.getJSONObject(getString(R.string.response_tag_sys));
                String country = sysObject.getString(getString(R.string.response_tag_country));

                JSONArray weatherArray = responseFromAPI.getJSONArray(getString(R.string.response_tag_weather));
                JSONObject firstWeatherInfo = weatherArray.getJSONObject(0);
                String weatherMain = firstWeatherInfo.getString(getString(R.string.response_tag_main));
                String weatherDescription = firstWeatherInfo.getString(getString(R.string.response_tag_description));

                // Converting first letter to upper-case for all the strings
                cityName = convertFirstLetterToUpperCase(cityName);
                weatherMain = convertFirstLetterToUpperCase(weatherMain);
                weatherDescription = convertFirstLetterToUpperCase(weatherDescription);

                // Switch case to handle all the Background images change
                switch (weatherMain) {
                    case "Rain":
                    case "Drizzle":
                        mainLayout.setBackground(getDrawable(R.drawable.rainy_background_new));
                        break;
                    case "Clouds":
                    case "Cloudy":
                        mainLayout.setBackground(getDrawable(R.drawable.clouds_background));
                        break;
                    case "Smoke":
                        mainLayout.setBackground(getDrawable(R.drawable.smoke_background));
                        break;
                    case "Clear":
                        mainLayout.setBackground(getDrawable(R.drawable.clear_sky_background));
                        break;
                    case "Sunny":
                        mainLayout.setBackground(getDrawable(R.drawable.sunny_background));
                        break;
                    case "Storm":
                    case "Thunderstorm":
                        mainLayout.setBackground(getDrawable(R.drawable.storm_background));
                        break;
                    case "Mist":
                    case "Haze":
                        mainLayout.setBackground(getDrawable(R.drawable.mist_background));
                        break;
                    case "Snow":
                        mainLayout.setBackground(getDrawable(R.drawable.snow_background));
                        break;
                    default:
                        mainLayout.setBackground(getDrawable(R.drawable.home_screen_background));
                        break;
                }

                // Setting all the TextView output
                cityView.setText(cityName + ", " + country);
                tempratureView.setText(String.valueOf(temp) + getString(R.string.degree_celcius_symbol));
                minTempView.setText(getString(R.string.min_temp) + String.valueOf(minTemp) + getString(R.string.degree_celcius_symbol));
                maxTempView.setText(getString(R.string.max_temp) + String.valueOf(maxTemp) + getString(R.string.degree_celcius_symbol));
                weatherMainView.setText(weatherMain);
                weatherDescriptionView.setText(weatherDescription);
                humidityView.setText(getString(R.string.humidity) + String.valueOf(humidity) + getString(R.string.percentage_symbol));
                cloudsAllView.setText(getString(R.string.clouds) + String.valueOf(clouds_all) + getString(R.string.percentage_symbol));
            }
            catch (Exception e) {
                // Writing the exception to Logcat for debug purposes
                Log.i(LOG_TAG, e.getMessage());

                // If user enters invalid city, then displaying "Please enter valid city name!" toast
                Toast.makeText(MainActivity.this, getString(R.string.toast_invalid_city), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * This method converts the input kelvin temperature to celsius.
     *
     * @param temperature_in_kel Temperature in kelvin
     * @return Temperature in Celsius
     */
    private int convertTemperatureToC(double temperature_in_kel) {
        // 273.5 is subtracted from kelvin temperature
        double temp_in_c = temperature_in_kel - KEL_TO_C;

        // Rounding off the temperature to integer
        temp_in_c = Math.round(temp_in_c);
        return (int) temp_in_c;
    }

    /**
     * This method converts first letter of the input string to upper case and return the converted string.
     * This method is used for converting strings for city name, weather description, humidity, etc.
     *
     * @param inputString String for which the first letter needs to be converted to uppercase
     * @return String having first letter in upper case and rest all letters in lowercase
     */
    private String convertFirstLetterToUpperCase(String inputString) {
        // Converting the input string to lower cases
        inputString = inputString.toLowerCase();

        // Converting only the first character to uppercase
        String firstChar = inputString.substring(0, 1).toUpperCase();

        // Appending the first uppercase letter with the original city name
        inputString = firstChar + inputString.substring(1);

        // Return the converted string
        return inputString;
    }
}