package br.com.sprint.sprint.dto;

import java.math.BigDecimal;

public class AssetResponse {
    private Long id;
    private String symbol;
    private String name;
    private BigDecimal currentValue;

    public AssetResponse(Long id, String symbol, String name, BigDecimal currentValue) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.currentValue = currentValue;
    }

    public Long getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }
}
