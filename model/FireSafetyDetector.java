package firesaftyproject.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import org.json.JSONObject;
import java.nio.file.Paths;

public class FireSafetyDetector {
    private static final String MODEL_DIR = Paths.get(System.getProperty("user.dir"), 
        "src", "firesaftyproject", "model", "main project", "main project").toString();
    
    public static JSONObject analyzeImage(String imagePath) throws Exception {
        try {
            // Get Python executable path - try both 'python' and 'python3'
            String pythonCommand = "python";
            try {
                Process pythonTest = Runtime.getRuntime().exec("python --version");
                if (pythonTest.waitFor() != 0) {
                    pythonCommand = "python3";
                }
            } catch (Exception e) {
                pythonCommand = "python3";
            }

            // Build the command to run the Python script
            String scriptPath = Paths.get(MODEL_DIR, "predict_service.py").toString();
            
            // Debug information
            System.out.println("Working Directory: " + MODEL_DIR);
            System.out.println("Script Path: " + scriptPath);
            System.out.println("Image Path: " + imagePath);
            
            // Verify files exist
            File scriptFile = new File(scriptPath);
            File modelFile = new File(Paths.get(MODEL_DIR, "yolov8n.pt").toString());
            File imageFile = new File(imagePath);
            
            if (!scriptFile.exists()) {
                throw new Exception("Python script not found at: " + scriptPath);
            }
            if (!modelFile.exists()) {
                throw new Exception("Model file not found at: " + modelFile.getAbsolutePath());
            }
            if (!imageFile.exists()) {
                throw new Exception("Image file not found at: " + imagePath);
            }

            ProcessBuilder processBuilder = new ProcessBuilder(
                pythonCommand,
                scriptPath,
                imagePath
            );
            
            // Set the working directory to where the model files are located
            processBuilder.directory(new File(MODEL_DIR));
            
            // Redirect error stream to output stream
            processBuilder.redirectErrorStream(true);
            
            // Start the process
            Process process = processBuilder.start();
            
            // Read the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String jsonLine = null;
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Python output: " + line);
                // Look for the line that starts with a JSON object
                if (line.trim().startsWith("{")) {
                    jsonLine = line.trim();
                }
                output.append(line).append("\n");
            }
            
            // Wait for the process to complete
            int exitCode = process.waitFor();
            
            if (exitCode == 0 && jsonLine != null) {
                try {
                    return new JSONObject(jsonLine);
                } catch (Exception e) {
                    throw new Exception("Failed to parse Python output as JSON: " + jsonLine + "\nFull output: " + output.toString());
                }
            } else {
                throw new Exception("Model prediction failed with exit code: " + exitCode + "\nOutput: " + output.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error analyzing image: " + e.getMessage());
        }
    }
} 