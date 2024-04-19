package com.solo83.currencyexchange.controllers;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solo83.currencyexchange.repository.currencies.Currency;
import com.solo83.currencyexchange.repository.currencies.CurrencyRepository;
import com.solo83.currencyexchange.repository.currencies.CurrencyRepositoryImpl;
import com.solo83.currencyexchange.repository.exchangerates.ExchangeRate;
import com.solo83.currencyexchange.repository.exchangerates.ExchangeRateRepository;
import com.solo83.currencyexchange.repository.exchangerates.ExchangeRatesRepositoryImpl;
import com.solo83.currencyexchange.utils.exceptions.CustomDbException;
import com.solo83.currencyexchange.utils.exceptions.RecordAlreadyExistException;
import com.solo83.currencyexchange.utils.Validator;
import com.solo83.currencyexchange.utils.Writer;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "exchangeRatesServlet", value = "/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private ObjectMapper mapper;
    private ExchangeRateRepository exchangeRateRepository;
    private CurrencyRepository currencyRepository;

    @Override
    public void init() {
        mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        exchangeRateRepository = new ExchangeRatesRepositoryImpl();
        currencyRepository = new CurrencyRepositoryImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {
            List<ExchangeRate> allExchangeRates = exchangeRateRepository.getAll();
            Writer.printMessage(resp, mapper, allExchangeRates);
        } catch (CustomDbException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {
            Validator.validateParameterMap(req.getParameterMap(), "baseCurrencyCode", "targetCurrencyCode", "rate");
            String baseCurrencyCode = Validator.validateParameterValue("baseCurrencyCode", req.getParameter("baseCurrencyCode"), "^[A-Z]{3}$");
            String targetCurrencyCode = Validator.validateParameterValue("targetCurrencyCode", req.getParameter("targetCurrencyCode"), "^[A-Z]{3}$");
            BigDecimal rate = new BigDecimal(Validator.validateParameterValue("rate", req.getParameter("rate"), "-?(?:\\d+(?:\\.\\d+)?|\\.\\d+)"));

            Optional<Currency> baseCurrencyOptional = currencyRepository.get(baseCurrencyCode);
            Optional<Currency> targetCurrencyOptional = currencyRepository.get(targetCurrencyCode);
            Optional<ExchangeRate> exchangeRate = Optional.empty();

            if (baseCurrencyOptional.isPresent() && targetCurrencyOptional.isPresent()) {
                exchangeRate = exchangeRateRepository.create(new ExchangeRate(baseCurrencyOptional.get(), targetCurrencyOptional.get(), rate));
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "One (or both) currencies from the pair does not exist in the Database");
            }

            if (exchangeRate.isPresent()) {
                resp.setStatus(201);
                Writer.printMessage(resp, mapper, exchangeRate.get());
            }

        } catch (CustomDbException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (RecordAlreadyExistException e) {
            resp.sendError(HttpServletResponse.SC_CONFLICT, e.getMessage());
        }
    }
}