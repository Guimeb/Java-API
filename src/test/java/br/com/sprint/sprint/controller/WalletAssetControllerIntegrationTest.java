package br.com.sprint.sprint.controller;

import br.com.sprint.sprint.dto.WalletAssetRequestCreate;
import br.com.sprint.sprint.dto.WalletAssetRequestUpdate;
import br.com.sprint.sprint.model.*;
import br.com.sprint.sprint.repository.*;
import br.com.sprint.sprint.service.WalletAssetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser
public class WalletAssetControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Repositórios para setup e limpeza
    @Autowired private UserRepository userRepository;
    @Autowired private WalletRepository walletRepository;
    @Autowired private AssetRepository assetRepository;
    @Autowired private WalletAssetRepository walletAssetRepository;
    @Autowired private TransactionRepository transactionRepository;

    // Injetamos o Service real para ajudar a preparar o estado do banco
    @Autowired
    private WalletAssetService walletAssetService;

    private User testUser;
    private Wallet testWallet;
    private Asset testAsset;

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
    }

    @Test
    void testBuyAsset() throws Exception {
        WalletAssetRequestCreate request = new WalletAssetRequestCreate();
        request.setAssetId(testAsset.getId());
        request.setQuantity(new BigDecimal("10"));
        request.setPurchasePrice(new BigDecimal("170.00"));

        mockMvc.perform(post("/users/" + testUser.getId() + "/wallet/assets/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assetId", is(testAsset.getId().intValue())))
                .andExpect(jsonPath("$.quantity", is(10))) // <--- CORRIGIDO DE 10.0
                .andExpect(jsonPath("$.averagePrice", is(170.0)));
    }

    @Test
    void testSellAsset() throws Exception {
        // Primeiro, compra 10 ações usando o serviço real
        walletAssetService.transact(
                testWallet.getId(),
                testAsset.getId(),
                new BigDecimal("10"),
                new BigDecimal("170.00"),
                "BUY"
        );

        // Agora, vende 3 ações
        WalletAssetRequestCreate sellRequest = new WalletAssetRequestCreate();
        sellRequest.setAssetId(testAsset.getId());
        sellRequest.setQuantity(new BigDecimal("3"));
        sellRequest.setPurchasePrice(new BigDecimal("180.00")); // Preço de venda

        mockMvc.perform(post("/users/" + testUser.getId() + "/wallet/assets/sell")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sellRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assetId", is(testAsset.getId().intValue())))
                .andExpect(jsonPath("$.quantity", is(7))) // <--- CORRIGIDO DE 7.0
                .andExpect(jsonPath("$.averagePrice", is(170.0))); 
    }

    @Test
    void testUpdateAsset() throws Exception {
        // Primeiro, compra 10 ações usando o serviço
        WalletAsset wa = walletAssetService.transact(
                testWallet.getId(),
                testAsset.getId(),
                new BigDecimal("10"),
                new BigDecimal("170.00"),
                "BUY"
        );

        // O ID do WalletAsset que acabamos de criar
        Long walletAssetId = wa.getId();

        // Requisição para atualizar a quantidade (ex: importação manual)
        WalletAssetRequestUpdate updateRequest = new WalletAssetRequestUpdate();
        updateRequest.setWalletAssetId(walletAssetId);
        updateRequest.setQuantity(new BigDecimal("15")); // Nova quantidade total
        updateRequest.setAveragePrice(new BigDecimal("172.00")); // Novo preço médio

        mockMvc.perform(put("/users/" + testUser.getId() + "/wallet/assets/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(walletAssetId.intValue())))
                .andExpect(jsonPath("$.quantity", is(15))) // <--- CORRIGIDO DE 15.0
                .andExpect(jsonPath("$.averagePrice", is(172.0)));
    }

    @Test
    void testListAssets() throws Exception {
        // Compra 10 ações para que a lista não esteja vazia
        walletAssetService.transact(
                testWallet.getId(),
                testAsset.getId(),
                new BigDecimal("10"),
                new BigDecimal("170.00"),
                "BUY"
        );

        mockMvc.perform(get("/users/" + testUser.getId() + "/wallet/assets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].assetId", is(testAsset.getId().intValue())))
                .andExpect(jsonPath("$[0].quantity", is(10))); // <--- CORRIGIDO DE 10.0
    }
}