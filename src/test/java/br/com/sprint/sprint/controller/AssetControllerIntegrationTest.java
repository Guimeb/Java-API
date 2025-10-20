package br.com.sprint.sprint.controller;

import br.com.sprint.sprint.dto.AssetRequestCreate;
import br.com.sprint.sprint.dto.AssetRequestUpdate;
import br.com.sprint.sprint.model.Asset;
import br.com.sprint.sprint.repository.*;
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
@WithMockUser // Permite acesso a todos os endpoints, evitando 401
public class AssetControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Repositórios para setup e limpeza
    @Autowired private AssetRepository assetRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private WalletAssetRepository walletAssetRepository;
    @Autowired private WalletRepository walletRepository;
    @Autowired private UserRepository userRepository;


    private Asset testAsset;

    @BeforeEach
    void setup() {
        // Limpa TODOS os repositórios na ordem correta (evita erro de foreign key)
        transactionRepository.deleteAll();
        walletAssetRepository.deleteAll();
        walletRepository.deleteAll();
        userRepository.deleteAll();
        assetRepository.deleteAll();

        // Cria um ativo base para os testes de GET, PUT, DELETE
        testAsset = new Asset();
        testAsset.setSymbol("PETR4");
        testAsset.setName("Petrobras");
        testAsset.setCurrentValue(new BigDecimal("35.00"));
        testAsset = assetRepository.save(testAsset);
    }

    @Test
    void testCreateAsset() throws Exception {
        AssetRequestCreate request = new AssetRequestCreate();
        request.setSymbol("AAPL");
        request.setName("Apple Inc.");
        request.setCurrentValue(new BigDecimal("170.00"));

        mockMvc.perform(post("/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol", is("AAPL")))
                .andExpect(jsonPath("$.name", is("Apple Inc.")));
    }

    @Test
    void testListAllAssets() throws Exception {
        // O testAsset criado no @BeforeEach já está no banco
        mockMvc.perform(get("/assets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].symbol", is(testAsset.getSymbol())));
    }

    @Test
    void testGetAssetById() throws Exception {
        mockMvc.perform(get("/assets/" + testAsset.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testAsset.getId().intValue())))
                .andExpect(jsonPath("$.symbol", is("PETR4")));
    }

    @Test
    void testUpdateAsset() throws Exception {
        AssetRequestUpdate request = new AssetRequestUpdate();
        request.setId(testAsset.getId());
        request.setName("Petrobras S.A.");
        request.setSymbol(testAsset.getSymbol()); // Símbolo não muda
        request.setCurrentValue(new BigDecimal("40.00"));

        mockMvc.perform(put("/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testAsset.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Petrobras S.A.")))
                .andExpect(jsonPath("$.currentValue", is(40.0)));
    }

    @Test
    void testDeleteAsset() throws Exception {
        mockMvc.perform(delete("/assets/" + testAsset.getId()))
                .andExpect(status().isOk());
    }
}