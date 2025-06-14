package br.com.sprint.sprint.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.com.sprint.sprint.dto.WalletResponse;
import br.com.sprint.sprint.model.Wallet;
import br.com.sprint.sprint.service.WalletService;

@RestController
@RequestMapping("/users/{userId}/wallet")
public class WalletController {

    private final WalletService service;

    public WalletController(WalletService service) {
        this.service = service;
    }

    @GetMapping
    public WalletResponse get(@PathVariable Long userId) {
        Wallet w = service.getWalletByUser(userId);
        return new WalletResponse(w.getId(), w.getUser().getId());
    }
}
