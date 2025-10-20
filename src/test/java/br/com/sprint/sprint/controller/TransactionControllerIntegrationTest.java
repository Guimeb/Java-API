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

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser
public class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // Repositórios para setup e limpeza
    @Autowired private UserRepository userRepository;
    @Autowired private WalletRepository walletRepository;
    @Autowired private AssetRepository assetRepository;
    @Autowired private WalletAssetRepository walletAssetRepository;
    @Autowired private TransactionRepository transactionRepository;

    // Injetamos o Service real para criar uma transação de verdade
    @Autowired
    private WalletAssetService walletAssetService;

    private User testUser;
    private Wallet testWallet;
    private Asset testAsset;
    private Transaction testTransaction;

    @BeforeEach
    void setup() {
        // Limpa TODOS os repositórios na ordem correta (evita erro de foreign key)
        transactionRepository.deleteAll();
        walletAssetRepository.deleteAll();
        walletRepository.deleteAll();
        userRepository.deleteAll();
        assetRepository.deleteAll();

        // Cria User
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@user.com");
        testUser.setPassword("password");
        testUser = userRepository.save(testUser);

        // Cria Wallet
        testWallet = new Wallet();
        testWallet.setUser(testUser);
        testWallet = walletRepository.save(testWallet);

        // Cria Asset
        testAsset = new Asset();
        testAsset.setSymbol("AAPL");
        testAsset.setName("Apple Inc.");
        testAsset.setCurrentValue(new BigDecimal("170.00"));
        testAsset = assetRepository.save(testAsset);

        // Cria uma transação de compra, que é o que o service faz
        walletAssetService.transact(
                testWallet.getId(),
                testAsset.getId(),
                new BigDecimal("10"),
                new BigDecimal("170.00"),
                "BUY"
        );

        // Pega a transação que acabou de ser criada (o service.transact deve criar uma)
        testTransaction = transactionRepository.findAll().get(0);
    }

    @Test
    void testListByWallet() throws Exception {
        mockMvc.perform(get("/transactions/wallet/" + testWallet.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testTransaction.getId().intValue())))
                .andExpect(jsonPath("$[0].walletId", is(testWallet.getId().intValue())))
                .andExpect(jsonPath("$[0].transactionType", is("BUY")));
    }

    @Test
    void testListByAsset() throws Exception {
        mockMvc.perform(get("/transactions/asset/" + testAsset.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testTransaction.getId().intValue())))
                .andExpect(jsonPath("$[0].assetId", is(testAsset.getId().intValue())))
                .andExpect(jsonPath("$[0].transactionType", is("BUY")));
    }
}