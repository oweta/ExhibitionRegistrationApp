package com.vu.exhibition;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
        columnNames.add("Registration ID");
        columnNames.add("Student Name");
        columnNames.add("Faculty");
        columnNames.add("Project Title");
        columnNames.add("Contact Number");
        columnNames.add("Email Address");
        columnNames.add("Photo"); // We'll show photo here

        Vector<Vector<Object>> data = new Vector<>();

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

                String imagePath = rs.getString("ImagePath");
                ImageIcon icon;
                try {
                    ImageIcon original = new ImageIcon(imagePath);
                    Image scaled = original.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                    icon = new ImageIcon(scaled);
                } catch (Exception e) {
                    icon = new ImageIcon(); // Empty if path is invalid
                }

                row.add(icon);
                data.add(row);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching data: " + ex.getMessage());
            ex.printStackTrace();
        }

        DefaultTableModel model = new DefaultTableModel() {
            public Class<?> getColumnClass(int column) {
                if (column == 6)
                    return ImageIcon.class; // Photo column
                return String.class;
            }

            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (String col : columnNames) {
            model.addColumn(col);
        }

        for (Vector<Object> row : data) {
            model.addRow(row.toArray());
        }

        JTable table = new JTable(model);
        table.setRowHeight(55); // So image fits nicely
        table.setAutoCreateRowSorter(true);

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);

        // Top panel with search and export
        JPanel topPanel = new JPanel(new BorderLayout());

        // Search box
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Search:"));
        JTextField filterField = new JTextField(25);
        filterPanel.add(filterField);
        topPanel.add(filterPanel, BorderLayout.WEST);

        // Export button
        JButton exportButton = new JButton("Export file");
        exportButton.addActionListener(e -> exportToExcel(model));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(exportButton);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Filter logic
        filterField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                newFilter();
            }

            public void removeUpdate(DocumentEvent e) {
                newFilter();
            }

            public void changedUpdate(DocumentEvent e) {
                newFilter();
            }

            private void newFilter() {
                String text = filterField.getText();
                if (text.trim().isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        // Layout setup
        setLayout(new BorderLayout(10, 10));
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void exportToExcel(DefaultTableModel model) {
        String[] options = { "Excel (.xlsx)", "CSV (.csv)", "PDF (.pdf)", "Text (.txt)" };
        String selectedOption = (String) JOptionPane.showInputDialog(
                this,
                "Choose export format:",
                "Export",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (selectedOption == null)
            return;

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save File");

        int userSelection = chooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION)
            return;

        String path = chooser.getSelectedFile().getAbsolutePath();

        try {
            if (selectedOption.contains("Excel")) {
                if (!path.endsWith(".xlsx"))
                    path += ".xlsx";

                XSSFWorkbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Participants");

                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < model.getColumnCount(); i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(model.getColumnName(i));
                }

                for (int rowIdx = 0; rowIdx < model.getRowCount(); rowIdx++) {
                    Row row = sheet.createRow(rowIdx + 1);
                    for (int colIdx = 0; colIdx < model.getColumnCount(); colIdx++) {
                        Object value = model.getValueAt(rowIdx, colIdx);
                        if (value instanceof ImageIcon) {
                            row.createCell(colIdx).setCellValue("[Image]");
                        } else {
                            row.createCell(colIdx).setCellValue(value != null ? value.toString() : "");
                        }
                    }
                }

                try (FileOutputStream fos = new FileOutputStream(path)) {
                    workbook.write(fos);
                    workbook.close();
                }

            } else if (selectedOption.contains("CSV")) {
                if (!path.endsWith(".csv"))
                    path += ".csv";

                try (FileOutputStream fos = new FileOutputStream(path)) {
                    StringBuilder sb = new StringBuilder();

                    for (int i = 0; i < model.getColumnCount(); i++) {
                        sb.append(model.getColumnName(i)).append(",");
                    }
                    sb.append("\n");

                    for (int row = 0; row < model.getRowCount(); row++) {
                        for (int col = 0; col < model.getColumnCount(); col++) {
                            Object val = model.getValueAt(row, col);
                            if (val instanceof ImageIcon) {
                                sb.append("[Image]");
                            } else {
                                sb.append(val != null ? val.toString() : "");
                            }
                            sb.append(",");
                        }
                        sb.append("\n");
                    }

                    fos.write(sb.toString().getBytes());
                }

            } else {
                JOptionPane.showMessageDialog(this, selectedOption + " export not yet implemented.");
            }

            JOptionPane.showMessageDialog(this, "Export successful.");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
