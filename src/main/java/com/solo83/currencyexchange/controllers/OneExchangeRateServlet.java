package com.solo83.currencyexchange.controllers;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solo83.currencyexchange.repository.exchangerates.ExchangeRate;
import com.solo83.currencyexchange.repository.exchangerates.ExchangeRateRepository;
import com.solo83.currencyexchange.repository.exchangerates.ExchangeRatesRepositoryImpl;
import com.solo83.currencyexchange.utils.exceptions.CustomDbException;
import com.solo83.currencyexchange.utils.Validator;
import com.solo83.currencyexchange.utils.Writer;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@WebServlet(name = "OneExchangeRateServlet", value = "/exchangeRate/*")
public class OneExchangeRateServlet extends HttpServlet {

    private ObjectMapper mapper;
    private ExchangeRateRepository<ExchangeRate> repository;

    @Override
    public void init() {
        mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        repository = new ExchangeRatesRepositoryImpl();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String method = req.getMethod();

        switch (method) {
            case "GET":
                this.doGet(req, resp);
            case "PATCH":
                this.doPatch(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {
            Map<String, String> exchangeRateCodes = Validator.validateOneExchangeRateGetRequestPairInUrl(req.getRequestURI());
            String baseCurrencyCode = exchangeRateCodes.get("baseCurrency");
            String targetCurrencyCode = exchangeRateCodes.get("targetCurrency");
            Optional<ExchangeRate> exchangeRateOptional = repository.get(baseCurrencyCode, targetCurrencyCode);

            if (exchangeRateOptional.isPresent()) {
                Writer.printMessage(resp, mapper, exchangeRateOptional.get());
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Exchange rate for pair '" + baseCurrencyCode + targetCurrencyCode + "' not found in Database");
            }

        } catch (CustomDbException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }

    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {
            Map<String, String> exchangeRateCodes = Validator.validateOneExchangeRateGetRequestPairInUrl(req.getRequestURI());
            Validator.validateParameterMap(req.getParameterMap(), "rate");
            String baseCurrencyCode = exchangeRateCodes.get("baseCurrency");
            String targetCurrencyCode = exchangeRateCodes.get("targetCurrency");
            BigDecimal rate = new BigDecimal(Validator.validateParameterValue("rate", req.getParameter("rate"), "-?(?:\\d+(?:\\.\\d+)?|\\.\\d+)"));
            Optional<ExchangeRate> exchangeRateOptional = repository.update(baseCurrencyCode, targetCurrencyCode, rate);

            if (exchangeRateOptional.isPresent()) {
                Writer.printMessage(resp, mapper, exchangeRateOptional.get());
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Exchange rate for pair '" + baseCurrencyCode + targetCurrencyCode + "' not found in Database");
            }

        } catch (CustomDbException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }

    }
}
