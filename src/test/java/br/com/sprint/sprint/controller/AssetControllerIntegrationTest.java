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

/**
 * Teste de Integração para AssetController.
 * Configurações: SpringBootTest (contexto completo), AutoConfigureMockMvc (MockMvc),
 * Transactional (rollback de dados) e WithMockUser (ignora segurança 401).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser
public class AssetControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc; // Simula requisições HTTP

    @Autowired
    private ObjectMapper objectMapper; // Converte objetos para JSON

    // Repositórios para manipulação de dados no setup/limpeza
    @Autowired private AssetRepository assetRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private WalletAssetRepository walletAssetRepository;
    @Autowired private WalletRepository walletRepository;
    @Autowired private UserRepository userRepository;


    private Asset testAsset; // Ativo base para os testes de leitura/modificação

    /**
     * Limpa o banco e cria um ativo base antes de cada teste.
     */
    @BeforeEach
    void setup() {
        // Limpa repositórios em ordem de dependência (para evitar FK errors)
        transactionRepository.deleteAll();
        walletAssetRepository.deleteAll();
        walletRepository.deleteAll();
        userRepository.deleteAll();
        assetRepository.deleteAll();

        // Cria e salva um ativo base
        testAsset = new Asset();
        testAsset.setSymbol("PETR4");
        testAsset.setName("Petrobras");
        testAsset.setCurrentValue(new BigDecimal("35.00"));
        testAsset = assetRepository.save(testAsset);
    }

    /**
     * Testa o endpoint POST /assets para criação de um novo ativo.
     */
    @Test
    void testCreateAsset() throws Exception {
        AssetRequestCreate request = new AssetRequestCreate();
        request.setSymbol("AAPL");
        request.setName("Apple Inc.");
        request.setCurrentValue(new BigDecimal("170.00"));

        mockMvc.perform(post("/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // Espera HTTP 200
                .andExpect(jsonPath("$.symbol", is("AAPL"))); // Verifica a resposta
    }

    /**
     * Testa o endpoint GET /assets para listar todos os ativos.
     */
    @Test
    void testListAllAssets() throws Exception {
        mockMvc.perform(get("/assets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))) // Verifica se retornou 1 item (o ativo base)
                .andExpect(jsonPath("$[0].symbol", is(testAsset.getSymbol())));
    }

    /**
     * Testa o endpoint GET /assets/{id} para buscar um ativo específico.
     */
    @Test
    void testGetAssetById() throws Exception {
        mockMvc.perform(get("/assets/" + testAsset.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testAsset.getId().intValue())));
    }

    /**
     * Testa o endpoint PUT /assets para atualizar um ativo existente.
     */
    @Test
    void testUpdateAsset() throws Exception {
        AssetRequestUpdate request = new AssetRequestUpdate();
        request.setId(testAsset.getId());
        request.setName("Petrobras S.A.");
        request.setSymbol(testAsset.getSymbol());
        request.setCurrentValue(new BigDecimal("40.00"));

        mockMvc.perform(put("/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Petrobras S.A.")))
                .andExpect(jsonPath("$.currentValue", is(40.0)));
    }

    /**
     * Testa o endpoint DELETE /assets/{id} para exclusão de um ativo.
     */
    @Test
    void testDeleteAsset() throws Exception {
        mockMvc.perform(delete("/assets/" + testAsset.getId()))
                .andExpect(status().isOk());
    }
}