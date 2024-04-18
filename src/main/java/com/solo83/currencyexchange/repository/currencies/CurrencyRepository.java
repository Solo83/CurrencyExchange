package com.solo83.currencyexchange.repository.currencies;

import com.solo83.currencyexchange.utils.RecordNotFoundException;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CurrencyRepository<Currency> {
       List<Currency> getAll() throws SQLException;
       Optional<Currency> get(String code) throws SQLException, RecordNotFoundException;
       Optional<Currency> create(Currency entity) throws SQLException, RecordNotFoundException;

}
