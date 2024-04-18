package com.solo83.currencyexchange.repository.exchangerates;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.solo83.currencyexchange.repository.currencies.Currency;
import java.math.BigDecimal;

@JsonPropertyOrder({"id", "BaseCurrency", "TargetCurrency","rate"})
public class ExchangeRate {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("BaseCurrency")
    private Currency BaseCurrency;
    @JsonProperty("TargetCurrency")
    private Currency TargetCurrency;
    @JsonProperty("rate")
    private BigDecimal rate;

    public ExchangeRate(Integer id, Currency baseCurrency, Currency targetCurrency, BigDecimal rate) {
        this.id = id;
        BaseCurrency = baseCurrency;
        TargetCurrency = targetCurrency;
        this.rate = rate;
    }

    public ExchangeRate(Currency baseCurrency, Currency targetCurrency, BigDecimal rate) {
        BaseCurrency = baseCurrency;
        TargetCurrency = targetCurrency;
        this.rate = rate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Currency getBaseCurrency() {
        return BaseCurrency;
    }

    public void setBaseCurrency(Currency baseCurrency) {
        BaseCurrency = baseCurrency;
    }

    public Currency getTargetCurrency() {
        return TargetCurrency;
    }

    public void setTargetCurrency(Currency targetCurrency) {
        TargetCurrency = targetCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
}