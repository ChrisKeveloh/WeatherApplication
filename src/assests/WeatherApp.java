package assests;

import java.net.HttpURLConnection;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import javax.swing.JDesktopPane;

public class WeatherApp{



    //fetch weather data for given location


    public static JSONObject getWeatherData(String locationName)
    {
        // get location coordinates using the geolocation API

        JSONArray locationData = getLocationData(locationName);

        // extrack latitude and longitude data
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude"); // 

        String urlString = "https://api.open-meteo.com/v1/forecast?" +
        "latitude=" + latitude + "&longitude=" + longitude +
        "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&temperature_unit=fahrenheit&wind_speed_unit=mph";


        try{
            // call and get response
            HttpURLConnection conn = fetchApiResponse(urlString);

            // check for response status
            // 200 means that connection was a success
            if(conn.getResponseCode() != 200)
            {
                System.out.println("Error: Could not connect to API");
                return null;
            }

            //store resulting json data
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while(scanner.hasNext())
            {
                //read and store data into the string builder

                resultJson.append(scanner.nextLine());
            }

            scanner.close();

            conn.disconnect();


            //parse through our data

            JSONParser parser = new JSONParser();
            JSONObject resultJsonObject = (JSONObject) parser.parse(String.valueOf(resultJson));

            //retrieve hourly data
            JSONObject hourly = (JSONObject) resultJsonObject.get("hourly");
            JSONArray time = (JSONArray) hourly.get("time");

            int index = findIndexOfCurrentTime(time);

            // get temperature
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            // get weather code
            JSONArray weathercode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weathercode.get(index));

            // get humidity
            JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            // get windspeed
            JSONArray windspeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windspeed = (double) windspeedData.get(index);

            // build the weather json data object that we are going to access in our frontend

            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature",temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity",humidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;





        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public static JSONArray getLocationData(String locationName)
    {
        locationName = locationName.replaceAll(" ", "+");


        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" + 
        locationName + "&count=10&language=en&format=json";

        try{
            //call api and get a response
            HttpURLConnection conn = fetchApiResponse(urlString);

            // check response status

            if(conn.getResponseCode() != 200)
            {
                System.out.println("Error: Could Not connect to API");
                return null;
            }

            
            else{

               StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());

                while(scanner.hasNext())
                {
                    resultJson.append(scanner.nextLine());
                }

                scanner.close();


                conn.disconnect();

                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                return locationData;

            }

        }catch(Exception e)
        {
            e.printStackTrace();
        }

        System.out.println("Type beat");
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString)

    {
        try{


            @SuppressWarnings("deprecation")
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            conn.connect();

            return conn;
        }catch(IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timeList)
    {
        String currentTime = getCurrentTime();
        
        // iterate through the time list to find the one that matches our current time
        for(int i = 0; i < timeList.size(); i++)
        {
            String time = (String) timeList.get(i);
            if(time.equalsIgnoreCase(currentTime)){
                //return the index
                return i;

            }
    

        }
        return 0;
    }

    public static String getCurrentTime()
    {
        //get current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();
        
        // format date to be 2023-09-02T00:00 format (this is how it is read in API)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        //format and print the currewnt date and time
        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;


        
    }

    // convert the weather code to something more readable
    private static String convertWeatherCode(long weathercode)
    {
        String weatherCondition = "";

        if(weathercode == 0L)
        {
            weatherCondition = "Clear";

        }
        else if(weathercode <= 3L && weathercode > 0)
        {
            weatherCondition = "Cloudy";
        }
        else if((weathercode >= 51L && weathercode <= 67L)  || (weathercode >= 80L && weathercode <= 99L))
        {
            weatherCondition = "Rain";
        }
        else if(weathercode >= 71L && weathercode <= 77L)
        {
            weatherCondition = "Snow";
        }

        return weatherCondition;


    }
}