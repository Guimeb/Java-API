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

@SpringBootTest // Inicia o contexto Spring Boot
@AutoConfigureMockMvc // Configura o MockMvc
@Transactional // Garante o rollback de dados
@WithMockUser // Simula autenticação
public class WalletAssetControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc; // Simula requisições HTTP

    @Autowired
    private ObjectMapper objectMapper; // Converte objetos para JSON

    // Repositórios para setup e limpeza de dados
    @Autowired private UserRepository userRepository;
    @Autowired private WalletRepository walletRepository;
    @Autowired private AssetRepository assetRepository;
    @Autowired private WalletAssetRepository walletAssetRepository;
    @Autowired private TransactionRepository transactionRepository;

    @Autowired
    private WalletAssetService walletAssetService; // Service usado para criar o estado inicial

    private User testUser;
    private Wallet testWallet;
    private Asset testAsset;

    @BeforeEach // Executado antes de cada teste
    void setup() {
        // Limpa todas as tabelas na ordem de dependência (Foreign Key)
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

        // Cria e salva Wallet vinculada ao User
        testWallet = new Wallet();
        testWallet.setUser(testUser);
        testWallet = walletRepository.save(testWallet);

        // Cria e salva Asset
        testAsset = new Asset();
        testAsset.setSymbol("AAPL");
        testAsset.setName("Apple Inc.");
        testAsset.setCurrentValue(new BigDecimal("170.00"));
        testAsset = assetRepository.save(testAsset);
    }

    @Test
    void testBuyAsset() throws Exception {
        // Testa o endpoint POST de compra de ativo
        WalletAssetRequestCreate request = new WalletAssetRequestCreate();
        request.setAssetId(testAsset.getId());
        request.setQuantity(new BigDecimal("10"));
        request.setPurchasePrice(new BigDecimal("170.00"));

        mockMvc.perform(post("/users/" + testUser.getId() + "/wallet/assets/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // Espera HTTP 200
                .andExpect(jsonPath("$.quantity", is(10)))
                .andExpect(jsonPath("$.averagePrice", is(170.0))); // Verifica o preço médio
    }

    @Test
    void testSellAsset() throws Exception {
        // Primeiro, realiza uma compra para ter o ativo na carteira
        walletAssetService.transact(
                testWallet.getId(),
                testAsset.getId(),
                new BigDecimal("10"),
                new BigDecimal("170.00"),
                "BUY"
        );

        // Prepara a requisição de venda
        WalletAssetRequestCreate sellRequest = new WalletAssetRequestCreate();
        sellRequest.setAssetId(testAsset.getId());
        sellRequest.setQuantity(new BigDecimal("3"));
        sellRequest.setPurchasePrice(new BigDecimal("180.00")); // Preço de venda (irrelevante para o preço médio, mas necessário)

        // Testa o endpoint POST de venda de ativo
        mockMvc.perform(post("/users/" + testUser.getId() + "/wallet/assets/sell")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sellRequest)))
                .andExpect(status().isOk()) // Espera HTTP 200
                .andExpect(jsonPath("$.quantity", is(7))) // Verifica a nova quantidade (10 - 3 = 7)
                .andExpect(jsonPath("$.averagePrice", is(170.0))); // Verifica se o preço médio (de custo) se manteve
    }

    @Test
    void testUpdateAsset() throws Exception {
        // Cria um WalletAsset inicial para ser atualizado
        WalletAsset wa = walletAssetService.transact(
                testWallet.getId(),
                testAsset.getId(),
                new BigDecimal("10"),
                new BigDecimal("170.00"),
                "BUY"
        );

        Long walletAssetId = wa.getId();

        // Prepara a requisição de atualização
        WalletAssetRequestUpdate updateRequest = new WalletAssetRequestUpdate();
        updateRequest.setWalletAssetId(walletAssetId);
        updateRequest.setQuantity(new BigDecimal("15"));
        updateRequest.setAveragePrice(new BigDecimal("172.00"));

        // Testa o endpoint PUT de atualização
        mockMvc.perform(put("/users/" + testUser.getId() + "/wallet/assets/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk()) // Espera HTTP 200
                .andExpect(jsonPath("$.id", is(walletAssetId.intValue())))
                .andExpect(jsonPath("$.quantity", is(15))) // Verifica a nova quantidade
                .andExpect(jsonPath("$.averagePrice", is(172.0))); // Verifica o novo preço médio
    }

    @Test
    void testListAssets() throws Exception {
        // Cria um WalletAsset na carteira para ser listado
        walletAssetService.transact(
                testWallet.getId(),
                testAsset.getId(),
                new BigDecimal("10"),
                new BigDecimal("170.00"),
                "BUY"
        );

        // Testa o endpoint GET para listar ativos da carteira
        mockMvc.perform(get("/users/" + testUser.getId() + "/wallet/assets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))) // Deve retornar 1 item
                .andExpect(jsonPath("$[0].assetId", is(testAsset.getId().intValue())))
                .andExpect(jsonPath("$[0].quantity", is(10)));
    }
}