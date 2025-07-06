package firesaftyproject;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FollowUp extends JPanel {
    private JLabel lblNocNo, lblApplicant, lblBuildingType, lblIssueDate, lblExpiryDate, lblStatus;
    private String username;
    private Statement statement;

    public FollowUp(String username, Statement s) {
        this.username = username;
        this.statement = s;
        setLayout(new BorderLayout(10, 10));
        setOpaque(false);

        JLabel heading = new JLabel("NOC Follow-Up Status", JLabel.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 22));
        heading.setForeground(new Color(0, 102, 204));
        add(heading, BorderLayout.NORTH);

        // Info Panel with Gradient Background
        JPanel infoPanel = new JPanel(new GridLayout(6, 1, 10, 10)) {
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
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        infoPanel.setOpaque(false);

        lblNocNo = new JLabel("NOC Ref: Loading...");
        lblApplicant = new JLabel("Applicant: Loading...");
        lblBuildingType = new JLabel("Building Type: Loading...");
        lblIssueDate = new JLabel("Issue Date: Loading...");
        lblExpiryDate = new JLabel("Expiry Date: Loading...");
        lblStatus = new JLabel("Status: Loading...");

        for (JLabel lbl : new JLabel[]{lblNocNo, lblApplicant, lblBuildingType, lblIssueDate, lblExpiryDate, lblStatus}) {
            lbl.setFont(new Font("Arial", Font.BOLD, 20));
            lbl.setForeground(Color.WHITE);
            infoPanel.add(lbl);
        }

        add(infoPanel, BorderLayout.CENTER);

       JPanel imagePanel = new JPanel();
imagePanel.setOpaque(false);
try {
    ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/New2.png"));
    Image i2 = i1.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH); // Adjust size for better fitting
    JLabel imageLabel = new JLabel(new ImageIcon(i2));
    imageLabel.setHorizontalAlignment(JLabel.CENTER);
    imagePanel.add(imageLabel);
} catch (Exception e) {
    System.out.println("Image not found: " + e.getMessage());
}

add(imagePanel, BorderLayout.EAST); // ✅ this was missing in your code


        fetchNOCData();
    }

    private void fetchNOCData() {
        try {
            String query = "SELECT * FROM firesafty.nocaccept WHERE username = '" + username + "'";
            ResultSet rs = statement.executeQuery(query);

            if (rs.next()) {
                java.sql.Date issuedDate = rs.getDate("date");
                String nocRef;
                if (issuedDate != null) {
                    LocalDate issueLocalDate = issuedDate.toLocalDate();
                    nocRef = rs.getString("username") + "-" + issueLocalDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                    lblIssueDate.setText("Issue Date: " + issueLocalDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    LocalDate expiryLocalDate = issueLocalDate.plusYears(1);
                    lblExpiryDate.setText("Expiry Date: " + expiryLocalDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    LocalDate today = LocalDate.now();
                    if (today.isAfter(expiryLocalDate)) {
                        lblStatus.setText("Status: ❌ Expired");
                        lblStatus.setForeground(Color.RED);
                    } else {
                        lblStatus.setText("Status: ✅ Active");
                        lblStatus.setForeground(new Color(0, 153, 0));
                    }
                } else {
                    nocRef = rs.getString("username");
                    lblIssueDate.setText("Issue Date: Not Available");
                    lblExpiryDate.setText("Expiry Date: Not Available");
                    lblStatus.setText("Status: No Record Found");
                }
                lblNocNo.setText("NOC Ref: " + nocRef);
                lblApplicant.setText("Applicant: " + rs.getString("username"));
                lblBuildingType.setText("Building Type: " + rs.getString("building_type"));
            } else {
                lblNocNo.setText("NOC Ref: Not Available");
                lblApplicant.setText("Applicant: Not Available");
                lblBuildingType.setText("Building Type: Not Available");
                lblIssueDate.setText("Issue Date: Not Available");
                lblExpiryDate.setText("Expiry Date: Not Available");
                lblStatus.setText("Status: No Record Found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching NOC data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            Conn c = new Conn();
            JFrame frame = new JFrame("Test FollowUp Panel");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 500); // Adjusted size to accommodate image
            frame.setLocationRelativeTo(null);
            frame.setLayout(new BorderLayout());
            frame.add(new FollowUp("testuser", c.s), BorderLayout.CENTER); // Replace 'testuser' as needed
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
