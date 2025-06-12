package com.vu.exhibition;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewAllParticipants extends JFrame {

    public ViewAllParticipants() {
        setTitle("All Participants");
        setSize(800, 400);
        setLocationRelativeTo(null);

        String[] columnNames = {
                "RegistrationID", "Student Name", "Faculty",
                "Project Title", "Contact", "Email", "Image Path"
        };

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);

        try (Connection conn = DBConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Participants")) {

            while (rs.next()) {
                Object[] row = {
                        rs.getString("RegistrationID"),
                        rs.getString("StudentName"),
                        rs.getString("Faculty"),
                        rs.getString("ProjectTitle"),
                        rs.getString("ContactNumber"),
                        rs.getString("EmailAddress"),
                        rs.getString("ImagePath")
                };
                model.addRow(row);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load data: " + ex.getMessage());
            ex.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        setVisible(true);
    }
}
