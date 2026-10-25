
package com.quizforge;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            getConfig("DB_URL", "jdbc:mysql://localhost:3306/quizforge");

    private static final String USER =
            getConfig("DB_USER", "root");

    private static final String PASSWORD =
            getConfig("DB_PASSWORD", "Aman@28");

    private static String getConfig(String name, String defaultValue) {
        String value = System.getenv(name);

        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        return value;
    }

    public static Connection getConnection() throws SQLException {

        if (PASSWORD.isBlank()) {
            throw new SQLException(
                    "Database password is not configured. Set DB_PASSWORD."
            );
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found!", e);
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
