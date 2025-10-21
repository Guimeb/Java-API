package br.com.sprint.sprint.service.impl;

import br.com.sprint.sprint.dto.UserRequestCreate;
import br.com.sprint.sprint.dto.UserRequestUpdate;
import br.com.sprint.sprint.dto.UserResquestDelete;
import br.com.sprint.sprint.exception.ResourceNotFoundException;
import br.com.sprint.sprint.model.User;
import br.com.sprint.sprint.repository.UserRepository;
import br.com.sprint.sprint.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Testes unitários para o serviço de Usuário (UserServiceImpl)
class UserServiceImplTest {

    @Mock
    private UserRepository repo; // Simula o repositório de dados

    @Mock
    private WalletService walletService; // Simula o serviço de Carteira (dependência)

    @Mock
    private PasswordEncoder passwordEncoder; // Simula o encoder de senha

    @InjectMocks
    private UserServiceImpl service; // Classe sendo testada

    @BeforeEach
    void setup() {
        // Inicializa os mocks
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveCriarUsuarioComSucesso() {
        UserRequestCreate dto = new UserRequestCreate();
        dto.setUsername("guilherme");
        dto.setEmail("gui@example.com");
        dto.setPassword("123");

        User user = new User();
        user.setId(1L);
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());

        // Simula a codificação da senha e o salvamento no repositório
        when(passwordEncoder.encode("123")).thenReturn("encoded123");
        when(repo.save(any(User.class))).thenReturn(user);

        User result = service.create(dto);

        assertNotNull(result);
        assertEquals("guilherme", result.getUsername());
        // Verifica se o usuário foi salvo
        verify(repo).save(any(User.class));
        // Verifica se a carteira foi criada após o salvamento
        verify(walletService).createWalletForUser(1L);
    }

    @Test
    void deveAtualizarUsuarioComSucesso() {
        UserRequestUpdate dto = new UserRequestUpdate();
        dto.setId(1L);
        dto.setUsername("novoNome");
        dto.setEmail("novo@example.com");
        dto.setPassword("novaSenha");

        User existente = new User();
        existente.setId(1L);
        existente.setUsername("antigo");
        existente.setEmail("antigo@example.com");

        // Simula a busca do usuário existente
        when(repo.findById(1L)).thenReturn(Optional.of(existente));
        when(passwordEncoder.encode("novaSenha")).thenReturn("encodedNova");
        // Simula o salvamento
        when(repo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User atualizado = service.update(dto);

        assertEquals("novoNome", atualizado.getUsername());
        assertEquals("novo@example.com", atualizado.getEmail());
        verify(repo).save(existente);
    }

    @Test
    void deveLancarErroAoAtualizarUsuarioNaoEncontrado() {
        UserRequestUpdate dto = new UserRequestUpdate();
        dto.setId(99L);
        // Simula que o usuário não existe
        when(repo.findById(99L)).thenReturn(Optional.empty());

        // Verifica a exceção de Recurso Não Encontrado
        assertThrows(ResourceNotFoundException.class, () -> service.update(dto));
    }

    @Test
    void deveDeletarUsuarioComSucesso() {
        UserResquestDelete dto = new UserResquestDelete();
        dto.setId(1L);

        User user = new User();
        user.setId(1L);

        // Simula que o usuário foi encontrado
        when(repo.findById(1L)).thenReturn(Optional.of(user));

        service.delete(dto);

        // Verifica se o método de deleção do repositório foi chamado
        verify(repo).delete(user);
    }

    @Test
    void deveLancarErroAoDeletarUsuarioNaoEncontrado() {
        UserResquestDelete dto = new UserResquestDelete();
        dto.setId(42L);

        // Simula que o usuário não foi encontrado
        when(repo.findById(42L)).thenReturn(Optional.empty());

        // Verifica a exceção de Recurso Não Encontrado
        assertThrows(ResourceNotFoundException.class, () -> service.delete(dto));
    }

    @Test
    void deveRetornarUsuarioPorId() {
        User user = new User();
        user.setId(1L);
        user.setUsername("gui");

        // Simula a busca bem-sucedida por ID
        when(repo.findById(1L)).thenReturn(Optional.of(user));

        User resultado = service.findById(1L);

        assertEquals("gui", resultado.getUsername());
    }

    @Test
    void deveLancarErroAoBuscarUsuarioInexistente() {
        // Simula a busca sem sucesso
        when(repo.findById(10L)).thenReturn(Optional.empty());

        // Verifica a exceção de Recurso Não Encontrado
        assertThrows(ResourceNotFoundException.class, () -> service.findById(10L));
    }

    @Test
    void deveListarTodosUsuarios() {
        // Simula o retorno de uma lista com 3 usuários
        when(repo.findAll()).thenReturn(List.of(new User(), new User(), new User()));

        List<User> lista = service.findAll();

        assertEquals(3, lista.size());
        // Verifica se o método findAll do repositório foi chamado
        verify(repo).findAll();
    }
}