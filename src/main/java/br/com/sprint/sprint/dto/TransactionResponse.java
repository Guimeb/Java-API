package br.com.sprint.sprint.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import br.com.sprint.sprint.model.Transaction.TransactionType;

public class TransactionResponse {
    private Long id;
    private Long walletId;
    private Long assetId;
    private BigDecimal quantity;
    private BigDecimal price;
    private TransactionType transactionType;
    private LocalDateTime transactionDate;

    public TransactionResponse(Long id, Long walletId, Long assetId, BigDecimal quantity,
            BigDecimal price, TransactionType transactionType, LocalDateTime transactionDate) {
        this.id = id;
        this.walletId = walletId;
        this.assetId = assetId;
        this.quantity = quantity;
        this.price = price;
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;
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

    public BigDecimal getPrice() {
        return price;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
}
