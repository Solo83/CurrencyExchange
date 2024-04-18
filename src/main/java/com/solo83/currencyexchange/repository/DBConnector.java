package com.solo83.currencyexchange.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {
    public static Connection getConnection() throws SQLException {

        final String DB_URI = "jdbc:sqlite::resource:DataBase.db";

        Connection connection;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(
                    DB_URI);
        }
        catch (Exception e) {
             throw new SQLException("Database connection error");
        }

        return connection;
    }


}
