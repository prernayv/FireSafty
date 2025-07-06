package firesaftyproject;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;

public class Admin extends JFrame {
    private JLabel lblSelectedService;
    private JPanel mainPanel;
    private JButton btnEmergency;
    private Timer blinkTimer;
    private boolean isBlinking = false;
    String susername;
    private Statement statement;
    private Connection connection;
    private Timer emergencyTimer;
    private int lastEmergencyId = -1; // Track the last seen emergency

    public Admin(String susername) {
        this.susername = susername;
        initializeDatabase();
        setTitle("Admin Dashboard");
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

        JLabel headingg = new JLabel(susername);
        headingg.setBounds(65, 10, 200, 25);
        headingg.setFont(new Font("ARIAL", Font.BOLD, 20));
        headingg.setForeground(Color.BLACK);
        menuPanel.add(headingg);

        
        JButton btnNOC = createButton("NOC", new Color(50, 150, 80), Color.WHITE);
        
        JButton btnLogin = createButton("Back to Login", new Color(100, 100, 100), Color.WHITE);

        
        btnNOC.setBounds(20, 310, 160, 50);
        
        btnLogin.setBounds(20, 430, 160, 50);

        
        menuPanel.add(btnNOC);
       
        menuPanel.add(btnLogin);
        
        btnEmergency = createButton("EMERGENCY", Color.RED, Color.WHITE);
        btnEmergency.setBounds(20, 600, 160, 40);
        btnEmergency.setFont(new Font("Arial", Font.BOLD, 18));
        menuPanel.add(btnEmergency);

        blinkTimer = new Timer(500, e -> {
            if (isBlinking) {
                btnEmergency.setBackground(Color.RED);
            } else {
                btnEmergency.setBackground(Color.WHITE);
                btnEmergency.setForeground(Color.BLACK);
            }
            isBlinking = !isBlinking;
        });
        blinkTimer.start();

        add(menuPanel, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        btnLogin.addActionListener(e->{
          setVisible(false);
          new LoginPage();
          
        });
        btnNOC.addActionListener(e -> {
            lblSelectedService.setText("NOC Applications");
            showNOCApplications();
        });

        btnEmergency.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "EMERGENCY ALERT!\nFire Department has been notified.\nPlease evacuate the building immediately!", 
                "EMERGENCY", 
                JOptionPane.ERROR_MESSAGE);
        });

        startEmergencyPolling();

        setVisible(true);
    }

    private void initializeDatabase() {
        try {
            Conn c = new Conn();
            this.connection = c.c;
            this.statement = c.s;
            if (this.statement == null) {
                throw new SQLException("Failed to initialize database connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error connecting to database: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showNOCApplications() {
        mainPanel.removeAll();
        try {
            if (statement == null) {
                initializeDatabase();
                if (statement == null) {
                    throw new SQLException("Database connection is not available");
                }
            }
            
            String query = "SELECT * FROM firesafty.noc";
            System.out.println("Executing query: " + query);
            ResultSet rs = statement.executeQuery(query);

            DefaultTableModel model = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 10;
                }
            };

            // Add columns
            model.addColumn("Name");
            model.addColumn("Building Type");
            model.addColumn("Floors");
            model.addColumn("Rooms");
            model.addColumn("Capacity");
            model.addColumn("Area");
            model.addColumn("Extinguishers");
            model.addColumn("Sprinklers");
            model.addColumn("Smoke Detectors");
            model.addColumn("Fire Alarms");
            model.addColumn("Actions");

            // Add data
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("username"),
                    rs.getString("building_type"),
                    rs.getInt("floor"),
                    rs.getInt("rooms"),
                    rs.getInt("capacity"),
                    rs.getInt("area"),
                    rs.getInt("extinguishers"),
                    rs.getInt("sprinklers"),
                    rs.getInt("smoke_detectors"),
                    rs.getInt("fire_alarms"),
                    "Actions"
                });
            }

            // Create table
            JTable table = new JTable(model);
            table.setFont(new Font("Arial", Font.PLAIN, 14));
            table.setRowHeight(40);
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
            table.getTableHeader().setBackground(new Color(50, 50, 50));
            table.getTableHeader().setForeground(Color.WHITE);
            table.setGridColor(Color.GRAY);
            table.setShowGrid(true);
            
            // Configure the Actions column
            table.getColumnModel().getColumn(10).setPreferredWidth(120);
            table.getColumnModel().getColumn(10).setMinWidth(120);
            table.getColumnModel().getColumn(10).setCellRenderer(new ButtonRenderer());
            table.getColumnModel().getColumn(10).setCellEditor(new ButtonEditor(new JCheckBox(), this, statement));

            // Add mouse listener for button clicks
            table.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int column = table.getColumnModel().getColumnIndexAtX(e.getX());
                    int row = e.getY() / table.getRowHeight();

                    if (row < table.getRowCount() && row >= 0 && column == 10) {
                        table.editCellAt(row, column);
                        Component editor = table.getEditorComponent();
                        if (editor != null) {
                            editor.requestFocusInWindow();
                        }
                    }
                }
            });

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                "NOC Applications",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16),
                Color.BLACK
            ));

            // Create buttons panel
            JPanel buttonPanel = new JPanel();
            buttonPanel.setBackground(new Color(249, 194, 56));
            JButton btnRefresh = createButton("Refresh", new Color(80, 150, 200), Color.WHITE);
            btnRefresh.addActionListener(e -> refreshTable(model, statement));
            buttonPanel.add(btnRefresh);

            mainPanel.add(scrollPane, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            mainPanel.revalidate();
            mainPanel.repaint();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading NOC applications: " + e.getMessage());
        }
    }

class ButtonRenderer implements TableCellRenderer {
    private final Color acceptBg = new Color(50, 150, 80);
    private final Color rejectBg = new Color(200, 50, 50);
    private final Color acceptFg = Color.WHITE;
    private final Color rejectFg = Color.WHITE;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        JPanel panel = new JPanel(new GridLayout(1, 2, 4, 0));
        panel.setOpaque(true);
        if (isSelected) {
            panel.setBackground(table.getSelectionBackground());
        } else {
            panel.setBackground(Color.WHITE);
        }

        JButton acceptBtn = new JButton("\u2713");  // Unicode checkmark
        acceptBtn.setFont(new Font("Arial Unicode MS", Font.BOLD, 24));
        acceptBtn.setBackground(acceptBg);
        acceptBtn.setForeground(acceptFg);
        acceptBtn.setOpaque(true);
        acceptBtn.setContentAreaFilled(true);
        acceptBtn.setBorderPainted(true);
        acceptBtn.setBorder(BorderFactory.createLineBorder(new Color(40, 120, 60), 2));
        acceptBtn.setFocusPainted(false);
        acceptBtn.setPreferredSize(new Dimension(48, 36));
        acceptBtn.setEnabled(false); // Not clickable in renderer

        JButton rejectBtn = new JButton("\u2717");  // Unicode X mark
        rejectBtn.setFont(new Font("Arial Unicode MS", Font.BOLD, 24));
        rejectBtn.setBackground(rejectBg);
        rejectBtn.setForeground(rejectFg);
        rejectBtn.setOpaque(true);
        rejectBtn.setContentAreaFilled(true);
        rejectBtn.setBorderPainted(true);
        rejectBtn.setBorder(BorderFactory.createLineBorder(new Color(160, 40, 40), 2));
        rejectBtn.setFocusPainted(false);
        rejectBtn.setPreferredSize(new Dimension(48, 36));
        rejectBtn.setEnabled(false); // Not clickable in renderer

        panel.add(acceptBtn);
        panel.add(rejectBtn);
        panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        return panel;
    }
}

    class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton acceptBtn, rejectBtn;
        private String username;
        private Admin parentFrame;
        private Statement statement;
        private JTable table;
        private int selectedRow;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox, Admin parentFrame, Statement statement) {
            super(checkBox);
            this.parentFrame = parentFrame;
            this.statement = statement;
            
            panel = new JPanel(new GridLayout(1, 2, 4, 0));
            
            acceptBtn = new JButton("\u2713");  // Unicode checkmark
            acceptBtn.setFont(new Font("Arial Unicode MS", Font.BOLD, 24));
            acceptBtn.setBackground(new Color(50, 150, 80));
            acceptBtn.setForeground(Color.WHITE);
            acceptBtn.setOpaque(true);
            acceptBtn.setContentAreaFilled(true);
            acceptBtn.setBorderPainted(true);
            acceptBtn.setBorder(BorderFactory.createLineBorder(new Color(40, 120, 60), 2));
            acceptBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            acceptBtn.setFocusPainted(false);
            
            rejectBtn = new JButton("\u2717");  // Unicode X mark
            rejectBtn.setFont(new Font("Arial Unicode MS", Font.BOLD, 24));
            rejectBtn.setBackground(new Color(200, 50, 50));
            rejectBtn.setForeground(Color.WHITE);
            rejectBtn.setOpaque(true);
            rejectBtn.setContentAreaFilled(true);
            rejectBtn.setBorderPainted(true);
            rejectBtn.setBorder(BorderFactory.createLineBorder(new Color(160, 40, 40), 2));
            rejectBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            rejectBtn.setFocusPainted(false);
            
            acceptBtn.addActionListener(e -> {
                isPushed = true;
                acceptApplication();
                fireEditingStopped();
            });
            
            rejectBtn.addActionListener(e -> {
                isPushed = true;
                rejectApplication();
                fireEditingStopped();
            });
            
            panel.add(acceptBtn);
            panel.add(rejectBtn);
            panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            panel.setBackground(Color.WHITE);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.table = table;
            this.selectedRow = row;
            this.username = table.getValueAt(row, 0).toString();
            isPushed = false;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return username;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }

        private void acceptApplication() {
            try {
                if (selectedRow < 0) {
                    JOptionPane.showMessageDialog(parentFrame, "Please select a row first");
                    return;
                }

                username = table.getValueAt(selectedRow, 0).toString();
                int choice = JOptionPane.showConfirmDialog(
                    parentFrame,
                    "Are you sure you want to accept this NOC application?",
                    "Confirm Accept",
                    JOptionPane.YES_NO_OPTION
                );

                if (choice == JOptionPane.YES_OPTION) {
                    // Insert into nocaccept
                    String insertQuery = "INSERT INTO firesafty.nocaccept (username, building_type, floor, rooms, capacity, area, " +
                                      "extinguishers, sprinklers, smoke_detectors, fire_alarms) VALUES ('" +
                                      username + "', '" + table.getValueAt(selectedRow, 1) + "', " + 
                                      table.getValueAt(selectedRow, 2) + ", " + table.getValueAt(selectedRow, 3) + ", " +
                                      table.getValueAt(selectedRow, 4) + ", " + table.getValueAt(selectedRow, 5) + ", " +
                                      table.getValueAt(selectedRow, 6) + ", " + table.getValueAt(selectedRow, 7) + ", " +
                                      table.getValueAt(selectedRow, 8) + ", " + table.getValueAt(selectedRow, 9) + ")";
                    statement.executeUpdate(insertQuery);

                    // Delete from noc table
                    String deleteQuery = "DELETE FROM firesafty.noc WHERE username = '" + username + "'";
                    statement.executeUpdate(deleteQuery);

                    // Stop editing BEFORE removing row!
                    fireEditingStopped();

                    // Remove row
                    ((DefaultTableModel) table.getModel()).removeRow(selectedRow);

                    JOptionPane.showMessageDialog(
                        parentFrame,
                        "NOC Application Accepted Successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, "Error accepting application: " + ex.getMessage());
            }
        }

        private void rejectApplication() {
            try {
                if (selectedRow < 0) {
                    JOptionPane.showMessageDialog(parentFrame, "Please select a row first");
                    return;
                }

                username = table.getValueAt(selectedRow, 0).toString();
                int choice = JOptionPane.showConfirmDialog(
                    parentFrame,
                    "Are you sure you want to reject this NOC application?",
                    "Confirm Reject",
                    JOptionPane.YES_NO_OPTION
                );

                if (choice == JOptionPane.YES_OPTION) {
                    // Delete from noc table
                    String deleteQuery = "DELETE FROM firesafty.noc WHERE username = '" + username + "'";
                    statement.executeUpdate(deleteQuery);

                    // Stop editing BEFORE removing row!
                    fireEditingStopped();

                    // Remove row
                    ((DefaultTableModel) table.getModel()).removeRow(selectedRow);

                    JOptionPane.showMessageDialog(
                        parentFrame,
                        "NOC Application Rejected Successfully!",
                        "Rejected",
                        JOptionPane.WARNING_MESSAGE
                    );
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, "Error rejecting application: " + ex.getMessage());
            }
        }
    }

    private void refreshTable(DefaultTableModel model, Statement statement) {
        try {
            // Clear existing rows
            model.setRowCount(0);
            
            // Fetch fresh data
            String query = "SELECT * FROM firesafty.noc";
            ResultSet rs = statement.executeQuery(query);
            
            // Add new rows
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("username"),
                    rs.getString("building_type"),
                    rs.getInt("floor"),
                    rs.getInt("rooms"),
                    rs.getInt("capacity"),
                    rs.getInt("area"),
                    rs.getInt("extinguishers"),
                    rs.getInt("sprinklers"),
                    rs.getInt("smoke_detectors"),
                    rs.getInt("fire_alarms"),
                    "Actions"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error refreshing table: " + e.getMessage());
        }
    }

    private JButton createButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false); 
        return button;
    }

    private void startEmergencyPolling() {
        emergencyTimer = new Timer(3000, e -> checkForEmergency());
        emergencyTimer.start();
    }

    private void checkForEmergency() {
        try {
            String query = "SELECT id, name, alert_time FROM firesafty.emergency_alerts ORDER BY id DESC LIMIT 1";
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                Timestamp alertTime = rs.getTimestamp("alert_time");
                if (id != lastEmergencyId) {
                    lastEmergencyId = id;
                    JOptionPane.showMessageDialog(this,
                        "EMERGENCY ALERT!\nReported by: " + name + "\nTime: " + alertTime,
                        "EMERGENCY",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showDetailsPanel(String username) {
        try {
            // Try nocaccept first
            String query = "SELECT * FROM firesafty.nocaccept WHERE username = ?";
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();
            if (!rs.next()) {
                // If not found, try noc
                query = "SELECT * FROM firesafty.noc WHERE username = ?";
                pst = connection.prepareStatement(query);
                pst.setString(1, username);
                rs = pst.executeQuery();
            }
            if (rs.next()) {
                JPanel detailsPanel = new JPanel(new GridLayout(0, 1));
                detailsPanel.setBorder(BorderFactory.createTitledBorder("Application Details"));
                detailsPanel.add(new JLabel("Username: " + rs.getString("username")));
                detailsPanel.add(new JLabel("Building Type: " + rs.getString("building_type")));
                detailsPanel.add(new JLabel("Floors: " + rs.getInt("floor")));
                detailsPanel.add(new JLabel("Rooms: " + rs.getInt("rooms")));
                detailsPanel.add(new JLabel("Capacity: " + rs.getInt("capacity")));
                detailsPanel.add(new JLabel("Area: " + rs.getInt("area")));
                detailsPanel.add(new JLabel("Extinguishers: " + rs.getInt("extinguishers")));
                detailsPanel.add(new JLabel("Sprinklers: " + rs.getInt("sprinklers")));
                detailsPanel.add(new JLabel("Smoke Detectors: " + rs.getInt("smoke_detectors")));
                detailsPanel.add(new JLabel("Fire Alarms: " + rs.getInt("fire_alarms")));
                mainPanel.removeAll();
                mainPanel.add(detailsPanel, BorderLayout.CENTER);
                mainPanel.revalidate();
                mainPanel.repaint();
            } else {
                JOptionPane.showMessageDialog(this, "No details found for this user.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetching details: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Admin("Admin");
    }
}

class NOC extends JPanel implements ActionListener {
    private JComboBox<String> cmbBuildingType;
    private JTextField[] textFields;
    private JButton btnSubmit;
    private String susername;
    private java.sql.Statement s;

    public NOC(String susername, java.sql.Statement s) {
        this.susername = susername;
        this.s = s;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        // Add building type combo box
        String[] buildingTypes = {"Residential", "Commercial", "Industrial", "Educational", "Hospital"};
        cmbBuildingType = new JComboBox<>(buildingTypes);
        addLabelAndComponent("Building Type:", cmbBuildingType, gbc);

        // Add text fields for numeric inputs
        String[] fieldLabels = {
            "Number of Floors", "Number of Rooms", "Capacity", "Area (sq ft)",
            "Number of Extinguishers", "Number of Sprinklers",
            "Number of Smoke Detectors", "Number of Fire Alarms"
        };
        textFields = new JTextField[fieldLabels.length];
        
        for (int i = 0; i < fieldLabels.length; i++) {
            textFields[i] = new JTextField(10);
            addLabelAndComponent(fieldLabels[i] + ":", textFields[i], gbc);
        }

        // Add submit button
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        btnSubmit = new JButton("Submit Application");
        btnSubmit.addActionListener(this);
        add(btnSubmit, gbc);
    }

    private void addLabelAndComponent(String labelText, JComponent component, GridBagConstraints gbc) {
        JLabel label = new JLabel(labelText);
        add(label, gbc);
        gbc.gridx++;
        add(component, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSubmit) {
            try {
                // Validate inputs
                for (JTextField field : textFields) {
                    if (field.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Please fill in all fields");
                        return;
                    }
                    try {
                        Integer.parseInt(field.getText());
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Please enter valid numbers in all fields");
                        return;
                    }
                }

                // Insert data into database with correct schema
                String query = "INSERT INTO firesafty.noc (username, building_type, floor, rooms, capacity, area, " +
                             "extinguishers, sprinklers, smoke_detectors, fire_alarms) VALUES ('" +
                             susername + "', '" + cmbBuildingType.getSelectedItem() + "', " +
                             textFields[0].getText() + ", " + textFields[1].getText() + ", " +
                             textFields[2].getText() + ", " + textFields[3].getText() + ", " +
                             textFields[4].getText() + ", " + textFields[5].getText() + ", " +
                             textFields[6].getText() + ", " + textFields[7].getText() + ")";

                System.out.println("Executing query: " + query);
                s.executeUpdate(query);
                JOptionPane.showMessageDialog(this, "Application submitted successfully!");

                // Clear form
                cmbBuildingType.setSelectedIndex(0);
                for (JTextField field : textFields) {
                    field.setText("");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }
} 