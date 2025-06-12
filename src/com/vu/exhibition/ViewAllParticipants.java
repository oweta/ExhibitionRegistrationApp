package com.vu.exhibition;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileOutputStream;
import java.sql.*;
import java.util.Vector;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ViewAllParticipants extends JFrame {

    public ViewAllParticipants() {
        setTitle("All Registered Participants");
        setSize(1000, 500);
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
        table.setAutoCreateRowSorter(true);

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel topPanel = new JPanel(new BorderLayout());

        // Filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Search:"));
        JTextField filterField = new JTextField(25);
        filterPanel.add(filterField);
        topPanel.add(filterPanel, BorderLayout.WEST);

        filterField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { newFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { newFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { newFilter(); }

            private void newFilter() {
                String text = filterField.getText();
                if (text.trim().isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        // Export Button
        JButton exportButton = new JButton("Export to Excel");
        exportButton.addActionListener(e -> exportToExcel(model));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(exportButton);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Layout
        setLayout(new BorderLayout(10, 10));
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void exportToExcel(DefaultTableModel model) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Participants");

            // Header
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < model.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(model.getColumnName(i));
            }

            // Data
            for (int rowIdx = 0; rowIdx < model.getRowCount(); rowIdx++) {
                Row row = sheet.createRow(rowIdx + 1);
                for (int colIdx = 0; colIdx < model.getColumnCount(); colIdx++) {
                    Object value = model.getValueAt(rowIdx, colIdx);
                    row.createCell(colIdx).setCellValue(value != null ? value.toString() : "");
                }
            }

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save Excel File");
            int userSelection = chooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                try (FileOutputStream fos = new FileOutputStream(chooser.getSelectedFile() + ".xlsx")) {
                    workbook.write(fos);
                    JOptionPane.showMessageDialog(this, "Data exported successfully.");
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
