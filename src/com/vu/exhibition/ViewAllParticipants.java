package com.vu.exhibition;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class ViewAllParticipants extends JFrame {

    public ViewAllParticipants() {
        setTitle("All Registered Participants");
        setSize(900, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Vector<String> columnNames = new Vector<>();
        Vector<Vector<Object>> data = new Vector<>();
        Set<String> facultySet = new HashSet<>();

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
                String faculty = rs.getString("Faculty");
                row.add(faculty);
                row.add(rs.getString("ProjectTitle"));
                row.add(rs.getString("ContactNumber"));
                row.add(rs.getString("EmailAddress"));
                row.add(rs.getString("ImagePath"));
                data.add(row);
                facultySet.add(faculty); // collect unique faculties
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching data: " + ex.getMessage());
            ex.printStackTrace();
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);

        // Filter panel components
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Text search field
        filterPanel.add(new JLabel("Search:"));
        JTextField filterField = new JTextField(20);
        filterPanel.add(filterField);

        // Faculty dropdown filter
        filterPanel.add(new JLabel("Filter by Faculty:"));
        JComboBox<String> facultyComboBox = new JComboBox<>();
        facultyComboBox.addItem("All Faculties");
        facultySet.stream().sorted().forEach(facultyComboBox::addItem);
        filterPanel.add(facultyComboBox);

        // Method to apply both filters
        Runnable applyFilters = () -> {
            String text = filterField.getText();
            String selectedFaculty = (String) facultyComboBox.getSelectedItem();

            List<RowFilter<Object, Object>> filters = new ArrayList<>();

            if (text != null && !text.trim().isEmpty()) {
                filters.add(RowFilter.regexFilter("(?i)" + text));
            }

            if (selectedFaculty != null && !"All Faculties".equals(selectedFaculty)) {
                filters.add(RowFilter.regexFilter("^" + Pattern.quote(selectedFaculty) + "$", 2)); // Faculty = column index 2
            }

            RowFilter<Object, Object> combinedFilter = filters.isEmpty() ? null : RowFilter.andFilter(filters);
            sorter.setRowFilter(combinedFilter);
        };

        // Text filter listener
        filterField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilters.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilters.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilters.run(); }
        });

        // ComboBox filter listener
        facultyComboBox.addActionListener(e -> applyFilters.run());

        // Layout
        setLayout(new BorderLayout());
        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }
}
