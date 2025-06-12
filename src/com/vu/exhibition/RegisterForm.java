package com.vu.exhibition;

import javax.swing.*;
import java.awt.*;

public class RegistrationForm extends JFrame {

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
        JTextField regIdField = new JTextField();
        formPanel.add(regIdField);

        formPanel.add(new JLabel("Student Name:"));
        JTextField nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Faculty:"));
        JTextField facultyField = new JTextField();
        formPanel.add(facultyField);

        formPanel.add(new JLabel("Project Title:"));
        JTextField titleField = new JTextField();
        formPanel.add(titleField);

        formPanel.add(new JLabel("Contact Number:"));
        JTextField contactField = new JTextField();
        formPanel.add(contactField);

        formPanel.add(new JLabel("Email Address:"));
        JTextField emailField = new JTextField();
        formPanel.add(emailField);

        formPanel.add(new JLabel("Image Path:"));
        JTextField imagePathField = new JTextField();
        formPanel.add(imagePathField);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(new JButton("Register"));
        buttonPanel.add(new JButton("Search"));
        buttonPanel.add(new JButton("Update"));
        buttonPanel.add(new JButton("Delete"));
        buttonPanel.add(new JButton("Clear"));
        buttonPanel.add(new JButton("Exit"));

        // Frame layout
        setLayout(new BorderLayout(10, 10));
        add(titleLabel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegistrationForm().setVisible(true));
    }
}
