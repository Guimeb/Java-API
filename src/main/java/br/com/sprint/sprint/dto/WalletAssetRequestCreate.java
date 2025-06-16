package br.com.sprint.sprint.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class WalletAssetRequestCreate {
    @NotNull
    private Long assetId;

    @NotNull
    @DecimalMin("0.0001")
    private BigDecimal quantity;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal purchasePrice;

    // Getters e Setters

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }
}