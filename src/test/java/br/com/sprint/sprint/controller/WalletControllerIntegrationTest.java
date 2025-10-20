package br.com.sprint.sprint.controller;

import br.com.sprint.sprint.model.User;
import br.com.sprint.sprint.model.Wallet;
import br.com.sprint.sprint.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser
public class WalletControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // Repositórios para setup e limpeza
    @Autowired private UserRepository userRepository;
    @Autowired private WalletRepository walletRepository;
    @Autowired private WalletAssetRepository walletAssetRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private AssetRepository assetRepository;


    private User testUser;
    private Wallet testWallet;

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
    }

    @Test
    void testGetWalletByUserId() throws Exception {
        mockMvc.perform(get("/users/" + testUser.getId() + "/wallet"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testWallet.getId().intValue())))
                .andExpect(jsonPath("$.userId", is(testUser.getId().intValue())));
    }
}