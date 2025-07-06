package firesaftyproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Desktop;

public class CertificatePanel extends JPanel implements Printable {
    private String username;
    private JPanel certificatePanel;
    private JButton btnDownload;
    private Font headerFont = new Font("Arial", Font.BOLD, 16);
    private Font titleFont = new Font("Arial", Font.BOLD, 20);
    private Font normalFont = new Font("Arial", Font.PLAIN, 14);
    private Font smallFont = new Font("Arial", Font.PLAIN, 12);

    public CertificatePanel(String username) {
        this.username = username;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // Prepare today's date and expiry date
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedToday = today.format(formatter);
        LocalDate expiryDate = today.plusYears(1);
        String formattedExpiry = expiryDate.format(formatter);

        // Create main certificate panel
        certificatePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        certificatePanel.setLayout(null);
        certificatePanel.setBackground(Color.WHITE);
        certificatePanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        certificatePanel.setPreferredSize(new Dimension(800, 950));

        // Center point calculations
        int centerX = certificatePanel.getPreferredSize().width / 2;
        int contentWidth = 700;
        int startY = 20;

        // Add page title
        JLabel lblPageTitle = createLabel("No Objection Certificate", new Font("Arial", Font.PLAIN, 18));
        centerComponent(lblPageTitle, centerX, startY, contentWidth, 30);
        lblPageTitle.setHorizontalAlignment(SwingConstants.CENTER);

        // Add header
        JLabel lblHeader1 = createLabel("GOVERNMENT OF NATIONAL CAPITAL TERRITORY OF DELHI", headerFont);
        JLabel lblHeader2 = createLabel("HEADQUARTERS: DELHI FIRE SERVICE, NEW DELHI-110001", headerFont);
        centerComponent(lblHeader1, centerX, startY + 50, contentWidth, 25);
        centerComponent(lblHeader2, centerX, startY + 75, contentWidth, 25);
        lblHeader1.setHorizontalAlignment(SwingConstants.CENTER);
        lblHeader2.setHorizontalAlignment(SwingConstants.CENTER);

        // Add certificate number and date
        JPanel nocPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 0));
        nocPanel.setBackground(Color.WHITE);
        JLabel lblNocNo = createLabel("No. F.6/DFS/NOC/" + today.getYear() + "/SZ/01", normalFont);
        JLabel lblDate = createLabel("Dated: " + formattedToday, normalFont);
        nocPanel.add(lblNocNo);
        nocPanel.add(lblDate);
        centerComponent(nocPanel, centerX, startY + 110, contentWidth, 30);

        // Add main title
        JLabel lblTitle = createLabel("NO OBJECTION CERTIFICATE", titleFont);
        centerComponent(lblTitle, centerX, startY + 150, contentWidth, 30);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        // Add certificate text
        String certText = "This is to certify that the building/premises owned/occupied by " + username + " has been inspected by the "
                       + "officers of Delhi Fire Service and found to be in compliance with the fire prevention and fire "
                       + "safety requirements as per the Delhi Fire Service Rules, 2010.";
        JTextArea txtCertificate = new JTextArea(certText);
        txtCertificate.setFont(normalFont);
        txtCertificate.setLineWrap(true);
        txtCertificate.setWrapStyleWord(true);
        txtCertificate.setEditable(false);
        txtCertificate.setBackground(Color.WHITE);
        centerComponent(txtCertificate, centerX, startY + 190, contentWidth, 80);

        // Add expiry date
        JLabel lblExpiry = createLabel("Expiry Date: " + formattedExpiry, normalFont);
        centerComponent(lblExpiry, centerX, startY + 280, contentWidth, 30);
        lblExpiry.setHorizontalAlignment(SwingConstants.CENTER);

        // Add conditions title
        JLabel lblConditions = createLabel("Conditions for the validity of NOC:", titleFont);
        centerComponent(lblConditions, centerX, startY + 320, contentWidth, 30);
        lblConditions.setHorizontalAlignment(SwingConstants.CENTER);

        // Add conditions
        String[] conditions = {
            "1. All fire safety arrangements shall be maintained in good working condition at all times.",
            "2. All means of escape shall be kept free from any obstruction.",
            "3. Regular fire safety audits shall be conducted.",
            "4. Staff shall be trained in fire safety procedures.",
            "5. This NOC is valid for a period of one year from the date of issue."
        };

        int conditionY = startY + 360;
        for (String condition : conditions) {
            JLabel lblCondition = createLabel(condition, normalFont);
            centerComponent(lblCondition, centerX, conditionY, contentWidth, 25);
            lblCondition.setHorizontalAlignment(SwingConstants.LEFT);
            conditionY += 30;
        }

        // Add all components to certificate panel
        certificatePanel.add(lblPageTitle);
        certificatePanel.add(lblHeader1);
        certificatePanel.add(lblHeader2);
        certificatePanel.add(nocPanel);
        certificatePanel.add(lblTitle);
        certificatePanel.add(txtCertificate);
        certificatePanel.add(lblExpiry);
        certificatePanel.add(lblConditions);

        // Add certificate panel to scroll pane
        JScrollPane scrollPane = new JScrollPane(certificatePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        // Add download button
        btnDownload = new JButton("Download Certificate");
        btnDownload.setFont(new Font("Arial", Font.BOLD, 14));
        btnDownload.setBackground(new Color(34, 139, 34));
        btnDownload.setForeground(Color.BLACK);
        btnDownload.addActionListener(e -> generateImage());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnDownload);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void centerComponent(JComponent component, int centerX, int y, int width, int height) {
        component.setBounds(centerX - (width / 2), y, width, height);
    }

    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        return label;
    }

    private void generateImage() {
        try {
            File outputDir = new File("certificates");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            BufferedImage image = new BufferedImage(
                certificatePanel.getWidth(),
                certificatePanel.getHeight(),
                BufferedImage.TYPE_INT_RGB
            );
            Graphics2D g2d = image.createGraphics();
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
            certificatePanel.paint(g2d);
            g2d.dispose();

            String fileName = "certificates/NOC_Certificate_" + username + ".png";
            File outputFile = new File(fileName);
            ImageIO.write(image, "PNG", outputFile);

            int option = JOptionPane.showConfirmDialog(
                this,
                "Certificate has been saved as: " + fileName + "\nWould you like to open it?",
                "Certificate Generated",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );

            if (option == JOptionPane.YES_OPTION) {
                Desktop.getDesktop().open(outputFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "Error generating certificate: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        double scaleX = pageFormat.getImageableWidth() / certificatePanel.getWidth();
        double scaleY = pageFormat.getImageableHeight() / certificatePanel.getHeight();
        double scale = Math.min(scaleX, scaleY);
        g2d.scale(scale, scale);

        certificatePanel.paint(g2d);

        return PAGE_EXISTS;
    }
}
