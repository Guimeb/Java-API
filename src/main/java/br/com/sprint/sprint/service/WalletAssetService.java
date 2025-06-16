package br.com.sprint.sprint.service;

import java.math.BigDecimal;
import java.util.List;
import br.com.sprint.sprint.model.WalletAsset;

public interface WalletAssetService {

    WalletAsset addToWallet(Long walletId, Long assetId, BigDecimal quantity, BigDecimal purchasePrice);

    List<WalletAsset> listByWallet(Long walletId);

    WalletAsset updateInWallet(Long walletId, Long walletAssetId, BigDecimal quantity, BigDecimal purchasePrice);

    void removeFromWallet(Long walletId, Long walletAssetId);
}
