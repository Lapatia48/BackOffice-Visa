package framework.visa.repository;

import framework.visa.entity.DemandeDossier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DemandeDossierRepository extends JpaRepository<DemandeDossier, Integer> {
    List<DemandeDossier> findByDemandeId(Integer demandeId);

    List<DemandeDossier> findByDemandeIdOrderByDossierLibelleAsc(Integer demandeId);

    List<DemandeDossier> findByDemandeIdIn(Collection<Integer> demandeIds);

    Optional<DemandeDossier> findFirstByDemandeIdAndDossierId(Integer demandeId, Integer dossierId);
}
