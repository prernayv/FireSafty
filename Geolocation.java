package firesaftyproject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import org.json.JSONObject;

public class Geolocation {
    private double latitude;
    private double longitude;
    
    public Geolocation() {
        try {
            // Get location from IP address
            URL url = new URL("http://ip-api.com/json");
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            // Parse JSON response
            JSONObject json = new JSONObject(response.toString());
            latitude = json.getDouble("lat");
            longitude = json.getDouble("lon");
            
        } catch (Exception e) {
            // Fallback to device location if available
            try {
                // Try to get location from system
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("win")) {
                    // Windows location service
                    Process process = Runtime.getRuntime().exec("powershell -command \"Get-Location\"");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String location = reader.readLine();
                    if (location != null) {
                        String[] coords = location.split(",");
                        latitude = Double.parseDouble(coords[0]);
                        longitude = Double.parseDouble(coords[1]);
                    }
                }
            } catch (Exception ex) {
                // If all methods fail, use default coordinates
                latitude = 0.0;
                longitude = 0.0;
            }
        }
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
} 