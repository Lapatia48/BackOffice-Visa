package framework.visa.repository;

import framework.visa.entity.HistoStatutDemande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface HistoStatutDemandeRepository extends JpaRepository<HistoStatutDemande, Integer> {
	@Query("""
		select h from HistoStatutDemande h
		join fetch h.demande d
		join fetch d.demandeur demandeur
		left join fetch h.statut statut
		where demandeur.id in :demandeurIds
		order by demandeur.id asc, h.dateChangement desc, h.id desc
	""")
	List<HistoStatutDemande> findByDemandeurIds(@Param("demandeurIds") Collection<Integer> demandeurIds);
}
