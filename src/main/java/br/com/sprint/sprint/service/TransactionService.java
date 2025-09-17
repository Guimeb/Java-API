package br.com.sprint.sprint.service;

import java.util.List;
import br.com.sprint.sprint.model.Transaction;

public interface TransactionService {
    Transaction createTransaction(Transaction transaction);

    List<Transaction> listByWallet(Long walletId);

    List<Transaction> listByAsset(Long assetId);
}
