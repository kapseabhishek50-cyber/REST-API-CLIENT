import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WeatherApp {

    // API ENDPOINT FOR LONDON (LAT: 51.5, LON: -0.12) FETCHING CURRENT WEATHER
    private static final String API_URL = "https://api.open-meteo.com";

    public static void main(String[] args) {
        System.out.println("CONNECTING TO PUBLIC WEATHER API...");

        try {
            // 1. INITIALIZE THE HTTP CLIENT
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            // 2. CONSTRUCT THE GET REQUEST
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .GET()
                    .build();

            // 3. SEND THE REQUEST AND GET THE RESPONSE BODY AS STRING
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                formatAndDisplay(response.body());
            } else {
                System.out.println("SERVER RETURNED ERROR CODE: " + response.statusCode());
            }

        } catch (Exception e) {
            System.err.println("CONNECTION ERROR: " + e.getMessage());
        }
    }

    /**
     * EXTRACTS AND DISPLAYS DATA USING REGEX (TO AVOID EXTERNAL JSON LIBRARIES)
     */
    private static void formatAndDisplay(String json) {
        System.out.println("\n====================================");
        System.out.println("      CURRENT WEATHER: LONDON       ");
        System.out.println("====================================");

        // EXTRACT VALUES USING REGULAR EXPRESSIONS
        String temp = getValue(json, "temperature_2m");
        String wind = getValue(json, "wind_speed_10m");
        String humidity = getValue(json, "relative_humidity_2m");

        System.out.printf("TEMPERATURE : %s°C%n", temp);
        System.out.printf("WIND SPEED  : %s km/h%n", wind);
        System.out.printf("HUMIDITY    : %s%%%n", humidity);
        System.out.println("====================================");
        System.out.println("DATA PROVIDED BY OPEN-METEO.COM (2026)");
    }

    private static String getValue(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\":\\s*([^,}]+)");
        Matcher matcher = pattern.matcher(json);
        return matcher.find() ? matcher.group(1) : "N/A";
    }
}
