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

// Testes unitários para a classe AssetServiceImpl, utilizando Mockito
class AssetServiceImplTest {

    @Mock
    private AssetRepository repo; // Simula o repositório

    @InjectMocks
    private AssetServiceImpl service; // Injeta os mocks na classe a ser testada

    @BeforeEach
    void setup() {
        // Inicializa os mocks antes de cada teste
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveCriarAssetComSucesso() {
        AssetRequestCreate dto = new AssetRequestCreate();
        dto.setSymbol("PETR4");
        dto.setName("Petrobras");
        dto.setCurrentValue(BigDecimal.valueOf(35.0));

        // Simula que o ativo não existe e, ao salvar, atribui um ID
        when(repo.existsBySymbol("PETR4")).thenReturn(false);
        when(repo.save(any(Asset.class))).thenAnswer(invocation -> {
            Asset asset = invocation.getArgument(0);
            asset.setId(1L);
            return asset;
        });

        Asset assetCriado = service.create(dto);

        assertNotNull(assetCriado);
        assertEquals("PETR4", assetCriado.getSymbol());
        // Verifica se o método save foi chamado uma vez
        verify(repo, times(1)).save(any(Asset.class));
    }

    @Test
    void deveLancarErroAoCriarAssetDuplicado() {
        AssetRequestCreate dto = new AssetRequestCreate();
        dto.setSymbol("PETR4");

        // Simula que o ativo com o símbolo já existe
        when(repo.existsBySymbol("PETR4")).thenReturn(true);

        // Verifica se a exceção correta é lançada
        assertThrows(IllegalStateException.class, () -> service.create(dto));
        // Verifica se o método save NUNCA foi chamado
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

        // Simula a busca do ativo e o salvamento
        when(repo.findById(1L)).thenReturn(Optional.of(existente));
        when(repo.save(any(Asset.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Asset atualizado = service.update(dto);

        // Verifica se os campos foram atualizados
        assertEquals("VALE3", atualizado.getSymbol());
        assertEquals("Vale", atualizado.getName());
        verify(repo).save(existente);
    }

    @Test
    void deveLancarErroAoAtualizarAssetNaoEncontrado() {
        AssetRequestUpdate dto = new AssetRequestUpdate();
        dto.setId(99L);

        // Simula que o ativo não existe
        when(repo.findById(99L)).thenReturn(Optional.empty());

        // Verifica se a exceção de Recurso Não Encontrado é lançada
        assertThrows(ResourceNotFoundException.class, () -> service.update(dto));
    }

    @Test
    void deveDeletarAsset() {
        Asset asset = new Asset();
        asset.setId(1L);
        // Simula que o ativo é encontrado
        when(repo.findById(1L)).thenReturn(Optional.of(asset));

        service.delete(1L);

        // Verifica se o método delete foi chamado com a entidade correta
        verify(repo).delete(asset);
    }

    @Test
    void deveLancarErroAoDeletarAssetInexistente() {
        // Simula que o ativo não é encontrado
        when(repo.findById(1L)).thenReturn(Optional.empty());

        // Verifica se a exceção de Recurso Não Encontrado é lançada
        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
    }

    @Test
    void deveRetornarAssetPorId() {
        Asset asset = new Asset();
        asset.setId(1L);
        asset.setSymbol("PETR4");

        // Simula a busca bem-sucedida por ID
        when(repo.findById(1L)).thenReturn(Optional.of(asset));

        Asset resultado = service.findById(1L);

        // Verifica o retorno
        assertEquals("PETR4", resultado.getSymbol());
    }

    @Test
    void deveListarTodosAssets() {
        // Simula o retorno de uma lista com dois ativos
        when(repo.findAll()).thenReturn(List.of(new Asset(), new Asset()));

        List<Asset> lista = service.findAll();

        // Verifica o tamanho da lista retornada
        assertEquals(2, lista.size());
    }
}