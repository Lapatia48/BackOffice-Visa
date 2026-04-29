package framework.visa.repository;

import framework.visa.entity.DemandeDossierScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DemandeDossierScanRepository extends JpaRepository<DemandeDossierScan, Integer> {
    @Query("""
        select s from DemandeDossierScan s
        where s.demandeDossier.id = :demandeDossierId
    """)
    Optional<DemandeDossierScan> findByDemandeDossierId(@Param("demandeDossierId") Integer demandeDossierId);

    @Query("""
        select s from DemandeDossierScan s
        join fetch s.demandeDossier dd
        join fetch dd.demande d
        join fetch dd.dossier dossier
        where d.id in :demandeIds
    """)
    List<DemandeDossierScan> findByDemandeIds(@Param("demandeIds") Collection<Integer> demandeIds);

    @Query("""
        select dd.dossier.id from DemandeDossierScan s
        join s.demandeDossier dd
        where dd.demande.id = :demandeId
    """)
    List<Integer> findScannedDossierIdsByDemandeId(@Param("demandeId") Integer demandeId);
}
