package br.com.sprint.sprint.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import br.com.sprint.sprint.model.vo.Price;
import br.com.sprint.sprint.model.vo.Quantity;

@Entity
@Table(name = "wallet_assets")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class WalletAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    @JsonBackReference
    private Wallet wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "quantity", nullable = false))
    private Quantity quantity = new Quantity(java.math.BigDecimal.ZERO);

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "average_price", nullable = false))
    private Price averagePrice = new Price(java.math.BigDecimal.ZERO);

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public void setQuantity(Quantity quantity) {
        this.quantity = quantity;
    }

    public Price getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(Price averagePrice) {
        this.averagePrice = averagePrice;
    }
}
