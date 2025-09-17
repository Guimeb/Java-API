package br.com.sprint.sprint.controller;

import java.util.List;
import java.util.stream.Collectors;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import br.com.sprint.sprint.dto.WalletAssetRequestCreate;
import br.com.sprint.sprint.dto.WalletAssetRequestUpdate;
import br.com.sprint.sprint.dto.WalletAssetResponse;
import br.com.sprint.sprint.model.WalletAsset;
import br.com.sprint.sprint.service.WalletAssetService;
import br.com.sprint.sprint.service.WalletService;

@RestController
@RequestMapping("/users/{userId}/wallet/assets")
public class WalletAssetController {

        private final WalletAssetService walletAssetService;
        private final WalletService walletService;

        public WalletAssetController(WalletAssetService walletAssetService, WalletService walletService) {
                this.walletAssetService = walletAssetService;
                this.walletService = walletService;
        }

        @PostMapping("/buy")
        public WalletAssetResponse buyAsset(@PathVariable Long userId,
                        @Valid @RequestBody WalletAssetRequestCreate request) {
                Long walletId = walletService.getWalletByUser(userId).getId();
                WalletAsset wa = walletAssetService.transact(walletId, request.getAssetId(),
                                request.getQuantity(), request.getPurchasePrice(), "BUY");
                return toResponse(wa);
        }

        @PostMapping("/sell")
        public WalletAssetResponse sellAsset(@PathVariable Long userId,
                        @Valid @RequestBody WalletAssetRequestCreate request) {
                Long walletId = walletService.getWalletByUser(userId).getId();
                WalletAsset wa = walletAssetService.transact(walletId, request.getAssetId(),
                                request.getQuantity(), request.getPurchasePrice(), "SELL");
                return toResponse(wa);
        }

        @PutMapping("/update")
        public WalletAssetResponse updateAsset(@PathVariable Long userId,
                        @Valid @RequestBody WalletAssetRequestUpdate request) {
                Long walletId = walletService.getWalletByUser(userId).getId();
                WalletAsset wa = walletAssetService.updateInWallet(walletId, request.getWalletAssetId(),
                                request.getQuantity(), request.getAveragePrice());
                return toResponse(wa);
        }

        @GetMapping
        public List<WalletAssetResponse> listAssets(@PathVariable Long userId) {
                Long walletId = walletService.getWalletByUser(userId).getId();
                List<WalletAsset> assets = walletAssetService.listByWallet(walletId);
                return assets.stream().map(this::toResponse).collect(Collectors.toList());
        }

        private WalletAssetResponse toResponse(WalletAsset wa) {
                return new WalletAssetResponse(
                                wa.getId(),
                                wa.getWallet().getId(),
                                wa.getAsset().getId(),
                                wa.getQuantity(),
                                wa.getAveragePrice());
        }
}
