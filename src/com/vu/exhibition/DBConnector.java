package com.vu.exhibition;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;

public class DBConnector {

    // Path for database location
    private static final String DB_PATH = "C:/xampp/htdocs/ExhibitionRegistrationApp/database/VUE_Exhibition.accdb";

    public static Connection connect() {
        Connection conn = null;
        try {
            File dbFile = new File(DB_PATH);
            if (!dbFile.exists()) {
                System.out.println("Database file NOT FOUND at: " + DB_PATH);
                return null;
            } else {
                System.out.println("Database file FOUND at: " + DB_PATH);
            }

            String url = "jdbc:ucanaccess://" + DB_PATH;
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to Access DB successful!");
        } catch (SQLException e) {
            System.out.println("Failed to connect to database.");
            e.printStackTrace();
        }
        return conn;
    }

    // For quick standalone test
    public static void main(String[] args) {
        System.out.println("Current working directory: " + System.getProperty("user.dir"));
        connect();
    }
}
