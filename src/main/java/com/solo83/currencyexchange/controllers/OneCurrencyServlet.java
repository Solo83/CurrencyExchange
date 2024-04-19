package com.solo83.currencyexchange.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.solo83.currencyexchange.repository.currencies.CurrencyRepository;
import com.solo83.currencyexchange.repository.currencies.CurrencyRepositoryImpl;
import com.solo83.currencyexchange.repository.currencies.Currency;
import com.solo83.currencyexchange.utils.exceptions.CustomDbException;
import com.solo83.currencyexchange.utils.exceptions.RecordAlreadyExistException;
import com.solo83.currencyexchange.utils.Validator;
import com.solo83.currencyexchange.utils.Writer;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;


@WebServlet(name = "oneCurrencyServlet", value = "/currency/*")
public class OneCurrencyServlet extends HttpServlet {
    private ObjectMapper mapper;
    private CurrencyRepository repository;

    @Override
    public void init() {
        mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        repository = new CurrencyRepositoryImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {
            String currencyCode = Validator.validateOneCurrencyGetRequestUrl(req.getRequestURI(), Validator.PATTERN_CURRENCY_CODE_IN_URL);
            Optional<Currency> currencyOptional = repository.get(currencyCode);
            if (currencyOptional.isPresent()) {
                Writer.printMessage(resp, mapper, currencyOptional.get());
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Currency '" + currencyCode + "' is not found in database");
            }

        } catch (CustomDbException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {
            Validator.validateParameterMap(req.getParameterMap(), "code", "fullname", "sign");
            String code = Validator.validateParameterValue("code", req.getParameter("code"), Validator.PATTERN_CURRENCY_CODE);
            String fullName = Validator.validateParameterValue("fullname", req.getParameter("fullname"),Validator.PATTERN_CURRENCY_FULLNAME);
            String sign = Validator.validateParameterValue("sign", req.getParameter("sign"), Validator.PATTERN_CURRENCY_SIGN);

            Optional<Currency> currencyOptional = repository.create(new Currency(fullName, code, sign));

            if (currencyOptional.isPresent()) {
                resp.setStatus(201);
                Writer.printMessage(resp, mapper, currencyOptional.get());
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Currency '" + code + "' is not found in database");
            }

        } catch (RecordAlreadyExistException e) {
            resp.sendError(HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (CustomDbException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

}
