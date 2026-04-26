package framework.visa.repository;

import framework.visa.entity.Etat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EtatRepository extends JpaRepository<Etat, Integer> {
    Optional<Etat> findFirstByLibelleIgnoreCase(String libelle);
}
