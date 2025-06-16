package br.com.sprint.sprint.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WalletAssetResponse {
    private Long id;
    private Long walletId;
    private Long assetId;
    private BigDecimal quantity;
    private BigDecimal purchasePrice;
    private LocalDateTime purchaseDate;

    public WalletAssetResponse(Long id, Long walletId, Long assetId,
            BigDecimal quantity, BigDecimal purchasePrice,
            LocalDateTime purchaseDate) {
        this.id = id;
        this.walletId = walletId;
        this.assetId = assetId;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
        this.purchaseDate = purchaseDate;
    }

    // Getters

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

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }
}
