package br.com.sprint.sprint.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import br.com.sprint.sprint.dto.*;
import br.com.sprint.sprint.model.WalletAsset;
import br.com.sprint.sprint.service.WalletAssetService;

@RestController
@RequestMapping("/wallets/{walletId}/assets")
public class WalletAssetController {

    private final WalletAssetService service;

    public WalletAssetController(WalletAssetService service) {
        this.service = service;
    }

    @PostMapping
    public WalletAssetResponse add(
        @PathVariable Long walletId,
        @Valid @RequestBody WalletAssetRequestCreate dto
    ) {
        WalletAsset wa = service.addToWallet(
            walletId,
            dto.getAssetId(),
            dto.getQuantity(),
            dto.getPurchasePrice()
        );
        return new WalletAssetResponse(
            wa.getId(),
            wa.getWallet().getId(),
            wa.getAsset().getId(),
            wa.getQuantity(),
            wa.getPurchasePrice(),
            wa.getPurchaseDate()
        );
    }

    @GetMapping
    public List<WalletAssetResponse> list(@PathVariable Long walletId) {
        return service.listByWallet(walletId).stream()
            .map(wa -> new WalletAssetResponse(
                wa.getId(),
                wa.getWallet().getId(),
                wa.getAsset().getId(),
                wa.getQuantity(),
                wa.getPurchasePrice(),
                wa.getPurchaseDate()
            ))
            .collect(Collectors.toList());
    }

    @PutMapping("/{walletAssetId}")
    public WalletAssetResponse update(
        @PathVariable Long walletId,
        @PathVariable Long walletAssetId,
        @Valid @RequestBody WalletAssetRequestUpdate dto
    ) {
        WalletAsset wa = service.updateInWallet(
            walletId,
            walletAssetId,
            dto.getQuantity(),
            dto.getPurchasePrice()
        );
        return new WalletAssetResponse(
            wa.getId(),
            wa.getWallet().getId(),
            wa.getAsset().getId(),
            wa.getQuantity(),
            wa.getPurchasePrice(),
            wa.getPurchaseDate()
        );
    }

    @DeleteMapping("/{walletAssetId}")
    public void remove(
        @PathVariable Long walletId,
        @PathVariable Long walletAssetId
    ) {
        service.removeFromWallet(walletId, walletAssetId);
    }
}
