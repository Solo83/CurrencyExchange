package com.solo83.currencyexchange.repository;

import com.solo83.currencyexchange.utils.exceptions.CustomDbException;

import java.sql.Connection;
import java.sql.DriverManager;


public class DBConnector {
    public static Connection getConnection() throws CustomDbException {

        final String DB_URI = "jdbc:sqlite::resource:DataBase.db";

        Connection connection;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(
                    DB_URI);
        }
        catch (Exception e) {
             throw new CustomDbException("Database connection error");
        }

        return connection;
    }


}
