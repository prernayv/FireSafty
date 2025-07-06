package firesaftyproject.model;

import org.json.JSONObject;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class TabNetPredictor {
    private static final String MODEL_DIR = "src/firesaftyproject/model/tabnet firesafety/";
    private static final String PYTHON_SCRIPT_PATH = MODEL_DIR + "predict_service.py";
    private static final String PYTHON_EXECUTABLE = "python";

    public static Map<String, Object> predict(Map<String, Object> inputData) {
        try {
            // Convert input data to JSON
            JSONObject jsonInput = new JSONObject(inputData);
            
            // Create process builder
            ProcessBuilder pb = new ProcessBuilder(
                PYTHON_EXECUTABLE,
                PYTHON_SCRIPT_PATH
            );
            
            // Set working directory to project root
            pb.directory(new File(System.getProperty("user.dir")));
            
            // Start the process
            Process process = pb.start();
            
            // Write input to process
            try (OutputStream os = process.getOutputStream();
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(os))) {
                writer.println(jsonInput.toString());
                writer.flush();
            }
            
            // Read output from process
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }
            }
            
            // Wait for process to complete
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Python script exited with code " + exitCode);
            }
            
            // Parse the JSON response
            JSONObject jsonResponse = new JSONObject(output.toString());
            Map<String, Object> result = new HashMap<>();
            
            if (jsonResponse.has("error")) {
                throw new RuntimeException("Prediction error: " + jsonResponse.getString("error"));
            }
            
            result.put("status", jsonResponse.getString("status"));
            result.put("is_safe", jsonResponse.getBoolean("is_safe"));
            result.put("confidence", jsonResponse.getDouble("confidence"));
            
            return result;
            
        } catch (Exception e) {
            throw new RuntimeException("Error during prediction: " + e.getMessage(), e);
        }
    }
} 