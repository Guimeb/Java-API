package br.com.sprint.sprint.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.sprint.sprint.exception.ResourceNotFoundException;
import br.com.sprint.sprint.model.User;
import br.com.sprint.sprint.model.Wallet;
import br.com.sprint.sprint.repository.UserRepository;
import br.com.sprint.sprint.repository.WalletRepository;
import br.com.sprint.sprint.service.WalletService;

@Service
@Transactional
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepo;
    private final UserRepository userRepo;

    public WalletServiceImpl(WalletRepository walletRepo, UserRepository userRepo) {
        this.walletRepo = walletRepo;
        this.userRepo = userRepo;
    }

    @Override
    public Wallet createWalletForUser(Long userId) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + userId));

        // opcionalmente verificar se já existe
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
}
