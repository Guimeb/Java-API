package br.com.sprint.sprint.service.impl;

import java.math.BigDecimal;
import java.util.List;

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
    public WalletAsset transact(Long walletId, Long assetId, BigDecimal quantityBd, BigDecimal priceBd, String type) {
        Wallet wallet = walletRepo.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet não encontrada: " + walletId));
        Asset asset = assetRepo.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset não encontrado: " + assetId));

        WalletAsset wa = waRepo.findByWalletIdAndAssetId(walletId, assetId)
                .orElseGet(() -> {
                    WalletAsset newWa = new WalletAsset();
                    newWa.setWallet(wallet);
                    newWa.setAsset(asset);
                    newWa.setQuantity(new br.com.sprint.sprint.model.vo.Quantity(java.math.BigDecimal.ZERO));
                    newWa.setAveragePrice(new br.com.sprint.sprint.model.vo.Price(java.math.BigDecimal.ZERO));
                    return newWa;
                });

        br.com.sprint.sprint.model.vo.Quantity quantity = new br.com.sprint.sprint.model.vo.Quantity(quantityBd);
        br.com.sprint.sprint.model.vo.Price price = new br.com.sprint.sprint.model.vo.Price(priceBd);

        if ("BUY".equalsIgnoreCase(type)) {
            br.com.sprint.sprint.model.vo.Price totalValue = new br.com.sprint.sprint.model.vo.Price(
                    wa.getAveragePrice().getValue().multiply(wa.getQuantity().getValue())
                            .add(price.getValue().multiply(quantity.getValue())));
            br.com.sprint.sprint.model.vo.Quantity newQuantity = wa.getQuantity().add(quantity);
            wa.setQuantity(newQuantity);
            wa.setAveragePrice(new br.com.sprint.sprint.model.vo.Price(
                    totalValue.getValue().divide(newQuantity.getValue(), 6, java.math.RoundingMode.HALF_UP)));
        } else if ("SELL".equalsIgnoreCase(type)) {
            if (wa.getQuantity().getValue().compareTo(quantity.getValue()) < 0) {
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
        tx.setQuantity(quantity.getValue());
        tx.setPrice(price.getValue());
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
    public WalletAsset updateInWallet(Long walletId, Long walletAssetId, BigDecimal quantityBd,
            BigDecimal averagePriceBd) {
        WalletAsset wa = waRepo.findByIdAndWalletId(walletAssetId, walletId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "WalletAsset não encontrado: " + walletAssetId + " na carteira " + walletId));
        wa.setQuantity(new br.com.sprint.sprint.model.vo.Quantity(quantityBd));
        wa.setAveragePrice(new br.com.sprint.sprint.model.vo.Price(averagePriceBd));
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
