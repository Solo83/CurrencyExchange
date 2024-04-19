package com.solo83.currencyexchange.repository.exchangerates;

import com.solo83.currencyexchange.repository.DBConnector;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;

import com.solo83.currencyexchange.repository.currencies.Currency;
import com.solo83.currencyexchange.utils.exceptions.CustomDbException;
import com.solo83.currencyexchange.utils.exceptions.RecordAlreadyExistException;
import org.sqlite.SQLiteException;

import java.util.List;
import java.util.Optional;


public class ExchangeRatesRepositoryImpl implements ExchangeRateRepository {
    @Override
    public List<ExchangeRate> getAll() throws CustomDbException {

        List<ExchangeRate> exchangeRates = new ArrayList<>();

        String sql = "SELECT ExchangeRates.id, C1.id,C1.FullName,C1.Code,C1.Sign,C2.id,C2.FullName,C2.Code,C2.Sign,ExchangeRates.rate from ExchangeRates join main.Currencies C1 on C1.ID = ExchangeRates.BaseCurrencyId join main.Currencies C2 on C2.ID = ExchangeRates.TargetCurrencyId";

        try (Connection connection = DBConnector.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                exchangeRates.add(createExchangeRate(resultSet));
            }
        } catch (SQLException e) {
            throw new CustomDbException(e.getMessage());
        }
        return exchangeRates;
    }

    @Override
    public Optional<ExchangeRate> get(String baseCode, String targetCode) throws CustomDbException {
        Optional<ExchangeRate> exchangeRate = Optional.empty();;

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement statement = connection
                     .prepareStatement("select ExchangeRates.id,C1.id,C1.FullName,C1.code,C1.sign,C2.id,C2.FullName,C2.code,C2.sign,Rate from ExchangeRates join  Currencies C1 on C1.id = ExchangeRates.BaseCurrencyId join  Currencies C2 on C2.id = ExchangeRates.TargetCurrencyId where C1.Code = ? and C2.Code = ?")) {
            statement.setString(1, baseCode);
            statement.setString(2, targetCode);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    exchangeRate = Optional.of(createExchangeRate(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new CustomDbException(e.getMessage());
        }
        return exchangeRate;
    }

    @Override
    public Optional<ExchangeRate> update(String baseCode, String targetCode, BigDecimal rate) throws CustomDbException {
        Optional<ExchangeRate> exchangeRate;

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement statement = connection
                     .prepareStatement("UPDATE ExchangeRates SET Rate = ? WHERE exists(select 1 from Currencies C1 where C1.ID = ExchangeRates.BaseCurrencyId and Code = ?) AND exists(select 1 from Currencies C2 where C2.ID = ExchangeRates.TargetCurrencyId and Code = ? )")) {

            statement.setBigDecimal(1, rate);
            statement.setString(2, baseCode);
            statement.setString(3, targetCode);
            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                exchangeRate = get(baseCode, targetCode);
            } else {
                exchangeRate = Optional.empty();
            }

        } catch (SQLException e) {
            throw new CustomDbException(e.getMessage());
        }

        return exchangeRate;
    }

    @Override
    public Optional<ExchangeRate> create(ExchangeRate entity) throws CustomDbException, RecordAlreadyExistException {
        Optional<ExchangeRate> exchangeRate = Optional.empty();

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement statement = connection
                     .prepareStatement("INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId,Rate) VALUES ((SELECT id from Currencies WHERE Code=?),  (SELECT id from Currencies WHERE Code=? ) , ?) ")) {

            statement.setString(1, entity.getBaseCurrency().getCode());
            statement.setString(2, entity.getTargetCurrency().getCode());
            statement.setBigDecimal(3, entity.getRate());
            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    exchangeRate = get(entity.getBaseCurrency().getCode(), entity.getTargetCurrency().getCode());
                }
            }

        } catch (SQLiteException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                throw new RecordAlreadyExistException("Pair '" + entity.getBaseCurrency().getCode() + entity.getTargetCurrency().getCode() + "' is already exist in Database");
            } else {
                throw new CustomDbException(e.getMessage());
            }
        } catch (SQLException e) {
            throw new CustomDbException(e.getMessage());
        }
        return exchangeRate;
    }

    private ExchangeRate createExchangeRate(ResultSet resultSet) throws SQLException {

        Integer id = resultSet.getInt(1);

        Integer baseCurrencyId = resultSet.getInt(2);
        String baseCurrencyName = resultSet.getString(3);
        String baseCurrencyCode = resultSet.getString(4);
        String baseCurrencySign = resultSet.getString(5);

        Currency baseCurrency = new Currency(baseCurrencyId, baseCurrencyName, baseCurrencyCode, baseCurrencySign);

        Integer targetCurrencyId = resultSet.getInt(6);
        String targetCurrencyName = resultSet.getString(7);
        String targetCurrencyCode = resultSet.getString(8);
        String targetCurrencySign = resultSet.getString(9);

        Currency targetCurrency = new Currency(targetCurrencyId, targetCurrencyName, targetCurrencyCode, targetCurrencySign);

        BigDecimal rate = resultSet.getBigDecimal(10);

        return new ExchangeRate(id, baseCurrency, targetCurrency, rate);
    }
}
