package com.vu.exhibition;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class ViewAllParticipants extends JFrame {

    public ViewAllParticipants() {
        setTitle("All Registered Participants");
        setSize(900, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Vector<String> columnNames = new Vector<>();
        Vector<Vector<Object>> data = new Vector<>();

        columnNames.add("Registration ID");
        columnNames.add("Student Name");
        columnNames.add("Faculty");
        columnNames.add("Project Title");
        columnNames.add("Contact Number");
        columnNames.add("Email Address");
        columnNames.add("Image Path");

        try (Connection conn = DBConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Participants")) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("RegistrationID"));
                row.add(rs.getString("StudentName"));
                row.add(rs.getString("Faculty"));
                row.add(rs.getString("ProjectTitle"));
                row.add(rs.getString("ContactNumber"));
                row.add(rs.getString("EmailAddress"));
                row.add(rs.getString("ImagePath"));
                data.add(row);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching data: " + ex.getMessage());
            ex.printStackTrace();
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true); // enable sorting

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);

        // Add a filter text box
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Search:"));
        JTextField filterField = new JTextField(30);
        filterPanel.add(filterField);

        filterField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { newFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { newFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { newFilter(); }

            private void newFilter() {
                String text = filterField.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        // Layout
        setLayout(new BorderLayout());
        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }
}
