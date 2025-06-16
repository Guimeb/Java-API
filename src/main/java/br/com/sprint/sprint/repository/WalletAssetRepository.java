package br.com.sprint.sprint.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.sprint.sprint.model.WalletAsset;

@Repository
public interface WalletAssetRepository extends JpaRepository<WalletAsset, Long> {
    List<WalletAsset> findByWalletId(Long walletId);
    Optional<WalletAsset> findByIdAndWalletId(Long id, Long walletId);
}
