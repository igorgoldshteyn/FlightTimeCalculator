package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        Calculator calculator = new Calculator();

        String json ="";

        InputStream is = Main.class.getClassLoader().getResourceAsStream("tickets.json");
        InputStreamReader isr = new InputStreamReader(is, "UTF-8");
        BufferedReader file = new BufferedReader(isr);

        String line = null;
        while ((line = file.readLine()) != null)
            json += line;

        file.close(); isr.close(); is.close();

        long[] JsonToFlightTimesArrayInMilliseconds = calculator.convertJsonToFlightTimesArrayInMilliseconds(json);

        System.out.println(calculator.calculateAverageFlightTime(JsonToFlightTimesArrayInMilliseconds));
        System.out.println(calculator.calculate90thPercentile(JsonToFlightTimesArrayInMilliseconds));
    }
}

class Calculator {

    public long[] convertJsonToFlightTimesArrayInMilliseconds(String json) throws ParseException {
        JSONObject obj = new JSONObject(json);
        JSONArray tickets = obj.getJSONArray("tickets");

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        long[] flightTimes = new long[tickets.length()];

        for (int x = 0; x < tickets.length(); x++) {

            Date departureTime = format.parse((tickets.getJSONObject(x)).get("departure_time").toString());
            Date arrivalTime = format.parse((tickets.getJSONObject(x)).get("arrival_time").toString());

            flightTimes[x] = arrivalTime.getTime() - departureTime.getTime();
        }
        return flightTimes;
    }

    public String calculateAverageFlightTime(long[] flightTimesArray) {
        long sumOfFlightTimes = 0;
        for (long time : flightTimesArray) {
            sumOfFlightTimes += time;
        }

        long averageTimeInMinutes = TimeUnit.MILLISECONDS.toMinutes(sumOfFlightTimes / flightTimesArray.length);

        long hours = averageTimeInMinutes / 60;
        long minutes = averageTimeInMinutes % 60;

        return hours + ":" + minutes;
    }

    public String calculate90thPercentile(long[] flightTimesArray) {

        Arrays.sort(flightTimesArray);

        int index = (int) Math.ceil(0.9 * flightTimesArray.length);
        long the90thPercentileInMilliseconds = TimeUnit.MILLISECONDS.toMinutes(flightTimesArray[index - 1]);

        long hours = the90thPercentileInMilliseconds / 60;
        long minutes = the90thPercentileInMilliseconds % 60;

        return hours + ":" + minutes;
    }
}



