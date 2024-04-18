package com.solo83.currencyexchange.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class Writer {

    public static void printMessage(HttpServletResponse resp, ObjectMapper mapper, Object obj) throws IOException {

        try (PrintWriter writer = resp.getWriter()) {
            String message = mapper.writeValueAsString(obj);
            writer.println(message);
        } catch (IOException e) {
            throw new IOException("Writer IO Exception");
        }
    }

}
