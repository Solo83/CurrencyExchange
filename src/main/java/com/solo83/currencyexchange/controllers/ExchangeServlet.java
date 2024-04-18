package com.solo83.currencyexchange.controllers;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solo83.currencyexchange.service.ExchangeService;
import com.solo83.currencyexchange.utils.exceptions.RecordNotFoundException;
import com.solo83.currencyexchange.utils.Validator;
import com.solo83.currencyexchange.utils.Writer;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.sqlite.SQLiteException;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
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

            Validator.parameterMapValidator(req.getParameterMap(), "from", "to", "amount");
            String from = Validator.requestValueChecker("from", req.getParameter("from"), "^[A-Z]{3}$");
            String to = Validator.requestValueChecker("to", req.getParameter("to"), "^[A-Z]{3}$");
            BigDecimal amount = new BigDecimal(Validator.requestValueChecker("amount", req.getParameter("amount"), "-?(?:\\d+(?:\\.\\d+)?|\\.\\d+)"));


            ExchangeService service = new ExchangeService(mapper);
            Optional<ObjectNode> exchange = service.exchange(from, to, amount);

            Writer.printMessage(resp, mapper, exchange.orElse(null));

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
