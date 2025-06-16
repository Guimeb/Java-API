package br.com.sprint.sprint.dto;

public class WalletResponse {
    private Long id;
    private Long userId;

    public WalletResponse(Long id, Long userId) {
        this.id = id;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }
}
