package com.solo83.currencyexchange.repository.currencies;

import com.solo83.currencyexchange.repository.DBConnector;
import com.solo83.currencyexchange.utils.exceptions.CustomDbException;
import com.solo83.currencyexchange.utils.exceptions.RecordAlreadyExistException;
import org.sqlite.SQLiteException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class CurrencyRepositoryImpl implements CurrencyRepository<Currency> {

    @Override
    public List<Currency> getAll() throws CustomDbException {

        List<Currency> currencies = new ArrayList<>();
        String sql = "SELECT * FROM Currencies";

        try (Connection connection = DBConnector.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                currencies.add(createCurrency(resultSet));
            }
        } catch (SQLException e) {
            throw new CustomDbException(e.getMessage());
        }
        return currencies;
    }

    @Override
    public Optional<Currency> get(String code) throws CustomDbException {

        Optional<Currency> currency = Optional.empty();

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement statement = connection
                     .prepareStatement("SELECT * FROM Currencies WHERE Code = ?")) {

            statement.setString(1, code);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    currency = Optional.of(createCurrency(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new CustomDbException(e.getMessage());
        }

        return currency;
    }

    @Override
    public Optional<Currency> create(Currency entity) throws RecordAlreadyExistException, CustomDbException {

        Optional<Currency> currency = Optional.empty();

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement statement = connection
                     .prepareStatement("INSERT INTO Currencies (Code,FullName,Sign) VALUES (?,?,?)")) {

            statement.setString(1, entity.getCode());
            statement.setString(2, entity.getName());
            statement.setString(3, entity.getSign());
            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    currency = get(entity.getCode());
                }
            }

        } catch (SQLiteException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                throw new RecordAlreadyExistException("Currency '" + entity.getCode() + "' is already exist in Database");
            } else {
                throw new CustomDbException(e.getMessage());
            }
        } catch (SQLException e) {
            throw new CustomDbException(e.getMessage());
        }
        return currency;
    }

    private Currency createCurrency(ResultSet resultSet) throws SQLException {
        Integer id = resultSet.getInt(1);
        String code = resultSet.getString(2);
        String name = resultSet.getString(3);
        String sign = resultSet.getString(4);
        return new Currency(id, name, code, sign);
    }
}

