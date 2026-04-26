package framework.visa.repository;

import framework.visa.entity.TypeDemande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TypeDemandeRepository extends JpaRepository<TypeDemande, Integer> {
	Optional<TypeDemande> findFirstByLibelleIgnoreCase(String libelle);
}
