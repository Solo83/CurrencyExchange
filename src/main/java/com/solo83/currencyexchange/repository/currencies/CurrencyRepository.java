package com.solo83.currencyexchange.repository.currencies;

import com.solo83.currencyexchange.utils.exceptions.CustomDbException;
import com.solo83.currencyexchange.utils.exceptions.RecordAlreadyExistException;

import java.util.List;
import java.util.Optional;

public interface CurrencyRepository {
       List<Currency> getAll() throws CustomDbException;
       Optional<Currency> get(String code) throws CustomDbException;
       Optional<Currency> create(Currency entity) throws RecordAlreadyExistException, CustomDbException;

}
