package com.solo83.currencyexchange.service;

import com.solo83.currencyexchange.repository.exchangerates.ExchangeRate;
import com.solo83.currencyexchange.repository.exchangerates.ExchangeRateRepository;
import com.solo83.currencyexchange.repository.exchangerates.ExchangeRatesRepositoryImpl;
import com.solo83.currencyexchange.utils.exceptions.CustomDbException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class ExchangeService {

    public Optional<ExchangeDTO> exchange(String from, String to, BigDecimal amount) throws CustomDbException {
        ExchangeRateRepository repository = new ExchangeRatesRepositoryImpl();

        Optional<ExchangeRate> exchangeRate = repository.get(from, to);
        ExchangeDTO exchangeDTO;

        if (exchangeRate.isPresent()) {
            BigDecimal convertedAmount = exchangeRate.get().getRate().multiply(amount);
            exchangeDTO = new ExchangeDTO(exchangeRate.get().getBaseCurrency(),
                    exchangeRate.get().getTargetCurrency(),
                    exchangeRate.get().getRate(), amount, convertedAmount);

            return Optional.of(exchangeDTO);
        }

        exchangeRate = repository.get(to, from);

        if (exchangeRate.isPresent()) {
            BigDecimal rate = BigDecimal.valueOf(1).divide(exchangeRate.get().getRate(), 4, RoundingMode.HALF_UP);
            BigDecimal convertedAmount = rate.multiply(amount);
            exchangeDTO = new ExchangeDTO(exchangeRate.get().getTargetCurrency(),
                    exchangeRate.get().getBaseCurrency(),
                    rate, amount, convertedAmount);

            return Optional.of(exchangeDTO);
        }

        Optional<ExchangeRate> exchangeUSDA = repository.get("USD", from);
        Optional<ExchangeRate> exchangeUSDB = repository.get("USD", to);

        if (exchangeUSDA.isPresent() && exchangeUSDB.isPresent()) {
            BigDecimal rate = BigDecimal.valueOf(1)
                    .divide(exchangeUSDA.get().getRate(), 4, RoundingMode.HALF_UP)
                    .multiply(exchangeUSDB.get().getRate());
            BigDecimal convertedAmount = rate.multiply(amount);

            exchangeDTO = new ExchangeDTO(exchangeUSDB.get().getTargetCurrency(),
                    exchangeUSDA.get().getTargetCurrency(),
                    rate, amount, convertedAmount);

            return Optional.of(exchangeDTO);
        }

        return Optional.empty();
    }
}
