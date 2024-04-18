package com.solo83.currencyexchange.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solo83.currencyexchange.repository.currencies.CurrencyRepository;
import com.solo83.currencyexchange.repository.currencies.CurrencyRepositoryImpl;
import com.solo83.currencyexchange.repository.currencies.Currency;
import com.solo83.currencyexchange.utils.Writer;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "currenciesServlet", value = "/currencies")
public class CurrenciesServlet extends HttpServlet {
    private ObjectMapper mapper;
    private CurrencyRepository<Currency> repository;

    @Override
    public void init() {
        mapper = new ObjectMapper();
        repository = new CurrencyRepositoryImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {

            List<Currency> allCurrencies = repository.getAll();
            Writer.printMessage(resp, mapper, allCurrencies);

        } catch (SQLException | IOException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }
}
