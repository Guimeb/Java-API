package br.com.sprint.sprint.service.impl;

import br.com.sprint.sprint.exception.ResourceNotFoundException;
import br.com.sprint.sprint.model.User;
import br.com.sprint.sprint.model.Wallet;
import br.com.sprint.sprint.repository.UserRepository;
import br.com.sprint.sprint.repository.WalletRepository;
import br.com.sprint.sprint.service.WalletAssetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Testes unitários para a lógica de serviço de Carteira (WalletServiceImpl)
class WalletServiceImplTest {

    @Mock
    private WalletRepository walletRepo;
    @Mock
    private UserRepository userRepo;
    @Mock
    private WalletAssetService walletAssetService; // Dependência para operações de ativo

    @InjectMocks
    private WalletServiceImpl service; // Classe sendo testada

    private User user;
    private Wallet wallet;

    @BeforeEach
    void setup() {
        // Inicializa os mocks
        MockitoAnnotations.openMocks(this);

        // Define as entidades de teste
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        wallet = new Wallet();
        wallet.setId(10L);
        wallet.setUser(user);
    }

    // -------------------------------
    // CREATE WALLET
    // -------------------------------

    @Test
    void deveCriarWalletParaUsuarioComSucesso() {
        // Simula o usuário encontrado e a ausência de carteira existente
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(walletRepo.findByUser(user)).thenReturn(Optional.empty());
        when(walletRepo.save(any(Wallet.class))).thenReturn(wallet);

        Wallet criada = service.createWalletForUser(1L);

        assertNotNull(criada);
        assertEquals(user, criada.getUser());
        verify(walletRepo).save(any(Wallet.class));
    }

    @Test
    void deveLancarErroQuandoUsuarioNaoEncontradoAoCriarWallet() {
        // Simula usuário não encontrado
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        // Verifica a exceção
        assertThrows(ResourceNotFoundException.class, () ->
                service.createWalletForUser(1L));
    }

    @Test
    void deveLancarErroQuandoWalletJaExisteParaUsuario() {
        // Simula usuário encontrado, mas carteira já existe
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(walletRepo.findByUser(user)).thenReturn(Optional.of(wallet));

        // Verifica a exceção
        assertThrows(IllegalStateException.class, () ->
                service.createWalletForUser(1L));
    }

    // -------------------------------
    // GET WALLET
    // -------------------------------

    @Test
    void deveBuscarWalletPorUsuarioComSucesso() {
        // Simula usuário e carteira encontrados
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(walletRepo.findByUser(user)).thenReturn(Optional.of(wallet));

        Wallet encontrada = service.getWalletByUser(1L);

        assertNotNull(encontrada);
        assertEquals(wallet, encontrada);
    }

    @Test
    void deveLancarErroQuandoUsuarioNaoEncontradoAoBuscarWallet() {
        // Simula usuário não encontrado
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        // Verifica a exceção
        assertThrows(ResourceNotFoundException.class, () ->
                service.getWalletByUser(1L));
    }

    @Test
    void deveLancarErroQuandoWalletNaoEncontradaAoBuscar() {
        // Simula usuário encontrado, mas carteira não existe
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(walletRepo.findByUser(user)).thenReturn(Optional.empty());

        // Verifica a exceção
        assertThrows(ResourceNotFoundException.class, () ->
                service.getWalletByUser(1L));
    }

    // -------------------------------
    // BUY ASSET
    // -------------------------------

    @Test
    void deveComprarAtivoComSucesso() {
        // Simula a carteira encontrada
        when(walletRepo.findById(10L)).thenReturn(Optional.of(wallet));

        Wallet resultado = service.buyAsset(10L, 20L, BigDecimal.TEN, BigDecimal.valueOf(5));

        assertNotNull(resultado);
        // Verifica se o método transact do serviço de WalletAsset foi chamado corretamente
        verify(walletAssetService).transact(10L, 20L, BigDecimal.TEN, BigDecimal.valueOf(5), "BUY");
    }

    @Test
    void deveLancarErroQuandoWalletNaoEncontradaAoComprar() {
        // Simula carteira não encontrada
        when(walletRepo.findById(10L)).thenReturn(Optional.empty());

        // Verifica a exceção
        assertThrows(ResourceNotFoundException.class, () ->
                service.buyAsset(10L, 20L, BigDecimal.ONE, BigDecimal.ONE));
    }

    // -------------------------------
    // SELL ASSET
    // -------------------------------

    @Test
    void deveVenderAtivoComSucesso() {
        // Simula a carteira encontrada
        when(walletRepo.findById(10L)).thenReturn(Optional.of(wallet));

        Wallet resultado = service.sellAsset(10L, 20L, BigDecimal.ONE, BigDecimal.TEN);

        assertNotNull(resultado);
        // Verifica se o método transact do serviço de WalletAsset foi chamado corretamente
        verify(walletAssetService).transact(10L, 20L, BigDecimal.ONE, BigDecimal.TEN, "SELL");
    }

    @Test
    void deveLancarErroQuandoWalletNaoEncontradaAoVender() {
        // Simula carteira não encontrada
        when(walletRepo.findById(10L)).thenReturn(Optional.empty());

        // Verifica a exceção
        assertThrows(ResourceNotFoundException.class, () ->
                service.sellAsset(10L, 20L, BigDecimal.ONE, BigDecimal.ONE));
    }
}