package firesaftyproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Front extends JFrame implements ActionListener {
    CircularButton userButton, adminButton;

    Front() {
        setTitle("Fire Safety App");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocation(0, 0);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.white);

        // Get screen dimensions
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();

        // Load and display image
        ImageIcon i41 = new ImageIcon(ClassLoader.getSystemResource("icons/fire.png"));
        Image i42 = i41.getImage().getScaledInstance((screenWidth * 2) / 3, screenHeight, Image.SCALE_DEFAULT);
        ImageIcon i43 = new ImageIcon(i42);
        JLabel image1 = new JLabel(i43);
        image1.setBounds(0, 0, (screenWidth * 2) / 3, screenHeight);
        add(image1);

        // Main panel (right side - 1/3 of screen width)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(249, 194, 56));
        mainPanel.setBounds((screenWidth * 2) / 3, 0, screenWidth / 3, screenHeight);
        add(mainPanel);

        // Calculate button size based on screen dimensions
        int buttonSize = Math.min(screenWidth / 4, screenHeight / 4);
        int buttonX = (screenWidth / 3 - buttonSize) / 2;

        // Creating the User Button (Circular)
        userButton = new CircularButton("User");
        userButton.setBounds(buttonX, screenHeight / 4, buttonSize, buttonSize);
        userButton.addActionListener(this);
        mainPanel.add(userButton);

        // Creating the Admin Button (Circular)
        adminButton = new CircularButton("Admin");
        adminButton.setBounds(buttonX, screenHeight / 2, buttonSize, buttonSize);
        adminButton.addActionListener(this);
        mainPanel.add(adminButton);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == userButton) {
            setVisible(false);  // Hide the front page
            new LoginPage();     // Open registration page for Customer
        } else if (ae.getSource() == adminButton) {
            setVisible(false);  // Hide the front page
            new LoginPage();     // Open registration page for Admin
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new Front();
        });
    }
}

// Custom CircularButton class
class CircularButton extends JButton {
    public CircularButton(String text) {
        super(text);
        setFont(new Font("Tahoma", Font.BOLD, 18));
        setForeground(Color.WHITE);
        setBackground(Color.BLACK);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (getModel().isArmed()) {
            g.setColor(Color.DARK_GRAY);
        } else {
            g.setColor(getBackground());
        }
        g.fillOval(0, 0, getWidth(), getHeight());
        g.setColor(Color.WHITE);
        g.setFont(getFont());
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = (getHeight() + fm.getAscent()) / 2 - 4;
        g.drawString(getText(), x, y);
    }

    @Override
    protected void paintBorder(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
    }

    @Override
    public boolean contains(int x, int y) {
        int radius = Math.min(getWidth(), getHeight()) / 2;
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        return Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) <= Math.pow(radius, 2);
    }
}


