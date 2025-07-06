package firesaftyproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;
import org.json.JSONObject;
import org.json.JSONArray;
import firesaftyproject.model.FireSafetyDetector;
import firesaftyproject.Geolocation;

public class LoginPage extends JFrame implements ActionListener {
    JButton login, cancel, signup, emergencyButton;
    JTextField username;
    JPasswordField password;
    Choice logginin;
    Timer blinkTimer;

    LoginPage() {
        setTitle("Login Page");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocation(0, 0);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(249, 194, 56));

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        int centerX = (screenWidth - 1200) / 2;
        int startX = centerX + 150;
        int labelWidth = 200;
        int fieldWidth = 200;
        int height = 30;
        int gap = 50;
        int startY = (screenHeight - 600) / 2;

        JPanel panel = new JPanel();
        panel.setBounds(centerX, startY, 1200, 600);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(249, 194, 56), 4),
                "Login",
                javax.swing.border.TitledBorder.LEADING,
                javax.swing.border.TitledBorder.TOP,
                new Font("Tahoma", Font.BOLD, 20),
                new Color(249, 194, 56)
        ));
        panel.setBackground(Color.white);
        panel.setLayout(null);
        add(panel);

        JLabel lblusername = new JLabel("Username:");
        lblusername.setBounds(startX, 50, labelWidth, height);
        lblusername.setForeground(Color.GRAY);
        lblusername.setFont(new Font("Tahoma", Font.BOLD, 20));
        panel.add(lblusername);

        username = new JTextField();
        username.setBounds(startX + labelWidth + 20, 50, fieldWidth, height);
        username.setFont(new Font("Tahoma", Font.PLAIN, 16));
        panel.add(username);

        JLabel lblpassword = new JLabel("Password:");
        lblpassword.setBounds(startX, 50 + gap, labelWidth, height);
        lblpassword.setForeground(Color.GRAY);
        lblpassword.setFont(new Font("Tahoma", Font.BOLD, 20));
        panel.add(lblpassword);

        password = new JPasswordField();
        password.setBounds(startX + labelWidth + 20, 50 + gap, fieldWidth, height);
        password.setFont(new Font("Tahoma", Font.PLAIN, 16));
        panel.add(password);

        JLabel logginInAs = new JLabel("Logging in as:");
        logginInAs.setBounds(startX, 50 + gap * 2, labelWidth, height);
        logginInAs.setForeground(Color.GRAY);
        logginInAs.setFont(new Font("Tahoma", Font.BOLD, 20));
        panel.add(logginInAs);

        logginin = new Choice();
        logginin.add("Admin");
        logginin.add("Customer");
        logginin.setBounds(startX + labelWidth + 20, 50 + gap * 2, fieldWidth, height);
        logginin.setFont(new Font("Tahoma", Font.PLAIN, 16));
        panel.add(logginin);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBounds(startX, 50 + gap * 3, labelWidth + fieldWidth + 20, height * 2);
        buttonPanel.setOpaque(false);
        panel.add(buttonPanel);

        login = new JButton("Login");
        cancel = new JButton("Cancel");
        signup = new JButton("Signup");
        JButton[] buttons = {login, cancel, signup};
        for (JButton b : buttons) {
             b.setPreferredSize(new Dimension(120, 35));
            b.setForeground(Color.BLACK); // or some gray color
            b.setBackground(Color.DARK_GRAY);  // or black if you want
            b.setEnabled(true); // KEEP enabled, don't disable
            b.setFocusable(false); // optional
             b.setFont(new Font("Tahoma", Font.BOLD, 16));
             b.addActionListener(this);
             buttonPanel.add(b);
        }

        // Emergency Button (blinking red, white text)
        emergencyButton = new JButton("EMERGENCY");
        emergencyButton.setBounds(startX, 50 + gap * 4 + 20, labelWidth + fieldWidth + 20, 40);
        emergencyButton.setBackground(Color.RED);
        emergencyButton.setForeground(Color.BLACK);
        emergencyButton.setFont(new Font("Tahoma", Font.BOLD, 20));
        emergencyButton.addActionListener(this);
        panel.add(emergencyButton);

        // Blinking effect
        blinkTimer = new Timer(500, e -> {
            if (emergencyButton.getBackground() == Color.RED) {
                emergencyButton.setBackground(Color.BLACK);
                emergencyButton.setForeground(Color.RED);
            } else {
                emergencyButton.setBackground(Color.RED);
                emergencyButton.setForeground(Color.BLACK);
            }
        });
        blinkTimer.start();

        // Right side image
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/New1.png"));
        Image i2 = i1.getImage().getScaledInstance(500, 400, Image.SCALE_DEFAULT);
        JLabel image = new JLabel(new ImageIcon(i2));
        image.setBounds(startX + labelWidth + fieldWidth + 100, 50, 500, 400);
        panel.add(image);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == login) {
            String susername = username.getText();
            String spassword = new String(password.getPassword());
            String user = logginin.getSelectedItem();

            try {
                Conn c = new Conn();
                String query = "select * from signup where username = '" + susername + "' and password = '" + spassword + "' and account_type ='" + user + "'";
                ResultSet rs = c.s.executeQuery(query);
                if (rs.next()) {
                    setVisible(false);
                    // Direct to different pages based on account type
                    if (user.equals("Admin")) {
                        new Admin(susername);
                    } else if (user.equals("Customer")) {
                        new MainFrame(susername);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid login credentials", "Login Error", JOptionPane.ERROR_MESSAGE);
                    username.setText("");
                    password.setText("");
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else if (ae.getSource() == cancel) {
            setVisible(false);
        } else if (ae.getSource() == signup) {
            setVisible(false);
            new signup();
        } else if (ae.getSource() == emergencyButton) {
            showEmergencyPopup();
        }
    }

    private void showEmergencyPopup() {
        JDialog dialog = new JDialog(this, "Select Image Source", true);
        dialog.setLayout(new GridLayout(2, 1, 10, 10));
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);

        JButton cameraBtn = new JButton("Capture from Camera");
        cameraBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        cameraBtn.addActionListener(e -> {
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Simulate camera capture.\n(Use webcam or Android intent in actual app)");
        });

        JButton galleryBtn = new JButton("Upload from Gallery");
        galleryBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        galleryBtn.addActionListener(e -> {
            dialog.dispose();
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Image from Gallery");
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                public boolean accept(File f) {
                    if (f.isDirectory()) return true;
                    String name = f.getName().toLowerCase();
                    return name.endsWith(".jpg") || name.endsWith(".jpeg") || 
                           name.endsWith(".png") || name.endsWith(".gif");
                }
                public String getDescription() {
                    return "Image files (*.jpg, *.jpeg, *.png, *.gif)";
                }
            });
            
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    // Show loading dialog
                    JDialog loadingDialog = new JDialog(this, "Analyzing Image", false);
                    JLabel loadingLabel = new JLabel("Analyzing image for fire safety issues...");
                    loadingLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
                    loadingLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                    loadingDialog.add(loadingLabel);
                    loadingDialog.pack();
                    loadingDialog.setLocationRelativeTo(this);
                    loadingDialog.setVisible(true);

                    // Analyze image in background thread
                    new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            try {
                                // Call the FireSafetyDetector to analyze the image
                                JSONObject result = FireSafetyDetector.analyzeImage(selectedFile.getAbsolutePath());
                                
                                if (result.getBoolean("success")) {
                                    // Process detections
                                    boolean fireDetected = result.getBoolean("fire_detected");
                                    JSONArray detectedObjects = result.getJSONArray("detected_objects");
                                    
                                    StringBuilder message = new StringBuilder();
                                    if (fireDetected) {
                                        message.append("🔥 FIRE DETECTED! 🔥\n\n");
                                        message.append("Detected Objects:\n");
                                        for (int i = 0; i < detectedObjects.length(); i++) {
                                            message.append("- ").append(detectedObjects.getString(i)).append("\n");
                                        }
                                        message.append("\nPlease confirm your location:");
                                        
                                        SwingUtilities.invokeLater(() -> {
    loadingDialog.dispose();

    JPanel userDetailsPanel = new JPanel(new GridLayout(4, 1, 10, 10));
    JTextField nameField = new JTextField();
    JTextField phoneField = new JTextField();
    userDetailsPanel.add(new JLabel("Enter your Name:"));
    userDetailsPanel.add(nameField);
    userDetailsPanel.add(new JLabel("Enter your Phone Number:"));
    userDetailsPanel.add(phoneField);

    int userDetailsResult = JOptionPane.showConfirmDialog(LoginPage.this,
        userDetailsPanel,
        "User Details",
        JOptionPane.OK_CANCEL_OPTION,
        JOptionPane.QUESTION_MESSAGE);

    if (userDetailsResult == JOptionPane.OK_OPTION) {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(LoginPage.this,
                "Name and Phone Number are required!",
                "Missing Information",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int gpsResult = JOptionPane.showConfirmDialog(LoginPage.this,
            "Fire detected!\n\nClick OK to get your live location.",
            "FIRE ALERT - Get Location",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (gpsResult == JOptionPane.OK_OPTION) {
            try {
                Geolocation geo = new Geolocation();
                double latitude = geo.getLatitude();
                double longitude = geo.getLongitude();

                // ✅ Save to database
                Conn c = new Conn();
                String insertQuery = "INSERT INTO emergency_alerts (name, phone, latitude, longitude) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = c.c.prepareStatement(insertQuery);
                ps.setString(1, name);
                ps.setString(2, phone);
                ps.setDouble(3, latitude);
                ps.setDouble(4, longitude);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(LoginPage.this,
                    "Emergency alert submitted successfully!\n\n" +
                    "Name: " + name + "\nPhone: " + phone + 
                    "\nLatitude: " + latitude + "\nLongitude: " + longitude,
                    "Alert Submitted",
                    JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(LoginPage.this,
                    "Error saving emergency data: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
});


                                    } else {
                                        message.append("✅ No fire detected in the image.\n\n");
                                        message.append("Detected Objects:\n");
                                        for (int i = 0; i < detectedObjects.length(); i++) {
                                            message.append("- ").append(detectedObjects.getString(i)).append("\n");
                                        }
                                        
                                        SwingUtilities.invokeLater(() -> {
                                            loadingDialog.dispose();
                                            JOptionPane.showMessageDialog(LoginPage.this,
                                                message.toString(),
                                                "Analysis Complete",
                                                JOptionPane.INFORMATION_MESSAGE);
                                        });
                                    }
                                } else {
                                    throw new Exception(result.getString("message"));
                                }
                            } catch (Exception ex) {
                                SwingUtilities.invokeLater(() -> {
                                    loadingDialog.dispose();
                                    JOptionPane.showMessageDialog(LoginPage.this,
                                        "Error analyzing image: " + ex.getMessage(),
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                                });
                            }
                            return null;
                        }
                    }.execute();
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "Error processing image: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        dialog.add(cameraBtn);
        dialog.add(galleryBtn);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LoginPage();
        });
    }
}