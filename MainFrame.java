package firesaftyproject;

import firesaftyproject.model.TabNetPredictor;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.sql.ResultSet;
import java.awt.print.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MainFrame extends JFrame {

    private JLabel lblSelectedService;
    private JButton btnApplyNOC, btnViewDownload;
    private JPanel mainPanel;
    
    private Timer blinkTimer;
    private boolean isBlinking = false;
    String susername;
    private java.sql.Connection conn;

    public MainFrame(String susername) {
        this.susername = susername;
        try {
            Conn c = new Conn();
            this.conn = c.s.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setTitle("Main Frame");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(249, 194, 56);
                Color color2 = Color.BLACK;
                GradientPaint gradient = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));

        JPanel topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(getWidth(), 50));
        topPanel.setBackground(Color.BLACK);
        topPanel.setLayout(new BorderLayout());

        lblSelectedService = new JLabel("Select a service", JLabel.CENTER);
        lblSelectedService.setFont(new Font("Arial", Font.BOLD, 24));
        lblSelectedService.setForeground(Color.WHITE);
        topPanel.add(lblSelectedService, BorderLayout.CENTER);

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(null);
        menuPanel.setBackground(new Color(220, 220, 220));
        menuPanel.setPreferredSize(new Dimension(200, getHeight()));

        // Add heading label
        JLabel headingg = new JLabel(susername);
        headingg.setBounds(65, 10, 200, 25);
        headingg.setFont(new Font("ARIAL", Font.BOLD, 20));
        headingg.setForeground(Color.BLACK);
        menuPanel.add(headingg);

        JButton btnDetails = createButton("Details", new Color(80, 150, 200), Color.WHITE);
        JButton btnNOC = createButton("NOC", new Color(50, 150, 80), Color.WHITE);
        JButton btnFollowUp  = createButton("Follow Up", new Color(200, 100, 50), Color.WHITE);
        JButton btnLogin = createButton("Back to Login", new Color(100, 100, 100), Color.WHITE);

        btnDetails.setBounds(20, 250, 160, 50);
        btnNOC.setBounds(20, 310, 160, 50);
        btnFollowUp.setBounds(20, 370, 160, 50);
        btnLogin.setBounds(20, 430, 160, 50);
        

        menuPanel.add(btnDetails);
        menuPanel.add(btnNOC);
        menuPanel.add(btnFollowUp);
        menuPanel.add(btnLogin);

        btnApplyNOC = createButton("Apply NOC", new Color(150, 150, 255), Color.WHITE);
        btnViewDownload = createButton("View/Download", new Color(150, 150, 255), Color.WHITE);
        btnViewDownload.setFont(new Font("Arial", Font.BOLD, 16)); 

        btnApplyNOC.setBounds(20, 490, 160, 50);
        btnViewDownload.setBounds(20, 550, 160, 40);

        btnApplyNOC.setVisible(false);
        btnViewDownload.setVisible(false);

        menuPanel.add(btnApplyNOC);
        menuPanel.add(btnViewDownload);

      

       

       

       

       

        add(menuPanel, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        btnNOC.addActionListener(e -> {
            btnApplyNOC.setVisible(true);
            btnViewDownload.setVisible(true);
            lblSelectedService.setText("NOC service selected");
        });

        btnApplyNOC.addActionListener(e -> {
            btnApplyNOC.setVisible(false);
            btnViewDownload.setVisible(false);
            lblSelectedService.setText("Applying NOC...");
            showApplyNOCForm();
        });

        btnViewDownload.addActionListener(e -> {
            lblSelectedService.setText("Viewing/Downloading Certificate...");
            showCertificatePanel();
        });

        btnDetails.addActionListener(e -> {
            resetView("Details service selected");
            showDetailsPanel(susername);
        });

        btnFollowUp.addActionListener(e -> {
            resetView("Follow Up service selected");
            try {
                Conn c = new Conn();
                mainPanel.add(new FollowUp(susername, c.s), BorderLayout.CENTER);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
            mainPanel.revalidate();
            mainPanel.repaint();
        });
        btnLogin.addActionListener(e -> {
           setVisible(false);
           new LoginPage();
        });
        
  

        setVisible(true);
    }

    private JButton createButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false); // (optional for flat style)

        
        return button;
    }

    private void resetView(String message) {
        btnApplyNOC.setVisible(false);
        btnViewDownload.setVisible(false);
        lblSelectedService.setText(message);
        mainPanel.removeAll();
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void showApplyNOCForm() {
        mainPanel.removeAll();
        try {
            Conn c = new Conn();
            mainPanel.add(new ApplyNOCPanel(susername, c.s), BorderLayout.CENTER);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void showCertificatePanel() {
        try {
            Conn c = new Conn();
            // Query database for user's NOC details from nocaccept table
            String query = "SELECT * FROM firesafty.nocaccept WHERE username = ?";
            PreparedStatement pst = c.s.getConnection().prepareStatement(query);
            pst.setString(1, susername);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                // Create certificate panel with user's details
                CertificatePanel certificatePanel = new CertificatePanel(susername);
                mainPanel.add(certificatePanel, BorderLayout.CENTER);
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Your application is not accepted by admin yet. Please wait for approval.",
                    "Application Not Accepted",
                    JOptionPane.WARNING_MESSAGE
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "Error accessing certificate: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

public void showDetailsPanel(String username) {
    try {
        System.out.println("Debug: Starting showDetailsPanel for username: " + username);
        Conn c = new Conn();
        
        // First try nocaccept table
        System.out.println("Debug: Trying nocaccept table first");
        String query = "SELECT * FROM noc WHERE username = ?";  // Removed firesafty schema
        PreparedStatement pst = c.s.getConnection().prepareStatement(query);
        pst.setString(1, username);
        System.out.println("Debug: Executing query: " + query + " with username: " + username);
        ResultSet rs = pst.executeQuery();
        
        boolean found = rs.next();
        System.out.println("Debug: Found in noc table: " + found);

        if (!found) {
            System.out.println("Debug: No results in noc table, trying nocaccept table");
            query = "SELECT * FROM nocaccept WHERE username = ?";  // Removed firesafty schema
            pst = c.s.getConnection().prepareStatement(query);
            pst.setString(1, username);
            System.out.println("Debug: Executing query: " + query + " with username: " + username);
            rs = pst.executeQuery();
            found = rs.next();
            System.out.println("Debug: Found in nocaccept table: " + found);
        }

        if (found) {
            System.out.println("Debug: Creating details panel");
            JPanel detailsPanel = new JPanel(new BorderLayout(20, 20));
            detailsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                "Application Details",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 24),
                Color.WHITE
            ));
            detailsPanel.setOpaque(false);

            // Image Panel
            JPanel imagePanel = new JPanel();
            imagePanel.setOpaque(false);
            try {
                ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/New2.png"));
                Image i2 = i1.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(i2));
                imageLabel.setHorizontalAlignment(JLabel.CENTER);
                imagePanel.add(imageLabel);
            } catch (Exception e) {
                System.out.println("Image not found: " + e.getMessage());
            }

            // Info Panel
            JPanel infoPanel = new JPanel(new GridLayout(0, 1, 10, 10));
            infoPanel.setOpaque(false);

            try {
                // Print all column values for debugging
                System.out.println("Debug: Reading database values:");
                System.out.println("Username: " + rs.getString("username"));
                System.out.println("Building Type: " + rs.getString("building_type"));
                System.out.println("Floor: " + rs.getInt("floor"));
                System.out.println("Rooms: " + rs.getInt("rooms"));
                System.out.println("Capacity: " + rs.getInt("capacity"));
                System.out.println("Area: " + rs.getInt("area"));
                System.out.println("Extinguishers: " + rs.getInt("extinguishers"));
                System.out.println("Sprinklers: " + rs.getInt("sprinklers"));
                System.out.println("Smoke Detectors: " + rs.getInt("smoke_detectors"));
                System.out.println("Fire Alarms: " + rs.getInt("fire_alarms"));

                // Add the values to the panel
                infoPanel.add(createDetailLabel("Username: " + rs.getString("username")));
                infoPanel.add(createDetailLabel("Building Type: " + rs.getString("building_type")));
                infoPanel.add(createDetailLabel("Floors: " + rs.getInt("floor")));
                infoPanel.add(createDetailLabel("Rooms: " + rs.getInt("rooms")));
                infoPanel.add(createDetailLabel("Capacity: " + rs.getInt("capacity")));
                infoPanel.add(createDetailLabel("Area: " + rs.getInt("area")));
                infoPanel.add(createDetailLabel("Extinguishers: " + rs.getInt("extinguishers")));
                infoPanel.add(createDetailLabel("Sprinklers: " + rs.getInt("sprinklers")));
                infoPanel.add(createDetailLabel("Smoke Detectors: " + rs.getInt("smoke_detectors")));
                infoPanel.add(createDetailLabel("Fire Alarms: " + rs.getInt("fire_alarms")));
            } catch (SQLException e) {
                System.out.println("Debug: Error reading values from ResultSet: " + e.getMessage());
                e.printStackTrace();
            }

            // Add to Details Panel
            detailsPanel.add(imagePanel, BorderLayout.EAST);
            detailsPanel.add(infoPanel, BorderLayout.CENTER);

            System.out.println("Debug: Updating main panel");
            mainPanel.removeAll();
            mainPanel.add(detailsPanel, BorderLayout.CENTER);
            mainPanel.revalidate();
            mainPanel.repaint();
        } else {
            System.out.println("Debug: No details found for user: " + username);
            JOptionPane.showMessageDialog(this, "No details found for this user.");
        }
    } catch (Exception e) {
        System.out.println("Debug: Exception occurred: " + e.getMessage());
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error fetching details: " + e.getMessage());
    }
}

// 🔥 Helper method to create nice white labels
private JLabel createDetailLabel(String text) {
    JLabel label = new JLabel(text);
    label.setFont(new Font("Arial", Font.PLAIN, 20));
    label.setForeground(Color.WHITE); // Text color white to show on gradient
    return label;
}

    public static void main(String[] args) {
        new MainFrame("Guest");
    }
}

class ApplyNOCPanel extends JPanel implements ActionListener {
    private JComboBox<String> cmbBuildingType;
    private JTextField[] textFields;
    private JButton btnSubmit;
    private String susername;
    private java.sql.Statement s;

    public ApplyNOCPanel(String susername, java.sql.Statement s) {
        this.susername = susername;
        this.s = s;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.WHITE, 3), "Building Details", TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 30), Color.WHITE));
        setOpaque(false);

        JLabel lblBuildingType = new JLabel("Building Type:");
        lblBuildingType.setFont(new Font("Arial", Font.BOLD, 24));
        lblBuildingType.setForeground(Color.WHITE);
        add(lblBuildingType, gbc);
        gbc.gridx = 1;
        String[] buildingTypes = {"Residential", "Commercial", "Industrial"};
        cmbBuildingType = new JComboBox<>(buildingTypes);
        cmbBuildingType.setFont(new Font("Arial", Font.PLAIN, 24));
        add(cmbBuildingType, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        String[] labels = {"No. of Floors:", "No. of Rooms:", "Capacity:", "Area in SqFt:", "No. of Extinguishers:", "No. of Fire Alarms:", "No. of Sprinklers:", "No. of Smoke Detectors:"};
        textFields = new JTextField[labels.length];
        
        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Arial", Font.BOLD, 24));
            lbl.setForeground(Color.WHITE);
            add(lbl, gbc);
            gbc.gridx = 1;
            textFields[i] = new JTextField(20);
            textFields[i].setFont(new Font("Arial", Font.PLAIN, 24));
            add(textFields[i], gbc);
            gbc.gridx = 0;
            gbc.gridy++;
        }

        btnSubmit = new JButton("Submit");
        btnSubmit.setFont(new Font("Arial", Font.BOLD, 24));
        btnSubmit.addActionListener(this);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy++;
        add(btnSubmit, gbc);
    }

    public void actionPerformed(ActionEvent ae) {
        if(ae.getSource() == btnSubmit) {
            try {
                // Validate fields
                for(JTextField field : textFields) {
                    if(field.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Please fill all fields");
                        return;
                    }
                }

                // Prepare data for TabNet model
                Map<String, Object> inputData = new HashMap<>();
                inputData.put("building_type", cmbBuildingType.getSelectedItem().toString().toLowerCase());
                inputData.put("floors", Integer.parseInt(textFields[0].getText()));
                inputData.put("rooms", Integer.parseInt(textFields[1].getText()));
                inputData.put("capacity", Integer.parseInt(textFields[2].getText()));
                inputData.put("area", Integer.parseInt(textFields[3].getText()));
                inputData.put("extinguishers", Integer.parseInt(textFields[4].getText()));
                inputData.put("fire_alarms", Integer.parseInt(textFields[5].getText()));
                inputData.put("sprinklers", Integer.parseInt(textFields[6].getText()));
                inputData.put("smoke_detectors", Integer.parseInt(textFields[7].getText()));

                // Show processing message
                System.out.println("Analyzing building safety requirements...");
                System.out.println("Building Type: " + inputData.get("building_type"));
                System.out.println("Floors: " + inputData.get("floors"));
                System.out.println("Rooms: " + inputData.get("rooms"));
                System.out.println("Capacity: " + inputData.get("capacity"));
                System.out.println("Area: " + inputData.get("area"));
                System.out.println("Extinguishers: " + inputData.get("extinguishers"));
                System.out.println("Fire Alarms: " + inputData.get("fire_alarms"));
                System.out.println("Sprinklers: " + inputData.get("sprinklers"));
                System.out.println("Smoke Detectors: " + inputData.get("smoke_detectors"));

                // Get prediction from TabNet model
                System.out.println("\n🤖 Running safety analysis through TabNet model...");
                Map<String, Object> prediction = TabNetPredictor.predict(inputData);
                boolean isSafe = (boolean) prediction.get("is_safe");
                double confidence = (double) prediction.get("confidence") * 100;
                String status = (String) prediction.get("status");

                System.out.println("\n📊 Safety Analysis Results:");
                System.out.println("Status: " + status);
                System.out.println("Confidence Level: " + String.format("%.2f%%", confidence));
                System.out.println("Safety Decision: " + (isSafe ? "✅ SAFE" : "❌ UNSAFE"));

                if (!isSafe) {
                    System.out.println("\n❌ Building does not meet fire safety requirements");
                    JOptionPane.showMessageDialog(this, 
                        String.format("Application rejected. The building does not meet fire safety requirements.\nConfidence: %.2f%%", confidence),
                        "Safety Check Failed",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // If safe, proceed with database insertion
                System.out.println("\n💾 Storing application in database...");
                String query = "insert into noc(username, building_type, floor, rooms, capacity, area, extinguishers, sprinklers, smoke_detectors, fire_alarms) values('" + 
                    susername + "', '" + 
                    cmbBuildingType.getSelectedItem() + "', '" + 
                    textFields[0].getText() + "', '" + 
                    textFields[1].getText() + "', '" + 
                    textFields[2].getText() + "', '" + 
                    textFields[3].getText() + "', '" + 
                    textFields[4].getText() + "', '" + 
                    textFields[5].getText() + "', '" + 
                    textFields[6].getText() + "', '" + 
                    textFields[7].getText() + "')";

                s.executeUpdate(query);
                System.out.println("✅ Application stored successfully");
                
                JOptionPane.showMessageDialog(null, 
                    String.format("NOC Application Submitted Successfully!\nSafety Check Passed (Confidence: %.2f%%)", confidence));
                
                // Clear all fields
                for(JTextField field : textFields) {
                    field.setText("");
                }
                cmbBuildingType.setSelectedIndex(0);
                
            } catch(Exception e) {
                System.out.println("❌ Error during safety analysis: " + e.getMessage());
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
