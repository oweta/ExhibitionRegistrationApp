package com.vu.exhibition;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {

    private static final String DB_PATH = "database/VUE_Exhibition.accdb";

    public static Connection connect() {
        Connection conn = null;
        try {
            // Build the full connection string
            String url = "jdbc:ucanaccess://" + DB_PATH;
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to Access DB successful!");
        } catch (SQLException e) {
            System.out.println("Failed to connect to database.");
            e.printStackTrace();
        }
        return conn;
    }

    // Simple test method
    public static void main(String[] args) {
        connect();
    }
}
