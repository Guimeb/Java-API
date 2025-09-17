package br.com.sprint.sprint.dto;

import java.math.BigDecimal;

public class WalletAssetResponse {
    private Long id;
    private Long walletId;
    private Long assetId;
    private BigDecimal quantity;
    private BigDecimal averagePrice;

    public WalletAssetResponse(Long id, Long walletId, Long assetId, BigDecimal quantity, BigDecimal averagePrice) {
        this.id = id;
        this.walletId = walletId;
        this.assetId = assetId;
        this.quantity = quantity;
        this.averagePrice = averagePrice;
    }

    public Long getId() {
        return id;
    }

    public Long getWalletId() {
        return walletId;
    }

    public Long getAssetId() {
        return assetId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getAveragePrice() {
        return averagePrice;
    }
}
