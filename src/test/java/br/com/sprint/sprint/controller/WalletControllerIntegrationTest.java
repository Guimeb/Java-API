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

@SpringBootTest // Inicia o contexto Spring Boot
@AutoConfigureMockMvc // Configura o MockMvc
@Transactional // Garante o rollback de dados
@WithMockUser // Simula usuário autenticado
public class WalletControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc; // Simula requisições HTTP

    // Repositórios para setup e limpeza de dados
    @Autowired private UserRepository userRepository;
    @Autowired private WalletRepository walletRepository;
    @Autowired private WalletAssetRepository walletAssetRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private AssetRepository assetRepository;


    private User testUser;
    private Wallet testWallet; // Carteira base criada para o teste

    @BeforeEach // Executado antes de cada método de teste
    void setup() {
        // Limpa TODOS os repositórios na ordem correta para garantir estado limpo
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

        // Cria e salva Wallet, vinculada ao User
        testWallet = new Wallet();
        testWallet.setUser(testUser);
        testWallet = walletRepository.save(testWallet);
    }

    @Test
    void testGetWalletByUserId() throws Exception {
        // Testa o endpoint GET /users/{userId}/wallet para buscar a carteira do usuário
        mockMvc.perform(get("/users/" + testUser.getId() + "/wallet"))
                .andExpect(status().isOk()) // Espera HTTP 200
                .andExpect(jsonPath("$.id", is(testWallet.getId().intValue()))) // Verifica o ID da carteira
                .andExpect(jsonPath("$.userId", is(testUser.getId().intValue()))); // Verifica o ID do usuário associado
    }
}