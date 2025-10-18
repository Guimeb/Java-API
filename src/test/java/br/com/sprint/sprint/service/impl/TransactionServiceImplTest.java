package br.com.sprint.sprint.service.impl;

import br.com.sprint.sprint.model.Transaction;
import br.com.sprint.sprint.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepo;

    @InjectMocks
    private TransactionServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveCriarTransacaoComSucesso() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);

        when(transactionRepo.save(any(Transaction.class))).thenReturn(transaction);

        Transaction resultado = service.createTransaction(transaction);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(transactionRepo, times(1)).save(transaction);
    }

    @Test
    void deveListarTransacoesPorWallet() {
        Long walletId = 10L;
        when(transactionRepo.findByWalletId(walletId))
                .thenReturn(List.of(new Transaction(), new Transaction()));

        List<Transaction> lista = service.listByWallet(walletId);

        assertEquals(2, lista.size());
        verify(transactionRepo, times(1)).findByWalletId(walletId);
    }

    @Test
    void deveListarTransacoesPorAsset() {
        Long assetId = 5L;
        when(transactionRepo.findByAssetId(assetId))
                .thenReturn(List.of(new Transaction()));

        List<Transaction> lista = service.listByAsset(assetId);

        assertEquals(1, lista.size());
        verify(transactionRepo, times(1)).findByAssetId(assetId);
    }
}
