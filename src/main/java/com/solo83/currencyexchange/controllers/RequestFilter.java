package com.solo83.currencyexchange.controllers;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/*")
public class RequestFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) {

        try {

            HttpServletResponse resp = (HttpServletResponse) response;

            resp.addHeader("Access-Control-Allow-Origin", "*");
            resp.addHeader("Access-Control-Allow-Methods", "POST,GET,PATCH");
            resp.addHeader("Access-Control-Max-Age", "3600");
            resp.addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");

            resp.setCharacterEncoding("UTF-8");
            request.setCharacterEncoding("UTF-8");
            resp.setContentType("application/json; charset=utf-8");
            filterChain.doFilter(request, resp);
        }

        catch(IOException | ServletException e) {
            System.out.println(e.getMessage());
        }
    }

}
