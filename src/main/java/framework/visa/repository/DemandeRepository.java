package framework.visa.repository;

import framework.visa.entity.Demande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DemandeRepository extends JpaRepository<Demande, Integer> {
	@Query("""
		select d from Demande d
		join fetch d.demandeur demandeur
		join fetch d.typeDemande typeDemande
		join fetch d.statut statut
		where exists (
			select 1 from DemandeDossier dd
			where dd.demande = d and dd.estFourni = false
		)
		order by d.dateDemande desc, d.id desc
	""")
	List<Demande> findDemandesen_courses();

	@Query("""
		select d from Demande d
		join fetch d.demandeur demandeur
		join fetch d.typeDemande typeDemande
		join fetch d.statut statut
		where d.id = :demandeId
	""")
	Optional<Demande> findDetailedById(@Param("demandeId") Integer demandeId);

	@Query("""
		select d from Demande d
		join fetch d.demandeur demandeur
		join fetch d.typeDemande typeDemande
		join fetch d.statut statut
		join fetch d.visa visa
		join fetch visa.categorieVisa categorie
		where lower(statut.libelle) = 'terminee'
			and lower(categorie.libelle) in ('nouveau_titre', 'nouveau titre')
		order by visa.dateDebut desc, d.id desc
	""")
	List<Demande> findDossiersTermineesNouveauTitre();
}
