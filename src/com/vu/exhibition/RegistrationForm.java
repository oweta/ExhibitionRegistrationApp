package com.vu.exhibition;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegistrationForm extends JFrame {

    private JTextField regIdField, nameField, facultyField, titleField, contactField, emailField, imagePathField;

    public RegistrationForm() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }

        setTitle("Exhibition Registration");
        setSize(650, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel titleLabel = new JLabel("Innovation Exhibition Registration", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(33, 150, 243));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 12, 12));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        formPanel.setBackground(Color.WHITE);

        regIdField = new JTextField();
        nameField = new JTextField();
        facultyField = new JTextField();
        titleField = new JTextField();
        contactField = new JTextField();
        emailField = new JTextField();
        imagePathField = new JTextField();

        addFormRow(formPanel, "Registration ID:", regIdField);
        addFormRow(formPanel, "Student Name:", nameField);
        addFormRow(formPanel, "Faculty:", facultyField);
        addFormRow(formPanel, "Project Title:", titleField);
        addFormRow(formPanel, "Contact Number:", contactField);
        addFormRow(formPanel, "Email Address:", emailField);
        addFormRow(formPanel, "Image Path:", imagePathField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        buttonPanel.add(styledButton("View All", e -> new ViewAllParticipants()));
        buttonPanel.add(styledButton("Register", e -> registerParticipant()));
        buttonPanel.add(styledButton("Search", e -> searchParticipant()));
        buttonPanel.add(styledButton("Update", e -> updateParticipant()));
        buttonPanel.add(styledButton("Delete", e -> deleteParticipant()));
        buttonPanel.add(styledButton("Clear", e -> clearForm()));
        buttonPanel.add(styledButton("Exit", e -> System.exit(0)));

        setLayout(new BorderLayout(10, 10));
        add(titleLabel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addFormRow(JPanel panel, String labelText, JTextField field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        panel.add(label);
        panel.add(field);
    }

    private JButton styledButton(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(new Color(33, 150, 243));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        btn.addActionListener(action);
        return btn;
    }

    private Connection connect() throws SQLException {
        return DBConnector.connect();
    }

    private void registerParticipant() {
    if (regIdField.getText().isBlank() || nameField.getText().isBlank()) {
        JOptionPane.showMessageDialog(this, "Please fill in at least Registration ID and Name.");
        return;
    }

    String sql = "INSERT INTO Participants (RegistrationID, StudentName, Faculty, ProjectTitle, ContactNumber, EmailAddress, ImagePath) VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = connect(); PreparedStatement pst = conn.prepareStatement(sql)) {
        pst.setString(1, regIdField.getText());
        pst.setString(2, nameField.getText());
        pst.setString(3, facultyField.getText());
        pst.setString(4, titleField.getText());
        pst.setString(5, contactField.getText());
        pst.setString(6, emailField.getText());
        pst.setString(7, imagePathField.getText());

        int inserted = pst.executeUpdate();
        if (inserted > 0) {
            JOptionPane.showMessageDialog(this, "Participant Registered!");
            clearForm();
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Registration Failed: " + ex.getMessage());
        ex.printStackTrace();
    }
}

private void searchParticipant() {
    String[] options = {"Registration ID", "Student Name", "Faculty"};
    String choice = (String) JOptionPane.showInputDialog(
            this,
            "Search by:",
            "Search Option",
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);

    if (choice == null) return; // user cancelled

    String value = JOptionPane.showInputDialog(this, "Enter " + choice + ":");
    if (value == null || value.isBlank()) {
        JOptionPane.showMessageDialog(this, "Search value cannot be empty.");
        return;
    }

    String sql = null;
    switch (choice) {
        case "Registration ID":
            sql = "SELECT * FROM Participants WHERE RegistrationID = ?";
            break;
        case "Student Name":
            sql = "SELECT * FROM Participants WHERE StudentName LIKE ?";
            value = "%" + value + "%";
            break;
        case "Faculty":
            sql = "SELECT * FROM Participants WHERE Faculty LIKE ?";
            value = "%" + value + "%";
            break;
    }

    try (Connection conn = connect(); PreparedStatement pst = conn.prepareStatement(sql)) {
        pst.setString(1, value);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            regIdField.setText(rs.getString("RegistrationID"));
            nameField.setText(rs.getString("StudentName"));
            facultyField.setText(rs.getString("Faculty"));
            titleField.setText(rs.getString("ProjectTitle"));
            contactField.setText(rs.getString("ContactNumber"));
            emailField.setText(rs.getString("EmailAddress"));
            imagePathField.setText(rs.getString("ImagePath"));
        } else {
            JOptionPane.showMessageDialog(this, "Participant not found.");
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Search Failed: " + ex.getMessage());
        ex.printStackTrace();
    }
}
    private void updateParticipant() {
    if (regIdField.getText().isBlank()) {
        JOptionPane.showMessageDialog(this, "Please enter Registration ID to update.");
        return;
    }

    String sql = "UPDATE Participants SET StudentName=?, Faculty=?, ProjectTitle=?, ContactNumber=?, EmailAddress=?, ImagePath=? WHERE RegistrationID=?";

    try (Connection conn = connect(); PreparedStatement pst = conn.prepareStatement(sql)) {
        pst.setString(1, nameField.getText());
        pst.setString(2, facultyField.getText());
        pst.setString(3, titleField.getText());
        pst.setString(4, contactField.getText());
        pst.setString(5, emailField.getText());
        pst.setString(6, imagePathField.getText());
        pst.setString(7, regIdField.getText());

        int updated = pst.executeUpdate();
        if (updated > 0) {
            JOptionPane.showMessageDialog(this, "Participant Updated!");
        } else {
            JOptionPane.showMessageDialog(this, "Update Failed: ID not found.");
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Update Failed: " + ex.getMessage());
        ex.printStackTrace();
    }
}

    private void deleteParticipant() {
    if (regIdField.getText().isBlank()) {
        JOptionPane.showMessageDialog(this, "Please enter Registration ID to delete.");
        return;
    }

    String sql = "DELETE FROM Participants WHERE RegistrationID = ?";

    try (Connection conn = connect(); PreparedStatement pst = conn.prepareStatement(sql)) {
        pst.setString(1, regIdField.getText());

        int deleted = pst.executeUpdate();
        if (deleted > 0) {
            JOptionPane.showMessageDialog(this, "Participant Deleted!");
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Delete Failed: ID not found.");
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Delete Failed: " + ex.getMessage());
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
