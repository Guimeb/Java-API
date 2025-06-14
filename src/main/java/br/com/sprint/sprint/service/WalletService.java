package br.com.sprint.sprint.service;

import br.com.sprint.sprint.model.Wallet;

public interface WalletService {
    Wallet createWalletForUser(Long userId);
    Wallet getWalletByUser(Long userId);
}
