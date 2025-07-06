package firesaftyproject;

import javax.swing.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import firesaftyproject.model.TabNetPredictor;

public class ApplyNOCPanel extends JPanel {
    private JTextField[] textFields;
    private String username;
    private JComboBox<String> buildingTypeComboBox;
    private Connection conn;

    public ApplyNOCPanel(String username, Connection conn) {
        this.username = username;
        this.conn = conn;
        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        // Create and add components
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        
        // Building Type ComboBox
        String[] buildingTypes = {"Residential", "Commercial", "Industrial", "Educational", "Hospital"};
        buildingTypeComboBox = new JComboBox<>(buildingTypes);
        formPanel.add(new JLabel("Building Type:"));
        formPanel.add(buildingTypeComboBox);
        
        // Create text fields
        String[] labels = {
            "Number of Floors:", "Number of Rooms:", "Building Capacity:",
            "Total Area (sq ft):", "Number of Extinguishers:", "Number of Sprinklers:",
            "Number of Smoke Detectors:", "Number of Fire Alarms:"
        };
        
        textFields = new JTextField[labels.length];
        for (int i = 0; i < labels.length; i++) {
            formPanel.add(new JLabel(labels[i]));
            textFields[i] = new JTextField(20);
            formPanel.add(textFields[i]);
        }
        
        // Submit button
        JButton submitButton = new JButton("Submit Application");
        submitButton.addActionListener(e -> submitNOCApplication());
        formPanel.add(submitButton);
        
        add(formPanel);
    }

    private void submitNOCApplication() {
        try {
            // Validate all fields
            for (JTextField field : textFields) {
                if (field.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this.getParent(), 
                        "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Validate numeric fields
            try {
                int floors = Integer.parseInt(textFields[0].getText());
                int rooms = Integer.parseInt(textFields[1].getText());
                int capacity = Integer.parseInt(textFields[2].getText());
                int area = Integer.parseInt(textFields[3].getText());
                int extinguishers = Integer.parseInt(textFields[4].getText());
                int sprinklers = Integer.parseInt(textFields[5].getText());
                int smokeDetectors = Integer.parseInt(textFields[6].getText());
                int fireAlarms = Integer.parseInt(textFields[7].getText());

                // Prepare input data for TabNet model
                Map<String, Object> inputData = new HashMap<>();
                inputData.put("building_type", buildingTypeComboBox.getSelectedItem().toString().toLowerCase());
                inputData.put("floors", floors);
                inputData.put("rooms", rooms);
                inputData.put("capacity", capacity);
                inputData.put("area", area);
                inputData.put("extinguishers", extinguishers);
                inputData.put("sprinklers", sprinklers);
                inputData.put("smoke_detectors", smokeDetectors);
                inputData.put("fire_alarms", fireAlarms);

                // Get prediction from TabNet model
                Map<String, Object> prediction = TabNetPredictor.predict(inputData);
                boolean isSafe = (boolean) prediction.get("is_safe");
                double confidence = (double) prediction.get("confidence") * 100;

                if (!isSafe) {
                    JOptionPane.showMessageDialog(this.getParent(), 
                        String.format("Application rejected. The building does not meet fire safety requirements.\nConfidence: %.2f%%", confidence),
                        "Safety Check Failed",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // If safe, proceed with database insertion
                String sql = "INSERT INTO noc (username, building_type, no_of_floors, no_of_rooms, capacity, area, no_of_extinguishers, no_of_sprinklers, no_of_smoke_detectors, no_of_fire_alarms, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, username);
                    pstmt.setString(2, buildingTypeComboBox.getSelectedItem().toString());
                    pstmt.setInt(3, floors);
                    pstmt.setInt(4, rooms);
                    pstmt.setInt(5, capacity);
                    pstmt.setInt(6, area);
                    pstmt.setInt(7, extinguishers);
                    pstmt.setInt(8, sprinklers);
                    pstmt.setInt(9, smokeDetectors);
                    pstmt.setInt(10, fireAlarms);
                    pstmt.setString(11, "Pending");
                    
                    pstmt.executeUpdate();
                    
                    JOptionPane.showMessageDialog(this.getParent(), 
                        String.format("Application submitted successfully!\nSafety Check Passed (Confidence: %.2f%%)", confidence),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Clear all fields
                    for (JTextField field : textFields) {
                        field.setText("");
                    }
                    buildingTypeComboBox.setSelectedIndex(0);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this.getParent(), 
                    "Please enter valid numbers for all numeric fields", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this.getParent(), 
                "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
} 