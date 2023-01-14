package com.evertimes.bugts.model.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Scanner;

public class ConnectionPool {
    private static Connection connection;
    private static ResourceBundle bundle = ResourceBundle.getBundle("connection");
    public static String connectionString;
    public static boolean isReady = true;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                connection = DriverManager.getConnection(connectionString);
                isReady=true;
            } catch (ClassNotFoundException | SQLException e) {
                isReady = false;

            }
        }
        return connection;
    }
}
