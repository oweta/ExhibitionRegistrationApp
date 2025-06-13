package com.vu.exhibition;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegistrationForm extends JFrame {

    private JTextField regIdField, nameField, facultyField, titleField, contactField, emailField, imagePathField;

    public RegistrationForm() {
        // Apply FlatLaf Look & Feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }

        setTitle("Exhibition Registration");
        setSize(650, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        // Title label
        JLabel titleLabel = new JLabel("Innovation Exhibition Registration", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(33, 150, 243));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        // Form panel
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

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));

        buttonPanel.add(styledButton("View All", e -> new ViewAllParticipants()));
        buttonPanel.add(styledButton("Register", e -> registerParticipant()));
        buttonPanel.add(styledButton("Search", e -> searchParticipant()));
        buttonPanel.add(styledButton("Update", e -> updateParticipant()));
        buttonPanel.add(styledButton("Delete", e -> deleteParticipant()));
        buttonPanel.add(styledButton("Clear", e -> clearForm()));
        buttonPanel.add(styledButton("Exit", e -> System.exit(0)));

        // Layout
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

    // Keep all logic methods (register, update, search, delete, clear)
    // These are unchanged from your current code
    // You can copy the registerParticipant(), searchParticipant(), etc. methods as-is here

    private void registerParticipant() {
        // Your existing code remains unchanged
        // ...
    }

    private void searchParticipant() {
        // Your existing code remains unchanged
        // ...
    }

    private void updateParticipant() {
        // Your existing code remains unchanged
        // ...
    }

    private void deleteParticipant() {
        // Your existing code remains unchanged
        // ...
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
