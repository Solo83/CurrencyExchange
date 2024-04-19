package com.solo83.currencyexchange.repository.exchangerates;

import com.solo83.currencyexchange.utils.exceptions.CustomDbException;
import com.solo83.currencyexchange.utils.exceptions.RecordAlreadyExistException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository <ExchangeRates>{
    List<ExchangeRates> getAll() throws CustomDbException;
    Optional<ExchangeRate> get(String baseCode, String targetCode) throws CustomDbException;
    Optional<ExchangeRate> update(String baseCode, String targetCode, BigDecimal rate) throws CustomDbException;
    Optional<ExchangeRate> create(ExchangeRate entity) throws CustomDbException, RecordAlreadyExistException;

}
