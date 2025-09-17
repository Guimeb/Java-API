package br.com.sprint.sprint.controller;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.*;

import br.com.sprint.sprint.dto.TransactionResponse;
import br.com.sprint.sprint.model.Transaction;
import br.com.sprint.sprint.service.TransactionService;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // Listar transações por carteira
    @GetMapping("/wallet/{walletId}")
    public List<TransactionResponse> listByWallet(@PathVariable Long walletId) {
        List<Transaction> transactions = transactionService.listByWallet(walletId);
        return transactions.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Listar transações por ativo
    @GetMapping("/asset/{assetId}")
    public List<TransactionResponse> listByAsset(@PathVariable Long assetId) {
        List<Transaction> transactions = transactionService.listByAsset(assetId);
        return transactions.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private TransactionResponse toResponse(Transaction tx) {
        return new TransactionResponse(
                tx.getId(),
                tx.getWallet().getId(),
                tx.getAsset().getId(),
                tx.getQuantity(),
                tx.getPrice(),
                tx.getTransactionType(),
                tx.getTransactionDate());
    }
}
