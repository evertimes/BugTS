package com.evertimes.bugts.model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionPool {
    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                //String connectionUrl = "jdbc:sqlserver://Andrey\\MSSQLSERVER;database=BugTracker;IntegratedSecurity=true";
    String connectionUrl = "jdbc:sqlserver://localhost;database=BugTracker;user=SA;password=Password1";
                connection = DriverManager.getConnection(connectionUrl);
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
}
