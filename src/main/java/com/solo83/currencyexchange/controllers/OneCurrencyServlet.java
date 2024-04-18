package com.solo83.currencyexchange.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.solo83.currencyexchange.repository.currencies.CurrencyRepository;
import com.solo83.currencyexchange.repository.currencies.CurrencyRepositoryImpl;
import com.solo83.currencyexchange.repository.currencies.Currency;
import com.solo83.currencyexchange.utils.RecordNotFoundException;
import com.solo83.currencyexchange.utils.Validator;
import com.solo83.currencyexchange.utils.Writer;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.sqlite.SQLiteException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;


@WebServlet(name = "oneCurrencyServlet", value = "/currency/*")
public class OneCurrencyServlet extends HttpServlet {
    private ObjectMapper mapper;
    private CurrencyRepository<Currency> repository;

    @Override
    public void init() {
        mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        repository = new CurrencyRepositoryImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {
            String currencyCode = Validator.oneCurrencyGetRequestUrlChecker(req.getRequestURI(), "[A-Z]{3}$");
            Optional<Currency> currentCurrency = repository.get(currencyCode);
            Writer.printMessage(resp, mapper, currentCurrency);

        } catch (SQLException | IOException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (RecordNotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {
            Validator.parameterMapValidator(req.getParameterMap(), "code", "fullname", "sign");
            String code = Validator.requestValueChecker("code", req.getParameter("code"), "^[A-Z]{3}$");
            String fullName = Validator.requestValueChecker("fullname", req.getParameter("fullname"), "(\\w+\\s+\\w+)");
            String sign = Validator.requestValueChecker("sign", req.getParameter("sign"), "\\p{Sc}");

            Optional<Currency> currencyOptional = repository.create(new Currency(fullName, code, sign));
            Writer.printMessage(resp, mapper, currencyOptional.orElse(null));

        } catch (SQLiteException e) {
            resp.sendError(HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (SQLException | IOException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (RecordNotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }

}
