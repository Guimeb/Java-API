package br.com.sprint.sprint.controller;

import br.com.sprint.sprint.model.*;
import br.com.sprint.sprint.repository.*;
import br.com.sprint.sprint.service.WalletAssetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest // Inicia o contexto Spring Boot completo
@AutoConfigureMockMvc // Configura o MockMvc para testes de endpoint
@Transactional // Garante rollback dos dados após cada teste
@WithMockUser // Simula um usuário autenticado (ignora o 401)
public class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // Repositórios injetados para setup e limpeza de dados
    @Autowired private UserRepository userRepository;
    @Autowired private WalletRepository walletRepository;
    @Autowired private AssetRepository assetRepository;
    @Autowired private WalletAssetRepository walletAssetRepository;
    @Autowired private TransactionRepository transactionRepository;

    // Service injetado para criar transações de forma funcional no setup
    @Autowired
    private WalletAssetService walletAssetService;

    private User testUser;
    private Wallet testWallet;
    private Asset testAsset;
    private Transaction testTransaction;

    @BeforeEach // Executado antes de cada método de teste
    void setup() {
        // Limpa todas as tabelas na ordem correta para evitar erros de Foreign Key
        transactionRepository.deleteAll();
        walletAssetRepository.deleteAll();
        walletRepository.deleteAll();
        userRepository.deleteAll();
        assetRepository.deleteAll();

        // Cria e salva User
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@user.com");
        testUser.setPassword("password");
        testUser = userRepository.save(testUser);

        // Cria e salva Wallet
        testWallet = new Wallet();
        testWallet.setUser(testUser);
        testWallet = walletRepository.save(testWallet);

        // Cria e salva Asset
        testAsset = new Asset();
        testAsset.setSymbol("AAPL");
        testAsset.setName("Apple Inc.");
        testAsset.setCurrentValue(new BigDecimal("170.00"));
        testAsset = assetRepository.save(testAsset);

        // Cria uma transação de compra usando o service
        walletAssetService.transact(
                testWallet.getId(),
                testAsset.getId(),
                new BigDecimal("10"),
                new BigDecimal("170.00"),
                "BUY"
        );

        // Pega a transação criada para validação
        testTransaction = transactionRepository.findAll().get(0);
    }

    @Test
    void testListByWallet() throws Exception {
        // Testa o endpoint GET para listar transações por carteira
        mockMvc.perform(get("/transactions/wallet/" + testWallet.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))) // Verifica se encontrou 1 transação
                .andExpect(jsonPath("$[0].id", is(testTransaction.getId().intValue())))
                .andExpect(jsonPath("$[0].walletId", is(testWallet.getId().intValue())))
                .andExpect(jsonPath("$[0].transactionType", is("BUY")));
    }

    @Test
    void testListByAsset() throws Exception {
        // Testa o endpoint GET para listar transações por ativo
        mockMvc.perform(get("/transactions/asset/" + testAsset.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))) // Verifica se encontrou 1 transação
                .andExpect(jsonPath("$[0].id", is(testTransaction.getId().intValue())))
                .andExpect(jsonPath("$[0].assetId", is(testAsset.getId().intValue())))
                .andExpect(jsonPath("$[0].transactionType", is("BUY")));
    }
}