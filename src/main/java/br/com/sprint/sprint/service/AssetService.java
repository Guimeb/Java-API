package br.com.sprint.sprint.service;

import java.util.List;
import br.com.sprint.sprint.dto.AssetRequestCreate;
import br.com.sprint.sprint.dto.AssetRequestUpdate;
import br.com.sprint.sprint.model.Asset;

public interface AssetService {
    Asset create(AssetRequestCreate dto);
    Asset update(AssetRequestUpdate dto);
    void delete(Long id);
    Asset findById(Long id);
    List<Asset> findAll();
}
