package com.solo83.currencyexchange.controllers;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solo83.currencyexchange.service.ExchangeService;
import com.solo83.currencyexchange.service.ExchangeDTO;
import com.solo83.currencyexchange.utils.exceptions.CustomDbException;
import com.solo83.currencyexchange.utils.Validator;
import com.solo83.currencyexchange.utils.Writer;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

@WebServlet(name = "exchangeServlet", value = "/exchange")
public class ExchangeServlet extends HttpServlet {
    private ObjectMapper mapper;

    @Override
    public void init() {
        mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {

            Validator.validateParameterMap(req.getParameterMap(), "from", "to", "amount");
            String from = Validator.validateParameterValue("from", req.getParameter("from"), "^[A-Z]{3}$");
            String to = Validator.validateParameterValue("to", req.getParameter("to"), "^[A-Z]{3}$");
            BigDecimal amount = new BigDecimal(Validator.validateParameterValue("amount", req.getParameter("amount"), "-?(?:\\d+(?:\\.\\d+)?|\\.\\d+)"));

            ExchangeService service = new ExchangeService();
            Optional<ExchangeDTO> exchange = service.exchange(from, to, amount);

            if (exchange.isPresent()) {
                Writer.printMessage(resp, mapper, exchange.get());
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Exchange not found");
            }

        } catch (CustomDbException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
