package br.com.sprint.sprint.service.impl;

import br.com.sprint.sprint.dto.AssetRequestCreate;
import br.com.sprint.sprint.dto.AssetRequestUpdate;
import br.com.sprint.sprint.exception.ResourceNotFoundException;
import br.com.sprint.sprint.model.Asset;
import br.com.sprint.sprint.repository.AssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AssetServiceImplTest {

    @Mock
    private AssetRepository repo;

    @InjectMocks
    private AssetServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveCriarAssetComSucesso() {
        AssetRequestCreate dto = new AssetRequestCreate();
        dto.setSymbol("PETR4");
        dto.setName("Petrobras");
        dto.setCurrentValue(BigDecimal.valueOf(35.0));

        when(repo.existsBySymbol("PETR4")).thenReturn(false);
        when(repo.save(any(Asset.class))).thenAnswer(invocation -> {
            Asset asset = invocation.getArgument(0);
            asset.setId(1L);
            return asset;
        });

        Asset assetCriado = service.create(dto);

        assertNotNull(assetCriado);
        assertEquals("PETR4", assetCriado.getSymbol());
        verify(repo, times(1)).save(any(Asset.class));
    }

    @Test
    void deveLancarErroAoCriarAssetDuplicado() {
        AssetRequestCreate dto = new AssetRequestCreate();
        dto.setSymbol("PETR4");

        when(repo.existsBySymbol("PETR4")).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> service.create(dto));
        verify(repo, never()).save(any());
    }

    @Test
    void deveAtualizarAsset() {
        AssetRequestUpdate dto = new AssetRequestUpdate();
        dto.setId(1L);
        dto.setSymbol("VALE3");
        dto.setName("Vale");
        dto.setCurrentValue(BigDecimal.valueOf(70.0));

        Asset existente = new Asset();
        existente.setId(1L);
        existente.setSymbol("OLD");
        existente.setName("Antiga");

        when(repo.findById(1L)).thenReturn(Optional.of(existente));
        when(repo.save(any(Asset.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Asset atualizado = service.update(dto);

        assertEquals("VALE3", atualizado.getSymbol());
        assertEquals("Vale", atualizado.getName());
        verify(repo).save(existente);
    }

    @Test
    void deveLancarErroAoAtualizarAssetNaoEncontrado() {
        AssetRequestUpdate dto = new AssetRequestUpdate();
        dto.setId(99L);

        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(dto));
    }

    @Test
    void deveDeletarAsset() {
        Asset asset = new Asset();
        asset.setId(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(asset));

        service.delete(1L);

        verify(repo).delete(asset);
    }

    @Test
    void deveLancarErroAoDeletarAssetInexistente() {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
    }

    @Test
    void deveRetornarAssetPorId() {
        Asset asset = new Asset();
        asset.setId(1L);
        asset.setSymbol("PETR4");

        when(repo.findById(1L)).thenReturn(Optional.of(asset));

        Asset resultado = service.findById(1L);

        assertEquals("PETR4", resultado.getSymbol());
    }

    @Test
    void deveListarTodosAssets() {
        when(repo.findAll()).thenReturn(List.of(new Asset(), new Asset()));

        List<Asset> lista = service.findAll();

        assertEquals(2, lista.size());
    }
}
