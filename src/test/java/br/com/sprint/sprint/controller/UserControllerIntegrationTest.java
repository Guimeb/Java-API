package br.com.sprint.sprint.controller;

import br.com.sprint.sprint.dto.UserRequestCreate;
import br.com.sprint.sprint.dto.UserRequestUpdate;
import br.com.sprint.sprint.model.User;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest // Inicia o contexto Spring Boot
@AutoConfigureMockMvc // Configura o MockMvc
@Transactional // Garante o rollback de dados
@WithMockUser // Evita erros 401 (Unauthorized)
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc; // Simula requisições HTTP

    @Autowired
    private ObjectMapper objectMapper; // Converte objetos para JSON

    // Repositórios para setup, limpeza e manipulação de dados
    @Autowired private UserRepository userRepository;
    @Autowired private WalletRepository walletRepository;
    @Autowired private WalletAssetRepository walletAssetRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private AssetRepository assetRepository;

    private User testUser; // Usuário base para testes de leitura/modificação

    @BeforeEach // Executado antes de cada método de teste
    void setup() {
        // Limpa todas as tabelas em ordem de dependência para garantir estado limpo
        transactionRepository.deleteAll();
        walletAssetRepository.deleteAll();
        walletRepository.deleteAll();
        userRepository.deleteAll();
        assetRepository.deleteAll();

        // Cria e salva um usuário base no banco de dados
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@user.com");
        testUser.setPassword("encodedPassword123");
        testUser = userRepository.save(testUser);
    }

    @Test
    void testCreateUser() throws Exception {
        // Testa o endpoint POST /users para criar um novo usuário
        UserRequestCreate request = new UserRequestCreate();
        request.setUsername("newuser");
        request.setEmail("new@user.com");
        request.setPassword("newPassword123");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // Espera HTTP 200
                .andExpect(jsonPath("$.username", is("newuser")))
                .andExpect(jsonPath("$.password").doesNotExist()); // Verifica que a senha não é retornada
    }

    @Test
    void testListAllUsers() throws Exception {
        // Testa o endpoint GET /users para listar todos os usuários
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))) // Verifica se encontrou 1 item (o criado no setup)
                .andExpect(jsonPath("$[0].username", is(testUser.getUsername())))
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    @Test
    void testGetUserById() throws Exception {
        // Testa o endpoint GET /users/{id} para buscar um usuário específico
        mockMvc.perform(get("/users/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testUser.getId().intValue())))
                .andExpect(jsonPath("$.username", is(testUser.getUsername())))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void testUpdateUser() throws Exception {
        // Testa o endpoint PUT /users para atualizar um usuário existente
        UserRequestUpdate request = new UserRequestUpdate();
        request.setId(testUser.getId());
        request.setUsername("updatedUser");
        request.setEmail("updated@user.com");
        request.setPassword("updatedPassword123");

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("updatedUser"))) // Verifica a atualização
                .andExpect(jsonPath("$.email", is("updated@user.com")))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void testDeleteUser() throws Exception {
        // Testa o endpoint DELETE /users/{id} para excluir um usuário
        mockMvc.perform(delete("/users/" + testUser.getId()))
                .andExpect(status().isOk()); // Espera HTTP 200
    }
}