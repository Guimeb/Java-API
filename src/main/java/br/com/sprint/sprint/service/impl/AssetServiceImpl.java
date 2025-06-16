package br.com.sprint.sprint.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.sprint.sprint.dto.AssetRequestCreate;
import br.com.sprint.sprint.dto.AssetRequestUpdate;
import br.com.sprint.sprint.exception.ResourceNotFoundException;
import br.com.sprint.sprint.model.Asset;
import br.com.sprint.sprint.repository.AssetRepository;
import br.com.sprint.sprint.service.AssetService;

@Service
@Transactional
public class AssetServiceImpl implements AssetService {

    private final AssetRepository repo;

    public AssetServiceImpl(AssetRepository repo) {
        this.repo = repo;
    }

    @Override
    public Asset create(AssetRequestCreate dto) {
        if (repo.existsBySymbol(dto.getSymbol())) {
            throw new IllegalStateException("Asset already exists: " + dto.getSymbol());
        }
        Asset asset = new Asset();
        asset.setSymbol(dto.getSymbol());
        asset.setName(dto.getName());
        asset.setCurrentValue(dto.getCurrentValue());
        return repo.save(asset);
    }

    @Override
    public Asset update(AssetRequestUpdate dto) {
        Asset asset = repo.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found: " + dto.getId()));
        asset.setSymbol(dto.getSymbol());
        asset.setName(dto.getName());
        asset.setCurrentValue(dto.getCurrentValue());
        return repo.save(asset);
    }

    @Override
    public void delete(Long id) {
        Asset asset = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found: " + id));
        repo.delete(asset);
    }

    @Override
    @Transactional(readOnly = true)
    public Asset findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Asset> findAll() {
        return repo.findAll();
    }
}
