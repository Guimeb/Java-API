package br.com.sprint.sprint.controller;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.*;
import br.com.sprint.sprint.dto.AssetRequestCreate;
import br.com.sprint.sprint.dto.AssetRequestUpdate;
import br.com.sprint.sprint.dto.AssetResponse;
import br.com.sprint.sprint.model.Asset;
import br.com.sprint.sprint.service.AssetService;

@RestController
@RequestMapping("/assets")
public class AssetController {

    private final AssetService service;

    public AssetController(AssetService service) {
        this.service = service;
    }

    @PostMapping
    public AssetResponse create(@RequestBody AssetRequestCreate dto) {
        Asset asset = service.create(dto);
        return new AssetResponse(asset.getId(), asset.getSymbol(), asset.getName(), asset.getCurrentValue());
    }

    @GetMapping
    public List<AssetResponse> listAll() {
        return service.findAll().stream()
            .map(a -> new AssetResponse(a.getId(), a.getSymbol(), a.getName(), a.getCurrentValue()))
            .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public AssetResponse getById(@PathVariable Long id) {
        Asset asset = service.findById(id);
        return new AssetResponse(asset.getId(), asset.getSymbol(), asset.getName(), asset.getCurrentValue());
    }

    @PutMapping
    public AssetResponse update(@RequestBody AssetRequestUpdate dto) {
        Asset asset = service.update(dto);
        return new AssetResponse(asset.getId(), asset.getSymbol(), asset.getName(), asset.getCurrentValue());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
