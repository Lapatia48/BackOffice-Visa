package framework.visa.repository;

import framework.visa.entity.DemandeurVisaCarteResident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DemandeurVisaCarteResidentRepository extends JpaRepository<DemandeurVisaCarteResident, Integer> {
    @Query("""
        select dvcr from DemandeurVisaCarteResident dvcr
        join fetch dvcr.demandeur demandeur
        join fetch dvcr.visa visa
        join fetch dvcr.carteResident carte
        where dvcr.visa.id = :visaId
        order by dvcr.id desc
    """)
    List<DemandeurVisaCarteResident> findWithDetailsByVisaId(@Param("visaId") Integer visaId);

    @Query("""
        select dvcr from DemandeurVisaCarteResident dvcr
        join fetch dvcr.demandeur demandeur
        join fetch dvcr.visa visa
        join fetch dvcr.carteResident carte
        where dvcr.visa.id in :visaIds
        order by dvcr.id desc
    """)
    List<DemandeurVisaCarteResident> findWithDetailsByVisaIdIn(@Param("visaIds") Collection<Integer> visaIds);

    @Query("""
        select dvcr from DemandeurVisaCarteResident dvcr
        join fetch dvcr.demandeur demandeur
        join fetch dvcr.visa visa
        join fetch dvcr.carteResident carte
        where dvcr.demandeur.id = :demandeurId
        order by dvcr.id desc
    """)
    List<DemandeurVisaCarteResident> findWithDetailsByDemandeurId(@Param("demandeurId") Integer demandeurId);

    @Query("""
        select dvcr from DemandeurVisaCarteResident dvcr
        join fetch dvcr.demandeur demandeur
        join fetch dvcr.visa visa
        join fetch dvcr.carteResident carte
        where dvcr.demandeur.id in :demandeurIds
        order by dvcr.id desc
    """)
    List<DemandeurVisaCarteResident> findWithDetailsByDemandeurIdIn(@Param("demandeurIds") Collection<Integer> demandeurIds);

    default Optional<DemandeurVisaCarteResident> findFirstWithDetailsByVisaId(Integer visaId) {
        List<DemandeurVisaCarteResident> rows = findWithDetailsByVisaId(visaId);
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }

    default Optional<DemandeurVisaCarteResident> findFirstWithDetailsByDemandeurId(Integer demandeurId) {
        List<DemandeurVisaCarteResident> rows = findWithDetailsByDemandeurId(demandeurId);
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }
}
