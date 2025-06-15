package br.com.sprint.sprint.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sprint.sprint.exception.ResourceNotFoundException;
import br.com.sprint.sprint.model.Asset;
import br.com.sprint.sprint.model.Wallet;
import br.com.sprint.sprint.model.WalletAsset;
import br.com.sprint.sprint.repository.AssetRepository;
import br.com.sprint.sprint.repository.WalletAssetRepository;
import br.com.sprint.sprint.repository.WalletRepository;
import br.com.sprint.sprint.service.WalletAssetService;

@Service
@Transactional
public class WalletAssetServiceImpl implements WalletAssetService {

    private final WalletAssetRepository waRepo;
    private final WalletRepository walletRepo;
    private final AssetRepository assetRepo;

    public WalletAssetServiceImpl(
        WalletAssetRepository waRepo,
        WalletRepository walletRepo,
        AssetRepository assetRepo
    ) {
        this.waRepo = waRepo;
        this.walletRepo = walletRepo;
        this.assetRepo = assetRepo;
    }

    @Override
    public WalletAsset addToWallet(
        Long walletId,
        Long assetId,
        BigDecimal quantity,
        BigDecimal purchasePrice
    ) {
        Wallet wallet = walletRepo.findById(walletId)
            .orElseThrow(() -> new ResourceNotFoundException("Wallet não encontrada: " + walletId));
        Asset asset = assetRepo.findById(assetId)
            .orElseThrow(() -> new ResourceNotFoundException("Asset não encontrado: " + assetId));

        WalletAsset wa = new WalletAsset();
        wa.setWallet(wallet);
        wa.setAsset(asset);
        wa.setQuantity(quantity);
        wa.setPurchasePrice(purchasePrice);
        return waRepo.save(wa);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalletAsset> listByWallet(Long walletId) {
        return waRepo.findByWalletId(walletId);
    }
}