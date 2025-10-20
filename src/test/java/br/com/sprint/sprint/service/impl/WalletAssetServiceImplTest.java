package br.com.sprint.sprint.service.impl;

import br.com.sprint.sprint.exception.ResourceNotFoundException;
import br.com.sprint.sprint.model.*;
import br.com.sprint.sprint.model.vo.Price;
import br.com.sprint.sprint.model.vo.Quantity;
import br.com.sprint.sprint.repository.AssetRepository;
import br.com.sprint.sprint.repository.TransactionRepository;
import br.com.sprint.sprint.repository.WalletAssetRepository;
import br.com.sprint.sprint.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WalletAssetServiceImplTest {

    @Mock
    private WalletAssetRepository waRepo;
    @Mock
    private WalletRepository walletRepo;
    @Mock
    private AssetRepository assetRepo;
    @Mock
    private TransactionRepository transactionRepo;

    @InjectMocks
    private WalletAssetServiceImpl service;

    private Wallet wallet;
    private Asset asset;
    private WalletAsset waExistente;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        wallet = new Wallet();
        wallet.setId(1L);

        asset = new Asset();
        asset.setId(10L);

        waExistente = new WalletAsset();
        waExistente.setId(100L);
        waExistente.setWallet(wallet);
        waExistente.setAsset(asset);
        waExistente.setQuantity(new Quantity(BigDecimal.valueOf(10)));
        waExistente.setAveragePrice(new Price(BigDecimal.valueOf(5)));
    }

    @Test
    void deveComprarAtivoComSucesso() {
        when(walletRepo.findById(1L)).thenReturn(Optional.of(wallet));
        when(assetRepo.findById(10L)).thenReturn(Optional.of(asset));
        when(waRepo.findByWalletIdAndAssetId(1L, 10L)).thenReturn(Optional.of(waExistente));
        when(waRepo.save(any(WalletAsset.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WalletAsset resultado = service.transact(1L, 10L, BigDecimal.valueOf(10), BigDecimal.valueOf(10), "BUY");

        assertNotNull(resultado);
        assertEquals(0, resultado.getQuantity().getValue().compareTo(BigDecimal.valueOf(20)));
        verify(transactionRepo).save(any(Transaction.class));
    }

    @Test
    void deveCriarNovoWalletAssetQuandoNaoExistirEComprar() {
        when(walletRepo.findById(1L)).thenReturn(Optional.of(wallet));
        when(assetRepo.findById(10L)).thenReturn(Optional.of(asset));
        when(waRepo.findByWalletIdAndAssetId(1L, 10L)).thenReturn(Optional.empty());
        when(waRepo.save(any(WalletAsset.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WalletAsset resultado = service.transact(1L, 10L, BigDecimal.valueOf(5), BigDecimal.valueOf(2), "BUY");

        assertEquals(0, resultado.getQuantity().getValue().compareTo(BigDecimal.valueOf(5)));
        assertEquals(0, resultado.getAveragePrice().getValue().compareTo(BigDecimal.valueOf(2)));
    }

    @Test
    void deveVenderParteDoAtivoComSucesso() {
        when(walletRepo.findById(1L)).thenReturn(Optional.of(wallet));
        when(assetRepo.findById(10L)).thenReturn(Optional.of(asset));
        when(waRepo.findByWalletIdAndAssetId(1L, 10L)).thenReturn(Optional.of(waExistente));
        when(waRepo.save(any(WalletAsset.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WalletAsset resultado = service.transact(1L, 10L, BigDecimal.valueOf(4), BigDecimal.valueOf(5), "SELL");

        assertEquals(0, resultado.getQuantity().getValue().compareTo(BigDecimal.valueOf(6)));
        verify(transactionRepo).save(any(Transaction.class));
    }

    @Test
    void deveLancarErroAoVenderMaisDoQueTem() {
        when(walletRepo.findById(1L)).thenReturn(Optional.of(wallet));
        when(assetRepo.findById(10L)).thenReturn(Optional.of(asset));
        when(waRepo.findByWalletIdAndAssetId(1L, 10L)).thenReturn(Optional.of(waExistente));

        assertThrows(IllegalArgumentException.class, () ->
                service.transact(1L, 10L, BigDecimal.valueOf(50), BigDecimal.valueOf(10), "SELL"));
    }

    @Test
    void deveLancarErroParaTipoInvalido() {
        when(walletRepo.findById(1L)).thenReturn(Optional.of(wallet));
        when(assetRepo.findById(10L)).thenReturn(Optional.of(asset));
        when(waRepo.findByWalletIdAndAssetId(1L, 10L)).thenReturn(Optional.of(waExistente));

        assertThrows(IllegalArgumentException.class, () ->
                service.transact(1L, 10L, BigDecimal.ONE, BigDecimal.ONE, "INVALIDO"));
    }

    @Test
    void deveLancarErroQuandoWalletNaoEncontrada() {
        when(walletRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () ->
                service.transact(1L, 10L, BigDecimal.ONE, BigDecimal.ONE, "BUY"));
    }

    @Test
    void deveLancarErroQuandoAssetNaoEncontrado() {
        when(walletRepo.findById(1L)).thenReturn(Optional.of(wallet));
        when(assetRepo.findById(10L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () ->
                service.transact(1L, 10L, BigDecimal.ONE, BigDecimal.ONE, "BUY"));
    }

    @Test
    void deveListarAtivosDaCarteira() {
        when(waRepo.findByWalletId(1L)).thenReturn(List.of(new WalletAsset(), new WalletAsset()));
        List<WalletAsset> lista = service.listByWallet(1L);
        assertEquals(2, lista.size());
        verify(waRepo).findByWalletId(1L);
    }

    @Test
    void deveAtualizarAtivoNaCarteira() {
        when(waRepo.findByIdAndWalletId(100L, 1L)).thenReturn(Optional.of(waExistente));
        when(waRepo.save(any(WalletAsset.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WalletAsset atualizado = service.updateInWallet(1L, 100L, BigDecimal.TEN, BigDecimal.valueOf(9));

        assertEquals(0, atualizado.getQuantity().getValue().compareTo(BigDecimal.TEN));
        assertEquals(0, atualizado.getAveragePrice().getValue().compareTo(BigDecimal.valueOf(9)));
    }

    @Test
    void deveLancarErroAoAtualizarAtivoNaoEncontrado() {
        when(waRepo.findByIdAndWalletId(100L, 1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () ->
                service.updateInWallet(1L, 100L, BigDecimal.TEN, BigDecimal.ONE));
    }

    @Test
    void deveRemoverAtivoDaCarteira() {
        when(waRepo.findByIdAndWalletId(100L, 1L)).thenReturn(Optional.of(waExistente));
        service.removeFromWallet(1L, 100L);
        verify(waRepo).delete(waExistente);
    }

    @Test
    void deveLancarErroAoRemoverAtivoNaoEncontrado() {
        when(waRepo.findByIdAndWalletId(100L, 1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () ->
                service.removeFromWallet(1L, 100L));
    }
}
