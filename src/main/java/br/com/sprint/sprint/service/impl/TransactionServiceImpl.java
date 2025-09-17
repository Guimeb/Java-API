package br.com.sprint.sprint.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.sprint.sprint.model.Transaction;
import br.com.sprint.sprint.repository.TransactionRepository;
import br.com.sprint.sprint.service.TransactionService;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepo;

    public TransactionServiceImpl(TransactionRepository transactionRepo) {
        this.transactionRepo = transactionRepo;
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        return transactionRepo.save(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> listByWallet(Long walletId) {
        return transactionRepo.findByWalletId(walletId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> listByAsset(Long assetId) {
        return transactionRepo.findByAssetId(assetId);
    }
}
