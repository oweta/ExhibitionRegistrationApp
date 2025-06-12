package com.vu.exhibition;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import com.vu.exhibition.DBConnector;

public class RegistrationForm extends JFrame {

    private JTextField regIdField, nameField, facultyField, titleField, contactField, emailField, imagePathField;

    public RegistrationForm() {
        setTitle("Exhibition Registration");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        // Create form components
        JLabel titleLabel = new JLabel("Innovation Exhibition Registration", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));

        formPanel.add(new JLabel("Registration ID:"));
        regIdField = new JTextField();
        formPanel.add(regIdField);

        formPanel.add(new JLabel("Student Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Faculty:"));
        facultyField = new JTextField();
        formPanel.add(facultyField);

        formPanel.add(new JLabel("Project Title:"));
        titleField = new JTextField();
        formPanel.add(titleField);

        formPanel.add(new JLabel("Contact Number:"));
        contactField = new JTextField();
        formPanel.add(contactField);

        formPanel.add(new JLabel("Email Address:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        formPanel.add(new JLabel("Image Path:"));
        imagePathField = new JTextField();
        formPanel.add(imagePathField);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> registerParticipant());
        buttonPanel.add(registerButton);

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchParticipant());
        buttonPanel.add(searchButton);

        buttonPanel.add(new JButton("Update"));
        buttonPanel.add(new JButton("Delete"));

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearForm());
        buttonPanel.add(clearButton);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
        buttonPanel.add(exitButton);

        // Frame layout
        setLayout(new BorderLayout(10, 10));
        add(titleLabel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void registerParticipant() {
        String regId = regIdField.getText().trim();
        String name = nameField.getText().trim();
        String faculty = facultyField.getText().trim();
        String title = titleField.getText().trim();
        String contact = contactField.getText().trim();
        String email = emailField.getText().trim();
        String imagePath = imagePathField.getText().trim();

        // Basic validation
        if (regId.isEmpty() || name.isEmpty() || faculty.isEmpty() || title.isEmpty()
                || contact.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$")) {
            JOptionPane.showMessageDialog(this, "Invalid email format.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO Participants (RegistrationID, StudentName, Faculty, ProjectTitle, ContactNumber, EmailAddress, ImagePath) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, regId);
            pstmt.setString(2, name);
            pstmt.setString(3, faculty);
            pstmt.setString(4, title);
            pstmt.setString(5, contact);
            pstmt.setString(6, email);
            pstmt.setString(7, imagePath);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Participant registered successfully!");
                clearForm();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error registering participant: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void searchParticipant() {
    String input = JOptionPane.showInputDialog(this, "Enter Registration ID or Student Name to search:");

    if (input == null || input.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Search cancelled or empty input.");
        return;
    }

    input = input.trim();

    try (Connection conn = DBConnector.connect()) {
        String query;
        PreparedStatement stmt;

        if (input.matches("\\d+")) { // all digits = assume ID
            query = "SELECT * FROM Participants WHERE RegistrationID = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, input);
        } else {
            query = "SELECT * FROM Participants WHERE StudentName LIKE ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + input + "%");
        }

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            regIdField.setText(rs.getString("RegistrationID"));
            nameField.setText(rs.getString("StudentName"));
            facultyField.setText(rs.getString("Faculty"));
            titleField.setText(rs.getString("ProjectTitle"));
            contactField.setText(rs.getString("ContactNumber"));
            emailField.setText(rs.getString("EmailAddress"));
            imagePathField.setText(rs.getString("ImagePath"));

            JOptionPane.showMessageDialog(this, "Participant found and loaded.");
        } else {
            JOptionPane.showMessageDialog(this, "No participant found.");
        }

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error during search: " + ex.getMessage());
        ex.printStackTrace();
    }
}


    private void clearForm() {
        regIdField.setText("");
        nameField.setText("");
        facultyField.setText("");
        titleField.setText("");
        contactField.setText("");
        emailField.setText("");
        imagePathField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegistrationForm().setVisible(true));
    }
}
