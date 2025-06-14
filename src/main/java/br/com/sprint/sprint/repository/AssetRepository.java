package br.com.sprint.sprint.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.sprint.sprint.model.Asset;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    boolean existsBySymbol(String symbol);
}
