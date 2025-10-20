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

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser // Evita erros 401
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Repositórios para setup e limpeza
    @Autowired private UserRepository userRepository;
    @Autowired private WalletRepository walletRepository;
    @Autowired private WalletAssetRepository walletAssetRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private AssetRepository assetRepository;

    private User testUser;

    @BeforeEach
    void setup() {
        // Limpa TODOS os repositórios na ordem correta (evita erro de foreign key)
        transactionRepository.deleteAll();
        walletAssetRepository.deleteAll();
        walletRepository.deleteAll();
        userRepository.deleteAll();
        assetRepository.deleteAll();

        // Cria um usuário base para os testes de GET, PUT, DELETE
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@user.com");
        testUser.setPassword("encodedPassword123"); // Simula uma senha já salva
        testUser = userRepository.save(testUser);
        
        // Assumindo que o UserService ou a lógica de negócio NÃO cria a Wallet automaticamente
        // Se o seu service CRIAR uma wallet junto com o user, este teste vai pegar
    }

    @Test
    void testCreateUser() throws Exception {
        UserRequestCreate request = new UserRequestCreate();
        request.setUsername("newuser");
        request.setEmail("new@user.com");
        request.setPassword("newPassword123");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("newuser")))
                .andExpect(jsonPath("$.email", is("new@user.com")))
                .andExpect(jsonPath("$.password").doesNotExist()); // VERIFICAÇÃO DE SEGURANÇA
    }

    @Test
    void testListAllUsers() throws Exception {
        // O testUser criado no @BeforeEach já está no banco
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is(testUser.getUsername())))
                .andExpect(jsonPath("$[0].password").doesNotExist()); // VERIFICAÇÃO DE SEGURANÇA
    }

    @Test
    void testGetUserById() throws Exception {
        mockMvc.perform(get("/users/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testUser.getId().intValue())))
                .andExpect(jsonPath("$.username", is(testUser.getUsername())))
                .andExpect(jsonPath("$.password").doesNotExist()); // VERIFICAÇÃO DE SEGURANÇA
    }

    @Test
    void testUpdateUser() throws Exception {
        UserRequestUpdate request = new UserRequestUpdate();
        request.setId(testUser.getId());
        request.setUsername("updatedUser");
        request.setEmail("updated@user.com");
        request.setPassword("updatedPassword123"); // Senha deve ser atualizada no service

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testUser.getId().intValue())))
                .andExpect(jsonPath("$.username", is("updatedUser")))
                .andExpect(jsonPath("$.email", is("updated@user.com")))
                .andExpect(jsonPath("$.password").doesNotExist()); // VERIFICAÇÃO DE SEGURANÇA
    }

    @Test
    void testDeleteUser() throws Exception {
        // Deleta o usuário criado no setup
        mockMvc.perform(delete("/users/" + testUser.getId()))
                .andExpect(status().isOk());
    }
}