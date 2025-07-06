package firesaftyproject;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.SpinnerDateModel;

public class signup extends JFrame implements ActionListener {
    JButton back, create, generateOtpBtn, verifyOtpBtn;
    Choice accountType, gender;
    JTextField meter, username, firstName, lastName, password, aadharNo, otpField, mobileNo;
    JTextArea addressArea;
    JSpinner dobSpinner;
    private JLabel lblGender, lblDOB, lblAadhar, lblAddress, lblMobile;
    private JScrollPane addressScrollPane;

    String generatedOtp = "";
    boolean isOtpVerified = false;

    signup() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocation(0, 0);
        getContentPane().setBackground(Color.white);
        setLayout(null);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();
        int centerX = (screenWidth - 800) / 2;
        int startX = centerX + 50;
        int labelWidth = 150;
        int fieldWidth = 200;
        int height = 30;
        int gap = 40;

        JPanel panel = new JPanel();
        panel.setBounds(50, 50, 1200, 650);
        panel.setBorder(new TitledBorder(new LineBorder(new Color(249, 194, 56), 4),
                "Create Account", TitledBorder.LEADING, TitledBorder.TOP,
                new Font("Tahoma", Font.BOLD, 20), new Color(249, 194, 56)));
        panel.setBackground(Color.white);
        panel.setLayout(null);
        add(panel);

        JLabel heading = new JLabel("Create Account");
        heading.setBounds(150, 50, 200, height);
        heading.setForeground(Color.GRAY);
        heading.setFont(new Font("Tahoma", Font.BOLD, 20));
        panel.add(heading);

        accountType = new Choice();
        accountType.add("Admin");
        accountType.add("Customer");
        accountType.setBounds(350, 60, 200, 90);
        panel.add(accountType);

        JLabel lblFirstName = new JLabel("First Name");
        lblFirstName.setBounds(150, 100, 200, height);
        lblFirstName.setForeground(Color.GRAY);
        lblFirstName.setFont(new Font("Tahoma", Font.BOLD, 20));
        panel.add(lblFirstName);

        firstName = new JTextField();
        firstName.setBounds(350, 100, fieldWidth, height);
        panel.add(firstName);

        JLabel lblLastName = new JLabel("Last Name");
        lblLastName.setBounds(150, 150, 200, height);
        lblLastName.setForeground(Color.GRAY);
        lblLastName.setFont(new Font("Tahoma", Font.BOLD, 20));
        panel.add(lblLastName);

        lastName = new JTextField();
        lastName.setBounds(350, 150, fieldWidth, height);
        panel.add(lastName);

        lblGender = new JLabel("Gender");
        lblGender.setBounds(150, 200, 200, height);
        lblGender.setForeground(Color.GRAY);
        lblGender.setFont(new Font("Tahoma", Font.BOLD, 20));
        panel.add(lblGender);

        gender = new Choice();
        gender.add("Male");
        gender.add("Female");
        gender.add("Other");
        gender.setBounds(350, 200, fieldWidth, height);
        panel.add(gender);

        lblDOB = new JLabel("Date of Birth");
        lblDOB.setBounds(150, 250, 200, height);
        lblDOB.setForeground(Color.GRAY);
        lblDOB.setFont(new Font("Tahoma", Font.BOLD, 20));
        panel.add(lblDOB);

        SpinnerDateModel dateModel = new SpinnerDateModel();
        dobSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dobSpinner, "dd/MM/yyyy");
        dobSpinner.setEditor(dateEditor);
        dobSpinner.setBounds(350, 250, fieldWidth, height);
        panel.add(dobSpinner);

        // Mobile number
        lblMobile = new JLabel("Mobile Number");
        lblMobile.setBounds(150, 300, 200, height);
        lblMobile.setForeground(Color.GRAY);
        lblMobile.setFont(new Font("Tahoma", Font.BOLD, 20));
        panel.add(lblMobile);

        mobileNo = new JTextField();
        mobileNo.setBounds(350, 300, fieldWidth, height);
        panel.add(mobileNo);

        // Aadhaar Number
        lblAadhar = new JLabel("Aadhar Number");
        lblAadhar.setBounds(150, 340, 200, height);
        lblAadhar.setForeground(Color.GRAY);
        lblAadhar.setFont(new Font("Tahoma", Font.BOLD, 20));
        panel.add(lblAadhar);

        aadharNo = new JTextField();
        aadharNo.setBounds(350, 340, fieldWidth, height);
        panel.add(aadharNo);

        generateOtpBtn = new JButton("Generate OTP");
        generateOtpBtn.setBounds(570, 340, 140, height);
        generateOtpBtn.setBackground(new Color(249, 194, 56));
        generateOtpBtn.setForeground(Color.BLACK);
        generateOtpBtn.setEnabled(false);
        generateOtpBtn.addActionListener(this);
        panel.add(generateOtpBtn);

        aadharNo.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { validateAadhar(); }
            public void removeUpdate(DocumentEvent e) { validateAadhar(); }
            public void changedUpdate(DocumentEvent e) { validateAadhar(); }

            private void validateAadhar() {
                String text = aadharNo.getText();
                generateOtpBtn.setEnabled(text.matches("\\d{12}"));
            }
        });

        otpField = new JTextField();
        otpField.setBounds(350, 380, fieldWidth, height);
        otpField.setVisible(false);
        panel.add(otpField);

        verifyOtpBtn = new JButton("Verify OTP");
        verifyOtpBtn.setBounds(570, 380, 140, height);
        verifyOtpBtn.setBackground(new Color(249, 194, 56));
        verifyOtpBtn.setForeground(Color.BLACK);
        verifyOtpBtn.setVisible(false);
        verifyOtpBtn.addActionListener(this);
        panel.add(verifyOtpBtn);

        lblAddress = new JLabel("Address");
        lblAddress.setBounds(150, 420, 200, height);
        lblAddress.setForeground(Color.GRAY);
        lblAddress.setFont(new Font("Tahoma", Font.BOLD, 20));
        panel.add(lblAddress);

        addressArea = new JTextArea();
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        addressScrollPane = new JScrollPane(addressArea);
        addressScrollPane.setBounds(350, 420, fieldWidth, height * 2);
        panel.add(addressScrollPane);

        JLabel lblusername = new JLabel("Username");
        lblusername.setBounds(150, 500, 200, height);
        lblusername.setForeground(Color.GRAY);
        lblusername.setFont(new Font("Tahoma", Font.BOLD, 20));
        panel.add(lblusername);

        username = new JTextField();
        username.setBounds(350, 500, fieldWidth, height);
        panel.add(username);

        JLabel lblpassword = new JLabel("Password");
        lblpassword.setBounds(150, 550, 200, height);
        lblpassword.setForeground(Color.GRAY);
        lblpassword.setFont(new Font("Tahoma", Font.BOLD, 20));
        panel.add(lblpassword);

        password = new JTextField();
        password.setBounds(350, 550, fieldWidth, height);
        panel.add(password);

        create = new JButton("Create");
        create.setBounds(startX + 50, 50 + gap * 14, 120, height);
        create.setBackground(Color.white);
        create.setForeground(Color.black);
        create.addActionListener(this);
        panel.add(create);

        back = new JButton("Back");
        back.setBounds(startX + 200, 50 + gap * 14, 120, height);
        back.setBackground(Color.white);
        back.setForeground(Color.black);
        back.addActionListener(this);
        panel.add(back);

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/New1.png"));
        Image i2 = i1.getImage().getScaledInstance(500, 500, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel image = new JLabel(i3);
        image.setBounds(startX + labelWidth + fieldWidth + 50, 50, 500, 500);
        panel.add(image);

        accountType.addItemListener(ae -> {
            String user = accountType.getSelectedItem();
            boolean isCustomer = user.equals("Customer");

            lblGender.setVisible(isCustomer);
            gender.setVisible(isCustomer);
            lblDOB.setVisible(isCustomer);
            dobSpinner.setVisible(isCustomer);
            lblAadhar.setVisible(isCustomer);
            aadharNo.setVisible(isCustomer);
            lblAddress.setVisible(isCustomer);
            addressScrollPane.setVisible(isCustomer);
            lblMobile.setVisible(isCustomer);
            mobileNo.setVisible(isCustomer);
            generateOtpBtn.setVisible(isCustomer);
            otpField.setVisible(false);
            verifyOtpBtn.setVisible(false);
        });

        accountType.select("Admin");
        lblGender.setVisible(false);
        gender.setVisible(false);
        lblDOB.setVisible(false);
        dobSpinner.setVisible(false);
        lblAadhar.setVisible(false);
        aadharNo.setVisible(false);
        lblAddress.setVisible(false);
        addressScrollPane.setVisible(false);
        lblMobile.setVisible(false);
        mobileNo.setVisible(false);
        generateOtpBtn.setVisible(false);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == generateOtpBtn) {
            generatedOtp = String.format("%06d", (int) (Math.random() * 1000000));

            // MOCK OTP SIMULATION
            System.out.println("=== SMS ===");
            System.out.println("Sending OTP to +91 " + mobileNo.getText());
            System.out.println("OTP: " + generatedOtp);
            System.out.println("====================");

            JOptionPane.showMessageDialog(null, "OTP has been sent to your mobile number (simulated)");
            otpField.setVisible(true);
            verifyOtpBtn.setVisible(true);

        } else if (ae.getSource() == verifyOtpBtn) {
            String enteredOtp = otpField.getText();
            if (enteredOtp.equals(generatedOtp)) {
                JOptionPane.showMessageDialog(null, "OTP Verified Successfully");
                isOtpVerified = true;
            } else {
                JOptionPane.showMessageDialog(null, "Invalid OTP. Please try again.");
                isOtpVerified = false;
            }

        } else if (ae.getSource() == create) {
            String atype = accountType.getSelectedItem();
            String susername = username.getText();
            String spassword = password.getText();
            String sfirstName = firstName.getText();
            String slastName = lastName.getText();

            try {
                if (atype.equals("Admin")) {
                    if (susername.isEmpty() || spassword.isEmpty() || sfirstName.isEmpty() || slastName.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Please fill all fields");
                        return;
                    }
                } else {
                    String sgender = gender.getSelectedItem();
                    String sdob = dobSpinner.getValue().toString();
                    String saadhar = aadharNo.getText();
                    String saddress = addressArea.getText();
                    String smobile = mobileNo.getText();

                    if (susername.isEmpty() || spassword.isEmpty() || sfirstName.isEmpty() || slastName.isEmpty() ||
                            saadhar.isEmpty() || saddress.isEmpty() || smobile.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Please fill all fields");
                        return;
                    }

                    if (!saadhar.matches("\\d{12}")) {
                        JOptionPane.showMessageDialog(null, "Aadhar number must be 12 digits");
                        return;
                    }

                    if (!smobile.matches("\\d{10}")) {
                        JOptionPane.showMessageDialog(null, "Mobile number must be 10 digits");
                        return;
                    }

                    if (!isOtpVerified) {
                        JOptionPane.showMessageDialog(null, "Please verify OTP before creating account");
                        return;
                    }
                }

                Conn c = new Conn();
                String checkQuery = "SELECT username FROM signup WHERE username = '" + susername + "'";
                ResultSet rs = c.s.executeQuery(checkQuery);

                if (rs.next()) {
                    JOptionPane.showMessageDialog(null, "Username already exists. Please choose a different username.");
                    return;
                }

                String query;
                if (atype.equals("Admin")) {
                    query = "INSERT INTO signup (account_type, username, password, first_name, last_name) VALUES " +
                            "('" + atype + "', '" + susername + "', '" + spassword + "', '" + sfirstName + "', '" + slastName + "')";
                } else {
                    java.util.Date utilDate = (java.util.Date) dobSpinner.getValue();
                    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

                    query = "INSERT INTO signup (account_type, username, password, first_name, last_name, gender, date_of_birth, aadhar_number, address, mobile_number) VALUES " +
                            "('" + atype + "', '" + susername + "', '" + spassword + "', '" + sfirstName + "', '" + slastName + "', '" +
                            gender.getSelectedItem() + "', '" + sqlDate + "', '" + aadharNo.getText() + "', '" + addressArea.getText() + "', '" + mobileNo.getText() + "')";
                }

                int result = c.s.executeUpdate(query);
                if (result > 0) {
                    JOptionPane.showMessageDialog(null, "Account Created Successfully");
                    setVisible(false);
                    new LoginPage();
                } else {
                    JOptionPane.showMessageDialog(null, "Error creating account. Please try again.");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (ae.getSource() == back) {
            setVisible(false);
            new LoginPage();
        }
    }

    public static void main(String[] args) {
        new signup();
    }
}