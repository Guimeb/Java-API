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

// Testes unitários para o serviço de Transação (Service)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepo; // Simula o repositório

    @InjectMocks
    private TransactionServiceImpl service; // Injeta os mocks no serviço

    @BeforeEach
    void setup() {
        // Inicializa os mocks antes de cada teste
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveCriarTransacaoComSucesso() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);

        // Simula o salvamento no repositório
        when(transactionRepo.save(any(Transaction.class))).thenReturn(transaction);

        Transaction resultado = service.createTransaction(transaction);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        // Verifica se o método save foi chamado
        verify(transactionRepo, times(1)).save(transaction);
    }

    @Test
    void deveListarTransacoesPorWallet() {
        Long walletId = 10L;
        // Simula o retorno de uma lista de transações para o ID da carteira
        when(transactionRepo.findByWalletId(walletId))
                .thenReturn(List.of(new Transaction(), new Transaction()));

        List<Transaction> lista = service.listByWallet(walletId);

        assertEquals(2, lista.size());
        // Verifica se o método de busca do repositório foi chamado
        verify(transactionRepo, times(1)).findByWalletId(walletId);
    }

    @Test
    void deveListarTransacoesPorAsset() {
        Long assetId = 5L;
        // Simula o retorno de uma lista de transações para o ID do ativo
        when(transactionRepo.findByAssetId(assetId))
                .thenReturn(List.of(new Transaction()));

        List<Transaction> lista = service.listByAsset(assetId);

        assertEquals(1, lista.size());
        // Verifica se o método de busca do repositório foi chamado
        verify(transactionRepo, times(1)).findByAssetId(assetId);
    }
}