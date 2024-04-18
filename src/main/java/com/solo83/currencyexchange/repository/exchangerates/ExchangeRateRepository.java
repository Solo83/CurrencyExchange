package com.solo83.currencyexchange.repository.exchangerates;

import com.solo83.currencyexchange.utils.RecordNotFoundException;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository <ExchangeRates>{
    List<ExchangeRates> getAll() throws SQLException;
    Optional<ExchangeRate> get(String baseCode, String targetCode) throws SQLException, RecordNotFoundException;
    Optional<ExchangeRate> update(String baseCode, String targetCode, BigDecimal rate) throws SQLException, RecordNotFoundException;
    Optional<ExchangeRate> create(ExchangeRate entity) throws SQLException;

}
