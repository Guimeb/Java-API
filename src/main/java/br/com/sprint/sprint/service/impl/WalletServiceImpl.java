package br.com.sprint.sprint.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sprint.sprint.exception.ResourceNotFoundException;
import br.com.sprint.sprint.model.User;
import br.com.sprint.sprint.model.Wallet;
import br.com.sprint.sprint.repository.UserRepository;
import br.com.sprint.sprint.repository.WalletRepository;
import br.com.sprint.sprint.service.WalletAssetService;
import br.com.sprint.sprint.service.WalletService;

import java.math.BigDecimal;

@Service
@Transactional
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepo;
    private final UserRepository userRepo;
    private final WalletAssetService walletAssetService;

    public WalletServiceImpl(WalletRepository walletRepo, UserRepository userRepo,
            WalletAssetService walletAssetService) {
        this.walletRepo = walletRepo;
        this.userRepo = userRepo;
        this.walletAssetService = walletAssetService;
    }

    @Override
    public Wallet createWalletForUser(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + userId));

        if (walletRepo.findByUser(user).isPresent()) {
            throw new IllegalStateException("Wallet já existe para o usuário: " + userId);
        }

        Wallet w = new Wallet();
        w.setUser(user);
        return walletRepo.save(w);
    }

    @Override
    @Transactional(readOnly = true)
    public Wallet getWalletByUser(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + userId));

        return walletRepo.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet não encontrada para o usuário: " + userId));
    }

    @Override
    public Wallet buyAsset(Long walletId, Long assetId, BigDecimal quantity, BigDecimal pricePerUnit) {
        walletAssetService.transact(walletId, assetId, quantity, pricePerUnit, "BUY");
        return walletRepo.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet não encontrada: " + walletId));
    }

    @Override
    public Wallet sellAsset(Long walletId, Long assetId, BigDecimal quantity, BigDecimal pricePerUnit) {
        walletAssetService.transact(walletId, assetId, quantity, pricePerUnit, "SELL");
        return walletRepo.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet não encontrada: " + walletId));
    }
}
