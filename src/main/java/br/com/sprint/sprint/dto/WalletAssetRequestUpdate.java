package br.com.sprint.sprint.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class WalletAssetRequestUpdate {
    @NotNull
    private Long walletAssetId;

    @NotNull
    @DecimalMin("0.0001")
    private BigDecimal quantity;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal averagePrice;

    public Long getWalletAssetId() {
        return walletAssetId;
    }

    public void setWalletAssetId(Long walletAssetId) {
        this.walletAssetId = walletAssetId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(BigDecimal averagePrice) {
        this.averagePrice = averagePrice;
    }
}
