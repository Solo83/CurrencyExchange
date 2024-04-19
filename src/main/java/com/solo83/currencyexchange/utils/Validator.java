package com.solo83.currencyexchange.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    final public static String PATTERN_CURRENCY_CODE = "^[A-Z]{3}$";
    final public static String PATTERN_CURRENCY_SIGN = "\\p{Sc}";
    final public static String PATTERN_CURRENCY_FULLNAME = "(\\w+\\s+\\w+)";
    final public static String PATTERN_CURRENCY_CODE_IN_URL = "[A-Z]{3}$";
    final public static String PATTERN_BIGDECIMAL_RATE = "-?(?:\\d+(?:\\.\\d+)?|\\.\\d+)";


    public static String validateParameterValue(String parameterName, String value, String regexp) throws IllegalArgumentException {

        Matcher matcher = validateRegExp(value, regexp);

        if (value.trim().isEmpty() || !matcher.find()) {
            throw new IllegalArgumentException("Value '" + value + "' is not valid for parameter '" + parameterName + "'");
        }
        return value;
    }

    public static String validateOneCurrencyGetRequestUrl(String requestURL, String regexp) throws IllegalArgumentException {

        Matcher matcher = validateRegExp(requestURL, regexp);
        String currencyCode;

        if (matcher.find()) {
            currencyCode = matcher.group();
        } else {
            throw new IllegalArgumentException("Correct currency code is absent in request");
        }
        return currencyCode;
    }


    public static Map<String, String> validateOneExchangeRateGetRequestPairInUrl(String requestURL) throws IllegalArgumentException {

        Matcher matcher = validateRegExp(requestURL, "[A-Z]{6}+$");
        Map<String, String> map = new HashMap<>();

        if (matcher.find()) {
            if (matcher.group().length() % 2 == 0) {
                int halfLength = matcher.group().length() / 2;
                map.put("baseCurrency", matcher.group().substring(0, halfLength));
                map.put("targetCurrency", matcher.group().substring(halfLength));
            } else {
                throw new IllegalArgumentException("Error in RegExp");
            }
        } else {
            throw new IllegalArgumentException("The pair's currency codes are missing in URL");
        }

        return map;

    }

    public static void validateParameterMap(Map<String, String[]> parameterMap, String... args) throws IllegalArgumentException {

        for (String arg : args) {
            if (!parameterMap.containsKey(arg)) {
                throw new IllegalArgumentException("Required parameter '" + arg + "' is missing");
            }
        }
    }

    private static Matcher validateRegExp(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(input);
    }

}
