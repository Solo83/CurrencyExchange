package com.solo83.currencyexchange.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solo83.currencyexchange.repository.currencies.Currency;
import com.solo83.currencyexchange.repository.exchangerates.ExchangeRate;
import com.solo83.currencyexchange.repository.exchangerates.ExchangeRateRepository;
import com.solo83.currencyexchange.repository.exchangerates.ExchangeRatesRepositoryImpl;
import com.solo83.currencyexchange.utils.RecordNotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Optional;

public class ExchangeService {
    private final ObjectMapper mapper;

    public ExchangeService(ObjectMapper mapper) {
        this.mapper = mapper;
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    public Optional<ObjectNode> exchange(String from, String to, BigDecimal amount) throws SQLException, RecordNotFoundException {
        ExchangeRateRepository<ExchangeRate> repository = new ExchangeRatesRepositoryImpl();
        Optional<ExchangeRate> exchangeRate = Optional.empty();
        try {
            exchangeRate = repository.get(from, to);
        } catch (Exception ignored) {

        }
        if (exchangeRate.isPresent()) {
            BigDecimal convertedAmount = exchangeRate.get().getRate().multiply(amount);
            ObjectNode node = mapper.valueToTree(exchangeRate.get());
            node.remove("id");
            node.put("amount", amount);
            node.put("convertedAmount", convertedAmount);
            return Optional.of(node);
        }
        try {
            exchangeRate = repository.get(to, from);
        } catch (Exception ignored) {

        }
        if (exchangeRate.isPresent()) {
            ExchangeRate value = exchangeRate.get();
            BigDecimal rate = BigDecimal.valueOf(1).divide(value.getRate(), 4, RoundingMode.HALF_UP);
            value.setRate(rate);
            ObjectNode root = sendResponse(value.getBaseCurrency(), value.getTargetCurrency(), value.getRate(), amount);
            return Optional.of(root);
        }

        Optional<ExchangeRate> exchangeUSDA = Optional.empty();
        Optional<ExchangeRate> exchangeUSDB = Optional.empty();

        try {
            exchangeUSDA = repository.get("USD", from);
            exchangeUSDB = repository.get("USD", to);
        } catch (Exception ignored) {
        }

        if (exchangeUSDA.isPresent() && exchangeUSDB.isPresent()) {
            BigDecimal rate = BigDecimal.valueOf(1)
                    .divide(exchangeUSDA.get().getRate(), 4, RoundingMode.HALF_UP)
                    .multiply(exchangeUSDB.get().getRate());
            ObjectNode root = sendResponse(exchangeUSDB.get().getTargetCurrency(),
                    exchangeUSDA.get().getTargetCurrency(), rate, amount);
            return Optional.of(root);
        }

        throw new RecordNotFoundException("Exchange rate not found");
    }

    private ObjectNode sendResponse(Currency base, Currency target, BigDecimal rate, BigDecimal amount) {
        BigDecimal convertedAmount = rate.multiply(amount);
        ObjectNode rootNode = mapper.createObjectNode();
        ObjectNode baseCurrencyNode = mapper.valueToTree(target);
        ObjectNode targetCurrencyNode = mapper.valueToTree(base);
        rootNode.set("baseCurrency", baseCurrencyNode);
        rootNode.set("targetCurrency", targetCurrencyNode);
        rootNode.put("amount", amount);
        rootNode.put("convertedAmount", convertedAmount);
        return rootNode;
    }
}
