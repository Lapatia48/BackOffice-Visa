package framework.visa.repository;

import framework.visa.entity.StatutDemande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatutDemandeRepository extends JpaRepository<StatutDemande, Integer> {
	Optional<StatutDemande> findFirstByLibelleIgnoreCase(String libelle);
}
