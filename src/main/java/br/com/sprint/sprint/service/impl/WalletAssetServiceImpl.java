package br.com.sprint.sprint.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sprint.sprint.exception.ResourceNotFoundException;
import br.com.sprint.sprint.model.Asset;
import br.com.sprint.sprint.model.Transaction;
import br.com.sprint.sprint.model.Wallet;
import br.com.sprint.sprint.model.WalletAsset;
import br.com.sprint.sprint.repository.AssetRepository;
import br.com.sprint.sprint.repository.TransactionRepository;
import br.com.sprint.sprint.repository.WalletAssetRepository;
import br.com.sprint.sprint.repository.WalletRepository;
import br.com.sprint.sprint.service.WalletAssetService;

@Service
@Transactional
public class WalletAssetServiceImpl implements WalletAssetService {

    private final WalletAssetRepository waRepo;
    private final WalletRepository walletRepo;
    private final AssetRepository assetRepo;
    private final TransactionRepository transactionRepo;

    public WalletAssetServiceImpl(WalletAssetRepository waRepo,
            WalletRepository walletRepo,
            AssetRepository assetRepo,
            TransactionRepository transactionRepo) {
        this.waRepo = waRepo;
        this.walletRepo = walletRepo;
        this.assetRepo = assetRepo;
        this.transactionRepo = transactionRepo;
    }

    @Override
    public WalletAsset transact(Long walletId, Long assetId, BigDecimal quantity, BigDecimal pricePerUnit,
            String type) {
        Wallet wallet = walletRepo.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet não encontrada: " + walletId));
        Asset asset = assetRepo.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset não encontrado: " + assetId));

        WalletAsset wa = waRepo.findByWalletIdAndAssetId(walletId, assetId)
                .orElseGet(() -> {
                    WalletAsset newWa = new WalletAsset();
                    newWa.setWallet(wallet);
                    newWa.setAsset(asset);
                    newWa.setQuantity(BigDecimal.ZERO);
                    newWa.setAveragePrice(BigDecimal.ZERO);
                    return newWa;
                });

        if ("BUY".equalsIgnoreCase(type)) {
            BigDecimal totalValue = wa.getAveragePrice().multiply(wa.getQuantity())
                    .add(pricePerUnit.multiply(quantity));
            BigDecimal newQuantity = wa.getQuantity().add(quantity);
            wa.setQuantity(newQuantity);
            wa.setAveragePrice(totalValue.divide(newQuantity, 6, RoundingMode.HALF_UP));
        } else if ("SELL".equalsIgnoreCase(type)) {
            if (wa.getQuantity().compareTo(quantity) < 0) {
                throw new IllegalArgumentException("Não há quantidade suficiente para vender");
            }
            wa.setQuantity(wa.getQuantity().subtract(quantity));
        } else {
            throw new IllegalArgumentException("Tipo de transação inválido: " + type);
        }

        waRepo.save(wa);

        Transaction tx = new Transaction();
        tx.setWallet(wallet);
        tx.setAsset(asset);
        tx.setQuantity(quantity);
        tx.setPrice(pricePerUnit);
        tx.setTransactionType(
                "BUY".equalsIgnoreCase(type) ? Transaction.TransactionType.BUY : Transaction.TransactionType.SELL);
        transactionRepo.save(tx);

        return wa;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalletAsset> listByWallet(Long walletId) {
        return waRepo.findByWalletId(walletId);
    }

    @Override
    public WalletAsset updateInWallet(Long walletId, Long walletAssetId, BigDecimal quantity, BigDecimal averagePrice) {
        WalletAsset wa = waRepo.findByIdAndWalletId(walletAssetId, walletId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "WalletAsset não encontrado: " + walletAssetId + " na carteira " + walletId));
        wa.setQuantity(quantity);
        wa.setAveragePrice(averagePrice);
        return waRepo.save(wa);
    }

    @Override
    public void removeFromWallet(Long walletId, Long walletAssetId) {
        WalletAsset wa = waRepo.findByIdAndWalletId(walletAssetId, walletId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "WalletAsset não encontrado: " + walletAssetId + " na carteira " + walletId));
        waRepo.delete(wa);
    }
}
