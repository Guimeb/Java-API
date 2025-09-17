package br.com.sprint.sprint.service;

import br.com.sprint.sprint.model.Wallet;
import java.math.BigDecimal;

public interface WalletService {

    Wallet createWalletForUser(Long userId);

    Wallet getWalletByUser(Long userId);

    Wallet buyAsset(Long walletId, Long assetId, BigDecimal quantity, BigDecimal pricePerUnit);

    Wallet sellAsset(Long walletId, Long assetId, BigDecimal quantity, BigDecimal pricePerUnit);
}
