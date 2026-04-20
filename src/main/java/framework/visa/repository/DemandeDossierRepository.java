package framework.visa.repository;

import framework.visa.entity.DemandeDossier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DemandeDossierRepository extends JpaRepository<DemandeDossier, Integer> {
    List<DemandeDossier> findByDemandeId(Integer demandeId);
}
